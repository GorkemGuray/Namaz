package co.gorkem.namaz.wear.data

import co.gorkem.namaz.wear.data.remote.api.AladhanApiService
import co.gorkem.namaz.wear.data.remote.dto.CalendarResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearPrayerRepository @Inject constructor(
    private val apiService: AladhanApiService
) {
    fun getPrayerTimesFromApi(
        latitude: Double,
        longitude: Double,
        method: Int = 13,
        school: Int = 1
    ): Flow<Result<List<WearPrayerItem>>> = flow {
        try {
            val today = LocalDate.now()
            val response = apiService.getPrayerTimes(
                latitude = latitude,
                longitude = longitude,
                method = method,
                school = school,
                month = today.monthValue,
                year = today.year
            )
            if (response.code == 200 && response.data.isNotEmpty()) {
                val todayData = response.data.first()
                val prayers = listOf(
                    WearPrayerItem("IMSAK", cleanTime(todayData.timings.imsak)),
                    WearPrayerItem("GUNES", cleanTime(todayData.timings.sunrise)),
                    WearPrayerItem("OGLE", cleanTime(todayData.timings.dhuhr)),
                    WearPrayerItem("IKINDI", cleanTime(todayData.timings.asr)),
                    WearPrayerItem("AKSAM", cleanTime(todayData.timings.maghrib)),
                    WearPrayerItem("YATSI", cleanTime(todayData.timings.isha))
                )
                emit(Result.success(prayers))
            } else {
                emit(Result.failure(Exception("API error: ${response.code}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    private fun cleanTime(time: String): String {
        return time.replace(Regex("\\s*\\(.*\\)\\s*"), "").trim()
    }
}
