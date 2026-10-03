package co.gorkem.namaz.ui.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeIdStabilityTest {

    @Test
    fun appThemeSelection_hasExactElevenEntries() {
        assertEquals(11, AppThemeSelection.entries.size)
    }

    @Test
    fun appThemeSelection_existingIdsArePreservedInOrder() {
        assertEquals(0, AppThemeSelection.EMERALD_OASIS.ordinal)
        assertEquals(1, AppThemeSelection.SAPPHIRE_NIGHT.ordinal)
        assertEquals(2, AppThemeSelection.AMBER_SANDS.ordinal)
        assertEquals(3, AppThemeSelection.MONOCHROME_PLATINUM.ordinal)
    }

    @Test
    fun appThemeSelection_newIdsAreAppendedInExactReferenceOrder() {
        assertEquals(4, AppThemeSelection.YUMUSAK_AYDINLIK.ordinal)
        assertEquals(5, AppThemeSelection.TAM_AYDINLIK.ordinal)
        assertEquals(6, AppThemeSelection.SAMAN_KAGIDI.ordinal)
        assertEquals(7, AppThemeSelection.KARANLIK.ordinal)
        assertEquals(8, AppThemeSelection.GALAKSI.ordinal)
        assertEquals(9, AppThemeSelection.GUN_BATIMI.ordinal)
        assertEquals(10, AppThemeSelection.AMBER_GECE.ordinal)
    }

    @Test
    fun appThemeSelection_fixedLookSemantics_correctlyIdentified() {
        assertFalse(AppThemeSelection.EMERALD_OASIS.isFixedLook)
        assertFalse(AppThemeSelection.SAPPHIRE_NIGHT.isFixedLook)
        assertFalse(AppThemeSelection.AMBER_SANDS.isFixedLook)
        assertFalse(AppThemeSelection.MONOCHROME_PLATINUM.isFixedLook)

        assertTrue(AppThemeSelection.YUMUSAK_AYDINLIK.isFixedLook)
        assertTrue(AppThemeSelection.TAM_AYDINLIK.isFixedLook)
        assertTrue(AppThemeSelection.SAMAN_KAGIDI.isFixedLook)
        assertTrue(AppThemeSelection.KARANLIK.isFixedLook)
        assertTrue(AppThemeSelection.GALAKSI.isFixedLook)
        assertTrue(AppThemeSelection.GUN_BATIMI.isFixedLook)
        assertTrue(AppThemeSelection.AMBER_GECE.isFixedLook)
    }

    @Test
    fun appThemeSelection_fixedLookThemes_polarityIsCorrect() {
        assertFalse(AppThemeSelection.YUMUSAK_AYDINLIK.isDarkFixed)
        assertFalse(AppThemeSelection.TAM_AYDINLIK.isDarkFixed)
        assertFalse(AppThemeSelection.SAMAN_KAGIDI.isDarkFixed)

        assertTrue(AppThemeSelection.KARANLIK.isDarkFixed)
        assertTrue(AppThemeSelection.GALAKSI.isDarkFixed)
        assertTrue(AppThemeSelection.GUN_BATIMI.isDarkFixed)
        assertTrue(AppThemeSelection.AMBER_GECE.isDarkFixed)
    }

    @Test
    fun appThemeSelection_existingThemes_varyByDarkThemeParam() {
        val dynamicThemes = listOf(
            AppThemeSelection.EMERALD_OASIS,
            AppThemeSelection.SAPPHIRE_NIGHT,
            AppThemeSelection.AMBER_SANDS,
            AppThemeSelection.MONOCHROME_PLATINUM
        )

        for (theme in dynamicThemes) {
            val lightScheme = theme.toColorScheme(darkTheme = false)
            val darkScheme = theme.toColorScheme(darkTheme = true)
            assertNotEquals(lightScheme.background, darkScheme.background)
        }
    }

    @Test
    fun appThemeSelection_fixedLookThemes_ignoreDarkThemeParam() {
        val fixedThemes = listOf(
            AppThemeSelection.YUMUSAK_AYDINLIK,
            AppThemeSelection.TAM_AYDINLIK,
            AppThemeSelection.SAMAN_KAGIDI,
            AppThemeSelection.KARANLIK,
            AppThemeSelection.GALAKSI,
            AppThemeSelection.GUN_BATIMI,
            AppThemeSelection.AMBER_GECE
        )

        for (theme in fixedThemes) {
            val lightScheme = theme.toColorScheme(darkTheme = false)
            val darkScheme = theme.toColorScheme(darkTheme = true)
            assertEquals(lightScheme.background, darkScheme.background)
            assertEquals(lightScheme.primary, darkScheme.primary)
            assertEquals(lightScheme.surface, darkScheme.surface)
            assertEquals(lightScheme.onSurface, darkScheme.onSurface)
        }
    }
}
