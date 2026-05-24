package co.gorkem.namaz

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import co.gorkem.namaz.service.NotificationHelper
import co.gorkem.namaz.service.LocationSyncWorker
import co.gorkem.namaz.service.LocationPassiveReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class NamazApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var locationTracker: co.gorkem.namaz.service.LocationTracker

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(base))
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
        LocationSyncWorker.schedulePeriodic(this)
        locationTracker.registerPassiveLocationListener()
    }
}
