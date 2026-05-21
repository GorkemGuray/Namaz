package co.gorkem.namaz.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.local.room.PrayerTimeDao
import co.gorkem.namaz.data.local.room.PrayerTimeEntity
import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.data.remote.api.AladhanApiService
import co.gorkem.namaz.domain.calculator.CalculatedPrayerTimes
import co.gorkem.namaz.domain.calculator.PrayerTimeCalculator
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.repository.PrayerTimeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerTimeRepositoryImpl @Inject constructor(
    private val apiService: AladhanApiService,
    private val prayerTimeDao: PrayerTimeDao,
    private val calculator: PrayerTimeCalculator,
    private val preferencesRepository: UserPreferencesRepository,
    @ApplicationContext private val context: Context
) : PrayerTimeRepository {

    private suspend fun shouldUseLocalCalculation(): Boolean {
        val prefs = preferencesRepository.preferences.first()
        if (prefs.useLocalCalculation) return true
        return !isNetworkAvailable()
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(ConnectivityManager::class.java) ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun getPrayerTimes(
        location: LocationInfo,
        month: Int,
        year: Int,
        method: Int,
        school: Int
    ): Flow<NetworkResult<List<PrayerTime>>> = flow {
        val cached = prayerTimeDao.getPrayerTimesForMonth(location.cityName, month, year, method, school)

        if (cached.isEmpty()) {
            emit(NetworkResult.Loading)
        } else {
            emit(NetworkResult.Success(cached.map { it.toDomainModel() }))
        }

        try {
            val useLocal = shouldUseLocalCalculation()
            val prayerTimes = if (useLocal) {
                calculateLocally(location, month, year, method, school)
            } else {
                fetchFromApi(location, month, year, method, school)
            }

            if (prayerTimes.isNotEmpty()) {
                prayerTimeDao.insertAll(prayerTimes.map {
                    it.toEntity(location.cityName, month, year, method, school)
                })
                emit(NetworkResult.Success(prayerTimes))
            } else if (cached.isEmpty()) {
                emit(NetworkResult.Error("Vakitler alınamadı"))
            }
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                val fallbackTimes = calculateLocally(location, month, year, method, school)
                if (fallbackTimes.isNotEmpty()) {
                    prayerTimeDao.insertAll(fallbackTimes.map {
                        it.toEntity(location.cityName, month, year, method, school)
                    })
                    emit(NetworkResult.Success(fallbackTimes))
                } else {
                    emit(NetworkResult.Error(e.message ?: "Bilinmeyen hata"))
                }
            }
        }
    }

    private fun calculateLocally(
        location: LocationInfo,
        month: Int,
        year: Int,
        method: Int,
        school: Int
    ): List<PrayerTime> {
        val timezone = try {
            ZoneId.of(location.timezone)
        } catch (_: Exception) {
            ZoneId.of("Europe/Istanbul")
        }
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()
        return (1..daysInMonth).map { day ->
            val date = LocalDate.of(year, month, day)
            val times = calculator.calculate(date, location.latitude, location.longitude, timezone)

            PrayerTime(
                date = date.format(dateFormatter),
                gregorianDate = "",
                hijriDay = "",
                hijriMonthNumber = 0,
                hijriYear = "",
                imsak = minutesToTimeString(times.imsak),
                gunes = minutesToTimeString(times.gunes),
                ogle = minutesToTimeString(times.ogle),
                ikindi = minutesToTimeString(times.ikindi),
                aksam = minutesToTimeString(times.aksam),
                yatsi = minutesToTimeString(times.yatsi),
                timezone = location.timezone
            )
        }
    }

    private suspend fun fetchFromApi(
        location: LocationInfo,
        month: Int,
        year: Int,
        method: Int,
        school: Int
    ): List<PrayerTime> {
        val response = apiService.getPrayerTimes(
            latitude = location.latitude,
            longitude = location.longitude,
            method = method,
            school = school,
            month = month,
            year = year
        )
        if (response.code != 200) return emptyList()

        return response.data.map { dayData ->
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
    }

    suspend fun refreshPrayerTimes(location: LocationInfo, month: Int, year: Int, method: Int, school: Int): Result<List<PrayerTime>> {
        return try {
            val useLocal = shouldUseLocalCalculation()
            val prayerTimes = if (useLocal) {
                calculateLocally(location, month, year, method, school)
            } else {
                fetchFromApi(location, month, year, method, school)
            }

            if (prayerTimes.isNotEmpty()) {
                prayerTimeDao.insertAll(prayerTimes.map {
                    it.toEntity(location.cityName, month, year, method, school)
                })
                Result.success(prayerTimes)
            } else {
                val fallbackTimes = calculateLocally(location, month, year, method, school)
                if (fallbackTimes.isNotEmpty()) {
                    prayerTimeDao.insertAll(fallbackTimes.map {
                        it.toEntity(location.cityName, month, year, method, school)
                    })
                    Result.success(fallbackTimes)
                } else {
                    Result.failure(Exception("Vakitler alınamadı"))
                }
            }
        } catch (e: Exception) {
            val fallbackTimes = calculateLocally(location, month, year, method, school)
            if (fallbackTimes.isNotEmpty()) {
                prayerTimeDao.insertAll(fallbackTimes.map {
                    it.toEntity(location.cityName, month, year, method, school)
                })
                Result.success(fallbackTimes)
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun cleanupOldData(olderThanDays: Int = 30) {
        val cutoff = System.currentTimeMillis() - olderThanDays * 24 * 60 * 60 * 1000L
        prayerTimeDao.deleteOlderThan(cutoff)
    }

    private fun cleanTime(time: String): String {
        return time.replace(Regex("\\s*\\(.*\\)\\s*"), "").trim()
    }

    private fun minutesToTimeString(totalMinutes: Int): String {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return String.format("%02d:%02d", hours, minutes)
    }

    private fun PrayerTime.toEntity(cityName: String, month: Int, year: Int, method: Int, school: Int) = PrayerTimeEntity(
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
        cacheMethod = method,
        cacheSchool = school
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
