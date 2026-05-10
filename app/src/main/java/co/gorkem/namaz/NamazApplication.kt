package co.gorkem.namaz

import android.app.Application
import android.content.Context
import co.gorkem.namaz.service.NotificationHelper
import co.gorkem.namaz.service.PrayerRefreshWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NamazApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(base))
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        PrayerRefreshWorker.schedule(this)
    }
}
