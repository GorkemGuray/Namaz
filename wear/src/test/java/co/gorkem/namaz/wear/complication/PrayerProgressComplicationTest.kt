package co.gorkem.namaz.wear.complication

import androidx.wear.watchface.complications.data.ComplicationType
import co.gorkem.namaz.wear.R
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

class PrayerProgressComplicationTest {

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
            R.string.wear_prayer_progress -> "Namaz İlerleme"
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

    // (a) 3/6 hesabı
    @Test
    fun whenThreePrayersPassed_calculatesThreeOutOfSixAndNextPrayerTitle() {
        // 14:00 -> Öğle (12:45) geçti, İkindi (15:30) sıradaki
        val now = LocalTime.of(14, 0)
        val today = LocalDate.of(2026, 11, 9)

        val result = resolvePrayerProgressDisplay(
            data = sampleData,
            now = now,
            today = today,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertFalse(result.isLoading)
        assertEquals(3, result.passedCount)
        assertEquals(6, result.totalCount)
        assertEquals("3/6", result.progressText)
        assertEquals("İkindi", result.nextPrayerTitle)
    }

    // (b) 0/6 ve 6/6 sınırları
    @Test
    fun whenBeforeImsak_boundaryIsZeroOutOfSix() {
        // 04:00 -> İmsak (05:00) öncesi, henüz hiçbir vakit geçmedi
        val now = LocalTime.of(4, 0)
        val today = LocalDate.of(2026, 11, 9)

        val result = resolvePrayerProgressDisplay(
            data = sampleData,
            now = now,
            today = today,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertFalse(result.isLoading)
        assertEquals(0, result.passedCount)
        assertEquals("0/6", result.progressText)
        assertEquals("İmsak", result.nextPrayerTitle)
    }

    @Test
    fun whenAfterYatsi_boundaryIsSixOutOfSix() {
        // 21:00 -> Yatsı (19:30) sonrası, günün tüm 6 vakti geçti
        val now = LocalTime.of(21, 0)
        val today = LocalDate.of(2026, 11, 9)

        val result = resolvePrayerProgressDisplay(
            data = sampleData,
            now = now,
            today = today,
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertFalse(result.isLoading)
        assertEquals(6, result.passedCount)
        assertEquals("6/6", result.progressText)
        // Ertesi günün ilk vakti İmsak
        assertEquals("İmsak", result.nextPrayerTitle)
    }

    // (c) Boş veride loading
    @Test
    fun whenDataIsNull_returnsLoadingState() {
        val result = resolvePrayerProgressDisplay(
            data = null,
            now = LocalTime.of(12, 0),
            today = LocalDate.of(2026, 11, 9),
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertTrue(result.isLoading)
        assertEquals(0, result.passedCount)
        assertEquals(6, result.totalCount)
        assertEquals("Yükleniyor…", result.progressText)
        assertEquals("Namaz Vakitleri", result.nextPrayerTitle)
    }

    @Test
    fun whenPrayersListIsEmpty_returnsLoadingState() {
        val emptyData = WearPrayerData(
            cityName = "İstanbul",
            prayers = emptyList(),
            nextPrayer = null,
            kerahat = null
        )

        val result = resolvePrayerProgressDisplay(
            data = emptyData,
            now = LocalTime.of(12, 0),
            today = LocalDate.of(2026, 11, 9),
            getString = fakeStringResolver,
            prayerNameResolver = fakePrayerNameResolver
        )

        assertTrue(result.isLoading)
        assertEquals(0, result.passedCount)
        assertEquals("Yükleniyor…", result.progressText)
        assertEquals("Namaz Vakitleri", result.nextPrayerTitle)
    }

    // (d) Preview null değil
    @Test
    fun previewData_isNotNullForSupportedTypesAndContainsFullStaticData() {
        val rangedModel = resolvePrayerProgressPreviewTexts(ComplicationType.RANGED_VALUE, fakeStringResolver)
        assertNotNull("RANGED_VALUE önizleme null olmamalıdır", rangedModel)
        assertEquals(ComplicationType.RANGED_VALUE, rangedModel!!.type)
        assertEquals(3f, rangedModel.value, 0.01f)
        assertEquals(6f, rangedModel.max, 0.01f)
        assertEquals("3/6", rangedModel.text)
        assertEquals("İkindi", rangedModel.title)
        assertTrue("Önizleme statik olmalıdır", rangedModel.isStaticText)

        val shortModel = resolvePrayerProgressPreviewTexts(ComplicationType.SHORT_TEXT, fakeStringResolver)
        assertNotNull("SHORT_TEXT önizleme null olmamalıdır", shortModel)
        assertEquals(ComplicationType.SHORT_TEXT, shortModel!!.type)
        assertEquals("3/6", shortModel.text)
        assertEquals("İkindi", shortModel.title)
        assertTrue("Önizleme statik olmalıdır", shortModel.isStaticText)

        val unsupportedModel = resolvePrayerProgressPreviewTexts(ComplicationType.LONG_TEXT, fakeStringResolver)
        assertNull("Desteklenmeyen tip için null dönmelidir", unsupportedModel)
    }
}
