package co.gorkem.namaz.ui.theme

import android.app.Activity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import co.gorkem.namaz.domain.model.PrayerName

val LocalPrayerContext = staticCompositionLocalOf<PrayerName?> { null }

private fun blend(base: Color, tint: Color, ratio: Float): Color {
    return Color(
        red = base.red + (tint.red - base.red) * ratio,
        green = base.green + (tint.green - base.green) * ratio,
        blue = base.blue + (tint.blue - base.blue) * ratio,
        alpha = base.alpha
    )
}

@Composable
private fun prayerColorScheme(
    base: ColorScheme,
    prayer: PrayerName?,
    isDark: Boolean
): ColorScheme {
    if (prayer == null) return base

    val tint = if (isDark) {
        when (prayer) {
            PrayerName.IMSAK -> PrayerTints.DarkImsak
            PrayerName.GUNES -> PrayerTints.DarkGunes
            PrayerName.OGLE -> PrayerTints.DarkOgle
            PrayerName.IKINDI -> PrayerTints.DarkIkindi
            PrayerName.AKSAM -> PrayerTints.DarkAksam
            PrayerName.YATSI -> PrayerTints.DarkYatsi
        }
    } else {
        when (prayer) {
            PrayerName.IMSAK -> PrayerTints.Imsak
            PrayerName.GUNES -> PrayerTints.Gunes
            PrayerName.OGLE -> PrayerTints.Ogle
            PrayerName.IKINDI -> PrayerTints.Ikindi
            PrayerName.AKSAM -> PrayerTints.Aksam
            PrayerName.YATSI -> PrayerTints.Yatsi
        }
    }

    val animatedTint by animateColorAsState(
        targetValue = tint,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 300f)
    )

    return base.copy(
        surfaceTint = animatedTint,
        surface = blend(base.surface, animatedTint, if (isDark) 0.03f else 0.04f),
        primaryContainer = blend(base.primaryContainer, animatedTint, if (isDark) 0.05f else 0.08f),
        surfaceContainer = blend(base.surfaceContainer, animatedTint, if (isDark) 0.02f else 0.03f),
        surfaceContainerHigh = blend(base.surfaceContainerHigh, animatedTint, if (isDark) 0.03f else 0.04f)
    )
}

@Composable
fun NamazTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    currentTheme: AppThemeSelection = AppThemeSelection.EMERALD_OASIS,
    content: @Composable () -> Unit
) {
    val baseColorScheme = currentTheme.toColorScheme(darkTheme)
    val prayer = LocalPrayerContext.current
    val colorScheme = prayerColorScheme(baseColorScheme, prayer, darkTheme)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = ExpressiveShapes,
        typography = ExpressiveTypography,
        content = content
    )
}
