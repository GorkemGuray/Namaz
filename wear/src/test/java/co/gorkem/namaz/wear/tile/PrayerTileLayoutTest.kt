package co.gorkem.namaz.wear.tile

import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.data.WearKerahatInfo
import co.gorkem.namaz.wear.data.WearNextPrayer
import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.data.WearPrayerItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerTileLayoutTest {

    private val fakeStringResolver: (Int) -> String = { resId ->
        when (resId) {
            R.string.wear_loading -> "Yükleniyor…"
            R.string.wear_prayer_times -> "Namaz Vakitleri"
            R.string.wear_standalone_mode -> "Bağımsız mod"
            R.string.wear_minutes_left -> " dk kaldı"
            R.string.wear_kerahat_active -> "kerahat aktif"
            else -> "Res_$resId"
        }
    }

    private val fakePrayerNameResolver: (String) -> String = { name ->
        when (name) {
            "IMSAK" -> "İmsak"
            "GUNES" -> "Güneş"
            "OGLE" -> "Öğle"
            "IKINDI" -> "İkindi"
            "AKSAM" -> "Akşam"
            "YATSI" -> "Yatsı"
            else -> name
        }
    }

    @Test
    fun whenDataIsNull_showsLoadingTextsAndNoKerahatBadge() {
        val result = resolveTileTexts(
            data = null,
            source = DisplaySource.EMPTY,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertNull("Kerahat rozeti null olmalıdır", result.kerahatBadge)
        assertEquals("Yükleniyor…", result.mainText)
        assertEquals("Namaz Vakitleri", result.subText)
    }

    @Test
    fun whenKerahatActive_includesKerahatWarningInTile() {
        val dataWithActiveKerahat = WearPrayerData(
            cityName = "İstanbul",
            prayers = listOf(
                WearPrayerItem("IMSAK", "05:00"),
                WearPrayerItem("GUNES", "06:30"),
                WearPrayerItem("OGLE", "12:45"),
                WearPrayerItem("IKINDI", "15:30"),
                WearPrayerItem("AKSAM", "18:00"),
                WearPrayerItem("YATSI", "19:30")
            ),
            nextPrayer = WearNextPrayer(name = "OGLE", time = "12:45", remainingMinutes = 20),
            kerahat = WearKerahatInfo(
                isCurrentlyKerahat = true,
                prayerName = "GUNES",
                remainingMinutes = 15
            )
        )

        val result = resolveTileTexts(
            data = dataWithActiveKerahat,
            source = DisplaySource.LIVE,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertNotNull("Kerahat aktifken rozet gösterilmelidir", result.kerahatBadge)
        assertTrue("Rozet uyarı ikonu ve kerahat aktif metni içermelidir", result.kerahatBadge!!.contains("⚠️"))
        assertTrue("Kerahat aktif metni içermelidir", result.kerahatBadge!!.contains("kerahat aktif"))
        assertTrue("Kalan süre içermelidir (-15dk)", result.kerahatBadge!!.contains("-15dk"))
        assertEquals("Öğle: 12:45", result.mainText)
        assertEquals("20 dk kaldı", result.subText)
    }

    @Test
    fun whenKerahatInactive_doesNotIncludeKerahatWarning() {
        val dataWithInactiveKerahat = WearPrayerData(
            cityName = "Ankara",
            prayers = listOf(
                WearPrayerItem("IMSAK", "05:00"),
                WearPrayerItem("GUNES", "06:30"),
                WearPrayerItem("OGLE", "12:45"),
                WearPrayerItem("IKINDI", "15:30"),
                WearPrayerItem("AKSAM", "18:00"),
                WearPrayerItem("YATSI", "19:30")
            ),
            nextPrayer = WearNextPrayer(name = "AKSAM", time = "18:00", remainingMinutes = 45),
            kerahat = WearKerahatInfo(
                isCurrentlyKerahat = false,
                prayerName = "IKINDI",
                remainingMinutes = 40
            )
        )

        val result = resolveTileTexts(
            data = dataWithInactiveKerahat,
            source = DisplaySource.LIVE,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertNull("Kerahat aktif değilken rozet gösterilmemelidir", result.kerahatBadge)
        assertEquals("Akşam: 18:00", result.mainText)
        assertEquals("45 dk kaldı", result.subText)
    }

    @Test
    fun whenSourceIsCache_subTextIndicatesStandaloneMode() {
        val cachedData = WearPrayerData(
            cityName = "İzmir",
            prayers = emptyList(),
            nextPrayer = WearNextPrayer(name = "YATSI", time = "19:30", remainingMinutes = 60),
            kerahat = null
        )

        val result = resolveTileTexts(
            data = cachedData,
            source = DisplaySource.CACHE,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertNull(result.kerahatBadge)
        assertEquals("Yatsı: 19:30", result.mainText)
        assertEquals("Bağımsız mod", result.subText)
    }
}
