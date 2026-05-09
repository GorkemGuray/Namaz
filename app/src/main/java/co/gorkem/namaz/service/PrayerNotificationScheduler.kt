package co.gorkem.namaz.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrayerNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun schedulePrayerNotifications(
        prayerTimes: List<PrayerTime>,
        minutesBefore: Int,
        cityName: String
    ) {
        val today = LocalDate.now()
        val todayData = prayerTimes.find { it.date == today.format(DATE_FORMAT) }
            ?: return

        val prayers = todayData.getAllPrayers()
        val now = ZonedDateTime.now()

        prayers.forEachIndexed { index, (name, timeStr) ->
            val notificationTime = calculateNotificationTime(
                prayerTimeStr = timeStr,
                prayerDateStr = todayData.date,
                minutesBefore = minutesBefore
            ) ?: return@forEachIndexed

            if (notificationTime.isAfter(now)) {
                val delayMillis = java.time.Duration.between(now, notificationTime).toMillis()
                scheduleSingleNotification(
                    prayerName = name,
                    prayerTime = timeStr,
                    cityName = cityName,
                    delayMillis = delayMillis,
                    notificationId = index + 100
                )
            }
        }
    }

    fun cancelAllNotifications() {
        WorkManager.getInstance(context).cancelAllWorkByTag("prayer_notification")
    }

    private fun scheduleSingleNotification(
        prayerName: PrayerName,
        prayerTime: String,
        cityName: String,
        delayMillis: Long,
        notificationId: Int
    ) {
        val workRequest = OneTimeWorkRequestBuilder<PrayerNotificationWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .addTag("prayer_notification")
            .setInputData(
                androidx.work.Data.Builder()
                    .putString("prayer_name", prayerName.name)
                    .putString("prayer_time", prayerTime)
                    .putString("city_name", cityName)
                    .putInt("notification_id", notificationId)
                    .build()
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "prayer_${prayerName.name}_${LocalDate.now()}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun calculateNotificationTime(
        prayerTimeStr: String,
        prayerDateStr: String,
        minutesBefore: Int
    ): ZonedDateTime? {
        return try {
            val time = LocalTime.parse(prayerTimeStr, timeFormatter)
            val date = LocalDate.parse(prayerDateStr, DATE_FORMAT)
            val dateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault())
            dateTime.minusMinutes(minutesBefore.toLong())
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }
}
