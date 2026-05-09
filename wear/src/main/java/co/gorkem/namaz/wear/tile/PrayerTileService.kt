package co.gorkem.namaz.wear.tile

import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders as ProtoResourceBuilders
import androidx.wear.protolayout.TimelineBuilders as ProtoTimelineBuilders
import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.di.WearDataEntryPoint
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.EntryPoints
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

class PrayerTileService : TileService() {

    private val entryPoint by lazy {
        EntryPoints.get(applicationContext, WearDataEntryPoint::class.java)
    }

    override fun onTileResourcesRequest(
        requestParams: RequestBuilders.ResourcesRequest
    ): ListenableFuture<ProtoResourceBuilders.Resources> {
        return ProtoResourceBuilders.Resources.Builder().build().asFuture()
    }

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<TileBuilders.Tile> {
        val data: WearPrayerData? = runBlocking {
            entryPoint.dataLayerService.prayerData.first()
        }

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

        return tile.asFuture()
    }

    private fun buildLayout(data: WearPrayerData?): LayoutElementBuilders.Layout {
        val nextPrayer = data?.nextPrayer

        val text = if (nextPrayer != null) {
            "${nextPrayer.turkishName}: ${nextPrayer.time}"
        } else "Namaz Vakitleri"

        val subText = if (nextPrayer != null) {
            "${nextPrayer.remainingMinutes} dk kaldı"
        } else data?.cityName ?: "Yükleniyor..."

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

private fun <T> T.asFuture(): ListenableFuture<T> = object : ListenableFuture<T> {
    override fun get(): T = this@asFuture
    override fun get(timeout: Long, unit: TimeUnit): T = this@asFuture
    override fun isDone() = true
    override fun isCancelled() = false
    override fun cancel(mayInterruptIfRunning: Boolean) = false
    override fun addListener(listener: Runnable, executor: Executor) {
        executor.execute(listener)
    }
}
