package co.gorkem.namaz.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.local.datastore.activeDataSource
import co.gorkem.namaz.data.local.datastore.getLocationInfo
import co.gorkem.namaz.data.local.room.PrayerTimeDao
import co.gorkem.namaz.data.local.room.toDomainModel
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.model.SpecialDay
import co.gorkem.namaz.domain.usecase.GetSpecialDayUseCase
import co.gorkem.namaz.util.DateTimeUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class PrayerNotificationSchedule(
    val prayerName: PrayerName,
    val prayerTime: String,
    val targetDateTime: ZonedDateTime,
    val delayMillis: Long,
    val uniqueWorkName: String,
    val notificationId: Int
)

data class SpecialDayNotificationSchedule(
    val specialDay: SpecialDay,
    val targetDateTime: ZonedDateTime,
    val delayMillis: Long,
    val uniqueWorkName: String
)

fun resolveSpecialDayNotificationSchedule(
    date: LocalDate,
    specialDay: SpecialDay,
    now: ZonedDateTime
): SpecialDayNotificationSchedule? {
    val target = ZonedDateTime.of(date, LocalTime.of(9, 0), now.zone)
    if (!target.isAfter(now)) return null
    return SpecialDayNotificationSchedule(
        specialDay = specialDay,
        targetDateTime = target,
        delayMillis = Duration.between(now, target).toMillis(),
        uniqueWorkName = "special_day_${date.format(DateTimeUtils.DATE_FORMATTER)}"
    )
}

enum class NotificationReplanOutcome { SCHEDULED, NEEDS_REFRESH, DISABLED }

fun resolveNotificationSchedules(
    prayerTimes: List<PrayerTime>,
    minutesBefore: Int,
    now: ZonedDateTime
): List<PrayerNotificationSchedule> {
    val today = now.toLocalDate()
    return listOf(today, today.plusDays(1)).flatMapIndexed { dayOffset, targetDate ->
        val dayData = prayerTimes.find { it.date == targetDate.format(DateTimeUtils.DATE_FORMATTER) }
            ?: return@flatMapIndexed emptyList()
        dayData.getAllPrayers().mapIndexedNotNull { index, (name, timeStr) ->
            val target = try {
                ZonedDateTime.of(
                    targetDate,
                    LocalTime.parse(timeStr, DateTimeUtils.TIME_FORMATTER),
                    now.zone
                ).minusMinutes(minutesBefore.toLong())
            } catch (e: Exception) {
                null
            } ?: return@mapIndexedNotNull null

            if (!target.isAfter(now)) return@mapIndexedNotNull null

            PrayerNotificationSchedule(
                prayerName = name,
                prayerTime = timeStr,
                targetDateTime = target,
                delayMillis = Duration.between(now, target).toMillis(),
                uniqueWorkName = "prayer_${name.name}_${targetDate.format(DateTimeUtils.DATE_FORMATTER)}",
                notificationId = (dayOffset + 1) * 100 + index
            )
        }
    }
}

@Singleton
class PrayerNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prayerTimeDao: PrayerTimeDao,
    private val preferencesRepository: UserPreferencesRepository,
    private val getSpecialDayUseCase: GetSpecialDayUseCase
) {

    fun schedulePrayerNotifications(
        prayerTimes: List<PrayerTime>,
        minutesBefore: Int,
        cityName: String
    ) {
        resolveNotificationSchedules(prayerTimes, minutesBefore, ZonedDateTime.now())
            .forEach { schedule ->
                scheduleSingleNotification(
                    prayerName = schedule.prayerName,
                    prayerTime = schedule.prayerTime,
                    cityName = cityName,
                    delayMillis = schedule.delayMillis,
                    notificationId = schedule.notificationId,
                    uniqueWorkName = schedule.uniqueWorkName
                )
            }
        scheduleSpecialDayReminders(ZonedDateTime.now())
    }

    suspend fun rescheduleFromCache(cancelExisting: Boolean = true): NotificationReplanOutcome {
        val prefs = preferencesRepository.preferences.first()
        if (!prefs.notificationsEnabled) return NotificationReplanOutcome.DISABLED

        val location = prefs.getLocationInfo()
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val dataSource = prefs.activeDataSource

        val todayRows = prayerTimeDao.getPrayerTimesForMonth(
            cityName = location.cityName,
            month = today.monthValue,
            year = today.year,
            method = prefs.calculationMethod,
            school = prefs.school,
            dataSource = dataSource
        )
        val allRows = if (tomorrow.monthValue != today.monthValue || tomorrow.year != today.year) {
            todayRows + prayerTimeDao.getPrayerTimesForMonth(
                cityName = location.cityName,
                month = tomorrow.monthValue,
                year = tomorrow.year,
                method = prefs.calculationMethod,
                school = prefs.school,
                dataSource = dataSource
            )
        } else {
            todayRows
        }

        if (allRows.isEmpty()) return NotificationReplanOutcome.NEEDS_REFRESH

        if (cancelExisting) {
            cancelAllNotifications()
        }

        schedulePrayerNotifications(
            prayerTimes = allRows.map { it.toDomainModel() },
            minutesBefore = prefs.notificationMinutesBefore,
            cityName = location.cityName
        )

        val tomorrowKey = tomorrow.format(DateTimeUtils.DATE_FORMATTER)
        return if (allRows.any { it.date == tomorrowKey }) {
            NotificationReplanOutcome.SCHEDULED
        } else {
            NotificationReplanOutcome.NEEDS_REFRESH
        }
    }

    fun cancelAllNotifications() {
        WorkManager.getInstance(context).cancelAllWorkByTag("prayer_notification")
        WorkManager.getInstance(context).cancelAllWorkByTag("special_day_notification")
    }

    fun scheduleSpecialDayNotification(
        date: LocalDate,
        specialDay: SpecialDay,
        now: ZonedDateTime = ZonedDateTime.now()
    ) {
        val schedule = resolveSpecialDayNotificationSchedule(date, specialDay, now) ?: return

        val workRequest = OneTimeWorkRequestBuilder<SpecialDayNotificationWorker>()
            .setInitialDelay(schedule.delayMillis, TimeUnit.MILLISECONDS)
            .addTag("special_day_notification")
            .setInputData(
                androidx.work.Data.Builder()
                    .putString("special_day_type", specialDay.type.name)
                    .putString("date", date.format(DateTimeUtils.DATE_FORMATTER))
                    .build()
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            schedule.uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleSpecialDayReminders(now: ZonedDateTime = ZonedDateTime.now()) {
        listOf(now.toLocalDate(), now.toLocalDate().plusDays(1)).forEach { date ->
            val specialDay = getSpecialDayUseCase(date)
            if (specialDay != null) {
                scheduleSpecialDayNotification(date, specialDay, now)
            }
        }
    }

    private fun scheduleSingleNotification(
        prayerName: PrayerName,
        prayerTime: String,
        cityName: String,
        delayMillis: Long,
        notificationId: Int,
        uniqueWorkName: String
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
            uniqueWorkName,
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}
