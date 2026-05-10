package co.gorkem.namaz.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import co.gorkem.namaz.domain.model.LocationInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@HiltWorker
class PrayerRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val prayerTimeRepository: PrayerTimeRepositoryImpl,
    private val preferencesRepository: UserPreferencesRepository,
    private val notificationScheduler: PrayerNotificationScheduler
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = preferencesRepository.preferences.first()

            val location = if (prefs.manualCityName.isNotEmpty()) {
                LocationInfo(
                    latitude = prefs.manualLatitude,
                    longitude = prefs.manualLongitude,
                    timezone = "Europe/Istanbul",
                    cityName = prefs.manualCityName
                )
            } else {
                return Result.success()
            }

            val today = LocalDate.now()
            val result = prayerTimeRepository.refreshPrayerTimes(
                location = location,
                month = today.monthValue,
                year = today.year
            )

            if (result.isSuccess) {
                prayerTimeRepository.cleanupOldData(30)

                if (prefs.notificationsEnabled) {
                    val prayerTimes = result.getOrThrow()
                    notificationScheduler.cancelAllNotifications()
                    notificationScheduler.schedulePrayerNotifications(
                        prayerTimes = prayerTimes,
                        minutesBefore = prefs.notificationMinutesBefore,
                        cityName = location.cityName
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val WORK_NAME = "prayer_refresh_worker"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(false)
                .build()

            val request = PeriodicWorkRequestBuilder<PrayerRefreshWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
