package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class GetNextPrayerUseCaseTest {

    private val useCase = GetNextPrayerUseCase()

    private val samplePrayerTimes = PrayerTime(
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

    @Test
    fun beforeImsak_returnsImsak() {
        val result = useCase(
            todayPrayerTimes = samplePrayerTimes,
            now = LocalTime.of(2, 0),
            today = LocalDate.of(2026, 6, 6)
        )
        assertNotNull(result)
        assertEquals(PrayerName.IMSAK, result!!.prayerName)
        assertEquals("03:30", result.prayerTime)
        assertEquals(90, result.remainingMinutes)
    }

    @Test
    fun duringDay_returnsNextUpcomingPrayer() {
        val result = useCase(
            todayPrayerTimes = samplePrayerTimes,
            now = LocalTime.of(14, 0),
            today = LocalDate.of(2026, 6, 6)
        )
        assertNotNull(result)
        assertEquals(PrayerName.IKINDI, result!!.prayerName)
        assertEquals("17:05", result.prayerTime)
        assertEquals(185, result.remainingMinutes)
    }

    @Test
    fun afterYatsi_returnsTomorrowsImsak() {
        val result = useCase(
            todayPrayerTimes = samplePrayerTimes,
            now = LocalTime.of(23, 0),
            today = LocalDate.of(2026, 6, 6)
        )
        assertNotNull(result)
        assertEquals(PrayerName.IMSAK, result!!.prayerName)
        assertEquals("03:30", result.prayerTime)
        assertEquals(270, result.remainingMinutes)
    }
}
