package co.gorkem.namaz.wear.complication

import androidx.wear.watchface.complications.data.ComplicationType
import co.gorkem.namaz.wear.R
import co.gorkem.namaz.wear.data.WearKerahatInfo
import co.gorkem.namaz.wear.data.WearPrayerData
import co.gorkem.namaz.wear.data.WearPrayerItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class PrayerNextDetailComplicationTest {

    private val samplePrayers = listOf(
        WearPrayerItem("IMSAK", "05:00"),
        WearPrayerItem("GUNES", "06:30"),
        WearPrayerItem("OGLE", "12:45"),
        WearPrayerItem("IKINDI", "15:30"),
        WearPrayerItem("AKSAM", "18:00"),
        WearPrayerItem("YATSI", "19:30")
    )

    private val sampleData = WearPrayerData(
        cityName = "İstanbul",
        prayers = samplePrayers,
        nextPrayer = null,
        kerahat = null
    )

    private val fakeStringResolver: (Int) -> String = { resId ->
        when (resId) {
            R.string.prayer_imsak -> "İmsak"
            R.string.prayer_gunes -> "Güneş"
            R.string.prayer_ogle -> "Öğle"
            R.string.prayer_ikindi -> "İkindi"
            R.string.prayer_aksam -> "Akşam"
            R.string.prayer_yatsi -> "Yatsı"
            R.string.wear_loading -> "Yükleniyor…"
            R.string.wear_prayer_times -> "Namaz Vakitleri"
            R.string.wear_prayer_time -> "Namaz Vakti"
            R.string.wear_next_prayer_detail -> "Sıradaki Vakit Detay"
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

    // (a) Metin formatı "saat • kalan"
    @Test
    fun whenNormal_formatsTimeAndRemainingCorrectly() {
        // 14:00 -> Öğle (12:45) geçti, İkindi (15:30) sıradaki, kalan 90 dk (-1sa 30dk)
        val now = LocalTime.of(14, 0)
        val today = LocalDate.of(2026, 11, 9)

        val result = resolvePrayerNextDetailDisplay(
            data = sampleData,
            now = now,
            today = today,
            isEnglish = false,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertFalse(result.isLoading)
        assertEquals("İkindi", result.prayerName)
        assertEquals("15:30", result.prayerTime)
        assertEquals("-1sa 30dk", result.remainingText)
        assertEquals("İkindi", result.longTitle)
        assertEquals("15:30 \u2022 -1sa 30dk", result.longText)
        assertEquals("İkindi 15:30", result.shortTitle)
        assertEquals("-1sa 30dk", result.shortText)
        assertFalse(result.isKerahat)
        assertNull(result.kerahatBadge)
    }

    @Test
    fun formatRemainingTime_handlesVariousDurationsAndLanguages() {
        assertEquals("-1sa 30dk", formatComplicationRemainingTime(90, isEnglish = false))
        assertEquals("-1h 30m", formatComplicationRemainingTime(90, isEnglish = true))
        assertEquals("-45dk", formatComplicationRemainingTime(45, isEnglish = false))
        assertEquals("-45m", formatComplicationRemainingTime(45, isEnglish = true))
        assertEquals("-2sa", formatComplicationRemainingTime(120, isEnglish = false))
        assertEquals("-2h", formatComplicationRemainingTime(120, isEnglish = true))
        assertEquals("-0dk", formatComplicationRemainingTime(0, isEnglish = false))
    }

    // (b) Kerahat anında kerahat bilgisi (varsa — yoksa normal)
    @Test
    fun whenKerahatActive_includesKerahatInfo() {
        val now = LocalTime.of(14, 0)
        val today = LocalDate.of(2026, 11, 9)

        val dataWithKerahat = sampleData.copy(
            kerahat = WearKerahatInfo(
                isCurrentlyKerahat = true,
                prayerName = "GUNES",
                remainingMinutes = 15
            )
        )

        val result = resolvePrayerNextDetailDisplay(
            data = dataWithKerahat,
            now = now,
            today = today,
            isEnglish = false,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertTrue("Kerahat aktif olarak işaretlenmelidir", result.isKerahat)
        assertNotNull("Kerahat rozeti null olmamalıdır", result.kerahatBadge)
        assertTrue(result.kerahatBadge!!.contains("kerahat aktif"))
        assertTrue(result.kerahatBadge!!.contains("-15dk"))
        assertTrue("Başlıkta kerahat aktif bilgisi yer almalıdır", result.longTitle.contains("kerahat aktif"))
        assertTrue("Kısa başlıkta uyarı sembolü yer almalıdır", result.shortTitle.contains("\u26A0\uFE0F"))
        assertEquals("15:30 \u2022 -1sa 30dk", result.longText)
    }

    @Test
    fun whenKerahatInactive_returnsNormalInfo() {
        val now = LocalTime.of(14, 0)
        val today = LocalDate.of(2026, 11, 9)

        val dataWithInactiveKerahat = sampleData.copy(
            kerahat = WearKerahatInfo(
                isCurrentlyKerahat = false,
                prayerName = "IKINDI",
                remainingMinutes = 40
            )
        )

        val result = resolvePrayerNextDetailDisplay(
            data = dataWithInactiveKerahat,
            now = now,
            today = today,
            isEnglish = false,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertFalse("Kerahat aktif olmamalıdır", result.isKerahat)
        assertNull("Kerahat rozeti null olmalıdır", result.kerahatBadge)
        assertEquals("İkindi", result.longTitle)
        assertEquals("İkindi 15:30", result.shortTitle)
        assertEquals("15:30 \u2022 -1sa 30dk", result.longText)
    }

    @Test
    fun whenKerahatNull_returnsNormalInfo() {
        val now = LocalTime.of(14, 0)
        val today = LocalDate.of(2026, 11, 9)

        val result = resolvePrayerNextDetailDisplay(
            data = sampleData.copy(kerahat = null),
            now = now,
            today = today,
            isEnglish = false,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertFalse(result.isKerahat)
        assertNull(result.kerahatBadge)
        assertEquals("İkindi", result.longTitle)
        assertEquals("İkindi 15:30", result.shortTitle)
    }

    // (c) Boş veride loading
    @Test
    fun whenDataIsNull_returnsLoadingState() {
        val result = resolvePrayerNextDetailDisplay(
            data = null,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertTrue(result.isLoading)
        assertEquals("Namaz Vakitleri", result.longTitle)
        assertEquals("Yükleniyor…", result.longText)
        assertEquals("Namaz Vakitleri", result.shortTitle)
        assertEquals("Yükleniyor…", result.shortText)
        assertFalse(result.isKerahat)
        assertNull(result.kerahatBadge)
    }

    @Test
    fun whenPrayersListIsEmpty_returnsLoadingState() {
        val emptyData = WearPrayerData(
            cityName = "İstanbul",
            prayers = emptyList(),
            nextPrayer = null,
            kerahat = null
        )

        val result = resolvePrayerNextDetailDisplay(
            data = emptyData,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertTrue(result.isLoading)
        assertEquals("Namaz Vakitleri", result.longTitle)
        assertEquals("Yükleniyor…", result.longText)
        assertEquals("Namaz Vakitleri", result.shortTitle)
        assertEquals("Yükleniyor…", result.shortText)
    }

    // (d) Preview'lar null değil
    @Test
    fun previewData_isNotNullForSupportedTypesAndContainsStaticData() {
        val longModel = resolvePrayerNextDetailPreviewTexts(ComplicationType.LONG_TEXT, fakeStringResolver)
        assertNotNull("LONG_TEXT önizleme modeli null olmamalıdır", longModel)
        assertEquals(ComplicationType.LONG_TEXT, longModel!!.type)
        assertEquals("15:30 \u2022 -1sa 30dk", longModel.text)
        assertEquals("İkindi", longModel.title)
        assertTrue("Önizleme statik olmalıdır", longModel.isStaticText)

        val shortModel = resolvePrayerNextDetailPreviewTexts(ComplicationType.SHORT_TEXT, fakeStringResolver)
        assertNotNull("SHORT_TEXT önizleme modeli null olmamalıdır", shortModel)
        assertEquals(ComplicationType.SHORT_TEXT, shortModel!!.type)
        assertEquals("-1sa 30dk", shortModel.text)
        assertEquals("İkindi 15:30", shortModel.title)
        assertTrue("Önizleme statik olmalıdır", shortModel.isStaticText)

        val unsupportedModel = resolvePrayerNextDetailPreviewTexts(ComplicationType.RANGED_VALUE, fakeStringResolver)
        assertNull("Desteklenmeyen tip için null dönmelidir", unsupportedModel)
    }
}
