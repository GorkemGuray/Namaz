package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.util.DateTimeUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class GetNextPrayerUseCase @Inject constructor() {

    data class NextPrayerInfo(
        val prayerName: PrayerName,
        val prayerTime: String,
        val remainingMinutes: Long,
        val targetPrayerTimeInMillis: Long
    )

    operator fun invoke(todayPrayerTimes: PrayerTime): NextPrayerInfo? {
        val now = LocalTime.now()
        val today = LocalDate.now()

        val prayers = todayPrayerTimes.getAllPrayers()
            .map { (name, timeStr) ->
                name to LocalTime.parse(timeStr, DateTimeUtils.TIME_FORMATTER)
            }

        val nextPrayer = prayers.find { (_, time) -> time.isAfter(now) }

        return if (nextPrayer != null) {
            val (name, time) = nextPrayer
            val remainingMinutes = java.time.Duration.between(now, time).toMinutes()
            val targetDateTime = LocalDateTime.of(today, time)
            val targetMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            NextPrayerInfo(
                prayerName = name,
                prayerTime = time.format(DateTimeUtils.TIME_FORMATTER),
                remainingMinutes = remainingMinutes,
                targetPrayerTimeInMillis = targetMillis
            )
        } else {
            prayers.firstOrNull()?.let { (name, time) ->
                val remainingMinutes = java.time.Duration.between(now, time).toMinutes() + 24 * 60
                val targetDateTime = LocalDateTime.of(today.plusDays(1), time)
                val targetMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                NextPrayerInfo(
                    prayerName = name,
                    prayerTime = time.format(DateTimeUtils.TIME_FORMATTER),
                    remainingMinutes = remainingMinutes,
                    targetPrayerTimeInMillis = targetMillis
                )
            }
        }
    }
}
