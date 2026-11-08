package co.gorkem.namaz.service

import android.content.Context
import android.util.Log
import co.gorkem.namaz.BuildConfig
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NamazWidgetUpdater {
    private const val TAG = "NamazWidgetUpdater"

    fun update(context: Context) {
        NamazWidget.clearMemoryCache()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(NamazWidget::class.java)
                if (glanceIds.isNotEmpty()) {
                    val widget = NamazWidget()
                    glanceIds.forEach { glanceId ->
                        widget.update(context, glanceId)
                    }
                    if (BuildConfig.DEBUG) Log.d(TAG, "Widget updated successfully for ${glanceIds.size} instance(s)")
                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "No active widget instances found, skipping update")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update widget", e)
            }
        }
    }
}
