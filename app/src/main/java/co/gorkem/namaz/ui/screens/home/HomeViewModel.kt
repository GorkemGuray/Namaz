package co.gorkem.namaz.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.data.local.datastore.UserPreferences
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerDashboardUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import co.gorkem.namaz.domain.usecase.PrayerDashboardData
import co.gorkem.namaz.service.GeocoderHelper
import co.gorkem.namaz.service.LocationTracker
import co.gorkem.namaz.service.PrayerNotificationScheduler
import co.gorkem.namaz.service.WearKerahatInfo
import co.gorkem.namaz.service.WearNextPrayer
import co.gorkem.namaz.service.WearPrayerData
import co.gorkem.namaz.service.WearPrayerItem
import co.gorkem.namaz.service.WearSyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val getNextPrayerUseCase: GetNextPrayerUseCase,
    private val getPrayerDashboardUseCase: GetPrayerDashboardUseCase,
    private val locationTracker: LocationTracker,
    private val geocoderHelper: GeocoderHelper,
    private val notificationScheduler: PrayerNotificationScheduler,
    private val wearSyncManager: WearSyncManager,
    private val preferencesRepository: UserPreferencesRepository,
    private val prayerTimeRepo: PrayerTimeRepositoryImpl
) : ViewModel() {

    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTime>> = _prayerTimes.asStateFlow()

    private val _nextPrayer = MutableStateFlow<GetNextPrayerUseCase.NextPrayerInfo?>(null)
    val nextPrayer: StateFlow<GetNextPrayerUseCase.NextPrayerInfo?> = _nextPrayer.asStateFlow()

    private val _dashboardData = MutableStateFlow<PrayerDashboardData?>(null)
    val dashboardData: StateFlow<PrayerDashboardData?> = _dashboardData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _locationInfo = MutableStateFlow(LocationInfo.DEFAULT)
    val locationInfo: StateFlow<LocationInfo> = _locationInfo.asStateFlow()

    private val _needsLocationPermission = MutableStateFlow(false)
    val needsLocationPermission: StateFlow<Boolean> = _needsLocationPermission.asStateFlow()

    private var currentPreferences = UserPreferences()
    private var todayPrayerTime: PrayerTime? = null
    private var dashboardRefreshJob: Job? = null
    private var prayerTimesJob: Job? = null

    init {
        viewModelScope.launch {
            var lastCityKey: String? = null
            var lastMethod: Int = -1
            var lastSchool: Int = -1
            var lastNotificationsEnabled: Boolean? = null
            var lastNotificationMinutesBefore: Int? = null
            preferencesRepository.preferences.collect { prefs ->
                val notificationsEnabledChanged = lastNotificationsEnabled != null && prefs.notificationsEnabled != lastNotificationsEnabled
                val notificationMinutesBeforeChanged = lastNotificationMinutesBefore != null && prefs.notificationMinutesBefore != lastNotificationMinutesBefore

                currentPreferences = prefs
                lastNotificationsEnabled = prefs.notificationsEnabled
                lastNotificationMinutesBefore = prefs.notificationMinutesBefore

                val cityKey = "${prefs.manualDistrictId},${prefs.manualLatitude},${prefs.manualLongitude},${prefs.manualCityName}"
                val methodChanged = prefs.calculationMethod != lastMethod
                val schoolChanged = prefs.school != lastSchool
                if (cityKey != lastCityKey || methodChanged || schoolChanged) {
                    lastCityKey = cityKey
                    lastMethod = prefs.calculationMethod
                    lastSchool = prefs.school
                    if (prefs.manualCityName.isNotEmpty()) {
                        _needsLocationPermission.value = false
                        loadPrayerTimes(LocationInfo(
                            latitude = prefs.manualLatitude,
                            longitude = prefs.manualLongitude,
                            timezone = prefs.manualTimezone.ifEmpty { "Europe/Istanbul" },
                            cityName = prefs.manualCityName,
                            districtName = prefs.manualDistrictName,
                            districtId = prefs.manualDistrictId
                        ))
                    } else if (locationTracker.hasLocationPermission()) {
                        loadWithCurrentLocation()
                    } else {
                        _needsLocationPermission.value = true
                        loadPrayerTimes(LocationInfo.DEFAULT)
                    }
                } else if (notificationsEnabledChanged || notificationMinutesBeforeChanged) {
                    if (_prayerTimes.value.isNotEmpty()) {
                        scheduleNotifications(_prayerTimes.value, _locationInfo.value)
                    }
                }
            }
        }
    }

    fun onLocationPermissionGranted() {
        _needsLocationPermission.value = false
        loadWithCurrentLocation()
    }

    fun onLocationPermissionDenied() {
        _needsLocationPermission.value = false
        loadPrayerTimes(LocationInfo.DEFAULT)
    }

    fun useMyLocation() {
        if (locationTracker.hasLocationPermission()) {
            viewModelScope.launch {
                preferencesRepository.clearManualCity()
            }
        } else {
            _needsLocationPermission.value = true
        }
    }

    private fun loadWithCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    val info = geocoderHelper.getLocationInfo(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    val districtId = prayerTimeRepo.findDistrictId(info.cityName, info.districtName)
                    loadPrayerTimes(info.copy(districtId = districtId ?: ""))
                } else {
                    loadPrayerTimes(LocationInfo.DEFAULT)
                }
            } catch (e: Exception) {
                loadPrayerTimes(LocationInfo.DEFAULT)
            }
        }
    }

    fun loadPrayerTimes(location: LocationInfo) {
        prayerTimesJob?.cancel()
        _locationInfo.value = location
        _prayerTimes.value = emptyList()
        _error.value = null
        _nextPrayer.value = null
        _dashboardData.value = null
        val today = LocalDate.now()
        val todayKey = today.format(DATE_FORMAT)

        prayerTimesJob = viewModelScope.launch {
            getPrayerTimesUseCase(
                location = location,
                month = today.monthValue,
                year = today.year,
                method = currentPreferences.calculationMethod,
                school = currentPreferences.school
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _isLoading.value = true
                    is NetworkResult.Success -> {
                        _isLoading.value = false
                        _error.value = null
                        _prayerTimes.value = result.data
                        val todayData = result.data.find {
                            it.date == todayKey
                        }
                        if (todayData != null) {
                            todayPrayerTime = todayData
                            _nextPrayer.value = getNextPrayerUseCase(todayData)
                            refreshDashboard()
                            startDashboardRefresh()
                        }
                        if (result.data.isNotEmpty()) {
                            val resolvedTimezone = result.data.first().timezone
                            if (currentPreferences.manualCityName.isNotEmpty()) {
                                if (currentPreferences.manualTimezone != resolvedTimezone) {
                                    viewModelScope.launch {
                                        preferencesRepository.updateManualTimezone(resolvedTimezone)
                                    }
                                }
                            } else {
                                viewModelScope.launch {
                                    preferencesRepository.setLastAutoLocation(
                                        name = location.cityName,
                                        districtName = location.districtName,
                                        latitude = location.latitude,
                                        longitude = location.longitude,
                                        districtId = location.districtId,
                                        timezone = resolvedTimezone
                                    )
                                }
                            }
                        }
                        scheduleNotifications(result.data, location)
                        syncToWear(result.data, location)
                    }
                    is NetworkResult.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }

    fun refreshDashboardImmediately() {
        refreshDashboard()
    }

    private fun refreshDashboard() {
        val todayDate = LocalDate.now()
        val todayKey = todayDate.format(DATE_FORMAT)
        val today = _prayerTimes.value.find { it.date == todayKey } ?: todayPrayerTime ?: return
        todayPrayerTime = today

        _nextPrayer.value = getNextPrayerUseCase(today)

        val tomorrowKey = todayDate.plusDays(1).format(DATE_FORMAT)
        val tomorrow = _prayerTimes.value.find { it.date == tomorrowKey }
        _dashboardData.value = getPrayerDashboardUseCase.execute(today, tomorrow)

        syncToWear(_prayerTimes.value, _locationInfo.value)
    }

    private fun startDashboardRefresh() {
        dashboardRefreshJob?.cancel()
        dashboardRefreshJob = viewModelScope.launch {
            while (isActive) {
                delay(30_000L)
                refreshDashboard()
            }
        }
    }

    private fun scheduleNotifications(prayerTimes: List<PrayerTime>, location: LocationInfo) {
        notificationScheduler.cancelAllNotifications()
        if (!currentPreferences.notificationsEnabled) return

        val prefs = currentPreferences
        notificationScheduler.schedulePrayerNotifications(
            prayerTimes = prayerTimes,
            minutesBefore = prefs.notificationMinutesBefore,
            cityName = location.cityName
        )
    }

    private fun syncToWear(prayerTimes: List<PrayerTime>, location: LocationInfo) {
        val today = LocalDate.now()
        val todayData = prayerTimes.find { it.date == today.format(DATE_FORMAT) } ?: return

        viewModelScope.launch {
            val kerahatInfo = dashboardData.value?.kerahatInfo
            val wearKerahat = kerahatInfo?.let { k ->
                WearKerahatInfo(
                    isCurrentlyKerahat = k.isCurrentlyKerahat,
                    prayerName = k.prayerName.name,
                    remainingMinutes = if (k.isCurrentlyKerahat) k.remainingToEndMinutes else k.remainingToStartMinutes
                )
            }

            val wearData = WearPrayerData(
                cityName = location.cityName,
                prayers = todayData.getAllPrayers().map { (name, time) ->
                    WearPrayerItem(
                        name = name.name,
                        time = time
                    )
                },
                nextPrayer = _nextPrayer.value?.let {
                    WearNextPrayer(
                        name = it.prayerName.name,
                        time = it.prayerTime,
                        remainingMinutes = it.remainingMinutes
                    )
                },
                kerahat = wearKerahat
            )
            wearSyncManager.syncPrayerData(wearData)
        }
    }

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}
