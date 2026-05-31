package co.gorkem.namaz.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.LocationResult

class LocationPassiveReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (LocationResult.hasResult(intent)) {
            val locationResult = LocationResult.extractResult(intent)
            locationResult?.lastLocation?.let { location ->
                // If this is triggered, it means displacement > 2000m occurred.
                LocationSyncWorker.executeNow(context, location.latitude, location.longitude)
            }
        }
    }
}
