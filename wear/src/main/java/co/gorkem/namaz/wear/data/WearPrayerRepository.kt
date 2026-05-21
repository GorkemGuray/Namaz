package co.gorkem.namaz.wear.data

import co.gorkem.namaz.wear.data.remote.api.ImsakiyemApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearPrayerRepository @Inject constructor(
    private val apiService: ImsakiyemApiService
) {

    fun getPrayerTimesFromApi(
        districtId: String = DEFAULT_DISTRICT_ID
    ): Flow<Result<List<WearPrayerItem>>> = flow {
        try {
            val response = apiService.getMonthlyPrayerTimes(districtId)
            if (!response.success || response.data.isEmpty()) {
                emit(Result.failure(Exception("API error: ${response.code}")))
                return@flow
            }
            val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val todayKey = LocalDate.now().format(dateFormatter)
            val todayData = response.data.find {
                val apiDate = LocalDate.parse(it.date.substring(0, 10), DateTimeFormatter.ISO_LOCAL_DATE)
                apiDate.format(dateFormatter) == todayKey
            } ?: response.data.first()

            val prayers = listOf(
                WearPrayerItem("IMSAK", todayData.times.imsak),
                WearPrayerItem("GUNES", todayData.times.gunes),
                WearPrayerItem("OGLE", todayData.times.ogle),
                WearPrayerItem("IKINDI", todayData.times.ikindi),
                WearPrayerItem("AKSAM", todayData.times.aksam),
                WearPrayerItem("YATSI", todayData.times.yatsi)
            )
            emit(Result.success(prayers))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    companion object {
        const val DEFAULT_DISTRICT_ID = "9548"
    }
}
