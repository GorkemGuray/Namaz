package co.gorkem.namaz.service

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.util.DateTimeUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class PrayerNotificationSchedulingTest {

    private val zone: ZoneId = ZoneId.of("Europe/Istanbul")

    private fun nowAt(date: String, hour: Int, minute: Int): ZonedDateTime {
        val localDate = LocalDate.parse(date, DateTimeUtils.DATE_FORMATTER)
        return ZonedDateTime.of(localDate, LocalTime.of(hour, minute), zone)
    }

    private fun dayData(
        date: String,
        imsak: String = "05:30",
        gunes: String = "07:00",
        ogle: String = "13:00",
        ikindi: String = "16:00",
        aksam: String = "18:30",
        yatsi: String = "20:00"
    ): PrayerTime = PrayerTime(
        date = date,
        gregorianDate = "",
        hijriDay = "",
        hijriMonthNumber = 0,
        hijriYear = "",
        imsak = imsak,
        gunes = gunes,
        ogle = ogle,
        ikindi = ikindi,
        aksam = aksam,
        yatsi = yatsi,
        timezone = "Europe/Istanbul"
    )

    @Test
    fun sameDay_pastPrayersExcluded_futurePrayersScheduled() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("07-11-2026"), dayData("08-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)

        val todayNames = schedules
            .filter { it.uniqueWorkName.contains("07-11-2026") }
            .filter { it.prayerTime == "13:00" || it.prayerTime == "16:00" || it.prayerTime == "18:30" || it.prayerTime == "20:00" }
            .map { it.prayerName }
        assertEquals(
            listOf(PrayerName.OGLE, PrayerName.IKINDI, PrayerName.AKSAM, PrayerName.YATSI),
            todayNames
        )
    }

    @Test
    fun nextDay_allPrayersScheduled() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("07-11-2026"), dayData("08-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)

        val tomorrowNames = schedules.map { it.prayerName }
        assertTrue(tomorrowNames.containsAll(PrayerName.values().toList()))
        assertEquals(4 + 6, schedules.size)
    }

    @Test
    fun minutesBefore_shiftsTargetTime() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("07-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 15, now = now)

        val ogle = schedules.first { it.prayerName == PrayerName.OGLE }
        assertEquals(
            ZonedDateTime.of(LocalDate.parse("07-11-2026", DateTimeUtils.DATE_FORMATTER), LocalTime.of(12, 45), zone),
            ogle.targetDateTime
        )
        assertEquals(Duration.ofMinutes(45).toMillis(), ogle.delayMillis)
    }

    @Test
    fun nextDayMissing_gracefullySkipped() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("07-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)

        assertEquals(4, schedules.size)
        assertTrue(schedules.all { it.uniqueWorkName.contains("07-11-2026") })
    }

    @Test
    fun todayMissing_nextDayStillScheduled() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("08-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)

        assertEquals(6, schedules.size)
        assertTrue(schedules.all { it.uniqueWorkName.contains("08-11-2026") })
    }

    @Test
    fun uniqueWorkNames_includePrayerDate_andDaysAreDistinct() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("07-11-2026"), dayData("08-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)
        val workNames = schedules.map { it.uniqueWorkName }

        assertTrue(workNames.contains("prayer_YATSI_07-11-2026"))
        assertTrue(workNames.contains("prayer_YATSI_08-11-2026"))
        assertEquals(workNames.size, workNames.toSet().size)
    }

    @Test
    fun notificationIds_areDistinctPerDay() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("07-11-2026"), dayData("08-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)

        val todayIds = schedules.filter { it.uniqueWorkName.contains("07-11-2026") }.map { it.notificationId }
        val tomorrowIds = schedules.filter { it.uniqueWorkName.contains("08-11-2026") }.map { it.notificationId }
        assertTrue(todayIds.all { it in 100..105 })
        assertTrue(tomorrowIds.all { it in 200..205 })
        assertEquals(102, schedules.first { it.prayerName == PrayerName.OGLE }.notificationId)
        assertEquals(200, schedules.first { it.prayerName == PrayerName.IMSAK && it.uniqueWorkName.contains("08-11-2026") }.notificationId)
    }

    @Test
    fun invalidTimeText_prayerSkippedWithoutCrash() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("07-11-2026", ogle = ""))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)

        val todayNames = schedules.filter { it.uniqueWorkName.contains("07-11-2026") }.map { it.prayerName }
        assertTrue(todayNames.isNotEmpty())
        assertTrue(!todayNames.contains(PrayerName.OGLE))
    }

    @Test
    fun targetExactlyNow_isExcluded() {
        val now = nowAt("07-11-2026", 13, 0)
        val data = listOf(dayData("07-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)

        val todayNames = schedules.map { it.prayerName }
        assertTrue(!todayNames.contains(PrayerName.OGLE))
        assertTrue(todayNames.contains(PrayerName.IKINDI))
    }

    @Test
    fun delayMillis_alwaysMatchesTargetTime() {
        val now = nowAt("07-11-2026", 12, 0)
        val data = listOf(dayData("07-11-2026"), dayData("08-11-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 15, now = now)

        schedules.forEach { schedule ->
            assertEquals(
                Duration.between(now, schedule.targetDateTime).toMillis(),
                schedule.delayMillis
            )
        }
    }

    @Test
    fun monthBoundary_nextMonthRowScheduled() {
        val now = nowAt("30-11-2026", 22, 0)
        val data = listOf(dayData("30-11-2026"), dayData("01-12-2026"))

        val schedules = resolveNotificationSchedules(data, minutesBefore = 0, now = now)

        val tomorrowSchedules = schedules.filter { it.uniqueWorkName.contains("01-12-2026") }
        assertEquals(6, tomorrowSchedules.size)
        assertTrue(tomorrowSchedules.all { it.targetDateTime.monthValue == 12 })
        assertTrue(schedules.none { it.uniqueWorkName.contains("30-11-2026") })
    }
}
