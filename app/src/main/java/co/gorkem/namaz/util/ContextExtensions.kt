package co.gorkem.namaz.util

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context

val Context.alarmManager: AlarmManager?
    get() = getSystemService(Context.ALARM_SERVICE) as? AlarmManager

val Context.notificationManager: NotificationManager?
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
