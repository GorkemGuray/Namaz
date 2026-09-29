package co.gorkem.namaz.wear.tile

import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders as ProtoResourceBuilders
import androidx.wear.protolayout.TimelineBuilders as ProtoTimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.di.WearDataEntryPoint
import co.gorkem.namaz.wear.calculateNextPrayer
import co.gorkem.namaz.wear.prayerNameFromEnum
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import dagger.hilt.EntryPoints

const val STALE_CACHE_MILLIS = 24 * 60 * 60 * 1000L

enum class DisplaySource { LIVE, CACHE, EMPTY, PREVIEW }

data class ResolvedDisplay(
    val data: WearPrayerData?,
    val source: DisplaySource
)

fun isCacheUsable(data: WearPrayerData?, nowMillis: Long): Boolean {
    if (data == null) return false
    return nowMillis - data.timestamp < STALE_CACHE_MILLIS
}

fun resolveDisplayData(
    live: WearPrayerData?,
    cached: WearPrayerData?,
    nowMillis: Long
): ResolvedDisplay {
    if (isCacheUsable(live, nowMillis)) {
        return ResolvedDisplay(live, DisplaySource.LIVE)
    }
    if (isCacheUsable(cached, nowMillis)) {
        return ResolvedDisplay(cached, DisplaySource.CACHE)
    }
    return ResolvedDisplay(null, DisplaySource.EMPTY)
}

@OptIn(ExperimentalHorologistApi::class)
class PrayerTileService : SuspendingTileService() {

    private val entryPoint by lazy {
        EntryPoints.get(applicationContext, WearDataEntryPoint::class.java)
    }

    override suspend fun resourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ProtoResourceBuilders.Resources {
        return ProtoResourceBuilders.Resources.Builder().setVersion("1").build()
    }

    override suspend fun tileRequest(
        requestParams: RequestBuilders.TileRequest
    ): TileBuilders.Tile {
        val live = entryPoint.dataLayerService.getLatestPrayerData()
        val resolved = resolveDisplayData(
            live = live,
            cached = live,
            nowMillis = System.currentTimeMillis()
        )

        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(
                ProtoTimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        ProtoTimelineBuilders.TimelineEntry.Builder()
                            .setLayout(buildLayout(resolved))
                            .build()
                    )
                    .build()
            )
            .build()

        return tile
    }

    private fun buildLayout(resolved: ResolvedDisplay): LayoutElementBuilders.Layout {
        val data = resolved.data

        val (text, subText) = if (data == null) {
            getString(R.string.wear_loading) to getString(R.string.wear_prayer_times)
        } else {
            val nextPrayer = data.nextPrayer
                ?: calculateNextPrayer(data.prayers)
            if (nextPrayer != null) {
                "${prayerNameFromEnum(this, nextPrayer.name)}: ${nextPrayer.time}" to
                    (if (resolved.source == DisplaySource.CACHE) {
                        getString(R.string.wear_standalone_mode)
                    } else {
                        nextPrayer.remainingMinutes.toString() + getString(R.string.wear_minutes_left)
                    })
            } else {
                getString(R.string.wear_prayer_times) to data.cityName
            }
        }

        return LayoutElementBuilders.Layout.Builder()
            .setRoot(
                LayoutElementBuilders.Column.Builder()
                    .addContent(
                        LayoutElementBuilders.Text.Builder()
                            .setText(text).build()
                    )
                    .addContent(
                        LayoutElementBuilders.Text.Builder()
                            .setText(subText).build()
                    )
                    .build()
            )
            .build()
    }
}
