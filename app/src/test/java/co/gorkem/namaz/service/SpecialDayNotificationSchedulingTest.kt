package co.gorkem.namaz.service

import co.gorkem.namaz.domain.model.SpecialDay
import co.gorkem.namaz.domain.model.SpecialDayType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class SpecialDayNotificationSchedulingTest {

    private val zone: ZoneId = ZoneId.of("Europe/Istanbul")

    @Test
    fun beforeNineAm_schedulesNotificationAtNineAm() {
        val date = LocalDate.of(2026, 3, 20)
        val specialDay = SpecialDay(type = SpecialDayType.FRIDAY, isFriday = true, isKandil = false)
        val now = ZonedDateTime.of(date, LocalTime.of(7, 30), zone)

        val schedule = resolveSpecialDayNotificationSchedule(date, specialDay, now)
        assertNotNull(schedule)
        assertEquals("special_day_20-03-2026", schedule!!.uniqueWorkName)
        assertEquals(LocalTime.of(9, 0), schedule.targetDateTime.toLocalTime())
        assertEquals(90 * 60 * 1000L, schedule.delayMillis)
    }

    @Test
    fun afterNineAm_doesNotSchedule() {
        val date = LocalDate.of(2026, 3, 20)
        val specialDay = SpecialDay(type = SpecialDayType.FRIDAY, isFriday = true, isKandil = false)
        val now = ZonedDateTime.of(date, LocalTime.of(9, 1), zone)

        val schedule = resolveSpecialDayNotificationSchedule(date, specialDay, now)
        assertNull(schedule)
    }

    @Test
    fun futureDate_schedulesForTargetDateAtNineAm() {
        val today = LocalDate.of(2026, 3, 19)
        val tomorrow = today.plusDays(1)
        val specialDay = SpecialDay(type = SpecialDayType.FRIDAY, isFriday = true, isKandil = false)
        val now = ZonedDateTime.of(today, LocalTime.of(22, 0), zone)

        val schedule = resolveSpecialDayNotificationSchedule(tomorrow, specialDay, now)
        assertNotNull(schedule)
        assertEquals("special_day_20-03-2026", schedule!!.uniqueWorkName)
        assertTrue(schedule.delayMillis > 0)
        assertEquals(11 * 60 * 60 * 1000L, schedule.delayMillis)
    }
}
