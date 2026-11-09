package co.gorkem.namaz.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import co.gorkem.namaz.wear.MainActivity
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.calculateNextPrayer
import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.data.WearPrayerItem
import co.gorkem.namaz.wear.di.WearDataEntryPoint
import co.gorkem.namaz.wear.prayerNameFromEnum
import dagger.hilt.EntryPoints
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class PrayerProgressDisplayData(
    val passedCount: Int,
    val totalCount: Int = 6,
    val progressText: String,
    val nextPrayerTitle: String,
    val isLoading: Boolean = false
)

fun calculatePassedPrayers(
    prayers: List<WearPrayerItem>,
    now: LocalTime = LocalTime.now()
): Int {
    if (prayers.isEmpty()) return 0
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    return prayers.count { prayer ->
        val time = LocalTime.parse(prayer.time, timeFormatter)
        !time.isAfter(now)
    }.coerceIn(0, 6)
}

fun resolvePrayerProgressDisplay(
    data: WearPrayerData?,
    now: LocalTime = LocalTime.now(),
    today: LocalDate = LocalDate.now(),
    getString: (Int) -> String,
    prayerNameResolver: (String) -> String
): PrayerProgressDisplayData {
    if (data == null || data.prayers.isEmpty()) {
        return PrayerProgressDisplayData(
            passedCount = 0,
            totalCount = 6,
            progressText = getString(R.string.wear_loading),
            nextPrayerTitle = getString(R.string.wear_prayer_times),
            isLoading = true
        )
    }

    val passed = calculatePassedPrayers(data.prayers, now)
    val nextPrayer = calculateNextPrayer(data.prayers, now, today)
    val nextPrayerName = nextPrayer?.let { prayerNameResolver(it.name) } ?: getString(R.string.wear_prayer_times)

    return PrayerProgressDisplayData(
        passedCount = passed,
        totalCount = 6,
        progressText = "$passed/6",
        nextPrayerTitle = nextPrayerName,
        isLoading = false
    )
}

fun resolvePrayerProgressPreviewTexts(
    type: ComplicationType,
    getString: (Int) -> String
): ComplicationPreviewModel? {
    return when (type) {
        ComplicationType.RANGED_VALUE, ComplicationType.SHORT_TEXT -> {
            ComplicationPreviewModel(
                type = type,
                text = "3/6",
                title = getString(R.string.prayer_ikindi),
                isStaticText = true,
                value = 3f,
                min = 0f,
                max = 6f
            )
        }
        else -> null
    }
}

fun resolvePrayerProgressPreview(
    type: ComplicationType,
    getString: (Int) -> String
): ComplicationData? {
    val model = resolvePrayerProgressPreviewTexts(type, getString) ?: return null
    val text = PlainComplicationText.Builder(text = model.text).build()
    val title = PlainComplicationText.Builder(text = model.title).build()
    val contentDesc = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_progress)).build()

    return when (type) {
        ComplicationType.RANGED_VALUE -> {
            RangedValueComplicationData.Builder(
                value = model.value,
                min = model.min,
                max = model.max,
                contentDescription = contentDesc
            )
            .setText(text)
            .setTitle(title)
            .build()
        }
        ComplicationType.SHORT_TEXT -> {
            ShortTextComplicationData.Builder(
                text = text,
                contentDescription = contentDesc
            )
            .setTitle(title)
            .build()
        }
        else -> null
    }
}

class PrayerProgressComplicationService : SuspendingComplicationDataSourceService() {

    private val entryPoint by lazy {
        EntryPoints.get(applicationContext, WearDataEntryPoint::class.java)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return resolvePrayerProgressPreview(type) { getString(it) }
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

        return buildData(request.complicationType, resolved, tapIntent)
    }

    override fun onComplicationActivated(complicationInstanceId: Int, type: ComplicationType) {}
    override fun onComplicationDeactivated(complicationInstanceId: Int) {}

    private fun buildLoadingData(type: ComplicationType): ComplicationData {
        val loading = PlainComplicationText.Builder(text = getString(R.string.wear_loading)).build()
        val title = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_times)).build()
        val contentDesc = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_progress)).build()

        return when (type) {
            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    value = 0f,
                    min = 0f,
                    max = 6f,
                    contentDescription = contentDesc
                )
                .setText(loading)
                .setTitle(title)
                .build()
            }
            else -> {
                ShortTextComplicationData.Builder(
                    text = loading,
                    contentDescription = contentDesc
                )
                .setTitle(title)
                .build()
            }
        }
    }

    private fun buildData(
        type: ComplicationType,
        resolved: co.gorkem.namaz.wear.tile.ResolvedDisplay,
        tapAction: PendingIntent
    ): ComplicationData {
        val displayData = resolvePrayerProgressDisplay(
            data = resolved.data,
            now = LocalTime.now(),
            today = LocalDate.now(),
            getString = { getString(it) },
            prayerNameResolver = { prayerNameFromEnum(this, it) }
        )

        val nextPrayer = resolved.data?.let { data ->
            val prayers = data.prayers
            if (prayers.isNotEmpty()) calculateNextPrayer(prayers) else data.nextPrayer
        }

        if (nextPrayer != null) {
            co.gorkem.namaz.wear.scheduleComplicationUpdateAlarm(this, nextPrayer.targetTimeMillis)
        }

        val text = PlainComplicationText.Builder(text = displayData.progressText).build()
        val titleText = if (resolved.source == co.gorkem.namaz.wear.tile.DisplaySource.CACHE && !displayData.isLoading) {
            PlainComplicationText.Builder(text = "${displayData.nextPrayerTitle} · ${getString(R.string.wear_standalone_mode)}").build()
        } else {
            PlainComplicationText.Builder(text = displayData.nextPrayerTitle).build()
        }
        val contentDesc = PlainComplicationText.Builder(text = getString(R.string.wear_prayer_progress)).build()

        return when (type) {
            ComplicationType.RANGED_VALUE -> {
                RangedValueComplicationData.Builder(
                    value = displayData.passedCount.toFloat(),
                    min = 0f,
                    max = displayData.totalCount.toFloat(),
                    contentDescription = contentDesc
                )
                .setText(text)
                .setTitle(titleText)
                .setTapAction(tapAction)
                .build()
            }
            else -> {
                ShortTextComplicationData.Builder(
                    text = text,
                    contentDescription = contentDesc
                )
                .setTitle(titleText)
                .setTapAction(tapAction)
                .build()
            }
        }
    }
}
