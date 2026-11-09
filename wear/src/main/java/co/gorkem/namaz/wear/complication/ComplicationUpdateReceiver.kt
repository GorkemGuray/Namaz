package co.gorkem.namaz.wear.complication

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester

class ComplicationUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        try {
            val requester1 = ComplicationDataSourceUpdateRequester.create(
                context = context,
                complicationDataSourceComponent = ComponentName(context, PrayerComplicationService::class.java)
            )
            requester1.requestUpdateAll()
            
            val requester2 = ComplicationDataSourceUpdateRequester.create(
                context = context,
                complicationDataSourceComponent = ComponentName(context, PrayerCountdownComplicationService::class.java)
            )
            requester2.requestUpdateAll()

            val requester3 = ComplicationDataSourceUpdateRequester.create(
                context = context,
                complicationDataSourceComponent = ComponentName(context, PrayerProgressComplicationService::class.java)
            )
            requester3.requestUpdateAll()

            val requester4 = ComplicationDataSourceUpdateRequester.create(
                context = context,
                complicationDataSourceComponent = ComponentName(context, PrayerNextDetailComplicationService::class.java)
            )
            requester4.requestUpdateAll()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
