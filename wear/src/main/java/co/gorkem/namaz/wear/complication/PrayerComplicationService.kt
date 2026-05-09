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
import co.gorkem.namaz.wear.di.WearDataEntryPoint
import dagger.hilt.EntryPoints
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class PrayerComplicationService : SuspendingComplicationDataSourceService() {

    private val entryPoint by lazy {
        EntryPoints.get(applicationContext, WearDataEntryPoint::class.java)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = "Namaz").build(),
            contentDescription = PlainComplicationText.Builder(text = "Namaz Vakti").build()
        ).build()
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        val data = runBlocking {
            entryPoint.dataLayerService.prayerData.first()
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
                text = if (nextPrayer != null)
                    "${nextPrayer.turkishName} ${nextPrayer.time}"
                else "--:--"
            ).build(),
            contentDescription = PlainComplicationText.Builder(
                text = "Namaz Vakti"
            ).build()
        )

        return builder
            .setTapAction(tapAction)
            .build()
    }
}
