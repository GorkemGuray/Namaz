package co.gorkem.namaz.wear.ui.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Colors

private val WearColors = Colors(
    primary = androidx.compose.ui.graphics.Color(0xFF4CAF50),
    primaryVariant = androidx.compose.ui.graphics.Color(0xFF388E3C),
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC6),
    secondaryVariant = androidx.compose.ui.graphics.Color(0xFF018786),
    background = androidx.compose.ui.graphics.Color.Black,
    surface = androidx.compose.ui.graphics.Color.Black,
    error = androidx.compose.ui.graphics.Color(0xFFCF6679),
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    onSecondary = androidx.compose.ui.graphics.Color.Black,
    onBackground = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color.White,
    onError = androidx.compose.ui.graphics.Color.Black
)

@Composable
fun WearTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = WearColors,
        content = content
    )
}
