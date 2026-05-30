package co.gorkem.namaz.wear

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import co.gorkem.namaz.wear.ui.WearViewModel
import co.gorkem.namaz.wear.ui.screens.WearHomeScreen
import co.gorkem.namaz.wear.ui.screens.WearQiblaScreen
import co.gorkem.namaz.wear.ui.screens.WearZikirmatikScreen
import co.gorkem.namaz.wear.ui.theme.WearTheme

@Composable
fun WearApp(
    viewModel: WearViewModel = hiltViewModel()
) {
    val themeId by viewModel.themeId.collectAsState()
    val navController = androidx.wear.compose.navigation.rememberSwipeDismissableNavController()
    
    WearTheme(themeId = themeId) {
        AppScaffold {
            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") {
                    WearHomeScreen(
                        viewModel = viewModel,
                        onNavigateToQibla = {
                            navController.navigate("qibla")
                        },
                        onNavigateToZikirmatik = {
                            navController.navigate("zikirmatik")
                        }
                    )
                }
                composable("qibla") {
                    WearQiblaScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
                composable("zikirmatik") {
                    WearZikirmatikScreen(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
