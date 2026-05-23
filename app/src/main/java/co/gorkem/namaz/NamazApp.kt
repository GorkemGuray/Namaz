package co.gorkem.namaz

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R
import co.gorkem.namaz.ui.screens.calendar.CalendarScreen
import co.gorkem.namaz.ui.screens.cityselection.CitySelectionScreen
import co.gorkem.namaz.ui.screens.home.HomeScreen
import co.gorkem.namaz.ui.screens.qibla.QiblaScreen
import co.gorkem.namaz.ui.screens.settings.SettingsScreen

sealed class BottomNavItem(
    val titleRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Times : BottomNavItem(
        titleRes = R.string.nav_times,
        selectedIcon = Icons.Filled.Schedule,
        unselectedIcon = Icons.Rounded.Schedule
    )
    data object Calendar : BottomNavItem(
        titleRes = R.string.nav_calendar,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Rounded.CalendarMonth
    )
    data object Qibla : BottomNavItem(
        titleRes = R.string.nav_qibla,
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Rounded.Explore
    )
    data object Settings : BottomNavItem(
        titleRes = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Rounded.Settings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamazApp() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showCitySheet by rememberSaveable { mutableStateOf(false) }

    val items = listOf(BottomNavItem.Times, BottomNavItem.Calendar, BottomNavItem.Qibla, BottomNavItem.Settings)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                items.forEachIndexed { index, item ->
                    val selected = selectedTab == index
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
                        onClick = { selectedTab = index },
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
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                slideInHorizontally(
                    animationSpec = tween(300, easing = EaseInOut),
                    initialOffsetX = { fullWidth -> fullWidth / 5 * direction }
                ) + fadeIn(tween(300)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(300, easing = EaseInOut),
                    targetOffsetX = { fullWidth -> fullWidth / 5 * -direction }
                ) + fadeOut(tween(300))
            },
            label = "tab_switch"
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(
                    modifier = Modifier.padding(paddingValues),
                    onLocationClick = { showCitySheet = true }
                )
                1 -> CalendarScreen(
                    modifier = Modifier.padding(paddingValues)
                )
                2 -> QiblaScreen(
                    modifier = Modifier.padding(paddingValues),
                    onSetLocationClick = { showCitySheet = true }
                )
                3 -> SettingsScreen(modifier = Modifier.padding(paddingValues))
            }
        }
    }

    if (showCitySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCitySheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            CitySelectionScreen(
                onCitySelected = { showCitySheet = false },
                onBack = { showCitySheet = false }
            )
        }
    }
}
