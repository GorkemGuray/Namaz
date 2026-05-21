package co.gorkem.namaz.ui.screens.cityselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.data.local.datastore.RecentCity
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.remote.api.ImsakiyemApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LocationSearchResult(
    val displayName: String,
    val cityName: String,
    val districtName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val districtId: String = ""
)

@HiltViewModel
class CitySelectionViewModel @Inject constructor(
    private val apiService: ImsakiyemApiService,
    private val preferencesRepository: UserPreferencesRepository
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
                val response = apiService.searchDistricts(query = query.trim())
                if (response.success) {
                    _searchResults.value = response.data.map { district ->
                        LocationSearchResult(
                            displayName = "${district.name}, Türkiye",
                            cityName = district.name,
                            districtName = district.name,
                            countryName = "Türkiye",
                            latitude = 0.0,
                            longitude = 0.0,
                            districtId = district.id
                        )
                    }
                } else {
                    _searchResults.value = emptyList()
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
        preferencesRepository.setManualCity(
            name = result.displayName,
            latitude = result.latitude,
            longitude = result.longitude,
            districtId = result.districtId
        )
    }

    suspend fun saveRecentCity(city: RecentCity) {
        preferencesRepository.setManualCity(
            name = city.name,
            latitude = city.latitude,
            longitude = city.longitude,
            districtId = city.districtId ?: ""
        )
    }

    suspend fun useMyLocation() {
        preferencesRepository.clearManualCity()
    }

    fun clearRecentCities() {
        viewModelScope.launch {
            preferencesRepository.clearRecentCities()
        }
    }
}
