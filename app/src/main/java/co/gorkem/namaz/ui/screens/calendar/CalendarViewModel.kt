package co.gorkem.namaz.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.data.local.datastore.UserPreferences
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getPrayerTimesUseCase: GetPrayerTimesUseCase,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _prayerTimes = MutableStateFlow<List<PrayerTime>>(emptyList())
    val prayerTimes: StateFlow<List<PrayerTime>> = _prayerTimes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _locationInfo = MutableStateFlow(LocationInfo.DEFAULT)
    val locationInfo: StateFlow<LocationInfo> = _locationInfo.asStateFlow()

    private val today = LocalDate.now()
    private val _selectedYear = MutableStateFlow(today.year)
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(today.monthValue)
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private var currentPreferences = UserPreferences()
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collectLatest { prefs ->
                currentPreferences = prefs
                loadTimes()
            }
        }
    }

    fun nextMonth() {
        val currentMonth = _selectedMonth.value
        val currentYear = _selectedYear.value
        if (currentMonth == 12) {
            _selectedMonth.value = 1
            _selectedYear.value = currentYear + 1
        } else {
            _selectedMonth.value = currentMonth + 1
        }
        loadTimes()
    }

    fun prevMonth() {
        val currentMonth = _selectedMonth.value
        val currentYear = _selectedYear.value
        if (currentMonth == 1) {
            _selectedMonth.value = 12
            _selectedYear.value = currentYear - 1
        } else {
            _selectedMonth.value = currentMonth - 1
        }
        loadTimes()
    }

    private fun loadTimes() {
        loadJob?.cancel()
        
        val resolvedLocation = if (currentPreferences.manualCityName.isNotEmpty()) {
            LocationInfo(
                latitude = currentPreferences.manualLatitude,
                longitude = currentPreferences.manualLongitude,
                timezone = currentPreferences.manualTimezone.ifEmpty { "Europe/Istanbul" },
                cityName = currentPreferences.manualCityName,
                districtName = currentPreferences.manualDistrictName,
                districtId = currentPreferences.manualDistrictId
            )
        } else if (currentPreferences.lastAutoCityName.isNotEmpty()) {
            LocationInfo(
                latitude = currentPreferences.lastAutoLatitude,
                longitude = currentPreferences.lastAutoLongitude,
                timezone = currentPreferences.lastAutoTimezone,
                cityName = currentPreferences.lastAutoCityName,
                districtName = currentPreferences.lastAutoDistrictName,
                districtId = currentPreferences.lastAutoDistrictId
            )
        } else {
            LocationInfo.DEFAULT
        }
        
        _locationInfo.value = resolvedLocation
        
        loadJob = viewModelScope.launch {
            getPrayerTimesUseCase(
                location = resolvedLocation,
                month = _selectedMonth.value,
                year = _selectedYear.value,
                method = currentPreferences.calculationMethod,
                school = currentPreferences.school
            ).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _isLoading.value = true
                    }
                    is NetworkResult.Success -> {
                        _isLoading.value = false
                        _error.value = null
                        _prayerTimes.value = result.data
                    }
                    is NetworkResult.Error -> {
                        _isLoading.value = false
                        _error.value = result.message
                    }
                }
            }
        }
    }
}
