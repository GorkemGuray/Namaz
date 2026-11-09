package co.gorkem.namaz.wear.complication

import android.app.PendingIntent
import android.content.Intent
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import co.gorkem.namaz.wear.MainActivity
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.calculateNextPrayer
import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.di.WearDataEntryPoint
import co.gorkem.namaz.wear.prayerNameFromEnum
import dagger.hilt.EntryPoints
import java.time.LocalDate
import java.time.LocalTime

data class PrayerNextDetailDisplayData(
    val prayerName: String,
    val prayerTime: String,
    val remainingText: String,
    val longTitle: String,
    val longText: String,
    val shortTitle: String,
    val shortText: String,
    val kerahatBadge: String? = null,
    val isKerahat: Boolean = false,
    val isLoading: Boolean = false
)

fun formatComplicationRemainingTime(remainingMinutes: Long, isEnglish: Boolean = false): String {
    val nonNegative = remainingMinutes.coerceAtLeast(0)
    val hrs = nonNegative / 60
    val mins = nonNegative % 60
    val saUnit = if (isEnglish) "h" else "sa"
    val dkUnit = if (isEnglish) "m" else "dk"

    return when {
        hrs > 0 && mins > 0 -> "-${hrs}$saUnit ${mins}$dkUnit"
        hrs > 0 -> "-${hrs}$saUnit"
        else -> "-${mins}$dkUnit"
    }
}

fun resolvePrayerNextDetailDisplay(
    data: WearPrayerData?,
    now: LocalTime = LocalTime.now(),
    today: LocalDate = LocalDate.now(),
    isEnglish: Boolean = false,
    getString: (Int) -> String,
    prayerNameResolver: (String) -> String
): PrayerNextDetailDisplayData {
    if (data == null || data.prayers.isEmpty()) {
        return PrayerNextDetailDisplayData(
            prayerName = "",
            prayerTime = "",
            remainingText = "",
            longTitle = getString(R.string.wear_prayer_times),
            longText = getString(R.string.wear_loading),
            shortTitle = getString(R.string.wear_prayer_times),
            shortText = getString(R.string.wear_loading),
            kerahatBadge = null,
            isKerahat = false,
            isLoading = true
        )
    }

    val nextPrayer = data.nextPrayer ?: calculateNextPrayer(data.prayers, now, today)
    if (nextPrayer == null) {
        return PrayerNextDetailDisplayData(
            prayerName = "",
            prayerTime = "",
            remainingText = "",
            longTitle = data.cityName.ifEmpty { getString(R.string.wear_prayer_times) },
            longText = getString(R.string.wear_loading),
            shortTitle = data.cityName.ifEmpty { getString(R.string.wear_prayer_times) },
            shortText = getString(R.string.wear_loading),
            kerahatBadge = null,
            isKerahat = false,
            isLoading = true
        )
    }

    val localizedPrayerName = prayerNameResolver(nextPrayer.name)
    val remainingFormatted = formatComplicationRemainingTime(nextPrayer.remainingMinutes, isEnglish)
    val timeFormatted = nextPrayer.time
    val longText = "$timeFormatted \u2022 $remainingFormatted"

    val kerahat = data.kerahat
    val isKerahat = kerahat != null && kerahat.isCurrentlyKerahat
    val kerahatBadge = if (isKerahat) {
        "\u26A0\uFE0F " + getString(R.string.wear_kerahat_active) + " (-${kerahat.remainingMinutes}dk)"
    } else {
        null
    }

    val longTitle = if (isKerahat) {
        "$localizedPrayerName \u00B7 " + getString(R.string.wear_kerahat_active)
    } else {
        localizedPrayerName
    }

    val shortTitle = if (isKerahat) {
        "$localizedPrayerName $timeFormatted \u00B7 \u26A0\uFE0F"
    } else {
        "$localizedPrayerName $timeFormatted"
    }

    return PrayerNextDetailDisplayData(
        prayerName = localizedPrayerName,
        prayerTime = timeFormatted,
        remainingText = remainingFormatted,
        longTitle = longTitle,
        longText = longText,
        shortTitle = shortTitle,
        shortText = remainingFormatted,
        kerahatBadge = kerahatBadge,
        isKerahat = isKerahat,
        isLoading = false
    )
}

fun resolvePrayerNextDetailPreviewTexts(
    type: ComplicationType,
    getString: (Int) -> String
): ComplicationPreviewModel? {
    val ikindiName = getString(R.string.prayer_ikindi)
    return when (type) {
        ComplicationType.LONG_TEXT -> {
            ComplicationPreviewModel(
                type = type,
                text = "15:30 \u2022 -1sa 30dk",
                title = ikindiName,
                isStaticText = true
            )
        }
        ComplicationType.SHORT_TEXT -> {
            ComplicationPreviewModel(
                type = type,
                text = "-1sa 30dk",
                title = "$ikindiName 15:30",
                isStaticText = true
            )
        }
        else -> null
    }
}

fun resolvePrayerNextDetailPreview(
    type: ComplicationType,
    getString: (Int) -> String
): ComplicationData? {
    val model = resolvePrayerNextDetailPreviewTexts(type, getString) ?: return null
    val text = PlainComplicationText.Builder(text = model.text).build()
    val title = PlainComplicationText.Builder(text = model.title).build()
    val contentDesc = PlainComplicationText.Builder(text = getString(R.string.wear_next_prayer_detail)).build()

    return when (type) {
        ComplicationType.LONG_TEXT -> {
            LongTextComplicationData.Builder(
                text = text,
                contentDescription = contentDesc
            )
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

class PrayerNextDetailComplicationService : SuspendingComplicationDataSourceService() {

    private val entryPoint by lazy {
        EntryPoints.get(applicationContext, WearDataEntryPoint::class.java)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return resolvePrayerNextDetailPreview(type) { getString(it) }
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
        val contentDesc = PlainComplicationText.Builder(text = getString(R.string.wear_next_prayer_detail)).build()

        return when (type) {
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = loading,
                    contentDescription = contentDesc
                )
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
        val isEn = resources.configuration.locales[0]?.language?.startsWith("en") == true
        val displayData = resolvePrayerNextDetailDisplay(
            data = resolved.data,
            now = LocalTime.now(),
            today = LocalDate.now(),
            isEnglish = isEn,
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

        val isStandalone = resolved.source == co.gorkem.namaz.wear.tile.DisplaySource.CACHE
        val longTitleStr = if (isStandalone && !displayData.isLoading) {
            "${displayData.longTitle} \u00B7 ${getString(R.string.wear_standalone_mode)}"
        } else {
            displayData.longTitle
        }
        val shortTitleStr = if (isStandalone && !displayData.isLoading) {
            "${displayData.shortTitle} \u00B7 ${getString(R.string.wear_standalone_mode)}"
        } else {
            displayData.shortTitle
        }

        val contentDesc = PlainComplicationText.Builder(text = getString(R.string.wear_next_prayer_detail)).build()

        return when (type) {
            ComplicationType.LONG_TEXT -> {
                LongTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = displayData.longText).build(),
                    contentDescription = contentDesc
                )
                .setTitle(PlainComplicationText.Builder(text = longTitleStr).build())
                .setTapAction(tapAction)
                .build()
            }
            else -> {
                ShortTextComplicationData.Builder(
                    text = PlainComplicationText.Builder(text = displayData.shortText).build(),
                    contentDescription = contentDesc
                )
                .setTitle(PlainComplicationText.Builder(text = shortTitleStr).build())
                .setTapAction(tapAction)
                .build()
            }
        }
    }
}
