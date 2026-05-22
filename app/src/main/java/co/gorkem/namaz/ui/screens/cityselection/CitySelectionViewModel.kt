package co.gorkem.namaz.ui.screens.cityselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.data.local.datastore.RecentCity
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.remote.api.ImsakiyemApiService
import co.gorkem.namaz.data.remote.api.NominatimApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

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
    @Named("nominatim") private val nominatimApiService: NominatimApiService,
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
                val prefs = preferencesRepository.preferences.first()
                val activeDataSource = if (prefs.useLocalCalculation) 2 else prefs.dataSource

                if (activeDataSource == 1) {
                    val upperQuery = query.trim().uppercase(java.util.Locale.forLanguageTag("tr"))
                    val response = apiService.searchDistricts(query = upperQuery)
                    if (response.success) {
                        _searchResults.value = response.districts.map { district ->
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
                } else {
                    val response = nominatimApiService.search(query = query.trim())
                    _searchResults.value = response.map { place ->
                        val lat = place.lat.toDoubleOrNull() ?: 0.0
                        val lon = place.lon.toDoubleOrNull() ?: 0.0
                        val cityName = place.address?.cityName ?: ""
                        val districtName = place.address?.districtName ?: ""
                        val countryName = place.address?.country ?: ""
                        val finalCityName = if (cityName.isNotEmpty()) cityName else place.displayName.substringBefore(",")

                        LocationSearchResult(
                            displayName = place.displayName,
                            cityName = finalCityName,
                            districtName = districtName,
                            countryName = countryName,
                            latitude = lat,
                            longitude = lon,
                            districtId = ""
                        )
                    }
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

        preferencesRepository.setManualCity(
            name = cleanName,
            districtName = result.districtName,
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
