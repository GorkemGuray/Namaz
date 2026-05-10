package co.gorkem.namaz.data.repository

import co.gorkem.namaz.data.local.room.PrayerTimeDao
import co.gorkem.namaz.data.local.room.PrayerTimeEntity
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
    private val apiService: AladhanApiService,
    private val prayerTimeDao: PrayerTimeDao
) : PrayerTimeRepository {

    override fun getPrayerTimes(
        location: LocationInfo,
        month: Int,
        year: Int,
        method: Int,
        school: Int
    ): Flow<NetworkResult<List<PrayerTime>>> = flow {
        val cached = prayerTimeDao.getPrayerTimesForMonth(location.cityName, month, year, method)

        if (cached.isEmpty()) {
            emit(NetworkResult.Loading)
        } else {
            emit(NetworkResult.Success(cached.map { it.toDomainModel() }))
        }

        try {
            val response = apiService.getPrayerTimes(
                latitude = location.latitude,
                longitude = location.longitude,
                method = method,
                school = school,
                month = month,
                year = year
            )
            if (response.code == 200) {
                val prayerTimes = response.data.map { dayData ->
                    PrayerTime(
                        date = dayData.date.gregorian.date,
                        gregorianDate = dayData.date.readable,
                        hijriDay = dayData.date.hijri.day,
                        hijriMonthNumber = dayData.date.hijri.month.number,
                        hijriYear = dayData.date.hijri.year,
                        imsak = cleanTime(dayData.timings.imsak),
                        gunes = cleanTime(dayData.timings.sunrise),
                        ogle = cleanTime(dayData.timings.dhuhr),
                        ikindi = cleanTime(dayData.timings.asr),
                        aksam = cleanTime(dayData.timings.maghrib),
                        yatsi = cleanTime(dayData.timings.isha),
                        timezone = dayData.meta.timezone
                    )
                }

                prayerTimeDao.insertAll(prayerTimes.map {
                    it.toEntity(location.cityName, month, year, method)
                })

                emit(NetworkResult.Success(prayerTimes))
            } else {
                if (cached.isEmpty()) {
                    emit(NetworkResult.Error("API error: ${response.status} (${response.code})"))
                }
            }
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(NetworkResult.Error(e.message ?: "Unknown error"))
            }
        }
    }

    suspend fun refreshPrayerTimes(location: LocationInfo, month: Int, year: Int, method: Int, school: Int): Result<List<PrayerTime>> {
        return try {
            val response = apiService.getPrayerTimes(
                latitude = location.latitude,
                longitude = location.longitude,
                method = method,
                school = school,
                month = month,
                year = year
            )
            if (response.code == 200) {
                val prayerTimes = response.data.map { dayData ->
                    PrayerTime(
                        date = dayData.date.gregorian.date,
                        gregorianDate = dayData.date.readable,
                        hijriDay = dayData.date.hijri.day,
                        hijriMonthNumber = dayData.date.hijri.month.number,
                        hijriYear = dayData.date.hijri.year,
                        imsak = cleanTime(dayData.timings.imsak),
                        gunes = cleanTime(dayData.timings.sunrise),
                        ogle = cleanTime(dayData.timings.dhuhr),
                        ikindi = cleanTime(dayData.timings.asr),
                        aksam = cleanTime(dayData.timings.maghrib),
                        yatsi = cleanTime(dayData.timings.isha),
                        timezone = dayData.meta.timezone
                    )
                }
                prayerTimeDao.insertAll(prayerTimes.map {
                    it.toEntity(location.cityName, month, year, method)
                })
                Result.success(prayerTimes)
            } else {
                Result.failure(Exception("API error: ${response.status} (${response.code})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cleanupOldData(olderThanDays: Int = 30) {
        val cutoff = System.currentTimeMillis() - olderThanDays * 24 * 60 * 60 * 1000L
        prayerTimeDao.deleteOlderThan(cutoff)
    }

    private fun cleanTime(time: String): String {
        return time.replace(Regex("\\s*\\(.*\\)\\s*"), "").trim()
    }

    private fun PrayerTime.toEntity(cityName: String, month: Int, year: Int, method: Int) = PrayerTimeEntity(
        date = date,
        cityName = cityName,
        gregorianDate = gregorianDate,
        hijriDay = hijriDay,
        hijriMonthNumber = hijriMonthNumber,
        hijriYear = hijriYear,
        imsak = imsak,
        gunes = gunes,
        ogle = ogle,
        ikindi = ikindi,
        aksam = aksam,
        yatsi = yatsi,
        timezone = timezone,
        cacheMonth = month,
        cacheYear = year,
        cacheMethod = method
    )

    private fun PrayerTimeEntity.toDomainModel() = PrayerTime(
        date = date,
        gregorianDate = gregorianDate,
        hijriDay = hijriDay,
        hijriMonthNumber = hijriMonthNumber,
        hijriYear = hijriYear,
        imsak = imsak,
        gunes = gunes,
        ogle = ogle,
        ikindi = ikindi,
        aksam = aksam,
        yatsi = yatsi,
        timezone = timezone
    )
}
