package co.gorkem.namaz.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.gorkem.namaz.domain.model.PrayerName
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PrayerNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val prayerNameStr = inputData.getString("prayer_name") ?: return Result.failure()
        val prayerTime = inputData.getString("prayer_time") ?: return Result.failure()
        val cityName = inputData.getString("city_name") ?: ""
        val notificationId = inputData.getInt("notification_id", 0)

        val prayerName = try {
            PrayerName.valueOf(prayerNameStr)
        } catch (e: IllegalArgumentException) {
            return Result.failure()
        }

        NotificationHelper.showPrayerNotification(
            context = applicationContext,
            prayerName = prayerName,
            prayerTime = prayerTime,
            cityName = cityName,
            notificationId = notificationId
        )

        return Result.success()
    }
}
