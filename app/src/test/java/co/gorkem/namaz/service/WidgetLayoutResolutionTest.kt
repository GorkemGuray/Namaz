package co.gorkem.namaz.service

import co.gorkem.namaz.R
import co.gorkem.namaz.ui.theme.AppThemeSelection
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetLayoutResolutionTest {

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
}
