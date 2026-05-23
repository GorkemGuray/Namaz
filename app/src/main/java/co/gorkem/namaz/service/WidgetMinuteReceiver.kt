package co.gorkem.namaz.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetMinuteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Minute tick received")

        val hasWidgets = hasActiveWidgets(context)
        if (!hasWidgets) {
            Log.d(TAG, "No active widgets, cancelling")
            cancelMinuteUpdates(context)
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.Default).launch {
            try {
                NamazWidget().updateAll(context)
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
        private const val REQUEST_CODE = 9001

        fun scheduleNextMinute(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val canSchedule = try {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.canScheduleExactAlarms()
                } catch (e: Exception) {
                    false
                }
                if (!canSchedule) {
                    Log.w(TAG, "Exact alarms not permitted, skipping schedule")
                    return
                }
            }

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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

            val nextMinute = ((System.currentTimeMillis() / 60000) + 1) * 60000

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC,
                        nextMinute,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC,
                        nextMinute,
                        pendingIntent
                    )
                }
                Log.d(TAG, "Scheduled next tick at $nextMinute")
            } catch (e: SecurityException) {
                Log.w(TAG, "Cannot schedule exact alarm, falling back to inexact", e)
                alarmManager.set(AlarmManager.RTC, nextMinute, pendingIntent)
            }
        }

        fun cancelMinuteUpdates(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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

        private fun hasActiveWidgets(context: Context): Boolean {
            val manager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, NamazWidgetReceiver::class.java)
            return manager.getAppWidgetIds(componentName).isNotEmpty()
        }
    }
}
