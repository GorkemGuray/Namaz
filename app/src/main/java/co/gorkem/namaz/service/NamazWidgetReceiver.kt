package co.gorkem.namaz.service

import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NamazWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NamazWidget()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_PRAYER_TIME_TRANSITION) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    val manager = GlanceAppWidgetManager(context)
                    val glanceIds = manager.getGlanceIds(NamazWidget::class.java)
                    if (glanceIds.isNotEmpty()) {
                        val widget = NamazWidget()
                        glanceIds.forEach { glanceId ->
                            widget.update(context, glanceId)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("NamazWidgetReceiver", "Failed to update widgets on transition alarm", e)
                } finally {
                    pendingResult.finish()
                }
            }
        } else {
            super.onReceive(context, intent)
        }
    }

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        WidgetMinuteReceiver.scheduleNextMinute(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        WidgetMinuteReceiver.cancelMinuteUpdates(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { id ->
            NamazWidgetPrefs.clearWidgetPrefs(context, id)
        }
    }

    companion object {
        const val ACTION_PRAYER_TIME_TRANSITION = "co.gorkem.namaz.ACTION_PRAYER_TIME_TRANSITION"
    }
}
