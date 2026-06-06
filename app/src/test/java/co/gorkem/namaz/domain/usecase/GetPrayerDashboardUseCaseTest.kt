package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class GetPrayerDashboardUseCaseTest {

    private val getNextPrayerUseCase = GetNextPrayerUseCase()
    private val useCase = GetPrayerDashboardUseCase(getNextPrayerUseCase)

    private val todayTimes = PrayerTime(
        date = "06-06-2026",
        gregorianDate = "06-06-2026",
        hijriDay = "20",
        hijriMonthNumber = 11,
        hijriYear = "1447",
        imsak = "03:30",
        gunes = "05:15",
        ogle = "13:10",
        ikindi = "17:05",
        aksam = "20:30",
        yatsi = "22:15",
        timezone = "Europe/Istanbul"
    )

    private val tomorrowTimes = PrayerTime(
        date = "07-06-2026",
        gregorianDate = "07-06-2026",
        hijriDay = "21",
        hijriMonthNumber = 11,
        hijriYear = "1447",
        imsak = "03:29",
        gunes = "05:14",
        ogle = "13:10",
        ikindi = "17:06",
        aksam = "20:31",
        yatsi = "22:16",
        timezone = "Europe/Istanbul"
    )

    @Test
    fun periodResolution_sabahPeriod() {
        val result = useCase.execute(
            todayPrayerTimes = todayTimes,
            tomorrowPrayerTimes = tomorrowTimes,
            now = LocalTime.of(4, 0)
        )
        assertNotNull(result)
        assertEquals(PeriodType.SABAH, result!!.currentPeriod.periodType)
        assertEquals(PrayerName.IMSAK, result.currentPeriod.prayerName)
        assertEquals(LocalTime.of(5, 15), result.currentPeriod.endTime)
        assertEquals(75, result.remainingToEndOfPeriodMinutes)
    }

    @Test
    fun periodResolution_kuslukPeriod() {
        val result = useCase.execute(
            todayPrayerTimes = todayTimes,
            tomorrowPrayerTimes = tomorrowTimes,
            now = LocalTime.of(8, 0)
        )
        assertNotNull(result)
        assertEquals(PeriodType.KUSLUK, result!!.currentPeriod.periodType)
        assertEquals(PrayerName.GUNES, result.currentPeriod.prayerName)
        assertEquals(LocalTime.of(13, 10), result.currentPeriod.endTime)
    }

    @Test
    fun periodResolution_yatsiPeriod_beforeMidnight() {
        val result = useCase.execute(
            todayPrayerTimes = todayTimes,
            tomorrowPrayerTimes = tomorrowTimes,
            now = LocalTime.of(23, 0)
        )
        assertNotNull(result)
        assertEquals(PeriodType.YATSI, result!!.currentPeriod.periodType)
        assertEquals(PrayerName.YATSI, result.currentPeriod.prayerName)
        assertEquals(LocalTime.of(3, 29), result.currentPeriod.endTime)
        assertEquals(269, result.remainingToEndOfPeriodMinutes)
    }

    @Test
    fun periodResolution_yatsiPeriod_afterMidnight() {
        val result = useCase.execute(
            todayPrayerTimes = todayTimes,
            tomorrowPrayerTimes = tomorrowTimes,
            now = LocalTime.of(1, 0)
        )
        assertNotNull(result)
        assertEquals(PeriodType.YATSI, result!!.currentPeriod.periodType)
        assertEquals(PrayerName.YATSI, result.currentPeriod.prayerName)
        assertEquals(LocalTime.of(3, 30), result.currentPeriod.endTime)
        assertEquals(150, result.remainingToEndOfPeriodMinutes)
    }

    @Test
    fun kerahatDetection_morningKerahat() {
        val result = useCase.execute(
            todayPrayerTimes = todayTimes,
            tomorrowPrayerTimes = tomorrowTimes,
            now = LocalTime.of(5, 30)
        )
        assertNotNull(result)
        assertNotNull(result!!.kerahatInfo)
        assertTrue(result.kerahatInfo!!.isCurrentlyKerahat)
        assertEquals(PrayerName.GUNES, result.kerahatInfo!!.prayerName)
        assertEquals(30, result.kerahatInfo!!.remainingToEndMinutes)
    }

    @Test
    fun kerahatDetection_noonKerahat() {
        val result = useCase.execute(
            todayPrayerTimes = todayTimes,
            tomorrowPrayerTimes = tomorrowTimes,
            now = LocalTime.of(12, 50)
        )
        assertNotNull(result)
        assertNotNull(result!!.kerahatInfo)
        assertTrue(result.kerahatInfo!!.isCurrentlyKerahat)
        assertEquals(PrayerName.OGLE, result.kerahatInfo!!.prayerName)
    }

    @Test
    fun kerahatDetection_eveningKerahat() {
        val result = useCase.execute(
            todayPrayerTimes = todayTimes,
            tomorrowPrayerTimes = tomorrowTimes,
            now = LocalTime.of(20, 0)
        )
        assertNotNull(result)
        assertNotNull(result!!.kerahatInfo)
        assertTrue(result.kerahatInfo!!.isCurrentlyKerahat)
        assertEquals(PrayerName.AKSAM, result.kerahatInfo!!.prayerName)
    }

    @Test
    fun nextKerahatPrediction_duringAfternoon() {
        val result = useCase.execute(
            todayPrayerTimes = todayTimes,
            tomorrowPrayerTimes = tomorrowTimes,
            now = LocalTime.of(15, 0)
        )
        assertNotNull(result)
        assertNull(result!!.kerahatInfo)
        assertNotNull(result.nextKerahatInfo)
        assertEquals(PrayerName.AKSAM, result.nextKerahatInfo!!.prayerName)
        assertEquals(285, result.nextKerahatInfo!!.remainingToStartMinutes)
    }
}
