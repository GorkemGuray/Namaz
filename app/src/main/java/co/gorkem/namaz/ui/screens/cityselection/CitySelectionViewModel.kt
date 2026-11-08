package co.gorkem.namaz.ui.screens.cityselection

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.R
import co.gorkem.namaz.data.local.datastore.RecentCity
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.domain.model.LocationSearchResult
import co.gorkem.namaz.domain.repository.PrayerTimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

typealias LocationSearchResult = co.gorkem.namaz.domain.model.LocationSearchResult

@HiltViewModel
class CitySelectionViewModel @Inject constructor(
    private val repository: PrayerTimeRepository,
    private val preferencesRepository: UserPreferencesRepository,
    @ApplicationContext private val context: Context? = null
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<LocationSearchResult>>(emptyList())
    val searchResults: StateFlow<List<LocationSearchResult>> = _searchResults.asStateFlow()

    val recentCities = preferencesRepository.preferences.map { it.recentCities }

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.length < 2) {
            _isSearching.value = false
            _searchResults.value = emptyList()
            return
        }

        _isSearching.value = true
        searchJob = viewModelScope.launch {
            delay(400)
            try {
                val prefs = preferencesRepository.preferences.first()
                val activeDataSource = if (prefs.useLocalCalculation) 2 else prefs.dataSource

                if (activeDataSource == 1) {
                    val upperQuery = query.trim().uppercase(Locale.forLanguageTag("tr"))
                    val districtId = repository.findDistrictId(upperQuery)
                    if (districtId != null) {
                        val countryName = runCatching { context?.getString(R.string.country_turkey) }.getOrNull() ?: "Türkiye"
                        _searchResults.value = listOf(
                            LocationSearchResult(
                                displayName = "$upperQuery, $countryName",
                                cityName = upperQuery,
                                districtName = upperQuery,
                                countryName = countryName,
                                latitude = 0.0,
                                longitude = 0.0,
                                districtId = districtId
                            )
                        )
                    } else {
                        _searchResults.value = emptyList()
                    }
                } else {
                    _searchResults.value = repository.searchLocations(query.trim())
                }
            } catch (_: Exception) {
                _searchResults.value = emptyList()
            }
            _isSearching.value = false
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _isSearching.value = false
        _searchResults.value = emptyList()
        searchJob?.cancel()
    }

    suspend fun saveLocation(result: LocationSearchResult) {
        val cleanName = if (result.districtId.isNotEmpty()) {
            result.cityName
        } else {
            if (result.cityName.isNotBlank()) {
                val hasDistrict = result.districtName.isNotBlank() && result.districtName != result.cityName
                if (hasDistrict) {
                    "${result.cityName}, ${result.districtName}"
                } else if (result.countryName.isNotBlank()) {
                    "${result.cityName}, ${result.countryName}"
                } else {
                    result.cityName
                }
            } else {
                result.displayName.substringBefore(",")
            }
        }

        val finalDistrictId = if (result.districtId.isNotEmpty()) {
            result.districtId
        } else {
            repository.findDistrictId(result.cityName, result.districtName) ?: ""
        }

        preferencesRepository.setManualCity(
            name = cleanName,
            districtName = result.districtName,
            latitude = result.latitude,
            longitude = result.longitude,
            districtId = finalDistrictId
        )
        context?.let { co.gorkem.namaz.service.NamazWidgetUpdater.update(it) }
    }

    suspend fun saveRecentCity(city: RecentCity) {
        preferencesRepository.setManualCity(
            name = city.name,
            latitude = city.latitude,
            longitude = city.longitude,
            districtId = city.districtId ?: ""
        )
        context?.let { co.gorkem.namaz.service.NamazWidgetUpdater.update(it) }
    }

    suspend fun useMyLocation() {
        preferencesRepository.clearManualCity()
        context?.let { co.gorkem.namaz.service.NamazWidgetUpdater.update(it) }
    }

    fun clearRecentCities() {
        viewModelScope.launch {
            preferencesRepository.clearRecentCities()
        }
    }
}
