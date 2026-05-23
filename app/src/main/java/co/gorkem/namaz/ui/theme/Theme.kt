package co.gorkem.namaz.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import co.gorkem.namaz.domain.model.PrayerName

// ========== Fallback Color Schemes ==========

internal val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceTint = LightSurfaceTint,
    inverseSurface = LightInverseSurface,
    inverseOnSurface = LightInverseOnSurface,
    inversePrimary = LightInversePrimary,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim,
    surfaceDim = LightSurfaceDim,
    surfaceBright = LightSurfaceBright,
    surfaceContainerLowest = LightSurfaceContainerLowest,
    surfaceContainerLow = LightSurfaceContainerLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceContainerHigh,
    surfaceContainerHighest = LightSurfaceContainerHighest
)

internal val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceTint = DarkSurfaceTint,
    inverseSurface = DarkInverseSurface,
    inverseOnSurface = DarkInverseOnSurface,
    inversePrimary = DarkInversePrimary,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    scrim = DarkScrim,
    surfaceDim = DarkSurfaceDim,
    surfaceBright = DarkSurfaceBright,
    surfaceContainerLowest = DarkSurfaceContainerLowest,
    surfaceContainerLow = DarkSurfaceContainerLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaceContainerHighest
)

// ========== Prayer Context ==========

/**
 * CompositionLocal for the current prayer context.
 * When set, the theme subtly shifts its color tone to match the prayer time.
 */
val LocalPrayerContext = staticCompositionLocalOf<PrayerName?> { null }

// ========== Color Blend Utility ==========

private fun blend(base: Color, tint: Color, ratio: Float): Color {
    return Color(
        red = base.red + (tint.red - base.red) * ratio,
        green = base.green + (tint.green - base.green) * ratio,
        blue = base.blue + (tint.blue - base.blue) * ratio,
        alpha = base.alpha
    )
}

// ========== Prayer-Tinted Color Scheme ==========

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

// ========== Main Theme Composable ==========

@Composable
fun NamazTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val prayer = LocalPrayerContext.current
    val colorScheme = prayerColorScheme(baseColorScheme, prayer, darkTheme)

    // Apply edge-to-edge status bar styling
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
