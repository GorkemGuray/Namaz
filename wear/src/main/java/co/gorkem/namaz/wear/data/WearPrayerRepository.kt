package co.gorkem.namaz.wear.data

import co.gorkem.namaz.core.AladhanApiService
import co.gorkem.namaz.wear.data.remote.api.ImsakiyemApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearPrayerRepository @Inject constructor(
    private val aladhanApiService: AladhanApiService,
    private val imsakiyemApiService: ImsakiyemApiService
) {

    fun loadStandalone(
        latitude: Double = DEFAULT_LATITUDE,
        longitude: Double = DEFAULT_LONGITUDE,
        districtId: String = DEFAULT_DISTRICT_ID,
        method: Int = 13,
        school: Int = 1,
        dataSource: Int = 0
    ): Flow<Result<List<WearPrayerItem>>> = flow {
        val result: Result<List<WearPrayerItem>> = if (dataSource == 1) {
            val targetDistrictId = districtId.ifEmpty { DEFAULT_DISTRICT_ID }
            try {
                val response = imsakiyemApiService.getMonthlyPrayerTimes(targetDistrictId)
                if ((!response.success && response.code != 200) || response.data.isEmpty()) {
                    Result.failure(Exception("Imsakiyem API error: ${response.message.ifEmpty { response.code.toString() }}"))
                } else {
                    val today = LocalDate.now()
                    val todayIso = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    val todayDmy = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    val todayData = response.data.find {
                        it.date.startsWith(todayIso) || it.date == todayDmy
                    } ?: response.data.firstOrNull()

                    if (todayData == null) {
                        Result.failure(Exception("No prayer data for today"))
                    } else {
                        val prayers = listOf(
                            WearPrayerItem("IMSAK", cleanTime(todayData.times.imsak)),
                            WearPrayerItem("GUNES", cleanTime(todayData.times.gunes)),
                            WearPrayerItem("OGLE", cleanTime(todayData.times.ogle)),
                            WearPrayerItem("IKINDI", cleanTime(todayData.times.ikindi)),
                            WearPrayerItem("AKSAM", cleanTime(todayData.times.aksam)),
                            WearPrayerItem("YATSI", cleanTime(todayData.times.yatsi))
                        )
                        Result.success(prayers)
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
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
                    Result.failure(Exception("API error: ${response.code}"))
                } else {
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
                    Result.success(prayers)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        emit(result)
    }

    fun loadStandalone(
        method: Int,
        school: Int,
        dataSource: Int
    ): Flow<Result<List<WearPrayerItem>>> = loadStandalone(
        latitude = DEFAULT_LATITUDE,
        longitude = DEFAULT_LONGITUDE,
        districtId = DEFAULT_DISTRICT_ID,
        method = method,
        school = school,
        dataSource = dataSource
    )

    fun getPrayerTimesFromApi(
        latitude: Double = DEFAULT_LATITUDE,
        longitude: Double = DEFAULT_LONGITUDE,
        method: Int = 13,
        school: Int = 1,
        districtId: String = DEFAULT_DISTRICT_ID,
        dataSource: Int = 0
    ): Flow<Result<List<WearPrayerItem>>> = loadStandalone(
        latitude = latitude,
        longitude = longitude,
        districtId = districtId,
        method = method,
        school = school,
        dataSource = dataSource
    )

    suspend fun findDistrictId(query: String): String? {
        return try {
            val response = imsakiyemApiService.searchDistricts(query)
            if (response.data.isNotEmpty()) {
                response.data.first().id
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun cleanTime(timeStr: String): String {
        return timeStr.substringBefore(" ").trim()
    }

    companion object {
        const val DEFAULT_LATITUDE = 41.0082
        const val DEFAULT_LONGITUDE = 28.9784
        const val DEFAULT_DISTRICT_ID = "9548"
    }
}
