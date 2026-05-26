package co.gorkem.namaz.wear

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.AppScaffold
import co.gorkem.namaz.wear.ui.screens.WearHomeScreen
import co.gorkem.namaz.wear.ui.theme.WearTheme

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import co.gorkem.namaz.wear.ui.WearViewModel

@Composable
fun WearApp(
    viewModel: WearViewModel = hiltViewModel()
) {
    val themeId by viewModel.themeId.collectAsState()
    
    WearTheme(themeId = themeId) {
        AppScaffold {
            WearHomeScreen(viewModel = viewModel)
        }
    }
}
