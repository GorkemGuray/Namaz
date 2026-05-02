package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetNextPrayerUseCase @Inject constructor() {

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    data class NextPrayerInfo(
        val prayerName: PrayerName,
        val prayerTime: String,
        val remainingMinutes: Long
    )

    operator fun invoke(todayPrayerTimes: PrayerTime): NextPrayerInfo? {
        val now = LocalTime.now()

        val prayers = todayPrayerTimes.getAllPrayers()
            .map { (name, timeStr) ->
                name to LocalTime.parse(timeStr, timeFormatter)
            }

        val nextPrayer = prayers.find { (_, time) -> time.isAfter(now) }

        return if (nextPrayer != null) {
            val (name, time) = nextPrayer
            val remainingMinutes = java.time.Duration.between(now, time).toMinutes()
            NextPrayerInfo(
                prayerName = name,
                prayerTime = time.format(timeFormatter),
                remainingMinutes = remainingMinutes
            )
        } else {
            prayers.firstOrNull()?.let { (name, time) ->
                NextPrayerInfo(
                    prayerName = name,
                    prayerTime = time.format(timeFormatter),
                    remainingMinutes = java.time.Duration.between(now, time).toMinutes() + 24 * 60
                )
            }
        }
    }
}
