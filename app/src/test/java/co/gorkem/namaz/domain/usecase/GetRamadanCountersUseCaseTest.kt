package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class GetRamadanCountersUseCaseTest {

    private lateinit var useCase: GetRamadanCountersUseCase
    private val testDate = LocalDate.of(2026, 3, 15)

    private fun createPrayerTime(
        hijriMonth: Int,
        imsak: String = "05:15",
        aksam: String = "19:20",
        date: String = "2026-03-15"
    ): PrayerTime {
        return PrayerTime(
            date = date,
            gregorianDate = "15.03.2026",
            hijriDay = "25",
            hijriMonthNumber = hijriMonth,
            hijriYear = "1447",
            imsak = imsak,
            gunes = "06:40",
            ogle = "13:10",
            ikindi = "16:30",
            aksam = aksam,
            yatsi = "20:40",
            timezone = "Europe/Istanbul"
        )
    }

    @Before
    fun setUp() {
        useCase = GetRamadanCountersUseCase()
    }

    @Test
    fun nonRamadanMonth_returnsNull() {
        val nonRamadanTimes = createPrayerTime(hijriMonth = 8)
        val result = useCase(
            todayPrayerTimes = nonRamadanTimes,
            now = LocalTime.of(12, 0),
            today = testDate
        )
        assertNull(result)
    }

    @Test
    fun ramadanMonth_returnsRamadanCounters() {
        val ramadanTimes = createPrayerTime(hijriMonth = 9)
        val result = useCase(
            todayPrayerTimes = ramadanTimes,
            now = LocalTime.of(12, 0),
            today = testDate
        )
        assertNotNull(result)
        assertTrue(result!!.isRamadan)
    }

    @Test
    fun beforeSahur_bothSahurAndIftarTargetToday() {
        val ramadanTimes = createPrayerTime(hijriMonth = 9, imsak = "05:15", aksam = "19:20")
        val now = LocalTime.of(4, 15)

        val result = useCase(
            todayPrayerTimes = ramadanTimes,
            now = now,
            today = testDate
        )

        assertNotNull(result)
        assertEquals("05:15", result!!.sahur.prayerTime)
        assertEquals(60L, result.sahur.remainingMinutes)

        assertEquals("19:20", result.iftar.prayerTime)
        assertEquals(15 * 60 + 5L, result.iftar.remainingMinutes)
    }

    @Test
    fun fastingHours_sahurTargetsTomorrow_iftarTargetsToday() {
        val ramadanTimes = createPrayerTime(hijriMonth = 9, imsak = "05:15", aksam = "19:20")
        val tomorrowTimes = createPrayerTime(hijriMonth = 9, imsak = "05:13", aksam = "19:21", date = "2026-03-16")
        val now = LocalTime.of(14, 0)

        val result = useCase(
            todayPrayerTimes = ramadanTimes,
            now = now,
            tomorrowPrayerTimes = tomorrowTimes,
            today = testDate
        )

        assertNotNull(result)
        assertEquals("05:13", result!!.sahur.prayerTime)
        val expectedSahurMinutes = (10 * 60) + (5 * 60) + 13L
        assertEquals(expectedSahurMinutes, result.sahur.remainingMinutes)

        assertEquals("19:20", result.iftar.prayerTime)
        assertEquals(5 * 60 + 20L, result.iftar.remainingMinutes)
    }

    @Test
    fun afterIftar_bothSahurAndIftarTargetTomorrow() {
        val ramadanTimes = createPrayerTime(hijriMonth = 9, imsak = "05:15", aksam = "19:20")
        val tomorrowTimes = createPrayerTime(hijriMonth = 9, imsak = "05:13", aksam = "19:21", date = "2026-03-16")
        val now = LocalTime.of(21, 0)

        val result = useCase(
            todayPrayerTimes = ramadanTimes,
            now = now,
            tomorrowPrayerTimes = tomorrowTimes,
            today = testDate
        )

        assertNotNull(result)
        assertEquals("05:13", result!!.sahur.prayerTime)
        val expectedSahurMinutes = (3 * 60) + (5 * 60) + 13L
        assertEquals(expectedSahurMinutes, result.sahur.remainingMinutes)

        assertEquals("19:21", result.iftar.prayerTime)
        val expectedIftarMinutes = (3 * 60) + (19 * 60) + 21L
        assertEquals(expectedIftarMinutes, result.iftar.remainingMinutes)
    }

    @Test
    fun tomorrowPrayerTimesNull_fallbackToTodayTimesWithOneDayOffset() {
        val ramadanTimes = createPrayerTime(hijriMonth = 9, imsak = "05:15", aksam = "19:20")
        val now = LocalTime.of(21, 0)

        val result = useCase(
            todayPrayerTimes = ramadanTimes,
            now = now,
            tomorrowPrayerTimes = null,
            today = testDate
        )

        assertNotNull(result)
        assertEquals("05:15", result!!.sahur.prayerTime)
        assertEquals("19:20", result.iftar.prayerTime)
    }
}
