package co.gorkem.namaz.service

import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R
import co.gorkem.namaz.ui.theme.AppThemeSelection
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WidgetLayoutResolutionTest {

    @Before
    fun setUp() {
        WidgetLayoutResolver.clearMemoryCache()
    }

    @Test
    fun allKnownThemes_resolveToOwnLayout() {
        val expected = mapOf(
            AppThemeSelection.EMERALD_OASIS to R.layout.widget_chronometer_emerald,
            AppThemeSelection.SAPPHIRE_NIGHT to R.layout.widget_chronometer_sapphire,
            AppThemeSelection.AMBER_SANDS to R.layout.widget_chronometer_amber,
            AppThemeSelection.MONOCHROME_PLATINUM to R.layout.widget_chronometer_monochrome,
            AppThemeSelection.YUMUSAK_AYDINLIK to R.layout.widget_chronometer_yumusak,
            AppThemeSelection.TAM_AYDINLIK to R.layout.widget_chronometer_tam,
            AppThemeSelection.SAMAN_KAGIDI to R.layout.widget_chronometer_saman,
            AppThemeSelection.KARANLIK to R.layout.widget_chronometer_karanlik,
            AppThemeSelection.GALAKSI to R.layout.widget_chronometer_galaksi,
            AppThemeSelection.GUN_BATIMI to R.layout.widget_chronometer_gun_batimi,
            AppThemeSelection.AMBER_GECE to R.layout.widget_chronometer_amber_gece
        )
        expected.forEach { (theme, layout) ->
            assertEquals(theme.name, layout, getChronometerLayoutRes(theme, false))
        }
    }

    @Test
    fun kerahatActive_overridesThemeLayout() {
        AppThemeSelection.entries.forEach { theme ->
            assertEquals(
                theme.name,
                R.layout.widget_chronometer_kerahat,
                getChronometerLayoutRes(theme, true)
            )
        }
    }

    @Test
    fun manualCompact_resolvesToCompact_regardlessOfReportedSize() {
        // (a) manuel Kompakt küçük size'da da Kompakt
        val compactSmall = resolveWidgetLayout(
            appWidgetId = 1,
            layoutPreference = NamazWidgetPrefs.LAYOUT_COMPACT,
            width = 100.dp,
            height = 100.dp
        )
        assertEquals(WidgetLayoutType.COMPACT, compactSmall)

        // Büyük size bildirilse de Kompakt kilitli kalır
        val compactLarge = resolveWidgetLayout(
            appWidgetId = 1,
            layoutPreference = NamazWidgetPrefs.LAYOUT_COMPACT,
            width = 320.dp,
            height = 200.dp
        )
        assertEquals(WidgetLayoutType.COMPACT, compactLarge)
    }

    @Test
    fun manualExpanded_resolvesToExpanded_regardlessOfReportedSize() {
        // (b) manuel Geniş küçük size'da Geniş (Tecno HiOS reboot/glitch durumunda da Geniş kalır)
        val expandedSmall = resolveWidgetLayout(
            appWidgetId = 2,
            layoutPreference = NamazWidgetPrefs.LAYOUT_EXPANDED,
            width = 100.dp,
            height = 100.dp
        )
        assertEquals(WidgetLayoutType.EXPANDED, expandedSmall)

        // Normal büyük size'da da Geniş
        val expandedLarge = resolveWidgetLayout(
            appWidgetId = 2,
            layoutPreference = NamazWidgetPrefs.LAYOUT_EXPANDED,
            width = 320.dp,
            height = 200.dp
        )
        assertEquals(WidgetLayoutType.EXPANDED, expandedLarge)
    }

    @Test
    fun autoMode_singleSmallFrame_doesNotFlip_twoConsecutiveFrames_flips() {
        // (c) otomatik modda tek karelik küçük size flip ETMEZ, iki kare üst üste ederse eder
        val widgetId = 10

        // 1. Kare: Başlangıçta normal geniş boyut bildirilir -> Geniş
        val frame1 = resolveWidgetLayout(
            appWidgetId = widgetId,
            layoutPreference = NamazWidgetPrefs.LAYOUT_AUTO,
            width = 300.dp,
            height = 200.dp
        )
        assertEquals(WidgetLayoutType.EXPANDED, frame1)

        // 2. Kare: Launcher anlık küçük size bildirdi (tek kare) -> flip ETMEZ!
        val frame2 = resolveWidgetLayout(
            appWidgetId = widgetId,
            layoutPreference = NamazWidgetPrefs.LAYOUT_AUTO,
            width = 100.dp,
            height = 100.dp
        )
        assertEquals(WidgetLayoutType.EXPANDED, frame2)

        // 3. Kare: İkinci kez üst üste küçük size bildirildi -> flip EDER!
        val frame3 = resolveWidgetLayout(
            appWidgetId = widgetId,
            layoutPreference = NamazWidgetPrefs.LAYOUT_AUTO,
            width = 100.dp,
            height = 100.dp
        )
        assertEquals(WidgetLayoutType.COMPACT, frame3)
    }

    @Test
    fun autoMode_transientSmallGlitch_recoversWithoutFlipping() {
        val widgetId = 20

        // 1. Kare: Normal geniş
        val frame1 = resolveWidgetLayout(widgetId, NamazWidgetPrefs.LAYOUT_AUTO, 300.dp, 200.dp)
        assertEquals(WidgetLayoutType.EXPANDED, frame1)

        // 2. Kare: Anlık küçük glitch
        val frame2 = resolveWidgetLayout(widgetId, NamazWidgetPrefs.LAYOUT_AUTO, 100.dp, 100.dp)
        assertEquals(WidgetLayoutType.EXPANDED, frame2)

        // 3. Kare: Tekrar geniş boyuta döndü -> glitch absorbe edildi, pending sıfırlandı
        val frame3 = resolveWidgetLayout(widgetId, NamazWidgetPrefs.LAYOUT_AUTO, 300.dp, 200.dp)
        assertEquals(WidgetLayoutType.EXPANDED, frame3)

        // 4. Kare: Gelecekte tek karelik başka bir küçük glitch gelirse yine flip etmez
        val frame4 = resolveWidgetLayout(widgetId, NamazWidgetPrefs.LAYOUT_AUTO, 100.dp, 100.dp)
        assertEquals(WidgetLayoutType.EXPANDED, frame4)
    }

    @Test
    fun autoMode_compactToExpanded_requiresTwoConsecutiveLargeFrames() {
        val widgetId = 25

        // 1. Kare: Kompakt boyut
        val frame1 = resolveWidgetLayout(widgetId, NamazWidgetPrefs.LAYOUT_AUTO, 100.dp, 100.dp)
        assertEquals(WidgetLayoutType.COMPACT, frame1)

        // 2. Kare: Tek karelik geniş boyut -> hemen flip etmez
        val frame2 = resolveWidgetLayout(widgetId, NamazWidgetPrefs.LAYOUT_AUTO, 300.dp, 200.dp)
        assertEquals(WidgetLayoutType.COMPACT, frame2)

        // 3. Kare: İkinci karede de geniş boyut -> flip eder
        val frame3 = resolveWidgetLayout(widgetId, NamazWidgetPrefs.LAYOUT_AUTO, 300.dp, 200.dp)
        assertEquals(WidgetLayoutType.EXPANDED, frame3)
    }

    @Test
    fun legacyPreferences_0and1_mapCorrectly() {
        // Eski kayıt 0: Auto gibi çalışmalı (büyük boyutta Expanded)
        val legacyAuto = resolveWidgetLayout(30, 0, 300.dp, 200.dp)
        assertEquals(WidgetLayoutType.EXPANDED, legacyAuto)

        // Eski kayıt 1: Kompakt olarak kilitli kalmalı (büyük boyutta da Compact)
        val legacyCompact = resolveWidgetLayout(31, 1, 300.dp, 200.dp)
        assertEquals(WidgetLayoutType.COMPACT, legacyCompact)
    }

    @Test
    fun multipleWidgets_haveIsolatedState() {
        val widgetA = 40
        val widgetB = 41

        // Widget A geniş başlatıldı
        resolveWidgetLayout(widgetA, NamazWidgetPrefs.LAYOUT_AUTO, 300.dp, 200.dp)

        // Widget B kompakt başlatıldı
        resolveWidgetLayout(widgetB, NamazWidgetPrefs.LAYOUT_AUTO, 100.dp, 100.dp)

        // Widget A tek kare glitch aldı -> hala EXPANDED
        val aState = resolveWidgetLayout(widgetA, NamazWidgetPrefs.LAYOUT_AUTO, 100.dp, 100.dp)
        assertEquals(WidgetLayoutType.EXPANDED, aState)

        // Widget B hala COMPACT
        val bState = resolveWidgetLayout(widgetB, NamazWidgetPrefs.LAYOUT_AUTO, 100.dp, 100.dp)
        assertEquals(WidgetLayoutType.COMPACT, bState)
    }

    @Test
    fun heightLessThan90dp_resolvesToSingleRow_regardlessOfPreference() {
        // (a) height 70dp -> SingleRow
        // Otomatik modda SingleRow
        val autoSingleRow = resolveWidgetLayout(
            appWidgetId = 50,
            layoutPreference = NamazWidgetPrefs.LAYOUT_AUTO,
            width = 300.dp,
            height = 70.dp
        )
        assertEquals(WidgetLayoutType.SINGLE_ROW, autoSingleRow)

        // Manuel Geniş kilitli olsa bile 70dp'de SingleRow'a düşmeli (fiziksel zorunluluk)
        val expandedSingleRow = resolveWidgetLayout(
            appWidgetId = 51,
            layoutPreference = NamazWidgetPrefs.LAYOUT_EXPANDED,
            width = 300.dp,
            height = 70.dp
        )
        assertEquals(WidgetLayoutType.SINGLE_ROW, expandedSingleRow)

        // Manuel Kompakt kilitli olsa bile 70dp'de SingleRow'a düşmeli
        val compactSingleRow = resolveWidgetLayout(
            appWidgetId = 52,
            layoutPreference = NamazWidgetPrefs.LAYOUT_COMPACT,
            width = 300.dp,
            height = 70.dp
        )
        assertEquals(WidgetLayoutType.SINGLE_ROW, compactSingleRow)

        // 40dp (minHeight) durumunda da SingleRow
        val minHeightSingleRow = resolveWidgetLayout(
            appWidgetId = 53,
            layoutPreference = NamazWidgetPrefs.LAYOUT_AUTO,
            width = 300.dp,
            height = 40.dp
        )
        assertEquals(WidgetLayoutType.SINGLE_ROW, minHeightSingleRow)
    }

    @Test
    fun singleRow_kerahatActive_showsBadgeText() {
        // (b) kerahat + SingleRow -> badge metni "(Kerahat)"
        val kerahatBadge = getSingleRowBadgeText(isKerahat = true)
        assertEquals("(Kerahat)", kerahatBadge)

        // Kerahat aktif değilken badge metni boş olmalı
        val normalBadge = getSingleRowBadgeText(isKerahat = false)
        assertEquals("", normalBadge)

        // İngilizce dilde de badge metni "(Kerahat)"
        val englishBadge = getSingleRowBadgeText(isKerahat = true, language = "en")
        assertEquals("(Kerahat)", englishBadge)
    }

    @Test
    fun height140dpOrMore_preservesPreviousBehavior_noRegression() {
        // (c) 140dp+ -> eski davranış (regresyon yok)
        // 140dp ve üstünde büyük genişlikte Auto -> EXPANDED
        val autoExpandedAt140 = resolveWidgetLayout(
            appWidgetId = 60,
            layoutPreference = NamazWidgetPrefs.LAYOUT_AUTO,
            width = 300.dp,
            height = 140.dp
        )
        assertEquals(WidgetLayoutType.EXPANDED, autoExpandedAt140)

        // 180dp yükseklik Auto -> EXPANDED
        val autoExpandedAt180 = resolveWidgetLayout(
            appWidgetId = 61,
            layoutPreference = NamazWidgetPrefs.LAYOUT_AUTO,
            width = 300.dp,
            height = 180.dp
        )
        assertEquals(WidgetLayoutType.EXPANDED, autoExpandedAt180)

        // 150dp yükseklik ama dar genişlik (180dp < 220dp) -> COMPACT
        val autoCompactNarrow = resolveWidgetLayout(
            appWidgetId = 62,
            layoutPreference = NamazWidgetPrefs.LAYOUT_AUTO,
            width = 180.dp,
            height = 150.dp
        )
        assertEquals(WidgetLayoutType.COMPACT, autoCompactNarrow)

        // Manuel Geniş kilitli iken 140dp+ -> EXPANDED
        val manualExpandedAt140 = resolveWidgetLayout(
            appWidgetId = 63,
            layoutPreference = NamazWidgetPrefs.LAYOUT_EXPANDED,
            width = 100.dp,
            height = 140.dp
        )
        assertEquals(WidgetLayoutType.EXPANDED, manualExpandedAt140)

        // Manuel Kompakt kilitli iken 140dp+ -> COMPACT
        val manualCompactAt140 = resolveWidgetLayout(
            appWidgetId = 64,
            layoutPreference = NamazWidgetPrefs.LAYOUT_COMPACT,
            width = 300.dp,
            height = 140.dp
        )
        assertEquals(WidgetLayoutType.COMPACT, manualCompactAt140)
    }
}
