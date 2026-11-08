package co.gorkem.namaz.ui.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NavigationStateTest {

    private lateinit var navigationState: NavigationState
    private lateinit var navigator: Navigator

    private val topLevelRoutes = setOf<NavKey>(
        AppRoute.Times,
        AppRoute.Qibla,
        AppRoute.Zikirmatik,
        AppRoute.Calendar,
        AppRoute.Settings
    )

    @Before
    fun setUp() {
        val backStacks = topLevelRoutes.associateWith { key -> NavBackStack(key) }
        val topLevelRoute = mutableStateOf<NavKey>(AppRoute.Times)
        navigationState = NavigationState(
            startRoute = AppRoute.Times,
            topLevelRoute = topLevelRoute,
            backStacks = backStacks
        )
        navigator = Navigator(navigationState)
    }

    @Test
    fun initialState_startsAtTimes() {
        assertEquals(AppRoute.Times, navigationState.startRoute)
        assertEquals(AppRoute.Times, navigationState.topLevelRoute)
        assertEquals(listOf(AppRoute.Times), navigationState.stacksInUse)
    }

    @Test
    fun switchTopLevelRoute_updatesTopLevelRouteAndStacksInUse() {
        navigator.navigate(AppRoute.Qibla)
        assertEquals(AppRoute.Qibla, navigationState.topLevelRoute)
        assertEquals(listOf(AppRoute.Times, AppRoute.Qibla), navigationState.stacksInUse)
    }

    @Test
    fun pushSubRoute_addsToCurrentTabBackStack() {
        navigator.navigate(AppRoute.Qibla)
        navigator.navigate(AppRoute.CitySelection)

        val qiblaStack = navigationState.backStacks[AppRoute.Qibla]!!
        assertEquals(2, qiblaStack.size)
        assertEquals(listOf(AppRoute.Qibla, AppRoute.CitySelection), qiblaStack)
        assertEquals(AppRoute.Qibla, navigationState.topLevelRoute)
    }

    @Test
    fun goBack_fromSubRoute_popsSubRouteAndKeepsCurrentTab() {
        navigator.navigate(AppRoute.Qibla)
        navigator.navigate(AppRoute.CitySelection)

        val handled = navigator.goBack()
        assertTrue(handled)

        val qiblaStack = navigationState.backStacks[AppRoute.Qibla]!!
        assertEquals(1, qiblaStack.size)
        assertEquals(listOf(AppRoute.Qibla), qiblaStack)
        assertEquals(AppRoute.Qibla, navigationState.topLevelRoute)
    }

    @Test
    fun goBack_fromTopLevelNonStartRoute_returnsToStartRoute() {
        navigator.navigate(AppRoute.Calendar)
        assertEquals(AppRoute.Calendar, navigationState.topLevelRoute)

        val handled = navigator.goBack()
        assertTrue(handled)
        assertEquals(AppRoute.Times, navigationState.topLevelRoute)
        assertEquals(listOf(AppRoute.Times), navigationState.stacksInUse)
    }

    @Test
    fun goBack_fromStartRouteBase_returnsFalseForExit() {
        val handled = navigator.goBack()
        assertFalse(handled)
        assertEquals(AppRoute.Times, navigationState.topLevelRoute)
    }

    @Test
    fun tabSwitching_preservesIndependentStacks() {
        navigator.navigate(AppRoute.CitySelection) // pushes to Times stack
        val timesStack = navigationState.backStacks[AppRoute.Times]!!
        assertEquals(listOf(AppRoute.Times, AppRoute.CitySelection), timesStack)

        navigator.navigate(AppRoute.Settings)
        assertEquals(AppRoute.Settings, navigationState.topLevelRoute)

        navigator.navigate(AppRoute.Times)
        assertEquals(AppRoute.Times, navigationState.topLevelRoute)
        assertEquals(listOf(AppRoute.Times, AppRoute.CitySelection), navigationState.backStacks[AppRoute.Times])
    }

    @Test
    fun allFiveTabs_canBeNavigatedTo() {
        val tabs = listOf(
            AppRoute.Times,
            AppRoute.Qibla,
            AppRoute.Zikirmatik,
            AppRoute.Calendar,
            AppRoute.Settings
        )

        for (tab in tabs) {
            navigator.navigate(tab)
            assertEquals(tab, navigationState.topLevelRoute)
        }
    }
}
