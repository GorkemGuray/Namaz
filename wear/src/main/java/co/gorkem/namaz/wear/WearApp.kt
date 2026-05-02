package co.gorkem.namaz.wear

import androidx.compose.runtime.Composable
import co.gorkem.namaz.wear.ui.screens.WearHomeScreen
import co.gorkem.namaz.wear.ui.theme.WearTheme

@Composable
fun WearApp() {
    WearTheme {
        WearHomeScreen()
    }
}
