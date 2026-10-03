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
        4 -> YumusakAydinlikColorScheme
        5 -> TamAydinlikColorScheme
        6 -> SamanKagidiColorScheme
        7 -> KaranlikColorScheme
        8 -> GalaksiColorScheme
        9 -> GunBatimiColorScheme
        10 -> AmberGeceColorScheme
        else -> EmeraldColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WearTypography,
        content = content
    )
}
