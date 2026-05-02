package co.gorkem.namaz.ui.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import co.gorkem.namaz.service.GeocoderHelper
import co.gorkem.namaz.service.LocationTracker
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
    private val savedStateHandle: SavedStateHandle
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

    init {
        if (locationTracker.hasLocationPermission()) {
            loadWithCurrentLocation()
        } else {
            _needsLocationPermission.value = true
            loadPrayerTimes(LocationInfo.DEFAULT)
        }

        savedStateHandle.get<LocationInfo>("selected_city")?.let { city ->
            _locationInfo.value = city
            loadPrayerTimes(city)
            savedStateHandle.remove<LocationInfo>("selected_city")
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
                    _locationInfo.value = info
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
                    }
                    is NetworkResult.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}
