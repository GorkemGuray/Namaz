package co.gorkem.namaz.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.data.TimeDifferenceComplicationText
import androidx.wear.watchface.complications.data.TimeDifferenceStyle
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import co.gorkem.namaz.wear.MainActivity
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.di.WearDataEntryPoint
import co.gorkem.namaz.wear.prayerNameFromEnum
import dagger.hilt.EntryPoints
import java.time.Instant

class PrayerCountdownComplicationService : SuspendingComplicationDataSourceService() {

    private val entryPoint by lazy {
        EntryPoints.get(applicationContext, WearDataEntryPoint::class.java)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        val previewTime = Instant.now().plusSeconds(3600) // 1 hour from now
        val timeDiffText = TimeDifferenceComplicationText.Builder(
            style = TimeDifferenceStyle.STOPWATCH,
            countDownTimeReference = androidx.wear.watchface.complications.data.CountDownTimeReference(previewTime)
        ).setText("-^1").build()

        val titleText = PlainComplicationText.Builder(text = getString(R.string.prayer_aksam)).build()
        val contentDesc = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_time)).build()

        return when (type) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = timeDiffText,
                    contentDescription = contentDesc
                )
                .setTitle(titleText)
                .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = timeDiffText,
                    contentDescription = contentDesc
                )
                .setTitle(titleText)
                .build()
            }
            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val data = kotlinx.coroutines.withTimeoutOrNull(2000L) {
            entryPoint.dataLayerService.prayerData.value
        }

        if (data == null) {
            return androidx.wear.watchface.complications.data.NoDataComplicationData()
        }

        val tapIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return buildData(request.complicationType, data, tapIntent) ?: androidx.wear.watchface.complications.data.NoDataComplicationData()
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {}
    override fun onComplicationDeactivated(complicationInstanceId: Int) {}

    private fun buildData(
        type: ComplicationType,
        data: co.gorkem.namaz.wear.data.WearPrayerData?,
        tapAction: PendingIntent
    ): ComplicationData? {
        val nextPrayer = data?.nextPrayer ?: return null

        val targetInstant = Instant.ofEpochMilli(nextPrayer.targetTimeMillis)
        val timeDiffText = TimeDifferenceComplicationText.Builder(
            style = TimeDifferenceStyle.STOPWATCH,
            countDownTimeReference = androidx.wear.watchface.complications.data.CountDownTimeReference(targetInstant)
        ).setText("-^1").build()

        val titleText = PlainComplicationText.Builder(
            text = prayerNameFromEnum(this, co.gorkem.namaz.wear.getPreviousPrayer(nextPrayer.name))
        ).build()

        val contentDesc = PlainComplicationText.Builder(
            text = getString(R.string.wear_prayer_time)
        ).build()

        return when (type) {
            ComplicationType.SHORT_TEXT -> {
                ShortTextComplicationData.Builder(
                    text = timeDiffText,
                    contentDescription = contentDesc
                )
                .setTitle(titleText)
                .setTapAction(tapAction)
                .build()
            }
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = timeDiffText,
                    contentDescription = contentDesc
                )
                .setTitle(titleText)
                .setTapAction(tapAction)
                .build()
            }
            else -> null
        }
    }
}
