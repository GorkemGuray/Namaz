package co.gorkem.namaz.wear

import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.data.WearPrayerItem
import co.gorkem.namaz.wear.tile.DisplaySource
import co.gorkem.namaz.wear.tile.STALE_CACHE_MILLIS
import co.gorkem.namaz.wear.tile.isCacheUsable
import co.gorkem.namaz.wear.tile.resolveDisplayData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WearDataFreshnessTest {

    private val now = 1_700_000_000_000L

    private fun cachedData(timestamp: Long, cityName: String = "Malatya") = WearPrayerData(
        cityName = cityName,
        prayers = listOf(
            WearPrayerItem("IMSAK", "03:30"),
            WearPrayerItem("GUNES", "05:15"),
            WearPrayerItem("OGLE", "13:10"),
            WearPrayerItem("IKINDI", "17:05"),
            WearPrayerItem("AKSAM", "20:30"),
            WearPrayerItem("YATSI", "22:15")
        ),
        nextPrayer = null,
        kerahat = null,
        themeId = 0,
        latitude = 39.0,
        longitude = 38.0,
        timestamp = timestamp
    )

    @Test
    fun freshCache_isUsable() {
        val data = cachedData(now - 60_000L)
        assertTrue(isCacheUsable(data, now))
    }

    @Test
    fun cacheExactlyAtThreshold_isStale() {
        val data = cachedData(now - STALE_CACHE_MILLIS)
        assertFalse(isCacheUsable(data, now))
    }

    @Test
    fun cacheJustOverThreshold_isStale() {
        val data = cachedData(now - STALE_CACHE_MILLIS - 1L)
        assertFalse(isCacheUsable(data, now))
    }

    @Test
    fun cacheExactly24hOld_isStale() {
        val data = cachedData(now - 24 * 60 * 60 * 1000L)
        assertFalse(isCacheUsable(data, now))
    }

    @Test
    fun cacheOneMinuteUnder24h_isUsable() {
        val data = cachedData(now - (24 * 60 * 60 * 1000L - 60_000L))
        assertTrue(isCacheUsable(data, now))
    }

    @Test
    fun cacheFromFuture_isUsable() {
        val data = cachedData(now + 120_000L)
        assertTrue(isCacheUsable(data, now))
    }

    @Test
    fun nullData_isNotUsable() {
        assertFalse(isCacheUsable(null, now))
    }

    @Test
    fun dataWithoutNextPrayer_isStillUsable() {
        val data = cachedData(now - 60_000L)
        assertEquals(null, data.nextPrayer)
        assertTrue(isCacheUsable(data, now))
    }

    @Test
    fun resolve_liveData_wins() {
        val live = cachedData(now, cityName = "Canlı")
        val cached = cachedData(now - 60_000L, cityName = "Önbellek")
        val resolved = resolveDisplayData(live = live, cached = cached, nowMillis = now)
        assertEquals(DisplaySource.LIVE, resolved.source)
        assertEquals("Canlı", resolved.data?.cityName)
    }

    @Test
    fun resolve_liveNull_cacheFresh_usesCache() {
        val cached = cachedData(now - 60_000L, cityName = "Önbellek")
        val resolved = resolveDisplayData(live = null, cached = cached, nowMillis = now)
        assertEquals(DisplaySource.CACHE, resolved.source)
        assertEquals("Önbellek", resolved.data?.cityName)
    }

    @Test
    fun resolve_liveNull_cacheStale_treatsAsEmpty() {
        val cached = cachedData(now - STALE_CACHE_MILLIS - 1L, cityName = "Bayat")
        val resolved = resolveDisplayData(live = null, cached = cached, nowMillis = now)
        assertEquals(DisplaySource.EMPTY, resolved.source)
        assertEquals(null, resolved.data)
    }

    @Test
    fun resolve_bothNull_treatsAsEmpty() {
        val resolved = resolveDisplayData(live = null, cached = null, nowMillis = now)
        assertEquals(DisplaySource.EMPTY, resolved.source)
        assertEquals(null, resolved.data)
    }

    @Test
    fun resolve_liveStale_cacheFresh_prefersCache() {
        val live = cachedData(now - STALE_CACHE_MILLIS - 1L, cityName = "BayatCanlı")
        val cached = cachedData(now - 60_000L, cityName = "TazeÖnbellek")
        val resolved = resolveDisplayData(live = live, cached = cached, nowMillis = now)
        assertEquals(DisplaySource.CACHE, resolved.source)
        assertEquals("TazeÖnbellek", resolved.data?.cityName)
    }

    @Test
    fun resolve_liveFresh_cacheNull_usesLive() {
        val live = cachedData(now - 1000L, cityName = "Canlı")
        val resolved = resolveDisplayData(live = live, cached = null, nowMillis = now)
        assertEquals(DisplaySource.LIVE, resolved.source)
    }

    @Test
    fun resolvedData_neverContainsInventedPrayerTimes() {
        val resolved = resolveDisplayData(live = null, cached = null, nowMillis = now)
        assertEquals(null, resolved.data)
        assertEquals(null, resolved.data?.nextPrayer)
    }

    @Test
    fun previewSource_isDistinctFromAllRealDeviceSources() {
        val realSources = listOf(DisplaySource.LIVE, DisplaySource.CACHE, DisplaySource.EMPTY)
        assertFalse(realSources.contains(DisplaySource.PREVIEW))
    }

    @Test
    fun previewSource_isNeverReturnedByResolve() {
        val combos = listOf(
            Triple(cachedData(now), null, now),
            Triple(null, cachedData(now), now),
            Triple(null, null, now),
            Triple(cachedData(now - STALE_CACHE_MILLIS - 1L), cachedData(now), now)
        )
        combos.forEach { (live, cached, at) ->
            assertFalse(resolveDisplayData(live, cached, at).source == DisplaySource.PREVIEW)
        }
    }

    @Test
    fun staleThreshold_is24Hours() {
        assertEquals(24 * 60 * 60 * 1000L, STALE_CACHE_MILLIS)
    }
}
