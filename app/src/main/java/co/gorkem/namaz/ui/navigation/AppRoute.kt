package co.gorkem.namaz.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object Times : AppRoute

    @Serializable
    data object Qibla : AppRoute

    @Serializable
    data object Zikirmatik : AppRoute

    @Serializable
    data object Calendar : AppRoute

    @Serializable
    data object Settings : AppRoute

    @Serializable
    data object CitySelection : AppRoute
}
