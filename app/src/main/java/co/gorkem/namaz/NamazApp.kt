package co.gorkem.namaz

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FilterVintage
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.FilterVintage
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import co.gorkem.namaz.R
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.ui.navigation.AppRoute
import co.gorkem.namaz.ui.navigation.BottomSheetSceneStrategy
import co.gorkem.namaz.ui.navigation.Navigator
import co.gorkem.namaz.ui.navigation.rememberNavigationState
import co.gorkem.namaz.ui.navigation.toEntries
import co.gorkem.namaz.ui.screens.calendar.CalendarScreen
import co.gorkem.namaz.ui.screens.cityselection.CitySelectionScreen
import co.gorkem.namaz.ui.screens.home.HomeScreen
import co.gorkem.namaz.ui.screens.qibla.QiblaScreen
import co.gorkem.namaz.ui.screens.settings.SettingsScreen
import co.gorkem.namaz.ui.screens.zikirmatik.ZikirmatikScreen
import co.gorkem.namaz.ui.theme.AppThemeSelection
import co.gorkem.namaz.ui.theme.NamazTheme
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppThemeEntryPoint {
    fun userPreferencesRepository(): UserPreferencesRepository
}

@Composable
fun rememberAppTheme(): AppThemeSelection {
    val context = LocalContext.current
    val entryPoint = remember {
        EntryPointAccessors.fromApplication(context.applicationContext, AppThemeEntryPoint::class.java)
    }
    val repo = remember { entryPoint.userPreferencesRepository() }
    val prefs by repo.preferences.collectAsState(initial = null)
    val ordinal = prefs?.appTheme ?: 0
    return AppThemeSelection.values().getOrElse(ordinal) { AppThemeSelection.EMERALD_OASIS }
}

sealed class BottomNavItem(
    val route: AppRoute,
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Times : BottomNavItem(
        route = AppRoute.Times,
        titleRes = R.string.nav_times,
        selectedIcon = Icons.Filled.Schedule,
        unselectedIcon = Icons.Rounded.Schedule
    )
    data object Qibla : BottomNavItem(
        route = AppRoute.Qibla,
        titleRes = R.string.nav_qibla,
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Rounded.Explore
    )
    data object Zikirmatik : BottomNavItem(
        route = AppRoute.Zikirmatik,
        titleRes = R.string.nav_zikirmatik,
        selectedIcon = Icons.Filled.FilterVintage,
        unselectedIcon = Icons.Rounded.FilterVintage
    )
    data object Calendar : BottomNavItem(
        route = AppRoute.Calendar,
        titleRes = R.string.nav_calendar,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Rounded.CalendarMonth
    )
    data object Settings : BottomNavItem(
        route = AppRoute.Settings,
        titleRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Rounded.Settings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamazApp() {
    val topLevelRoutes: Set<NavKey> = remember {
        setOf(
            AppRoute.Times,
            AppRoute.Qibla,
            AppRoute.Zikirmatik,
            AppRoute.Calendar,
            AppRoute.Settings
        )
    }

    val navigationState = rememberNavigationState(
        startRoute = AppRoute.Times,
        topLevelRoutes = topLevelRoutes
    )

    val navigator = remember { Navigator(navigationState) }
    val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }
    val currentTheme = rememberAppTheme()

    val items = remember {
        listOf(
            BottomNavItem.Times,
            BottomNavItem.Qibla,
            BottomNavItem.Zikirmatik,
            BottomNavItem.Calendar,
            BottomNavItem.Settings
        )
    }

    val topLevelOrder = remember {
        listOf(
            AppRoute.Times,
            AppRoute.Qibla,
            AppRoute.Zikirmatik,
            AppRoute.Calendar,
            AppRoute.Settings
        )
    }

    val entryProvider = entryProvider<NavKey> {
        entry<AppRoute.Times> {
            HomeScreen(
                onLocationClick = { navigator.navigate(AppRoute.CitySelection) },
                onNavigateToTesbihat = { navigator.navigate(AppRoute.Zikirmatik) }
            )
        }
        entry<AppRoute.Qibla> {
            QiblaScreen(
                onSetLocationClick = { navigator.navigate(AppRoute.CitySelection) }
            )
        }
        entry<AppRoute.Zikirmatik> {
            ZikirmatikScreen()
        }
        entry<AppRoute.Calendar> {
            CalendarScreen()
        }
        entry<AppRoute.Settings> {
            SettingsScreen()
        }
        entry<AppRoute.CitySelection>(
            metadata = BottomSheetSceneStrategy.bottomSheet()
        ) {
            CitySelectionScreen(
                onCitySelected = { navigator.goBack() },
                onBack = { navigator.goBack() }
            )
        }
    }

    NamazTheme(currentTheme = currentTheme) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    items.forEach { item ->
                        val selected = navigationState.topLevelRoute == item.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = stringResource(item.titleRes)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.titleRes),
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            selected = selected,
                            onClick = { navigator.navigate(item.route) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                modifier = Modifier.padding(paddingValues),
                onBack = { navigator.goBack() },
                sceneStrategy = remember { bottomSheetStrategy.then(SinglePaneSceneStrategy()) },
                transitionSpec = {
                    val initialKey = initialState.key as? AppRoute
                    val targetKey = targetState.key as? AppRoute
                    val initialIdx = topLevelOrder.indexOf(initialKey).takeIf { it >= 0 } ?: 0
                    val targetIdx = topLevelOrder.indexOf(targetKey).takeIf { it >= 0 } ?: 0
                    val direction = if (targetIdx >= initialIdx) 1 else -1
                    slideInHorizontally(
                        animationSpec = tween(300, easing = EaseInOut),
                        initialOffsetX = { fullWidth -> fullWidth / 5 * direction }
                    ) + fadeIn(tween(300)) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(300, easing = EaseInOut),
                        targetOffsetX = { fullWidth -> fullWidth / 5 * -direction }
                    ) + fadeOut(tween(300))
                },
                popTransitionSpec = {
                    val initialKey = initialState.key as? AppRoute
                    val targetKey = targetState.key as? AppRoute
                    val initialIdx = topLevelOrder.indexOf(initialKey).takeIf { it >= 0 } ?: 0
                    val targetIdx = topLevelOrder.indexOf(targetKey).takeIf { it >= 0 } ?: 0
                    val direction = if (targetIdx >= initialIdx) 1 else -1
                    slideInHorizontally(
                        animationSpec = tween(300, easing = EaseInOut),
                        initialOffsetX = { fullWidth -> fullWidth / 5 * direction }
                    ) + fadeIn(tween(300)) togetherWith
                    slideOutHorizontally(
                        animationSpec = tween(300, easing = EaseInOut),
                        targetOffsetX = { fullWidth -> fullWidth / 5 * -direction }
                    ) + fadeOut(tween(300))
                }
            )
        }
    }
}
