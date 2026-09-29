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
            entryPoint.dataLayerService.getLatestPrayerData()
        }

        val resolved = co.gorkem.namaz.wear.tile.resolveDisplayData(
            live = data,
            cached = data,
            nowMillis = System.currentTimeMillis()
        )

        if (resolved.source == co.gorkem.namaz.wear.tile.DisplaySource.EMPTY) {
            co.gorkem.namaz.wear.scheduleComplicationUpdateAlarm(this, System.currentTimeMillis() + 5 * 60 * 1000L)
            return buildLoadingData(request.complicationType)
        }

        val tapIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return buildData(request.complicationType, resolved, tapIntent) ?: buildLoadingData(request.complicationType)
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {}
    override fun onComplicationDeactivated(complicationInstanceId: Int) {}

    private fun buildLoadingData(type: ComplicationType): ComplicationData {
        val loading = PlainComplicationText.Builder(text = getString(R.string.wear_loading)).build()
        val titleText = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_times)).build()
        val contentDesc = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_time)).build()

        return when (type) {
            ComplicationType.SHORT_TEXT -> ShortTextComplicationData.Builder(
                text = loading,
                contentDescription = contentDesc
            ).setTitle(titleText).build()

            ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                text = loading,
                contentDescription = contentDesc
            ).setTitle(titleText).build()

            else -> ShortTextComplicationData.Builder(
                text = loading,
                contentDescription = contentDesc
            ).setTitle(titleText).build()
        }
    }

    private fun buildData(
        type: ComplicationType,
        resolved: co.gorkem.namaz.wear.tile.ResolvedDisplay,
        tapAction: PendingIntent
    ): ComplicationData? {
        val data = resolved.data
        val prayers = data?.prayers ?: emptyList()
        val nextPrayer = if (prayers.isNotEmpty()) {
            co.gorkem.namaz.wear.calculateNextPrayer(prayers)
        } else {
            data?.nextPrayer
        } ?: return null

        co.gorkem.namaz.wear.scheduleComplicationUpdateAlarm(this, nextPrayer.targetTimeMillis)

        val targetInstant = Instant.ofEpochMilli(nextPrayer.targetTimeMillis)
        val timeDiffText = TimeDifferenceComplicationText.Builder(
            style = TimeDifferenceStyle.STOPWATCH,
            countDownTimeReference = androidx.wear.watchface.complications.data.CountDownTimeReference(targetInstant)
        ).setText("-^1").build()

        val prayerLabel = prayerNameFromEnum(this, co.gorkem.namaz.wear.getPreviousPrayer(nextPrayer.name))
        val titleText = PlainComplicationText.Builder(
            text = if (resolved.source == co.gorkem.namaz.wear.tile.DisplaySource.CACHE) {
                "$prayerLabel · ${getString(R.string.wear_standalone_mode)}"
            } else {
                prayerLabel
            }
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
