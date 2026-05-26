package co.gorkem.namaz.wear.tile

import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders as ProtoResourceBuilders
import androidx.wear.protolayout.TimelineBuilders as ProtoTimelineBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.di.WearDataEntryPoint
import co.gorkem.namaz.wear.prayerNameFromEnum
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import dagger.hilt.EntryPoints

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
        val data: WearPrayerData? = entryPoint.dataLayerService.prayerData.value

        val tile = TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(
                ProtoTimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        ProtoTimelineBuilders.TimelineEntry.Builder()
                            .setLayout(buildLayout(data))
                            .build()
                    )
                    .build()
            )
            .build()

        return tile
    }

    private fun buildLayout(data: WearPrayerData?): LayoutElementBuilders.Layout {
        // If data is null, immediately serve a mock prayer time layout to bypass null states cleanly (e.g. in the preview carousel).
        if (data == null) {
            return LayoutElementBuilders.Layout.Builder()
                .setRoot(
                    LayoutElementBuilders.Column.Builder()
                        .addContent(
                            LayoutElementBuilders.Text.Builder()
                                .setText("AKSAM: 20:33").build()
                        )
                        .addContent(
                            LayoutElementBuilders.Text.Builder()
                                .setText(getString(R.string.wear_loading)).build()
                        )
                        .build()
                )
                .build()
        }

        val nextPrayer = data.nextPrayer

        val text = if (nextPrayer != null) {
            "${prayerNameFromEnum(this, nextPrayer.name)}: ${nextPrayer.time}"
        } else getString(R.string.wear_prayer_times)

        val subText = if (nextPrayer != null) {
            "${nextPrayer.remainingMinutes}" + getString(R.string.wear_minutes_left)
        } else data.cityName

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
