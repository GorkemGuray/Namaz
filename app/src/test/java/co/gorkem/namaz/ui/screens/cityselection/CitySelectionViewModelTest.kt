package co.gorkem.namaz.ui.screens.cityselection

import co.gorkem.namaz.data.local.datastore.RecentCity
import co.gorkem.namaz.data.local.datastore.UserPreferences
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.domain.model.NetworkResult
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.LocationSearchResult
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.repository.PrayerTimeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CitySelectionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakePrayerTimeRepository
    private lateinit var fakePreferencesRepository: FakeUserPreferencesRepository
    private lateinit var viewModel: CitySelectionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakePrayerTimeRepository()
        fakePreferencesRepository = FakeUserPreferencesRepository()
        viewModel = CitySelectionViewModel(
            repository = fakeRepository,
            preferencesRepository = fakePreferencesRepository,
            context = null
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun searchQuery_whenQueryLessThanTwoChars_returnsEmptyResults() = runTest {
        viewModel.onSearchQueryChange("a")
        advanceUntilIdle()

        assertFalse(viewModel.isSearching.value)
        assertTrue(viewModel.searchResults.value.isEmpty())
    }

    @Test
    fun searchQuery_nominatimSearch_returnsResultsAfterDebounce() = runTest {
        val expectedResults = listOf(
            LocationSearchResult(
                displayName = "Berlin, Germany",
                cityName = "Berlin",
                districtName = "",
                countryName = "Germany",
                latitude = 52.52,
                longitude = 13.405,
                districtId = ""
            )
        )
        fakeRepository.searchLocationsResult = expectedResults

        viewModel.onSearchQueryChange("Berlin")
        advanceTimeBy(300)
        assertTrue(viewModel.searchResults.value.isEmpty())

        advanceTimeBy(150)
        assertEquals(expectedResults, viewModel.searchResults.value)
        assertFalse(viewModel.isSearching.value)
    }

    @Test
    fun searchQuery_diyanetSearch_returnsDistrictMatch() = runTest {
        fakePreferencesRepository.updateTestDataSource(1)
        fakeRepository.findDistrictIdResult = "9548"

        viewModel.onSearchQueryChange("silivri")
        advanceTimeBy(450)

        assertEquals("SİLİVRİ", fakeRepository.lastFindDistrictIdQuery)
        assertEquals(1, viewModel.searchResults.value.size)
        val result = viewModel.searchResults.value.first()
        assertEquals("SİLİVRİ", result.cityName)
        assertEquals("9548", result.districtId)
        assertFalse(viewModel.isSearching.value)
    }

    @Test
    fun searchQuery_whenRepositoryThrows_returnsEmptyErrorState() = runTest {
        fakeRepository.shouldThrow = true

        viewModel.onSearchQueryChange("London")
        advanceTimeBy(450)

        assertTrue(viewModel.searchResults.value.isEmpty())
        assertFalse(viewModel.isSearching.value)
    }

    @Test
    fun clearSearch_resetsQueryAndResults() = runTest {
        fakeRepository.searchLocationsResult = listOf(
            LocationSearchResult(
                displayName = "Ankara",
                cityName = "Ankara",
                districtName = "",
                countryName = "Türkiye",
                latitude = 39.93,
                longitude = 32.85
            )
        )
        viewModel.onSearchQueryChange("Ankara")
        advanceTimeBy(450)
        assertEquals(1, viewModel.searchResults.value.size)

        viewModel.clearSearch()

        assertEquals("", viewModel.searchQuery.value)
        assertTrue(viewModel.searchResults.value.isEmpty())
        assertFalse(viewModel.isSearching.value)
    }

    @Test
    fun saveLocation_savesToPreferences() = runTest {
        val result = LocationSearchResult(
            displayName = "İstanbul, Fatih",
            cityName = "İstanbul",
            districtName = "Fatih",
            countryName = "Türkiye",
            latitude = 41.01,
            longitude = 28.95,
            districtId = "9549"
        )

        viewModel.saveLocation(result)

        assertEquals("İstanbul", fakePreferencesRepository.savedManualName)
        assertEquals("Fatih", fakePreferencesRepository.savedDistrictName)
        assertEquals("9549", fakePreferencesRepository.savedDistrictId)
    }

    private class FakePrayerTimeRepository : PrayerTimeRepository {
        var searchLocationsResult: List<LocationSearchResult> = emptyList()
        var findDistrictIdResult: String? = null
        var lastFindDistrictIdQuery: String? = null
        var shouldThrow: Boolean = false

        override fun getPrayerTimes(
            location: LocationInfo,
            month: Int,
            year: Int,
            method: Int,
            school: Int
        ): Flow<NetworkResult<List<PrayerTime>>> = emptyFlow()

        override suspend fun findDistrictId(cityName: String, districtName: String): String? {
            if (shouldThrow) throw RuntimeException("Network error")
            lastFindDistrictIdQuery = cityName
            return findDistrictIdResult
        }

        override suspend fun searchLocations(query: String): List<LocationSearchResult> {
            if (shouldThrow) throw RuntimeException("Network error")
            return searchLocationsResult
        }
    }

    private class FakeUserPreferencesRepository : UserPreferencesRepository {
        private val _prefs = MutableStateFlow(UserPreferences())
        override val preferences: Flow<UserPreferences> = _prefs

        var savedManualName: String? = null
        var savedDistrictName: String? = null
        var savedDistrictId: String? = null

        fun updateTestDataSource(source: Int) {
            _prefs.value = _prefs.value.copy(dataSource = source)
        }

        override suspend fun setNotificationsEnabled(enabled: Boolean) {}
        override suspend fun setNotificationPermissionRequested(requested: Boolean) {}
        override suspend fun setNotificationMinutesBefore(minutes: Int) {}
        override suspend fun setCalculationMethod(method: Int) {}
        override suspend fun setSchool(school: Int) {}
        override suspend fun setUseLocalCalculation(enabled: Boolean) {}
        override suspend fun setDataSource(source: Int) {}
        override suspend fun setLanguage(language: String) {}
        override suspend fun setAppTheme(theme: Int) {}

        override suspend fun setManualCity(
            name: String,
            districtName: String,
            latitude: Double,
            longitude: Double,
            districtId: String
        ) {
            savedManualName = name
            savedDistrictName = districtName
            savedDistrictId = districtId
        }

        override suspend fun clearManualCity() {
            savedManualName = null
        }

        override suspend fun clearRecentCities() {}
        override suspend fun setLastAutoLocation(
            name: String,
            districtName: String,
            latitude: Double,
            longitude: Double,
            districtId: String,
            timezone: String
        ) {}
        override suspend fun updateManualTimezone(timezone: String) {}
        override suspend fun setLastUpdateCheck() {}
    }
}
