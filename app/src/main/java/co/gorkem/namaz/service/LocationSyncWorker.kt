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
import co.gorkem.namaz.util.LocationMath

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
    private val geocoderHelper: GeocoderHelper,
    private val getNextPrayerUseCase: co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase,
    private val getPrayerDashboardUseCase: co.gorkem.namaz.domain.usecase.GetPrayerDashboardUseCase,
    private val wearSyncManager: co.gorkem.namaz.service.WearSyncManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = preferencesRepository.preferences.first()
            var currentLocationInfo = prefs.getLocationInfo()
            var locationChanged = false

            // Step 1: Location Check
            if (locationTracker.hasLocationPermission() && prefs.manualCityName.isEmpty()) {
                val inputLat = inputData.getDouble("lat", Double.NaN)
                val inputLon = inputData.getDouble("lon", Double.NaN)

                var freshLocation: Location? = if (!inputLat.isNaN() && !inputLon.isNaN()) {
                    Location("").apply {
                        latitude = inputLat
                        longitude = inputLon
                    }
                } else {
                    try {
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
                }

                if (freshLocation == null && inputLat.isNaN()) {
                    try {
                        freshLocation = suspendCancellableCoroutine { continuation ->
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { location -> continuation.resume(location) }
                                .addOnFailureListener { continuation.resume(null) }
                        }
                    } catch (e: Exception) {
                        // ignore
                    }
                }

                if (freshLocation != null) {
                    val isSignificant = LocationMath.isDistanceSignificant(
                        lat1 = freshLocation.latitude,
                        lon1 = freshLocation.longitude,
                        lat2 = currentLocationInfo.latitude,
                        lon2 = currentLocationInfo.longitude,
                        thresholdMeters = 10000.0
                    )

                    if (isSignificant) {
                        locationChanged = true
                        
                        val reverseGeocoded = geocoderHelper.getLocationInfo(freshLocation.latitude, freshLocation.longitude)
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
                val prayerTimes = result.getOrThrow()

                if (prefs.notificationsEnabled) {
                    notificationScheduler.cancelAllNotifications()
                    notificationScheduler.schedulePrayerNotifications(
                        prayerTimes = prayerTimes,
                        minutesBefore = prefs.notificationMinutesBefore,
                        cityName = currentLocationInfo.cityName
                    )
                }

                co.gorkem.namaz.service.NamazWidgetUpdater.update(appContext)

                wearSyncManager.syncData(
                    prayerTimes = prayerTimes,
                    location = currentLocationInfo,
                    themeId = prefs.appTheme,
                    getNextPrayerUseCase = getNextPrayerUseCase,
                    getPrayerDashboardUseCase = getPrayerDashboardUseCase
                )
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
                .setRequiresBatteryNotLow(true)
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

        fun executeNow(context: Context, latitude: Double? = null, longitude: Double? = null) {
            val dataBuilder = androidx.work.Data.Builder()
            if (latitude != null && longitude != null) {
                dataBuilder.putDouble("lat", latitude)
                dataBuilder.putDouble("lon", longitude)
            }
            val request = OneTimeWorkRequestBuilder<LocationSyncWorker>()
                .setInputData(dataBuilder.build())
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                ON_DEMAND_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
