package co.gorkem.namaz.domain.usecase

import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

enum class PeriodType {
    SABAH, KUSLUK, OGLE, IKINDI, AKSAM, YATSI
}

data class PrayerPeriod(
    val prayerName: PrayerName,
    val periodType: PeriodType,
    val startTime: LocalTime,
    val endTime: LocalTime
)

data class KerahatInfo(
    val isCurrentlyKerahat: Boolean,
    val prayerName: PrayerName,
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

    fun execute(
        todayPrayerTimes: PrayerTime,
        tomorrowPrayerTimes: PrayerTime? = null
    ): PrayerDashboardData? {
        val now = LocalTime.now()

        val prayers = todayPrayerTimes.getAllPrayers()
            .map { (name, timeStr) ->
                name to LocalTime.parse(timeStr, timeFormatter)
            }
            .toMap()

        val tomorrowPrayers = tomorrowPrayerTimes?.getAllPrayers()
            ?.map { (name, timeStr) ->
                name to LocalTime.parse(timeStr, timeFormatter)
            }
            ?.toMap()

        val nextPrayerInfo = getNextPrayerUseCase(todayPrayerTimes) ?: return null

        val currentPeriod = computeCurrentPeriod(now, prayers, tomorrowPrayers)
        val remainingToEnd = if (currentPeriod.endTime.isBefore(now)) {
            val toMidnight = Duration.between(now, LocalTime.MAX).plusMinutes(1)
            val fromMidnight = Duration.between(LocalTime.MIN, currentPeriod.endTime)
            toMidnight.plus(fromMidnight)
        } else {
            Duration.between(now, currentPeriod.endTime)
        }.toMinutes().coerceAtLeast(0)

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
        prayers: Map<PrayerName, LocalTime>,
        tomorrowPrayers: Map<PrayerName, LocalTime>?
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
                val endTime = when {
                    !isEarly -> gunes
                    now < imsak -> imsak
                    else -> tomorrowPrayers?.get(PrayerName.IMSAK) ?: LocalTime.MAX
                }
                PrayerPeriod(
                    prayerName = PrayerName.YATSI,
                    periodType = if (isEarly) PeriodType.YATSI else PeriodType.SABAH,
                    startTime = if (isEarly) yatsi else imsak,
                    endTime = endTime
                )
            }
            now in gunes..ogle -> PrayerPeriod(
                prayerName = PrayerName.GUNES,
                periodType = PeriodType.KUSLUK,
                startTime = gunes,
                endTime = ogle
            )
            now in ogle..ikindi -> PrayerPeriod(
                prayerName = PrayerName.OGLE,
                periodType = PeriodType.OGLE,
                startTime = ogle,
                endTime = ikindi
            )
            now in ikindi..aksam -> PrayerPeriod(
                prayerName = PrayerName.IKINDI,
                periodType = PeriodType.IKINDI,
                startTime = ikindi,
                endTime = aksam
            )
            now in aksam..yatsi -> PrayerPeriod(
                prayerName = PrayerName.AKSAM,
                periodType = PeriodType.AKSAM,
                startTime = aksam,
                endTime = yatsi
            )
            else -> PrayerPeriod(
                prayerName = PrayerName.YATSI,
                periodType = PeriodType.YATSI,
                startTime = yatsi,
                endTime = tomorrowPrayers?.get(PrayerName.IMSAK) ?: LocalTime.MAX
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
                prayerName = PrayerName.GUNES,
                start = gunes,
                end = gunes.plusMinutes(45)
            ),
            KerahatPeriod(
                prayerName = PrayerName.OGLE,
                start = ogle.minusMinutes(45),
                end = ogle
            ),
            KerahatPeriod(
                prayerName = PrayerName.AKSAM,
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
            prayerName = current.prayerName,
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
            prayerName = next.prayerName,
            startTime = next.start,
            endTime = next.end,
            remainingToStartMinutes = Duration.between(now, next.start).toMinutes().coerceAtLeast(0),
            remainingToEndMinutes = Duration.between(now, next.end).toMinutes().coerceAtLeast(0)
        )
    }

    private data class KerahatPeriod(
        val prayerName: PrayerName,
        val start: LocalTime,
        val end: LocalTime
    )
}
