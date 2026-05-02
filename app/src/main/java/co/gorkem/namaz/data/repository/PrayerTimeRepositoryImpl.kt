package co.gorkem.namaz.data.repository

import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.data.remote.api.AladhanApiService
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.repository.PrayerTimeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimeRepositoryImpl @Inject constructor(
    private val apiService: AladhanApiService
) : PrayerTimeRepository {

    override fun getPrayerTimes(
        location: LocationInfo,
        month: Int,
        year: Int
    ): Flow<NetworkResult<List<PrayerTime>>> = flow {
        emit(NetworkResult.Loading)
        try {
            val response = apiService.getPrayerTimes(
                latitude = location.latitude,
                longitude = location.longitude,
                month = month,
                year = year
            )
            if (response.code == 200) {
                val prayerTimes = response.data.map { (_, dayData) ->
                    PrayerTime(
                        date = dayData.date.gregorian.date,
                        gregorianDate = dayData.date.readable,
                        imsak = cleanTime(dayData.timings.imsak),
                        gunes = cleanTime(dayData.timings.sunrise),
                        ogle = cleanTime(dayData.timings.dhuhr),
                        ikindi = cleanTime(dayData.timings.asr),
                        aksam = cleanTime(dayData.timings.maghrib),
                        yatsi = cleanTime(dayData.timings.isha),
                        timezone = dayData.meta.timezone
                    )
                }
                emit(NetworkResult.Success(prayerTimes))
            } else {
                emit(NetworkResult.Error("API error: ${response.status} (${response.code})"))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    private fun cleanTime(time: String): String {
        return time.replace(Regex("\\s*\\(.*\\)\\s*"), "").trim()
    }
}
