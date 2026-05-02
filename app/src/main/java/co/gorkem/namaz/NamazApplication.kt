package co.gorkem.namaz

import android.app.Application
import co.gorkem.namaz.service.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NamazApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
