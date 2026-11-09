package co.gorkem.namaz.wear.complication

import androidx.wear.watchface.complications.data.ComplicationType
import co.gorkem.namaz.wear.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerComplicationPreviewTest {

    private val fakeStringResolver: (Int) -> String = { resId ->
        when (resId) {
            R.string.prayer_aksam -> "Akşam"
            R.string.wear_prayer_time -> "Namaz Vakti"
            R.string.wear_prayer_times -> "Namaz Vakitleri"
            R.string.wear_loading -> "Yükleniyor…"
            else -> "Res_$resId"
        }
    }

    @Test
    fun prayerComplication_previewSupportsShortText() {
        val model = resolvePrayerComplicationPreviewTexts(ComplicationType.SHORT_TEXT, fakeStringResolver)
        assertNotNull("SHORT_TEXT önizleme verisi null olmamalıdır", model)
        assertEquals(ComplicationType.SHORT_TEXT, model!!.type)
        assertEquals("20:28", model.text)
        assertEquals("Akşam", model.title)
        assertTrue(model.isStaticText)
    }

    @Test
    fun prayerComplication_previewSupportsLongText() {
        val model = resolvePrayerComplicationPreviewTexts(ComplicationType.LONG_TEXT, fakeStringResolver)
        assertNotNull("LONG_TEXT önizleme verisi null olmamalıdır", model)
        assertEquals(ComplicationType.LONG_TEXT, model!!.type)
        assertEquals("20:28", model.text)
        assertEquals("Akşam", model.title)
        assertTrue(model.isStaticText)
    }

    @Test
    fun prayerComplication_unsupportedTypeReturnsNull() {
        val model = resolvePrayerComplicationPreviewTexts(ComplicationType.RANGED_VALUE, fakeStringResolver)
        assertNull("Desteklenmeyen tip null dönmelidir", model)
    }

    @Test
    fun countdownComplication_previewSupportsShortTextWithStaticCountdownText() {
        val model = resolveCountdownComplicationPreviewTexts(ComplicationType.SHORT_TEXT, fakeStringResolver)
        assertNotNull("SHORT_TEXT önizleme verisi null olmamalıdır", model)
        assertEquals(ComplicationType.SHORT_TEXT, model!!.type)
        assertTrue("Önizleme metni statik olmalıdır (editörde boş kalmaması için)", model.isStaticText)
        assertEquals("-1sa 30dk", model.text)
        assertEquals("Akşam", model.title)
    }

    @Test
    fun countdownComplication_previewSupportsLongTextWithStaticCountdownText() {
        val model = resolveCountdownComplicationPreviewTexts(ComplicationType.LONG_TEXT, fakeStringResolver)
        assertNotNull("LONG_TEXT önizleme verisi null olmamalıdır", model)
        assertEquals(ComplicationType.LONG_TEXT, model!!.type)
        assertTrue("Önizleme metni statik olmalıdır (editörde boş kalmaması için)", model.isStaticText)
        assertEquals("-1sa 30dk", model.text)
        assertEquals("Akşam", model.title)
    }

    @Test
    fun countdownComplication_unsupportedTypeReturnsNull() {
        val model = resolveCountdownComplicationPreviewTexts(ComplicationType.RANGED_VALUE, fakeStringResolver)
        assertNull("Desteklenmeyen tip null dönmelidir", model)
    }
}
