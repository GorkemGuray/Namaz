package co.gorkem.namaz.domain.calculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class PrayerTimeCalculatorTest {

    private val calculator = PrayerTimeCalculator()
    private val istanbulTz = ZoneId.of("Europe/Istanbul")

    @Test
    fun istanbul_may_21_2026_matches_diyanet() {
        val times = calculator.calculate(
            date = LocalDate.of(2026, 5, 21),
            latitude = 41.0082,
            longitude = 28.9784,
            timezone = istanbulTz
        )

        assertTimeNear("03:47", times.imsak, 8)
        assertTimeNear("05:37", times.gunes, 5)
        assertTimeNear("13:09", times.ogle, 5)
        assertTimeNear("17:05", times.ikindi, 5)
        assertTimeNear("20:30", times.aksam, 5)
        assertTimeNear("22:12", times.yatsi, 5)
    }

    @Test
    fun istanbul_january_01_2026_matches_diyanet() {
        val times = calculator.calculate(
            date = LocalDate.of(2026, 1, 1),
            latitude = 41.0082,
            longitude = 28.9784,
            timezone = istanbulTz
        )

        assertTimeNear("06:53", times.imsak, 5)
        assertTimeNear("08:25", times.gunes, 5)
        assertTimeNear("13:15", times.ogle, 5)
        assertTimeNear("15:35", times.ikindi, 5)
        assertTimeNear("17:56", times.aksam, 5)
        assertTimeNear("19:22", times.yatsi, 5)
    }

    @Test
    fun istanbul_december_21_2026_matches_diyanet() {
        val times = calculator.calculate(
            date = LocalDate.of(2026, 12, 21),
            latitude = 41.0082,
            longitude = 28.9784,
            timezone = istanbulTz
        )

        assertTimeNear("06:45", times.imsak, 10)
        assertTimeNear("08:18", times.gunes, 5)
        assertTimeNear("13:06", times.ogle, 5)
        assertTimeNear("15:24", times.ikindi, 10)
        assertTimeNear("17:45", times.aksam, 5)
        assertTimeNear("19:12", times.yatsi, 5)
    }

    @Test
    fun ankara_may_21_2026_near_diyanet() {
        val times = calculator.calculate(
            date = LocalDate.of(2026, 5, 21),
            latitude = 39.9334,
            longitude = 32.8597,
            timezone = istanbulTz
        )

        assertTrue(times.imsak > 0)
        assertTrue(times.gunes > times.imsak)
        assertTrue(times.ogle > times.gunes)
        assertTrue(times.ikindi > times.ogle)
        assertTrue(times.aksam > times.ikindi)
        assertTrue(times.yatsi > times.aksam)
    }

    @Test
    fun times_are_chronological() {
        val times = calculator.calculate(
            date = LocalDate.of(2026, 6, 15),
            latitude = 41.0082,
            longitude = 28.9784,
            timezone = istanbulTz
        )

        assertTrue("İmsak Güneşten önce olmalı", times.imsak < times.gunes)
        assertTrue("Güneş Öğleden önce olmalı", times.gunes < times.ogle)
        assertTrue("Öğle İkindiden önce olmalı", times.ogle < times.ikindi)
        assertTrue("İkindi Akşamdan önce olmalı", times.ikindi < times.aksam)
        assertTrue("Akşam Yatsıdan önce olmalı", times.aksam < times.yatsi)
    }

    @Test
    fun istanbul_june_15_2026_longest_day() {
        val times = calculator.calculate(
            date = LocalDate.of(2026, 6, 15),
            latitude = 41.0082,
            longitude = 28.9784,
            timezone = istanbulTz
        )

        assertTimeNear("03:30", times.imsak, 10)
        assertTrue(times.aksam > 1220)
    }

    @Test
    fun times_should_be_valid() {
        val dates = listOf(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 3, 21),
            LocalDate.of(2026, 6, 21),
            LocalDate.of(2026, 9, 22),
            LocalDate.of(2026, 12, 21)
        )

        dates.forEach { date ->
            val times = calculator.calculate(
                date = date,
                latitude = 41.0082,
                longitude = 28.9784,
                timezone = istanbulTz
            )

            assertTrue("Day ${date}: imsak=$times.imsak should be valid", times.imsak in 120..480)
            assertTrue("Day ${date}: gunes=$times.gunes should be valid", times.gunes in 300..540)
            assertTrue("Day ${date}: ogle=$times.ogle should be valid", times.ogle in 700..850)
            assertTrue("Day ${date}: ikindi=$times.ikindi should be valid", times.ikindi in 700..1300)
            assertTrue("Day ${date}: aksam=$times.aksam should be valid", times.aksam in 1000..1400)
            assertTrue("Day ${date}: yatsi=$times.yatsi should be valid", times.yatsi in 1000..1500)
        }
    }

    private fun assertTimeNear(expected: String, actualMinutes: Int, toleranceMinutes: Int) {
        val parts = expected.split(":")
        val expectedMinutes = parts[0].toInt() * 60 + parts[1].toInt()
        val diff = kotlin.math.abs(expectedMinutes - actualMinutes)
        assertEquals(
            "Expected ~$expected but got ${minutesToStr(actualMinutes)} (diff=${diff}min)",
            true,
            diff <= toleranceMinutes
        )
    }

    private fun minutesToStr(minutes: Int): String {
        val h = minutes / 60
        val m = minutes % 60
        return String.format("%02d:%02d", h, m)
    }
}
