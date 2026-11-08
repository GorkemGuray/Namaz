package co.gorkem.namaz.wear.ui

import co.gorkem.namaz.wear.data.WearPrayerRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class StandaloneLocationResolutionTest {

    @Test
    fun gpsAvailable_usesGps() {
        val result = resolveStandaloneLocation(
            gpsLatitude = 48.8566,
            gpsLongitude = 2.3522,
            cachedLatitude = 41.0082,
            cachedLongitude = 28.9784,
            cachedCityName = "Istanbul"
        )

        assertEquals(48.8566, result.latitude, 0.0)
        assertEquals(2.3522, result.longitude, 0.0)
        assertEquals(LocationSource.GPS, result.source)
    }

    @Test
    fun gpsAvailable_takesPriorityOverCache() {
        val result = resolveStandaloneLocation(
            gpsLatitude = 39.9253,
            gpsLongitude = 32.8354,
            cachedLatitude = 41.0082,
            cachedLongitude = 28.9784,
            cachedCityName = "Istanbul"
        )

        assertEquals(39.9253, result.latitude, 0.0)
        assertEquals(32.8354, result.longitude, 0.0)
        assertEquals(LocationSource.GPS, result.source)
    }

    @Test
    fun gpsAvailable_usesKonumunuzLabel() {
        val result = resolveStandaloneLocation(
            gpsLatitude = 48.8566,
            gpsLongitude = 2.3522,
            cachedLatitude = null,
            cachedLongitude = null,
            cachedCityName = null
        )

        assertEquals("Konumunuz", result.cityName)
    }

    @Test
    fun gpsNull_cachePresent_usesCache() {
        val result = resolveStandaloneLocation(
            gpsLatitude = null,
            gpsLongitude = null,
            cachedLatitude = 40.9842,
            cachedLongitude = 29.0238,
            cachedCityName = "Malatya"
        )

        assertEquals(40.9842, result.latitude, 0.0)
        assertEquals(29.0238, result.longitude, 0.0)
        assertEquals("Malatya", result.cityName)
        assertEquals(LocationSource.CACHE, result.source)
    }

    @Test
    fun gpsNull_cacheBlankCity_fallsBackToKonumunuz() {
        val result = resolveStandaloneLocation(
            gpsLatitude = null,
            gpsLongitude = null,
            cachedLatitude = 40.9842,
            cachedLongitude = 29.0238,
            cachedCityName = "   "
        )

        assertEquals("Konumunuz", result.cityName)
        assertEquals(LocationSource.CACHE, result.source)
    }

    @Test
    fun gpsNull_noCache_usesDefault() {
        val result = resolveStandaloneLocation(
            gpsLatitude = null,
            gpsLongitude = null,
            cachedLatitude = null,
            cachedLongitude = null,
            cachedCityName = null
        )

        assertEquals(WearPrayerRepository.DEFAULT_LATITUDE, result.latitude, 0.0)
        assertEquals(WearPrayerRepository.DEFAULT_LONGITUDE, result.longitude, 0.0)
        assertEquals(LocationSource.DEFAULT, result.source)
    }

    @Test
    fun gpsNull_cacheWithoutCoordinates_usesDefault() {
        val result = resolveStandaloneLocation(
            gpsLatitude = null,
            gpsLongitude = null,
            cachedLatitude = 0.0,
            cachedLongitude = 0.0,
            cachedCityName = "Istanbul"
        )

        assertEquals(WearPrayerRepository.DEFAULT_LATITUDE, result.latitude, 0.0)
        assertEquals(WearPrayerRepository.DEFAULT_LONGITUDE, result.longitude, 0.0)
        assertEquals(LocationSource.DEFAULT, result.source)
    }

    @Test
    fun gpsNull_cacheHalfCoordinates_usesDefault() {
        val result = resolveStandaloneLocation(
            gpsLatitude = null,
            gpsLongitude = null,
            cachedLatitude = 40.9842,
            cachedLongitude = null,
            cachedCityName = "Malatya"
        )

        assertEquals(LocationSource.DEFAULT, result.source)
    }

    @Test
    fun gpsPartialCoordinates_usesDefault() {
        val result = resolveStandaloneLocation(
            gpsLatitude = 48.8566,
            gpsLongitude = null,
            cachedLatitude = 40.9842,
            cachedLongitude = 29.0238,
            cachedCityName = "Malatya"
        )

        assertEquals(LocationSource.CACHE, result.source)
        assertEquals(40.9842, result.latitude, 0.0)
    }

    @Test
    fun gpsWithDistrictId_usesGpsDistrictId() {
        val result = resolveStandaloneLocation(
            gpsLatitude = 39.9253,
            gpsLongitude = 32.8354,
            cachedLatitude = 41.0082,
            cachedLongitude = 28.9784,
            cachedCityName = "Istanbul",
            cachedDistrictId = "9548",
            gpsDistrictId = "9206"
        )

        assertEquals("9206", result.districtId)
    }

    @Test
    fun gpsWithoutDistrictId_cacheWithDistrictId_usesCachedDistrictId() {
        val result = resolveStandaloneLocation(
            gpsLatitude = 39.9253,
            gpsLongitude = 32.8354,
            cachedLatitude = 41.0082,
            cachedLongitude = 28.9784,
            cachedCityName = "Istanbul",
            cachedDistrictId = "9548",
            gpsDistrictId = null
        )

        assertEquals("9548", result.districtId)
    }

    @Test
    fun gpsWithoutDistrictId_cacheWithoutDistrictId_usesDefaultDistrictId() {
        val result = resolveStandaloneLocation(
            gpsLatitude = 39.9253,
            gpsLongitude = 32.8354,
            cachedLatitude = null,
            cachedLongitude = null,
            cachedCityName = null,
            cachedDistrictId = null,
            gpsDistrictId = null
        )

        assertEquals(WearPrayerRepository.DEFAULT_DISTRICT_ID, result.districtId)
    }

    @Test
    fun noGps_cacheWithDistrictId_usesCachedDistrictId() {
        val result = resolveStandaloneLocation(
            gpsLatitude = null,
            gpsLongitude = null,
            cachedLatitude = 40.9842,
            cachedLongitude = 29.0238,
            cachedCityName = "Kadikoy",
            cachedDistrictId = "9541"
        )

        assertEquals("9541", result.districtId)
    }

    @Test
    fun noGps_noCache_usesDefaultDistrictId() {
        val result = resolveStandaloneLocation(
            gpsLatitude = null,
            gpsLongitude = null,
            cachedLatitude = null,
            cachedLongitude = null,
            cachedCityName = null,
            cachedDistrictId = null
        )

        assertEquals(WearPrayerRepository.DEFAULT_DISTRICT_ID, result.districtId)
    }
}
