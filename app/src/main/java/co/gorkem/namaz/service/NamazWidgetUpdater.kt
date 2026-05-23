package co.gorkem.namaz.service

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NamazWidgetUpdater {
    fun update(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val componentName = ComponentName(context, NamazWidgetReceiver::class.java)
        val ids = manager.getAppWidgetIds(componentName)
        if (ids.isNotEmpty()) {
            CoroutineScope(Dispatchers.Default).launch {
                try {
                    NamazWidget().updateAll(context)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
