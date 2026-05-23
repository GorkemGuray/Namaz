package co.gorkem.namaz.service

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NamazWidgetUpdater {
    fun update(context: Context) {
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
                e.printStackTrace()
            }
        }
    }
}
