package co.gorkem.namaz.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

private val WearColorScheme = ColorScheme(
    primary = WearPrimary,
    onPrimary = WearOnPrimary,
    primaryContainer = WearPrimaryContainer,
    onPrimaryContainer = WearOnPrimaryContainer,
    primaryDim = WearPrimaryContainer,
    secondary = WearSecondary,
    onSecondary = WearOnSecondary,
    secondaryContainer = WearSecondaryContainer,
    onSecondaryContainer = WearOnSecondaryContainer,
    secondaryDim = WearSecondaryContainer,
    tertiary = WearTertiary,
    onTertiary = WearOnTertiary,
    tertiaryContainer = WearTertiaryContainer,
    onTertiaryContainer = WearOnTertiaryContainer,
    tertiaryDim = WearTertiaryContainer,
    background = WearBackground,
    onBackground = WearOnBackground,
    surfaceContainer = WearSurface,
    surfaceContainerLow = WearBackground,
    surfaceContainerHigh = WearSurface,
    onSurface = WearOnSurface,
    onSurfaceVariant = WearOnSurfaceVariant,
    outline = WearOutline,
    outlineVariant = WearOutlineVariant,
    error = WearError,
    onError = WearOnError,
    errorContainer = WearErrorContainer,
    onErrorContainer = WearOnErrorContainer,
    errorDim = WearErrorContainer
)

@Composable
fun WearTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WearColorScheme,
        typography = WearTypography,
        content = content
    )
}
