package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class PrayerPeriod(
    val prayerName: PrayerName,
    val label: String,
    val startTime: LocalTime,
    val endTime: LocalTime
)

data class KerahatInfo(
    val isCurrentlyKerahat: Boolean,
    val label: String,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val remainingToStartMinutes: Long,
    val remainingToEndMinutes: Long
)

data class PrayerDashboardData(
    val currentPeriod: PrayerPeriod,
    val remainingToEndOfPeriodMinutes: Long,
    val nextPrayer: GetNextPrayerUseCase.NextPrayerInfo,
    val kerahatInfo: KerahatInfo?,
    val nextKerahatInfo: KerahatInfo?
)

class GetPrayerDashboardUseCase @Inject constructor(
    private val getNextPrayerUseCase: GetNextPrayerUseCase
) {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun execute(todayPrayerTimes: PrayerTime): PrayerDashboardData? {
        val now = LocalTime.now()

        val prayers = todayPrayerTimes.getAllPrayers()
            .map { (name, timeStr) ->
                name to LocalTime.parse(timeStr, timeFormatter)
            }
            .toMap()

        val nextPrayerInfo = getNextPrayerUseCase(todayPrayerTimes) ?: return null

        val currentPeriod = computeCurrentPeriod(now, prayers)
        val remainingToEnd = Duration.between(now, currentPeriod.endTime).toMinutes().coerceAtLeast(0)

        val allKerahatPeriods = computeAllKerahatPeriods(prayers)
        val kerahatInfo = findCurrentKerahat(now, allKerahatPeriods)
        val nextKerahatInfo = if (kerahatInfo != null) {
            null
        } else {
            findNextKerahat(now, allKerahatPeriods)
        }

        return PrayerDashboardData(
            currentPeriod = currentPeriod,
            remainingToEndOfPeriodMinutes = remainingToEnd,
            nextPrayer = nextPrayerInfo,
            kerahatInfo = kerahatInfo,
            nextKerahatInfo = nextKerahatInfo
        )
    }

    private fun computeCurrentPeriod(
        now: LocalTime,
        prayers: Map<PrayerName, LocalTime>
    ): PrayerPeriod {
        val imsak = prayers[PrayerName.IMSAK]!!
        val gunes = prayers[PrayerName.GUNES]!!
        val ogle = prayers[PrayerName.OGLE]!!
        val ikindi = prayers[PrayerName.IKINDI]!!
        val aksam = prayers[PrayerName.AKSAM]!!
        val yatsi = prayers[PrayerName.YATSI]!!

        return when {
            now in imsak..gunes || now >= yatsi || now < imsak -> {
                val isEarly = now >= yatsi || now < imsak
                PrayerPeriod(
                    prayerName = PrayerName.YATSI,
                    label = if (isEarly) "Yatsı Vakti" else "Sabah Vakti",
                    startTime = if (isEarly) yatsi else imsak,
                    endTime = if (isEarly) LocalTime.MAX else gunes
                )
            }
            now in gunes..ogle -> PrayerPeriod(
                prayerName = PrayerName.GUNES,
                label = "Kuşluk Vakti",
                startTime = gunes,
                endTime = ogle
            )
            now in ogle..ikindi -> PrayerPeriod(
                prayerName = PrayerName.OGLE,
                label = "Öğle Vakti",
                startTime = ogle,
                endTime = ikindi
            )
            now in ikindi..aksam -> PrayerPeriod(
                prayerName = PrayerName.IKINDI,
                label = "İkindi Vakti",
                startTime = ikindi,
                endTime = aksam
            )
            now in aksam..yatsi -> PrayerPeriod(
                prayerName = PrayerName.AKSAM,
                label = "Akşam Vakti",
                startTime = aksam,
                endTime = yatsi
            )
            else -> PrayerPeriod(
                prayerName = PrayerName.YATSI,
                label = "Yatsı Vakti",
                startTime = yatsi,
                endTime = LocalTime.MAX
            )
        }
    }

    private fun computeAllKerahatPeriods(
        prayers: Map<PrayerName, LocalTime>
    ): List<KerahatPeriod> {
        val gunes = prayers[PrayerName.GUNES]!!
        val ogle = prayers[PrayerName.OGLE]!!
        val aksam = prayers[PrayerName.AKSAM]!!

        return listOf(
            KerahatPeriod(
                label = "Güneş Kerahati",
                start = gunes,
                end = gunes.plusMinutes(45)
            ),
            KerahatPeriod(
                label = "Öğle Kerahati",
                start = ogle.minusMinutes(10),
                end = ogle
            ),
            KerahatPeriod(
                label = "Akşam Kerahati",
                start = aksam.minusMinutes(45),
                end = aksam
            )
        )
    }

    private fun findCurrentKerahat(
        now: LocalTime,
        periods: List<KerahatPeriod>
    ): KerahatInfo? {
        val current = periods.find { now >= it.start && now < it.end } ?: return null
        return KerahatInfo(
            isCurrentlyKerahat = true,
            label = current.label,
            startTime = current.start,
            endTime = current.end,
            remainingToStartMinutes = 0,
            remainingToEndMinutes = Duration.between(now, current.end).toMinutes().coerceAtLeast(0)
        )
    }

    private fun findNextKerahat(
        now: LocalTime,
        periods: List<KerahatPeriod>
    ): KerahatInfo? {
        val next = periods.find { it.start > now } ?: return null
        return KerahatInfo(
            isCurrentlyKerahat = false,
            label = next.label,
            startTime = next.start,
            endTime = next.end,
            remainingToStartMinutes = Duration.between(now, next.start).toMinutes().coerceAtLeast(0),
            remainingToEndMinutes = Duration.between(now, next.end).toMinutes().coerceAtLeast(0)
        )
    }

    private data class KerahatPeriod(
        val label: String,
        val start: LocalTime,
        val end: LocalTime
    )
}
