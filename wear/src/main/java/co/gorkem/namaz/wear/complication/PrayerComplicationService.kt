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

        val resolved = co.gorkem.namaz.wear.tile.resolveDisplayData(
            live = data,
            cached = data,
            nowMillis = System.currentTimeMillis()
        )

        if (resolved.source == co.gorkem.namaz.wear.tile.DisplaySource.EMPTY) {
            co.gorkem.namaz.wear.scheduleComplicationUpdateAlarm(this, System.currentTimeMillis() + 5 * 60 * 1000L)
            return buildLoadingData()
        }

        val tapIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return buildData(resolved, tapIntent)
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {}
    override fun onComplicationDeactivated(complicationInstanceId: Int) {}

    private fun buildLoadingData(): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = getString(R.string.wear_loading)).build(),
            contentDescription = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_time)).build()
        )
            .setTitle(PlainComplicationText.Builder(text = getString(R.string.wear_prayer_times)).build())
            .build()
    }

    private fun buildData(
        resolved: co.gorkem.namaz.wear.tile.ResolvedDisplay,
        tapAction: PendingIntent
    ): ComplicationData {
        val data = resolved.data
        val prayers = data?.prayers ?: emptyList()
        val nextPrayer = if (prayers.isNotEmpty()) {
            co.gorkem.namaz.wear.calculateNextPrayer(prayers)
        } else {
            data?.nextPrayer
        }

        if (nextPrayer != null) {
            co.gorkem.namaz.wear.scheduleComplicationUpdateAlarm(this, nextPrayer.targetTimeMillis)
        }

        if (nextPrayer == null) {
            return ShortTextComplicationData.Builder(
                text = PlainComplicationText.Builder(text = getString(R.string.wear_loading)).build(),
                contentDescription = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_time)).build()
            )
                .setTitle(PlainComplicationText.Builder(text = data?.cityName ?: getString(R.string.wear_prayer_times)).build())
                .setTapAction(tapAction)
                .build()
        }

        val title = if (resolved.source == co.gorkem.namaz.wear.tile.DisplaySource.CACHE) {
            "${prayerNameFromEnum(this, nextPrayer.name)} · ${getString(R.string.wear_standalone_mode)}"
        } else {
            prayerNameFromEnum(this, nextPrayer.name)
        }

        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text = nextPrayer.time).build(),
            contentDescription = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_time)).build()
        )
            .setTitle(PlainComplicationText.Builder(text = title).build())
            .setTapAction(tapAction)
            .build()
    }
}
