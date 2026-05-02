package co.gorkem.namaz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.gorkem.namaz.ui.screens.cityselection.CitySelectionScreen
import co.gorkem.namaz.ui.screens.home.HomeScreen
import co.gorkem.namaz.ui.screens.settings.SettingsScreen

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val CITY_SELECTION = "city_selection"
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                onNavigateToCitySelection = {
                    navController.navigate(Routes.CITY_SELECTION)
                }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CITY_SELECTION) {
            CitySelectionScreen(
                onCitySelected = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}