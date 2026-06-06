package co.gorkem.namaz.wear

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import co.gorkem.namaz.wear.complication.ComplicationUpdateReceiver
import co.gorkem.namaz.wear.data.WearNextPrayer
import co.gorkem.namaz.wear.data.WearPrayerItem
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun prayerNameFromEnum(context: Context, name: String): String {
    return when (name) {
        "IMSAK" -> context.getString(R.string.prayer_imsak)
        "GUNES" -> context.getString(R.string.prayer_gunes)
        "OGLE" -> context.getString(R.string.prayer_ogle)
        "IKINDI" -> context.getString(R.string.prayer_ikindi)
        "AKSAM" -> context.getString(R.string.prayer_aksam)
        "YATSI" -> context.getString(R.string.prayer_yatsi)
        else -> name
    }
}

fun getPreviousPrayer(name: String): String {
    return when (name) {
        "IMSAK" -> "YATSI"
        "GUNES" -> "IMSAK"
        "OGLE" -> "GUNES"
        "IKINDI" -> "OGLE"
        "AKSAM" -> "IKINDI"
        "YATSI" -> "AKSAM"
        else -> name
    }
}

fun calculateNextPrayer(
    prayers: List<WearPrayerItem>,
    now: LocalTime = LocalTime.now(),
    today: LocalDate = LocalDate.now()
): WearNextPrayer? {
    if (prayers.isEmpty()) return null
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    
    var nextItem: WearPrayerItem? = null
    for (prayer in prayers) {
        val prayerTime = LocalTime.parse(prayer.time, timeFormatter)
        if (prayerTime.isAfter(now)) {
            nextItem = prayer
            break
        }
    }
    
    // If all prayers are passed, next is Fajr tomorrow
    if (nextItem == null) {
        nextItem = prayers.first()
    }
    
    val nextTime = LocalTime.parse(nextItem.time, timeFormatter)
    val targetDateTime = if (nextItem == prayers.first() && now.isAfter(LocalTime.of(12, 0))) {
        LocalDateTime.of(today.plusDays(1), nextTime)
    } else if (nextTime.isAfter(now)) {
        LocalDateTime.of(today, nextTime)
    } else {
        LocalDateTime.of(today.plusDays(1), nextTime)
    }
    
    val remainingSecs = java.time.Duration.between(LocalDateTime.of(today, now), targetDateTime).seconds.coerceAtLeast(0)
    val remainingMinutes = remainingSecs / 60
    
    return WearNextPrayer(
        name = nextItem.name,
        time = nextItem.time,
        remainingMinutes = remainingMinutes,
        targetTimeMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
}

fun scheduleComplicationUpdateAlarm(context: Context, triggerTimeMillis: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, ComplicationUpdateReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        999,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Cancel any existing alarm before scheduling a new one
    alarmManager.cancel(pendingIntent)

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    } catch (e: Exception) {
        // Fallback to inexact alarm if exact permission is not granted or throws SecurityException
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMillis,
            pendingIntent
        )
    }
}

