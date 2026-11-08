package co.gorkem.namaz.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.data.local.datastore.UserPreferences
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.local.datastore.getLocationInfo
import co.gorkem.namaz.domain.model.NetworkResult
import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerDashboardUseCase
import co.gorkem.namaz.domain.usecase.PrayerDashboardData
import co.gorkem.namaz.service.GeocoderHelper
import co.gorkem.namaz.service.LocationTracker
import co.gorkem.namaz.service.PrayerNotificationScheduler
import co.gorkem.namaz.service.LocationSyncWorker
import co.gorkem.namaz.service.NamazWidgetUpdater
import co.gorkem.namaz.service.WearKerahatInfo
import co.gorkem.namaz.service.WearNextPrayer
import co.gorkem.namaz.service.WearPrayerData
import co.gorkem.namaz.service.WearPrayerItem
import co.gorkem.namaz.service.WearSyncManager
import co.gorkem.namaz.util.DateTimeUtils
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import co.gorkem.namaz.util.NotificationPermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate
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
    private val prayerTimeRepo: PrayerTimeRepositoryImpl,
    @ApplicationContext private val context: Context
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

    private val _needsBackgroundLocationPermission = MutableStateFlow(false)
    val needsBackgroundLocationPermission: StateFlow<Boolean> = _needsBackgroundLocationPermission.asStateFlow()

    private var currentPreferences = UserPreferences()
    private var todayPrayerTime: PrayerTime? = null
    private var dashboardRefreshJob: Job? = null
    private var prayerTimesJob: Job? = null

    init {
        viewModelScope.launch {
            var lastCityKey: String? = null
            var lastMethod: Int = -1
            var lastSchool: Int = -1
            var lastTheme: Int = -1
            var lastNotificationsEnabled: Boolean? = null
            var lastNotificationMinutesBefore: Int? = null
            preferencesRepository.preferences.collect { prefs ->
                // Silently align toggle with system permission if previously requested and enabled, but now system permission is denied
                if (prefs.notificationsEnabled && prefs.notificationPermissionRequested &&
                    !NotificationPermissionHelper.checkNotificationPermission(context)) {
                    preferencesRepository.setNotificationsEnabled(false)
                    return@collect
                }

                val notificationsEnabledChanged = lastNotificationsEnabled != null && prefs.notificationsEnabled != lastNotificationsEnabled
                val notificationMinutesBeforeChanged = lastNotificationMinutesBefore != null && prefs.notificationMinutesBefore != lastNotificationMinutesBefore
                val themeChanged = lastTheme != -1 && prefs.appTheme != lastTheme

                currentPreferences = prefs
                lastNotificationsEnabled = prefs.notificationsEnabled
                lastNotificationMinutesBefore = prefs.notificationMinutesBefore
                lastTheme = prefs.appTheme

                val cityKey = if (prefs.manualCityName.isNotEmpty()) {
                    "manual:${prefs.manualDistrictId},${prefs.manualCityName}"
                } else {
                    "auto:${prefs.lastAutoDistrictId},${prefs.lastAutoCityName}"
                }
                
                val methodChanged = prefs.calculationMethod != lastMethod
                val schoolChanged = prefs.school != lastSchool
                
                if (themeChanged || cityKey != lastCityKey || methodChanged || schoolChanged) {
                    NamazWidgetUpdater.update(context)
                }

                if (cityKey != lastCityKey || methodChanged || schoolChanged) {
                    lastCityKey = cityKey
                    lastMethod = prefs.calculationMethod
                    lastSchool = prefs.school
                    if (prefs.manualCityName.isNotEmpty()) {
                        _needsLocationPermission.value = false
                        _needsBackgroundLocationPermission.value = false
                        loadPrayerTimes(prefs.getLocationInfo())
                    } else if (locationTracker.hasLocationPermission()) {
                        _needsLocationPermission.value = false
                        _needsBackgroundLocationPermission.value = !locationTracker.hasBackgroundLocationPermission()
                        val info = prefs.getLocationInfo()
                        if (info.cityName.isEmpty()) {
                            loadWithCurrentLocation()
                        } else {
                            loadPrayerTimes(info)
                            LocationSyncWorker.executeNow(context)
                        }
                    } else {
                        _needsLocationPermission.value = true
                        _needsBackgroundLocationPermission.value = false
                        loadPrayerTimes(LocationInfo.DEFAULT)
                    }
                } else if (themeChanged) {
                    if (_prayerTimes.value.isNotEmpty()) {
                        syncToWear(_prayerTimes.value, _locationInfo.value)
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
        _needsBackgroundLocationPermission.value = !locationTracker.hasBackgroundLocationPermission()
        locationTracker.registerPassiveLocationListener()
        loadWithCurrentLocation()
    }

    fun onLocationPermissionDenied() {
        _needsLocationPermission.value = false
        _needsBackgroundLocationPermission.value = false
        loadPrayerTimes(LocationInfo.DEFAULT)
    }

    fun useMyLocation() {
        if (locationTracker.hasLocationPermission()) {
            _needsBackgroundLocationPermission.value = !locationTracker.hasBackgroundLocationPermission()
            viewModelScope.launch {
                preferencesRepository.clearManualCity()
            }
        } else {
            _needsLocationPermission.value = true
        }
    }

    fun dismissBackgroundLocationPermission() {
        _needsBackgroundLocationPermission.value = false
    }

    fun onBackgroundLocationPermissionResult(granted: Boolean) {
        _needsBackgroundLocationPermission.value = !granted
        if (granted) {
            locationTracker.registerPassiveLocationListener()
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
        val todayKey = today.format(DateTimeUtils.DATE_FORMATTER)

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
                            NamazWidgetUpdater.update(context)
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
        val todayKey = todayDate.format(DateTimeUtils.DATE_FORMATTER)
        val today = _prayerTimes.value.find { it.date == todayKey } ?: todayPrayerTime ?: return
        todayPrayerTime = today

        _nextPrayer.value = getNextPrayerUseCase(today)

        val tomorrowKey = todayDate.plusDays(1).format(DateTimeUtils.DATE_FORMATTER)
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
        val todayData = prayerTimes.find { it.date == today.format(DateTimeUtils.DATE_FORMATTER) } ?: return

        viewModelScope.launch {
            val kerahatInfo = dashboardData.value?.kerahatInfo
            val localizedContext = co.gorkem.namaz.LocaleManager.applyLocale(context)
            
            val wearKerahat = kerahatInfo?.let { k ->
                WearKerahatInfo(
                    isCurrentlyKerahat = k.isCurrentlyKerahat,
                    prayerName = localizedContext.getString(k.prayerName.nameRes),
                    remainingMinutes = if (k.isCurrentlyKerahat) k.remainingToEndMinutes else k.remainingToStartMinutes
                )
            }

            val wearData = WearPrayerData(
                cityName = location.cityName,
                prayers = todayData.getAllPrayers().map { (name, time) ->
                    WearPrayerItem(
                        name = localizedContext.getString(name.nameRes),
                        time = time
                    )
                },
                nextPrayer = _nextPrayer.value?.let { nextPrayer ->
                    WearNextPrayer(
                        name = localizedContext.getString(nextPrayer.prayerName.nameRes),
                        time = nextPrayer.prayerTime,
                        remainingMinutes = nextPrayer.remainingMinutes,
                        targetTimeMillis = nextPrayer.targetPrayerTimeInMillis
                    )
                },
                kerahat = wearKerahat,
                themeId = currentPreferences?.appTheme ?: 0,
                latitude = location.latitude,
                longitude = location.longitude
            )
            wearSyncManager.syncPrayerData(wearData)
        }
    }
}
