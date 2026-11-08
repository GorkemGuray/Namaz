package co.gorkem.namaz.wear.data

import co.gorkem.namaz.core.AladhanApiService
import co.gorkem.namaz.core.AladhanDate
import co.gorkem.namaz.core.AladhanDayData
import co.gorkem.namaz.core.AladhanGregorianDate
import co.gorkem.namaz.core.AladhanHijriDate
import co.gorkem.namaz.core.AladhanHijriMonth
import co.gorkem.namaz.core.AladhanMeta
import co.gorkem.namaz.core.AladhanMonth
import co.gorkem.namaz.core.AladhanResponse
import co.gorkem.namaz.core.AladhanTimings
import co.gorkem.namaz.wear.data.remote.api.ImsakiyemApiService
import co.gorkem.namaz.wear.data.remote.dto.DayPrayerDto
import co.gorkem.namaz.wear.data.remote.dto.DistrictDto
import co.gorkem.namaz.wear.data.remote.dto.DistrictSearchResponse
import co.gorkem.namaz.wear.data.remote.dto.PrayerTimesDto
import co.gorkem.namaz.wear.data.remote.dto.PrayerTimesResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WearPrayerRepositoryTest {

    private class FakeAladhanApiService : AladhanApiService {
        var called = false
        var shouldThrow = false
        var errorCode = 200

        override suspend fun getMonthlyPrayerTimes(
            year: Int,
            month: Int,
            latitude: Double,
            longitude: Double,
            method: Int,
            school: Int
        ): AladhanResponse {
            called = true
            if (shouldThrow) {
                throw RuntimeException("Aladhan network failure")
            }
            if (errorCode != 200) {
                return AladhanResponse(code = errorCode, status = "ERROR", data = emptyList())
            }

            val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            val dayData = AladhanDayData(
                timings = AladhanTimings(
                    fajr = "05:30 (EST)",
                    sunrise = "07:00 (EST)",
                    dhuhr = "12:45 (EST)",
                    asr = "15:30 (EST)",
                    maghrib = "18:15 (EST)",
                    isha = "19:45 (EST)"
                ),
                date = AladhanDate(
                    gregorian = AladhanGregorianDate(
                        date = today,
                        day = "01",
                        month = AladhanMonth(1, "January"),
                        year = "2026"
                    ),
                    hijri = AladhanHijriDate(
                        day = "01",
                        month = AladhanHijriMonth(9, "Ramadan", "رمضان"),
                        year = "1447"
                    )
                ),
                meta = AladhanMeta(timezone = "Europe/Istanbul")
            )
            return AladhanResponse(code = 200, status = "OK", data = listOf(dayData))
        }
    }

    private class FakeImsakiyemApiService : ImsakiyemApiService {
        var called = false
        var lastDistrictId: String? = null
        var shouldThrow = false
        var success = true
        var searchResults = listOf(DistrictDto(id = "9548", name = "Silivri"))

        override suspend fun searchDistricts(query: String, countryId: Int): DistrictSearchResponse {
            if (shouldThrow) {
                throw RuntimeException("Search failed")
            }
            return DistrictSearchResponse(
                success = true,
                code = 200,
                message = "Success",
                data = searchResults
            )
        }

        override suspend fun getMonthlyPrayerTimes(districtId: String): PrayerTimesResponse {
            called = true
            lastDistrictId = districtId
            if (shouldThrow) {
                throw RuntimeException("Imsakiyem network failure")
            }
            if (!success) {
                return PrayerTimesResponse(success = false, code = 500, message = "Server error", data = emptyList())
            }

            val todayIso = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val dayData = DayPrayerDto(
                date = "${todayIso}T00:00:00",
                times = PrayerTimesDto(
                    imsak = "05:40",
                    gunes = "07:05",
                    ogle = "12:50",
                    ikindi = "15:35",
                    aksam = "18:20",
                    yatsi = "19:50"
                )
            )
            return PrayerTimesResponse(
                success = true,
                code = 200,
                message = "Success",
                data = listOf(dayData)
            )
        }
    }

    @Test
    fun dataSource0_callsAladhanApi_andEmitsMappedPrayers() = runBlocking {
        val aladhanApi = FakeAladhanApiService()
        val imsakiyemApi = FakeImsakiyemApiService()
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val result = repository.loadStandalone(dataSource = 0).first()

        assertTrue(aladhanApi.called)
        assertFalse(imsakiyemApi.called)
        assertTrue(result.isSuccess)
        val prayers = result.getOrThrow()
        assertEquals(6, prayers.size)
        assertEquals("05:30", prayers.find { it.name == "IMSAK" }?.time)
        assertEquals("18:15", prayers.find { it.name == "AKSAM" }?.time)
    }

    @Test
    fun dataSource1_callsImsakiyemApi_andEmitsMappedPrayers() = runBlocking {
        val aladhanApi = FakeAladhanApiService()
        val imsakiyemApi = FakeImsakiyemApiService()
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val result = repository.loadStandalone(districtId = "9541", dataSource = 1).first()

        assertTrue(imsakiyemApi.called)
        assertEquals("9541", imsakiyemApi.lastDistrictId)
        assertFalse(aladhanApi.called)
        assertTrue(result.isSuccess)
        val prayers = result.getOrThrow()
        assertEquals(6, prayers.size)
        assertEquals("05:40", prayers.find { it.name == "IMSAK" }?.time)
        assertEquals("18:20", prayers.find { it.name == "AKSAM" }?.time)
    }

    @Test
    fun dataSource1_withEmptyDistrictId_usesDefaultDistrictId() = runBlocking {
        val aladhanApi = FakeAladhanApiService()
        val imsakiyemApi = FakeImsakiyemApiService()
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val result = repository.loadStandalone(districtId = "", dataSource = 1).first()

        assertTrue(imsakiyemApi.called)
        assertEquals(WearPrayerRepository.DEFAULT_DISTRICT_ID, imsakiyemApi.lastDistrictId)
        assertTrue(result.isSuccess)
    }

    @Test
    fun dataSource1_apiError_emitsFailure() = runBlocking {
        val aladhanApi = FakeAladhanApiService()
        val imsakiyemApi = FakeImsakiyemApiService().apply { success = false }
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val result = repository.loadStandalone(dataSource = 1).first()

        assertTrue(result.isFailure)
    }

    @Test
    fun dataSource1_networkException_emitsFailure() = runBlocking {
        val aladhanApi = FakeAladhanApiService()
        val imsakiyemApi = FakeImsakiyemApiService().apply { shouldThrow = true }
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val result = repository.loadStandalone(dataSource = 1).first()

        assertTrue(result.isFailure)
    }

    @Test
    fun dataSource0_apiError_emitsFailure() = runBlocking {
        val aladhanApi = FakeAladhanApiService().apply { errorCode = 500 }
        val imsakiyemApi = FakeImsakiyemApiService()
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val result = repository.loadStandalone(dataSource = 0).first()

        assertTrue(result.isFailure)
    }

    @Test
    fun getPrayerTimesFromApi_defaultsToDataSource0() = runBlocking {
        val aladhanApi = FakeAladhanApiService()
        val imsakiyemApi = FakeImsakiyemApiService()
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val result = repository.getPrayerTimesFromApi().first()

        assertTrue(aladhanApi.called)
        assertFalse(imsakiyemApi.called)
        assertTrue(result.isSuccess)
    }

    @Test
    fun findDistrictId_returnsIdWhenAvailable() = runBlocking {
        val aladhanApi = FakeAladhanApiService()
        val imsakiyemApi = FakeImsakiyemApiService()
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val id = repository.findDistrictId("Silivri")

        assertEquals("9548", id)
    }

    @Test
    fun findDistrictId_returnsNullOnException() = runBlocking {
        val aladhanApi = FakeAladhanApiService()
        val imsakiyemApi = FakeImsakiyemApiService().apply { shouldThrow = true }
        val repository = WearPrayerRepository(aladhanApi, imsakiyemApi)

        val id = repository.findDistrictId("Unknown")

        assertNull(id)
    }
}
