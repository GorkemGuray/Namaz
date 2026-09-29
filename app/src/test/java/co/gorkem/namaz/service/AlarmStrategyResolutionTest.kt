package co.gorkem.namaz.service

import org.junit.Assert.assertEquals
import org.junit.Test

class AlarmStrategyResolutionTest {

    @Test
    fun preS_canScheduleExact_usesExact() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 23,
            canScheduleExact = true,
            exactAttemptFailed = false
        )
        assertEquals(AlarmStrategy.EXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun preS_noExactAttemptNeeded_usesExact() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 30,
            canScheduleExact = false,
            exactAttemptFailed = false
        )
        assertEquals(AlarmStrategy.EXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun preS_exactAttemptFailed_usesInexactAllowWhileIdle() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 23,
            canScheduleExact = true,
            exactAttemptFailed = true
        )
        assertEquals(AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun apiS_canScheduleExact_usesExact() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 31,
            canScheduleExact = true,
            exactAttemptFailed = false
        )
        assertEquals(AlarmStrategy.EXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun apiS_permissionDenied_usesInexactAllowWhileIdle() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 31,
            canScheduleExact = false,
            exactAttemptFailed = false
        )
        assertEquals(AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun apiTiramisu_permissionDenied_usesInexactAllowWhileIdle() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 33,
            canScheduleExact = false,
            exactAttemptFailed = false
        )
        assertEquals(AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun api36_permissionDenied_usesInexactAllowWhileIdle() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 36,
            canScheduleExact = false,
            exactAttemptFailed = false
        )
        assertEquals(AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun api36_permissionGranted_usesExact() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 36,
            canScheduleExact = true,
            exactAttemptFailed = false
        )
        assertEquals(AlarmStrategy.EXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun securityException_onApiS_fallsBackToInexact() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 31,
            canScheduleExact = true,
            exactAttemptFailed = true
        )
        assertEquals(AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun securityException_onApi36_fallsBackToInexact() {
        val strategy = resolveAlarmStrategy(
            apiLevel = 36,
            canScheduleExact = true,
            exactAttemptFailed = true
        )
        assertEquals(AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE, strategy)
    }

    @Test
    fun securityException_neverFallsBackToNonIdleAlarm() {
        for (api in listOf(23, 26, 30, 31, 33, 35, 36)) {
            val strategy = resolveAlarmStrategy(api, canScheduleExact = true, exactAttemptFailed = true)
            assertEquals(AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE, strategy)
        }
    }

    @Test
    fun permissionDenied_neverUsesExactOnApiSAndAbove() {
        for (api in listOf(31, 33, 35, 36)) {
            val strategy = resolveAlarmStrategy(api, canScheduleExact = false, exactAttemptFailed = false)
            assertEquals(AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE, strategy)
        }
    }

    @Test
    fun permissionDenied_beforeApiS_stillUsesExactBecauseNoPermissionConceptExists() {
        for (api in listOf(23, 26, 30)) {
            val strategy = resolveAlarmStrategy(api, canScheduleExact = false, exactAttemptFailed = false)
            assertEquals(AlarmStrategy.EXACT_ALLOW_WHILE_IDLE, strategy)
        }
    }

    @Test
    fun everyApiLevel_alwaysResolvesToAKnownStrategy() {
        for (api in 21..40) {
            for (granted in listOf(true, false)) {
                for (failed in listOf(true, false)) {
                    val strategy = resolveAlarmStrategy(api, granted, failed)
                    assertEquals(true, strategy in AlarmStrategy.values().toSet())
                }
            }
        }
    }

    @Test
    fun everyCombination_producesADozeSafeStrategy() {
        for (api in 21..40) {
            for (granted in listOf(true, false)) {
                for (failed in listOf(true, false)) {
                    assertEquals(
                        true,
                        resolveAlarmStrategy(api, granted, failed).allowsWhileIdle
                    )
                }
            }
        }
    }

    @Test
    fun everyStrategy_isDozeSafe() {
        AlarmStrategy.values().forEach { strategy ->
            assertEquals(true, strategy.allowsWhileIdle)
        }
    }

    @Test
    fun nextMinuteBoundary_isStrictlyInFuture() {
        val now = 1_700_000_123_456L
        val next = nextMinuteBoundary(now)
        assertEquals(true, next > now)
    }

    @Test
    fun nextMinuteBoundary_landsOnMinuteStart() {
        val now = 1_700_000_123_456L
        val next = nextMinuteBoundary(now)
        assertEquals(0L, next % 60_000L)
    }

    @Test
    fun nextMinuteBoundary_advancesAtMostOneMinute() {
        val now = 1_700_000_123_456L
        val next = nextMinuteBoundary(now)
        assertEquals(true, next - now <= 60_000L)
    }

    @Test
    fun nextMinuteBoundary_exactlyOnBoundary_advancesToNextMinute() {
        val boundary = 1_700_000_040_000L
        val next = nextMinuteBoundary(boundary)
        assertEquals(60_000L, next - boundary)
    }
}
