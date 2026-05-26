package co.gorkem.namaz.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

@Composable
fun WearTheme(
    themeId: Int = 0,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeId) {
        0 -> EmeraldColorScheme
        1 -> SapphireColorScheme
        2 -> AmberColorScheme
        3 -> MonochromeColorScheme
        else -> EmeraldColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WearTypography,
        content = content
    )
}
