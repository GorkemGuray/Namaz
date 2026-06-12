package co.gorkem.namaz.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class LocationTracker @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) {
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) return null

        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    continuation.resume(location)
                }
                .addOnFailureListener { _ ->
                    continuation.resume(null)
                }
                .addOnCanceledListener {
                    continuation.resume(null)
                }
        }
    }

    suspend fun getLastKnownLocation(): Location? {
        return getCurrentLocation()
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasBackgroundLocationPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            hasLocationPermission()
        }
    }

    fun registerPassiveLocationListener() {
        if (hasLocationPermission()) {
            try {
                val request = com.google.android.gms.location.LocationRequest.Builder(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    900000L // 15 minutes
                )
                    .setMinUpdateDistanceMeters(10000f) // 10km displacement for battery optimization
                    .setMinUpdateIntervalMillis(300000L) // 5 minutes fastest interval
                    .build()
                
                val intent = android.content.Intent(context, LocationPassiveReceiver::class.java)
                val pendingIntent = android.app.PendingIntent.getBroadcast(
                    context, 
                    0, 
                    intent, 
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_MUTABLE
                )
                
                fusedLocationClient.requestLocationUpdates(request, pendingIntent)
            } catch (e: SecurityException) {
                android.util.Log.e("LocationTracker", "SecurityException registering passive location listener", e)
            } catch (e: Exception) {
                android.util.Log.e("LocationTracker", "Failed to register passive location listener", e)
            }
        }
    }
}
