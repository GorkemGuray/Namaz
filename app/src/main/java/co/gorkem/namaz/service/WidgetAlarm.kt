package co.gorkem.namaz.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import co.gorkem.namaz.util.alarmManager

object WidgetAlarm {
    fun schedulePrayerTransitionAlarm(context: Context, targetEpochMillis: Long) {
        val triggerTime = targetEpochMillis + 1500
        if (triggerTime <= System.currentTimeMillis()) return

        val alarmManager = context.alarmManager ?: return

        val intent = Intent(context, NamazWidgetReceiver::class.java).apply {
            action = NamazWidgetReceiver.ACTION_PRAYER_TIME_TRANSITION
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            9002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)

        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                alarmManager.canScheduleExactAlarms()
            } catch (_: Exception) {
                false
            }
        } else {
            true
        }

        try {
            if (canScheduleExact) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            Log.e("WidgetAlarm", "SecurityException scheduling exact alarm, falling back to inexact", e)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } catch (e2: Exception) {
                Log.e("WidgetAlarm", "Failed to schedule inexact fallback alarm", e2)
            }
        } catch (e: Exception) {
            Log.e("WidgetAlarm", "Failed to schedule transition alarm", e)
        }
    }
}
