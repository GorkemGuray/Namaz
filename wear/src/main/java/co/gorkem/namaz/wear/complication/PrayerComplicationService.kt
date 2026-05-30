package co.gorkem.namaz.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import co.gorkem.namaz.wear.MainActivity
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.di.WearDataEntryPoint
import co.gorkem.namaz.wear.prayerNameFromEnum
import dagger.hilt.EntryPoints
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PrayerComplicationService : SuspendingComplicationDataSourceService() {

    private val entryPoint by lazy {
        EntryPoints.get(applicationContext, WearDataEntryPoint::class.java)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = "20:28").build(),
            contentDescription = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_time)).build()
        )
        .setTitle(PlainComplicationText.Builder(text = getString(R.string.prayer_aksam)).build())
        .build()
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val data = kotlinx.coroutines.withTimeoutOrNull(2000L) {
            entryPoint.dataLayerService.getLatestPrayerData()
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

        return buildData(data, tapIntent)
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {}
    override fun onComplicationDeactivated(complicationInstanceId: Int) {}

    private fun buildData(
        data: co.gorkem.namaz.wear.data.WearPrayerData?,
        tapAction: PendingIntent
    ): ComplicationData {
        val nextPrayer = data?.nextPrayer

        val builder = ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(
                text = nextPrayer?.time ?: "--:--"
            ).build(),
            contentDescription = PlainComplicationText.Builder(
                text = getString(R.string.wear_prayer_time)
            ).build()
        )

        if (nextPrayer != null) {
            builder.setTitle(
                PlainComplicationText.Builder(
                    text = prayerNameFromEnum(this, nextPrayer.name)
                ).build()
            )
        }

        return builder
            .setTapAction(tapAction)
            .build()
    }
}
