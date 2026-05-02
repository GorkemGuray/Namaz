package co.gorkem.namaz.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.data.local.datastore.UserPreferences
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import co.gorkem.namaz.service.GeocoderHelper
import co.gorkem.namaz.service.LocationTracker
import co.gorkem.namaz.service.PrayerNotificationScheduler
import co.gorkem.namaz.service.WearPrayerData
import co.gorkem.namaz.service.WearPrayerItem
import co.gorkem.namaz.service.WearNextPrayer
import co.gorkem.namaz.service.WearSyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val getNextPrayerUseCase: GetNextPrayerUseCase,
    private val locationTracker: LocationTracker,
    private val geocoderHelper: GeocoderHelper,
    private val notificationScheduler: PrayerNotificationScheduler,
    private val wearSyncManager: WearSyncManager,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTime>> = _prayerTimes.asStateFlow()

    private val _nextPrayer = MutableStateFlow<GetNextPrayerUseCase.NextPrayerInfo?>(null)
    val nextPrayer: StateFlow<GetNextPrayerUseCase.NextPrayerInfo?> = _nextPrayer.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _locationInfo = MutableStateFlow(LocationInfo.DEFAULT)
    val locationInfo: StateFlow<LocationInfo> = _locationInfo.asStateFlow()

    private val _needsLocationPermission = MutableStateFlow(false)
    val needsLocationPermission: StateFlow<Boolean> = _needsLocationPermission.asStateFlow()

    private var currentPreferences = UserPreferences()

    init {
        viewModelScope.launch {
            var lastCityKey: String? = null
            preferencesRepository.preferences.collect { prefs ->
                currentPreferences = prefs
                val cityKey = "${prefs.manualLatitude},${prefs.manualLongitude},${prefs.manualCityName}"
                if (cityKey != lastCityKey) {
                    lastCityKey = cityKey
                    if (prefs.manualCityName.isNotEmpty()) {
                        _needsLocationPermission.value = false
                        loadPrayerTimes(LocationInfo(
                            latitude = prefs.manualLatitude,
                            longitude = prefs.manualLongitude,
                            timezone = "Europe/Istanbul",
                            cityName = prefs.manualCityName
                        ))
                    } else if (locationTracker.hasLocationPermission()) {
                        loadWithCurrentLocation()
                    } else {
                        _needsLocationPermission.value = true
                        loadPrayerTimes(LocationInfo.DEFAULT)
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

    private fun loadWithCurrentLocation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val location = locationTracker.getCurrentLocation()
                if (location != null) {
                    val info = geocoderHelper.getLocationInfo(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                    loadPrayerTimes(info)
                } else {
                    loadPrayerTimes(LocationInfo.DEFAULT)
                }
            } catch (e: Exception) {
                loadPrayerTimes(LocationInfo.DEFAULT)
            }
        }
    }

    fun loadPrayerTimes(location: LocationInfo) {
        _locationInfo.value = location
        val today = LocalDate.now()
        viewModelScope.launch {
            getPrayerTimesUseCase(
                location = location,
                month = today.monthValue,
                year = today.year
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> _isLoading.value = true
                    is NetworkResult.Success -> {
                        _isLoading.value = false
                        _prayerTimes.value = result.data
                        val todayData = result.data.find {
                            it.date == today.format(DATE_FORMAT)
                        }
                        if (todayData != null) {
                            _nextPrayer.value = getNextPrayerUseCase(todayData)
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

    private fun scheduleNotifications(prayerTimes: List<PrayerTime>, location: LocationInfo) {
        if (!currentPreferences.notificationsEnabled) return

        val prefs = currentPreferences
        notificationScheduler.cancelAllNotifications()
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
            val wearData = WearPrayerData(
                cityName = location.cityName,
                prayers = todayData.getAllPrayers().map { (name, time) ->
                    WearPrayerItem(
                        name = name.name,
                        time = time,
                        turkishName = name.turkishName
                    )
                },
                nextPrayer = _nextPrayer.value?.let {
                    WearNextPrayer(
                        name = it.prayerName.name,
                        turkishName = it.prayerName.turkishName,
                        time = it.prayerTime,
                        remainingMinutes = it.remainingMinutes
                    )
                }
            )
            wearSyncManager.syncPrayerData(wearData)
        }
    }

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}