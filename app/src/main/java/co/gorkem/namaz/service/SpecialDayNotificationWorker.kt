package co.gorkem.namaz.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import co.gorkem.namaz.domain.usecase.GetSpecialDayUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class SpecialDayNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val getSpecialDayUseCase: GetSpecialDayUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now()
        val specialDay = getSpecialDayUseCase(today) ?: return Result.success()

        val title = applicationContext.getString(specialDay.titleRes)
        val message = applicationContext.getString(specialDay.messageRes)

        NotificationHelper.showSpecialDayNotification(
            context = applicationContext,
            title = title,
            message = message,
            notificationId = 9999
        )

        return Result.success()
    }
}
