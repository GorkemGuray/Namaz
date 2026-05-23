package co.gorkem.namaz.service

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class NamazWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = NamazWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        NamazWidgetWorker.startPeriodicUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        NamazWidgetWorker.stopPeriodicUpdate(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { id ->
            NamazWidgetPrefs.clearWidgetPrefs(context, id)
        }
    }
}
