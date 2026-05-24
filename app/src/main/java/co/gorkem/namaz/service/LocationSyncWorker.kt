package co.gorkem.namaz.service

import android.content.Context
import android.location.Location
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.local.datastore.getLocationInfo
import co.gorkem.namaz.data.local.room.PrayerTimeDao
import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@HiltWorker
class LocationSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val locationTracker: LocationTracker,
    private val preferencesRepository: UserPreferencesRepository,
    private val prayerTimeRepository: PrayerTimeRepositoryImpl,
    private val prayerTimeDao: PrayerTimeDao,
    private val notificationScheduler: PrayerNotificationScheduler,
    private val geocoderHelper: GeocoderHelper
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = preferencesRepository.preferences.first()
            var currentLocationInfo = prefs.getLocationInfo()
            var locationChanged = false

            // Step 1: Location Check
            if (locationTracker.hasLocationPermission() && prefs.manualCityName.isEmpty()) {
                val freshLocation: Location? = try {
                    suspendCancellableCoroutine { continuation ->
                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                            .addOnSuccessListener { location ->
                                continuation.resume(location)
                            }
                            .addOnFailureListener {
                                continuation.resume(null)
                            }
                            .addOnCanceledListener {
                                continuation.resume(null)
                            }
                    }
                } catch (e: Exception) {
                    null
                }

                if (freshLocation != null) {
                    val savedLocation = Location("").apply {
                        latitude = currentLocationInfo.latitude
                        longitude = currentLocationInfo.longitude
                    }

                    val distance = freshLocation.distanceTo(savedLocation)
                    if (distance > 2000f || currentLocationInfo.latitude == 0.0) {
                        locationChanged = true
                        
                        val reverseGeocoded = geocoderHelper.getLocationInfo(freshLocation.latitude, freshLocation.longitude)
                        if (reverseGeocoded != null) {
                            val districtId = prayerTimeRepository.findDistrictId(reverseGeocoded.cityName, reverseGeocoded.districtName) ?: ""
                            
                            preferencesRepository.setLastAutoLocation(
                                name = reverseGeocoded.cityName,
                                districtName = reverseGeocoded.districtName,
                                latitude = freshLocation.latitude,
                                longitude = freshLocation.longitude,
                                districtId = districtId,
                                timezone = reverseGeocoded.timezone
                            )
                            currentLocationInfo = preferencesRepository.preferences.first().getLocationInfo()
                        }
                    }
                }
            }

            // Step 2: Completeness Check
            val today = LocalDate.now()
            val activeDataSource = prayerTimeRepository.getActiveDataSource()
            val daysInMonth = today.lengthOfMonth()
            
            if (!locationChanged) {
                val recordCount = prayerTimeDao.getMonthRecordCount(
                    currentLocationInfo.cityName,
                    today.monthValue,
                    today.year,
                    prefs.calculationMethod,
                    prefs.school,
                    activeDataSource
                )
                if (recordCount >= daysInMonth) {
                    return Result.success() // TERMINATE THE WORKER IMMEDIATELY
                }
            }

            // Step 3: Conditional Refresh/Fetch
            val result = prayerTimeRepository.refreshPrayerTimes(
                location = currentLocationInfo,
                month = today.monthValue,
                year = today.year,
                method = prefs.calculationMethod,
                school = prefs.school
            )

            if (result.isSuccess) {
                prayerTimeRepository.cleanupOldData(30)

                if (prefs.notificationsEnabled) {
                    val prayerTimes = result.getOrThrow()
                    notificationScheduler.cancelAllNotifications()
                    notificationScheduler.schedulePrayerNotifications(
                        prayerTimes = prayerTimes,
                        minutesBefore = prefs.notificationMinutesBefore,
                        cityName = currentLocationInfo.cityName
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val PERIODIC_WORK_NAME = "location_sync_worker_periodic"
        private const val ON_DEMAND_WORK_NAME = "location_sync_worker_ondemand"

        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            val request = PeriodicWorkRequestBuilder<LocationSyncWorker>(
                repeatInterval = 12,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        fun executeNow(context: Context) {
            val request = OneTimeWorkRequestBuilder<LocationSyncWorker>().build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                ON_DEMAND_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
