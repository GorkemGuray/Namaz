package co.gorkem.namaz.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import co.gorkem.namaz.MainActivity
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.PrayerName

object NotificationHelper {
    const val CHANNEL_ID = "namaz_prayer_times"
    const val VOICE_CHANNEL_ID = "namaz_prayer_times_voice"
    const val TEST_NOTIFICATION_ID = 8888

    fun resolveNotificationChannelId(prayerVoiceEnabled: Boolean): String {
        return if (prayerVoiceEnabled) VOICE_CHANNEL_ID else CHANNEL_ID
    }

    fun getEzanSoundUriString(packageName: String): String {
        return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://$packageName/${R.raw.ezan}"
    }

    fun getEzanSoundUri(context: Context): Uri {
        return Uri.parse(getEzanSoundUriString(context.packageName))
    }

    fun getChannelSoundUriString(packageName: String, channelId: String): String? {
        return if (channelId == VOICE_CHANNEL_ID) {
            getEzanSoundUriString(packageName)
        } else {
            null
        }
    }

    fun getSoundUriString(packageName: String, voiceEnabled: Boolean): String? {
        return if (voiceEnabled) getEzanSoundUriString(packageName) else null
    }

    fun getNotificationSoundUri(context: Context, voiceEnabled: Boolean): Uri? {
        return if (voiceEnabled) getEzanSoundUri(context) else null
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_desc)
                setShowBadge(true)
                setSound(null, null)
            }
            notificationManager.createNotificationChannel(channel)

            // Re-create voice channel if it previously existed without sound
            val existingVoiceChannel = notificationManager.getNotificationChannel(VOICE_CHANNEL_ID)
            if (existingVoiceChannel != null && existingVoiceChannel.sound == null) {
                notificationManager.deleteNotificationChannel(VOICE_CHANNEL_ID)
            }

            val soundUri = getEzanSoundUri(context)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            val voiceChannel = NotificationChannel(
                VOICE_CHANNEL_ID,
                context.getString(R.string.notification_channel_prayer_voice_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_prayer_voice_desc)
                setShowBadge(true)
                setSound(soundUri, audioAttributes)
            }
            notificationManager.createNotificationChannel(voiceChannel)
        }
    }

    fun showPrayerNotification(
        context: Context,
        prayerName: PrayerName,
        prayerTime: String,
        cityName: String,
        notificationId: Int,
        voiceEnabled: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val localizedPrayerName = getNotificationPrayerName(context, prayerName)
        val title = context.getString(R.string.notification_title_format, localizedPrayerName)
        val text = context.getString(R.string.notification_text_format, prayerTime, cityName)
        val bigText = context.getString(R.string.notification_big_text_format, localizedPrayerName, prayerTime, cityName)

        val prayerColor = getPrayerColor(prayerName)
        val largeIconBitmap = getBitmapFromVectorDrawable(context, getIconResId(prayerName), prayerColor)

        val channelId = resolveNotificationChannelId(voiceEnabled)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(getIconResId(prayerName))
            .setLargeIcon(largeIconBitmap)
            .setColor(prayerColor)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setShowWhen(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .apply {
                if (voiceEnabled) {
                    // Gradual 10-second volume ramp is played via PrayerAudioPlayer
                    setSilent(true)
                }
            }
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun showTestPrayerNotification(context: Context) {
        showPrayerNotification(
            context = context,
            prayerName = PrayerName.OGLE,
            prayerTime = "13:05",
            cityName = context.getString(R.string.test_notification_city),
            notificationId = TEST_NOTIFICATION_ID,
            voiceEnabled = true
        )
    }

    fun showSpecialDayNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = 9999
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setShowWhen(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    private fun getNotificationPrayerName(context: Context, prayerName: PrayerName): String {
        val resId = when (prayerName) {
            PrayerName.IMSAK -> R.string.notification_prayer_imsak
            PrayerName.GUNES -> R.string.notification_prayer_gunes
            PrayerName.OGLE -> R.string.notification_prayer_ogle
            PrayerName.IKINDI -> R.string.notification_prayer_ikindi
            PrayerName.AKSAM -> R.string.notification_prayer_aksam
            PrayerName.YATSI -> R.string.notification_prayer_yatsi
        }
        return context.getString(resId)
    }

    private fun getPrayerColor(prayerName: PrayerName): Int {
        return when (prayerName) {
            PrayerName.IMSAK -> android.graphics.Color.parseColor("#1A3A5C")
            PrayerName.GUNES -> android.graphics.Color.parseColor("#D48820")
            PrayerName.OGLE -> android.graphics.Color.parseColor("#C4A000")
            PrayerName.IKINDI -> android.graphics.Color.parseColor("#B86030")
            PrayerName.AKSAM -> android.graphics.Color.parseColor("#9C3A3A")
            PrayerName.YATSI -> android.graphics.Color.parseColor("#3A2A6A")
        }
    }

    private fun getIconResId(prayerName: PrayerName): Int {
        return when (prayerName) {
            PrayerName.IMSAK -> R.drawable.ic_prayer_imsak
            PrayerName.GUNES -> R.drawable.ic_prayer_gunes
            PrayerName.OGLE -> R.drawable.ic_prayer_ogle
            PrayerName.IKINDI -> R.drawable.ic_prayer_ikindi
            PrayerName.AKSAM -> R.drawable.ic_prayer_aksam
            PrayerName.YATSI -> R.drawable.ic_prayer_yatsi
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int, bgColor: Int): android.graphics.Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        
        val size = 120
        val bitmap = android.graphics.Bitmap.createBitmap(size, size, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
        }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        
        val padding = 28
        drawable.setBounds(padding, padding, size - padding, size - padding)
        drawable.draw(canvas)
        
        return bitmap
    }
}
