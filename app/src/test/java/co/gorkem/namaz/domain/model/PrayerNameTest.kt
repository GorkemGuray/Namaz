package co.gorkem.namaz.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class PrayerNameTest {

    @Test
    fun turkishLocale_lowercasing_handlesDottedAndDotlessI() {
        val tr = Locale.forLanguageTag("tr")
        assertEquals("ıkındı", "IKINDI".lowercase(tr))
        assertEquals("ikindi", "İKİNDİ".lowercase(tr))
        assertEquals("ımsak", "IMSAK".lowercase(tr))
        assertEquals("imsak", "İMSAK".lowercase(tr))
    }

    @Test
    fun fromName_withTurkishLocale_resolvesCorrectly() {
        assertEquals(PrayerName.IMSAK, PrayerName.fromName("imsak"))
        assertEquals(PrayerName.IMSAK, PrayerName.fromName("İMSAK"))
        assertEquals(PrayerName.GUNES, PrayerName.fromName("sunrise"))
        assertEquals(PrayerName.OGLE, PrayerName.fromName("dhuhr"))
        assertEquals(PrayerName.IKINDI, PrayerName.fromName("asr"))
        assertEquals(PrayerName.AKSAM, PrayerName.fromName("maghrib"))
        assertEquals(PrayerName.YATSI, PrayerName.fromName("isha"))
        assertEquals(PrayerName.YATSI, PrayerName.fromName("İSHA"))
    }
}
