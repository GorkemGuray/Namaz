package co.gorkem.namaz.wear.data

import co.gorkem.namaz.core.AladhanApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearPrayerRepository @Inject constructor(
    private val aladhanApiService: AladhanApiService
) {

    fun getPrayerTimesFromApi(
        latitude: Double = DEFAULT_LATITUDE,
        longitude: Double = DEFAULT_LONGITUDE,
        method: Int = 13,
        school: Int = 1
    ): Flow<Result<List<WearPrayerItem>>> = flow {
        try {
            val today = LocalDate.now()
            val response = aladhanApiService.getMonthlyPrayerTimes(
                year = today.year,
                month = today.monthValue,
                latitude = latitude,
                longitude = longitude,
                method = method,
                school = school
            )
            if (response.code != 200 || response.data.isEmpty()) {
                emit(Result.failure(Exception("API error: ${response.code}")))
                return@flow
            }
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val todayKey = today.format(dateFormatter)
            val todayData = response.data.find {
                it.date.gregorian.date == todayKey
            } ?: response.data.first()

            val prayers = listOf(
                WearPrayerItem("IMSAK", cleanTime(todayData.timings.fajr)),
                WearPrayerItem("GUNES", cleanTime(todayData.timings.sunrise)),
                WearPrayerItem("OGLE", cleanTime(todayData.timings.dhuhr)),
                WearPrayerItem("IKINDI", cleanTime(todayData.timings.asr)),
                WearPrayerItem("AKSAM", cleanTime(todayData.timings.maghrib)),
                WearPrayerItem("YATSI", cleanTime(todayData.timings.isha))
            )
            emit(Result.success(prayers))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun cleanTime(timeStr: String): String {
        return timeStr.substringBefore(" ").trim()
    }

    companion object {
        const val DEFAULT_LATITUDE = 41.0082
        const val DEFAULT_LONGITUDE = 28.9784
    }
}
