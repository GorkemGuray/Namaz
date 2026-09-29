package co.gorkem.namaz.wear

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PrayerNameMappingTest {

    @Test
    fun imsak_mapsToImsakString() {
        assertEquals(R.string.prayer_imsak, prayerNameStringRes("IMSAK"))
    }

    @Test
    fun gunes_mapsToGunesString() {
        assertEquals(R.string.prayer_gunes, prayerNameStringRes("GUNES"))
    }

    @Test
    fun ogle_mapsToOgleString() {
        assertEquals(R.string.prayer_ogle, prayerNameStringRes("OGLE"))
    }

    @Test
    fun ikindi_mapsToIkindiString() {
        assertEquals(R.string.prayer_ikindi, prayerNameStringRes("IKINDI"))
    }

    @Test
    fun aksam_mapsToAksamString() {
        assertEquals(R.string.prayer_aksam, prayerNameStringRes("AKSAM"))
    }

    @Test
    fun yatsi_mapsToYatsiString() {
        assertEquals(R.string.prayer_yatsi, prayerNameStringRes("YATSI"))
    }

    @Test
    fun unknownName_mapsToNull() {
        assertNull(prayerNameStringRes("TEBLIG"))
    }

    @Test
    fun allPrayerNames_haveDistinctStringResources() {
        val names = listOf("IMSAK", "GUNES", "OGLE", "IKINDI", "AKSAM", "YATSI")
        val resources = names.map { prayerNameStringRes(it) }
        assertEquals(names.size, resources.toSet().size)
    }

    @Test
    fun previousPrayer_cyclesThroughAllPrayers() {
        assertEquals("YATSI", getPreviousPrayer("IMSAK"))
        assertEquals("IMSAK", getPreviousPrayer("GUNES"))
        assertEquals("GUNES", getPreviousPrayer("OGLE"))
        assertEquals("OGLE", getPreviousPrayer("IKINDI"))
        assertEquals("IKINDI", getPreviousPrayer("AKSAM"))
        assertEquals("AKSAM", getPreviousPrayer("YATSI"))
    }

    @Test
    fun previousPrayer_unknownName_returnsInput() {
        assertEquals("TEBLIG", getPreviousPrayer("TEBLIG"))
    }
}
