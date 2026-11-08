package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.util.DateTimeUtils
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

data class RamadanTargetInfo(
    val prayerTime: String,
    val remainingMinutes: Long,
    val targetTimeInMillis: Long
)

data class RamadanCounters(
    val isRamadan: Boolean,
    val sahur: RamadanTargetInfo,
    val iftar: RamadanTargetInfo
)

class GetRamadanCountersUseCase @Inject constructor() {

    operator fun invoke(
        todayPrayerTimes: PrayerTime,
        now: LocalTime = LocalTime.now(),
        tomorrowPrayerTimes: PrayerTime? = null,
        today: LocalDate = LocalDate.now()
    ): RamadanCounters? {
        if (todayPrayerTimes.hijriMonthNumber != 9) {
            return null
        }

        val todayImsak = LocalTime.parse(todayPrayerTimes.imsak, DateTimeUtils.TIME_FORMATTER)
        val todayAksam = LocalTime.parse(todayPrayerTimes.aksam, DateTimeUtils.TIME_FORMATTER)

        val sahurInfo = if (now.isBefore(todayImsak)) {
            calculateTargetInfo(
                targetTime = todayImsak,
                targetDate = today,
                nowTime = now,
                todayDate = today
            )
        } else {
            val tomorrowImsak = tomorrowPrayerTimes?.imsak?.let {
                LocalTime.parse(it, DateTimeUtils.TIME_FORMATTER)
            } ?: todayImsak
            calculateTargetInfo(
                targetTime = tomorrowImsak,
                targetDate = today.plusDays(1),
                nowTime = now,
                todayDate = today
            )
        }

        val iftarInfo = if (now.isBefore(todayAksam)) {
            calculateTargetInfo(
                targetTime = todayAksam,
                targetDate = today,
                nowTime = now,
                todayDate = today
            )
        } else {
            val tomorrowAksam = tomorrowPrayerTimes?.aksam?.let {
                LocalTime.parse(it, DateTimeUtils.TIME_FORMATTER)
            } ?: todayAksam
            calculateTargetInfo(
                targetTime = tomorrowAksam,
                targetDate = today.plusDays(1),
                nowTime = now,
                todayDate = today
            )
        }

        return RamadanCounters(
            isRamadan = true,
            sahur = sahurInfo,
            iftar = iftarInfo
        )
    }

    private fun calculateTargetInfo(
        targetTime: LocalTime,
        targetDate: LocalDate,
        nowTime: LocalTime,
        todayDate: LocalDate
    ): RamadanTargetInfo {
        val targetDateTime = LocalDateTime.of(targetDate, targetTime)
        val currentDateTime = LocalDateTime.of(todayDate, nowTime)
        val remainingMinutes = Duration.between(currentDateTime, targetDateTime).toMinutes().coerceAtLeast(0)
        val targetMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return RamadanTargetInfo(
            prayerTime = targetTime.format(DateTimeUtils.TIME_FORMATTER),
            remainingMinutes = remainingMinutes,
            targetTimeInMillis = targetMillis
        )
    }
}
