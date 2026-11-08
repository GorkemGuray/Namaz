package co.gorkem.namaz.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import co.gorkem.namaz.BuildConfig
import androidx.glance.appwidget.updateAll
import co.gorkem.namaz.util.alarmManager
import co.gorkem.namaz.util.LocationMath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

enum class AlarmStrategy(val allowsWhileIdle: Boolean) {
    EXACT_ALLOW_WHILE_IDLE(true),
    INEXACT_ALLOW_WHILE_IDLE(true)
}

fun resolveAlarmStrategy(
    apiLevel: Int,
    canScheduleExact: Boolean,
    exactAttemptFailed: Boolean
): AlarmStrategy {
    if (exactAttemptFailed) return AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE
    if (apiLevel < Build.VERSION_CODES.S) return AlarmStrategy.EXACT_ALLOW_WHILE_IDLE
    if (!canScheduleExact) return AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE
    return AlarmStrategy.EXACT_ALLOW_WHILE_IDLE
}

fun nextMinuteBoundary(nowMillis: Long): Long {
    return ((nowMillis / 60000) + 1) * 60000
}

class WidgetMinuteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val hasWidgets = hasActiveWidgets(context)
        if (!hasWidgets) {
            cancelMinuteUpdates(context)
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                NamazWidget().updateAll(context)
                checkLocationChange(context)
            } catch (e: Exception) {
                Log.e(TAG, "Widget update failed", e)
            } finally {
                pendingResult.finish()
            }
        }

        scheduleNextMinute(context)
    }

    companion object {
        private const val TAG = "WidgetMinuteReceiver"
        private const val ACTION_WIDGET_TICK = "co.gorkem.namaz.WIDGET_MINUTE_TICK"
        private const val GET_CURRENT_LOCATION_BACKOFF_MS = 5 * 60 * 1000L
        @Volatile private var lastGetCurrentLocationFailMs: Long = 0
        
        @android.annotation.SuppressLint("MissingPermission")
        private suspend fun checkLocationChange(context: Context) {
            try {
                val prefsRepo = co.gorkem.namaz.data.local.datastore.UserPreferencesRepositoryImpl(context.applicationContext)
                val prefs = prefsRepo.preferences.first()
                
                if (prefs.manualCityName.isNotEmpty()) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Manual city set, skipping location check")
                    return
                }

                val fusedLocationClient = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(context)
                val locationTracker = co.gorkem.namaz.service.LocationTracker(fusedLocationClient, context)
                if (!locationTracker.hasLocationPermission()) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "No location permission, skipping location check")
                    return
                }

                if (prefs.lastAutoLatitude == 0.0 && prefs.lastAutoLongitude == 0.0) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Saved auto-location uninitialized, triggering worker")
                    co.gorkem.namaz.service.LocationSyncWorker.executeNow(context)
                    return
                }

                var currentLocation: android.location.Location? = suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                        continuation.resume(loc)
                    }.addOnFailureListener {
                        continuation.resume(null)
                    }
                }

                if (currentLocation == null) {
                    val nowMs = System.currentTimeMillis()
                    if (nowMs - lastGetCurrentLocationFailMs >= GET_CURRENT_LOCATION_BACKOFF_MS) {
                        if (BuildConfig.DEBUG) Log.d(TAG, "lastLocation is null, falling back to getCurrentLocation")
                        currentLocation = try {
                            kotlinx.coroutines.withTimeout(25_000L) {
                                suspendCancellableCoroutine<android.location.Location?> { cont ->
                                    fusedLocationClient.getCurrentLocation(
                                        com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                                        null
                                    ).addOnSuccessListener { loc -> cont.resume(loc) }
                                     .addOnFailureListener { cont.resume(null) }
                                     .addOnCanceledListener { cont.resume(null) }
                                }
                            }
                        } catch (_: kotlinx.coroutines.TimeoutCancellationException) {
                            Log.w(TAG, "getCurrentLocation timed out after 25s")
                            null
                        }

                        if (currentLocation == null) {
                            lastGetCurrentLocationFailMs = nowMs
                            Log.w(TAG, "getCurrentLocation failed, backing off for 5 minutes")
                        }
                    } else {
                        if (BuildConfig.DEBUG) Log.d(TAG, "Still in getCurrentLocation backoff (${(GET_CURRENT_LOCATION_BACKOFF_MS - (nowMs - lastGetCurrentLocationFailMs)) / 1000}s remaining), skipping")
                    }
                }
                
                if (currentLocation != null) {
                    val isSignificant = LocationMath.isDistanceSignificant(
                        lat1 = currentLocation.latitude,
                        lon1 = currentLocation.longitude,
                        lat2 = prefs.lastAutoLatitude,
                        lon2 = prefs.lastAutoLongitude,
                        thresholdMeters = 10000.0
                    )
                    
                    if (isSignificant) {
                        Log.i(TAG, "Location changed >10km: (${prefs.lastAutoLatitude},${prefs.lastAutoLongitude}) → (${currentLocation.latitude},${currentLocation.longitude}), triggering worker")
                        co.gorkem.namaz.service.LocationSyncWorker.executeNow(context, currentLocation.latitude, currentLocation.longitude)
                    } else {
                        if (BuildConfig.DEBUG) Log.d(TAG, "Location within 10km, skipping")
                    }
                } else {
                    Log.w(TAG, "Could not obtain location (both lastLocation and getCurrentLocation failed), skipping")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check location change", e)
            }
        }
        private const val REQUEST_CODE = 9001

        fun scheduleNextMinute(context: Context) {
            val alarmManager = context.alarmManager ?: return

            cancelMinuteUpdates(context)

            val triggerIntent = Intent(context, WidgetMinuteReceiver::class.java).apply {
                action = ACTION_WIDGET_TICK
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                triggerIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val nextMinute = nextMinuteBoundary(System.currentTimeMillis())

            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    alarmManager.canScheduleExactAlarms()
                } catch (e: Exception) {
                    false
                }
            } else {
                true
            }

            val firstAttempt = resolveAlarmStrategy(
                apiLevel = Build.VERSION.SDK_INT,
                canScheduleExact = canScheduleExact,
                exactAttemptFailed = false
            )

            if (scheduleWithStrategy(alarmManager, firstAttempt, nextMinute, pendingIntent)) return

            if (firstAttempt != AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE) {
                val fallback = resolveAlarmStrategy(
                    apiLevel = Build.VERSION.SDK_INT,
                    canScheduleExact = canScheduleExact,
                    exactAttemptFailed = true
                )
                Log.w(TAG, "Exact alarm scheduling failed, degrading to ${fallback.name}")
                if (scheduleWithStrategy(alarmManager, fallback, nextMinute, pendingIntent)) return
            }

            alarmManager.set(AlarmManager.RTC, nextMinute, pendingIntent)
        }

        @SuppressLint("MissingPermission")
        private fun scheduleWithStrategy(
            alarmManager: AlarmManager,
            strategy: AlarmStrategy,
            triggerAtMillis: Long,
            pendingIntent: PendingIntent
        ): Boolean {
            return try {
                when (strategy) {
                    AlarmStrategy.EXACT_ALLOW_WHILE_IDLE ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, triggerAtMillis, pendingIntent)
                        } else {
                            alarmManager.setExact(AlarmManager.RTC, triggerAtMillis, pendingIntent)
                        }

                    AlarmStrategy.INEXACT_ALLOW_WHILE_IDLE ->
                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC, triggerAtMillis, pendingIntent)
                }
                true
            } catch (e: SecurityException) {
                Log.w(TAG, "${strategy.name} rejected, SecurityException", e)
                false
            } catch (e: Exception) {
                Log.e(TAG, "${strategy.name} failed", e)
                false
            }
        }

        fun cancelMinuteUpdates(context: Context) {
            val alarmManager = context.alarmManager ?: return
            val triggerIntent = Intent(context, WidgetMinuteReceiver::class.java).apply {
                action = ACTION_WIDGET_TICK
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                triggerIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        internal fun hasActiveWidgets(context: Context): Boolean {
            val manager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, NamazWidgetReceiver::class.java)
            return manager.getAppWidgetIds(componentName).isNotEmpty()
        }
    }
}
