package co.gorkem.namaz.wear

import co.gorkem.namaz.wear.data.WearPrayerItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class PrayerNextPrayerCalculationTest {

    private val samplePrayers = listOf(
        WearPrayerItem("IMSAK", "03:30"),
        WearPrayerItem("GUNES", "05:15"),
        WearPrayerItem("OGLE", "13:10"),
        WearPrayerItem("IKINDI", "17:05"),
        WearPrayerItem("AKSAM", "20:30"),
        WearPrayerItem("YATSI", "22:15")
    )

    @Test
    fun beforeImsak_calculatesImsak() {
        val result = calculateNextPrayer(
            prayers = samplePrayers,
            now = LocalTime.of(2, 0),
            today = LocalDate.of(2026, 6, 6)
        )
        assertNotNull(result)
        assertEquals("IMSAK", result!!.name)
        assertEquals("03:30", result.time)
        assertEquals(90, result.remainingMinutes)
    }

    @Test
    fun duringDay_calculatesNextUpcoming() {
        val result = calculateNextPrayer(
            prayers = samplePrayers,
            now = LocalTime.of(14, 0),
            today = LocalDate.of(2026, 6, 6)
        )
        assertNotNull(result)
        assertEquals("IKINDI", result!!.name)
        assertEquals("17:05", result.time)
        assertEquals(185, result.remainingMinutes)
    }

    @Test
    fun afterYatsi_calculatesTomorrowImsak() {
        val result = calculateNextPrayer(
            prayers = samplePrayers,
            now = LocalTime.of(23, 0),
            today = LocalDate.of(2026, 6, 6)
        )
        assertNotNull(result)
        assertEquals("IMSAK", result!!.name)
        assertEquals("03:30", result.time)
        assertEquals(270, result.remainingMinutes)
    }
}
