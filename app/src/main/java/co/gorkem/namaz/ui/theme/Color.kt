package co.gorkem.namaz.ui.theme

import androidx.compose.ui.graphics.Color

// ========== M3 Expressive Light Color Roles ==========
// Spiritual indigo + sage green + warm amber palette

val LightPrimary = Color(0xFF2E5B7A)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFD0E4FF)
val LightOnPrimaryContainer = Color(0xFF001D33)
val LightPrimaryFixed = Color(0xFFD0E4FF)
val LightPrimaryFixedDim = Color(0xFF9ECAFF)
val LightOnPrimaryFixed = Color(0xFF001D33)
val LightOnPrimaryFixedVariant = Color(0xFF0E4663)

val LightSecondary = Color(0xFF4D5E4E)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFCFE3CE)
val LightOnSecondaryContainer = Color(0xFF0A1B0F)
val LightSecondaryFixed = Color(0xFFCFE3CE)
val LightSecondaryFixedDim = Color(0xFFB4C7B3)
val LightOnSecondaryFixed = Color(0xFF0A1B0F)
val LightOnSecondaryFixedVariant = Color(0xFF364638)

val LightTertiary = Color(0xFF7A5900)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFFFDEA1)
val LightOnTertiaryContainer = Color(0xFF271900)
val LightTertiaryFixed = Color(0xFFFFDEA1)
val LightTertiaryFixedDim = Color(0xFFE8C462)
val LightOnTertiaryFixed = Color(0xFF271900)
val LightOnTertiaryFixedVariant = Color(0xFF5C4300)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

val LightBackground = Color(0xFFF8F9FF)
val LightOnBackground = Color(0xFF191C20)
val LightSurface = Color(0xFFF8F9FF)
val LightOnSurface = Color(0xFF191C20)
val LightSurfaceVariant = Color(0xFFDFE2EB)
val LightOnSurfaceVariant = Color(0xFF43474E)
val LightSurfaceTint = Color(0xFF2E5B7A)
val LightInverseSurface = Color(0xFF2E3135)
val LightInverseOnSurface = Color(0xFFF0F0F5)
val LightInversePrimary = Color(0xFF9ECAFF)
val LightSurfaceDim = Color(0xFFD9DBE1)
val LightSurfaceBright = Color(0xFFF8F9FF)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF2F4FA)
val LightSurfaceContainer = Color(0xFFEDEEF4)
val LightSurfaceContainerHigh = Color(0xFFE7E8EE)
val LightSurfaceContainerHighest = Color(0xFFE1E3E8)

val LightOutline = Color(0xFF73777F)
val LightOutlineVariant = Color(0xFFC3C7CF)
val LightScrim = Color(0xFF000000)

// ========== M3 Expressive Dark Color Roles ==========

val DarkPrimary = Color(0xFF9ECAFF)
val DarkOnPrimary = Color(0xFF00304E)
val DarkPrimaryContainer = Color(0xFF0E4663)
val DarkOnPrimaryContainer = Color(0xFFD0E4FF)
val DarkPrimaryFixed = Color(0xFFD0E4FF)
val DarkPrimaryFixedDim = Color(0xFF9ECAFF)
val DarkOnPrimaryFixed = Color(0xFF001D33)
val DarkOnPrimaryFixedVariant = Color(0xFF0E4663)

val DarkSecondary = Color(0xFFB4C7B3)
val DarkOnSecondary = Color(0xFF1F3021)
val DarkSecondaryContainer = Color(0xFF364638)
val DarkOnSecondaryContainer = Color(0xFFCFE3CE)
val DarkSecondaryFixed = Color(0xFFCFE3CE)
val DarkSecondaryFixedDim = Color(0xFFB4C7B3)
val DarkOnSecondaryFixed = Color(0xFF0A1B0F)
val DarkOnSecondaryFixedVariant = Color(0xFF364638)

val DarkTertiary = Color(0xFFE8C462)
val DarkOnTertiary = Color(0xFF402D00)
val DarkTertiaryContainer = Color(0xFF5C4300)
val DarkOnTertiaryContainer = Color(0xFFFFDEA1)
val DarkTertiaryFixed = Color(0xFFFFDEA1)
val DarkTertiaryFixedDim = Color(0xFFE8C462)
val DarkOnTertiaryFixed = Color(0xFF271900)
val DarkOnTertiaryFixedVariant = Color(0xFF5C4300)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

val DarkBackground = Color(0xFF111318)
val DarkOnBackground = Color(0xFFE1E3E8)
val DarkSurface = Color(0xFF111318)
val DarkOnSurface = Color(0xFFE1E3E8)
val DarkSurfaceVariant = Color(0xFF43474E)
val DarkOnSurfaceVariant = Color(0xFFC3C7CF)
val DarkSurfaceTint = Color(0xFF9ECAFF)
val DarkInverseSurface = Color(0xFFE1E3E8)
val DarkInverseOnSurface = Color(0xFF191C20)
val DarkInversePrimary = Color(0xFF2E5B7A)
val DarkSurfaceDim = Color(0xFF111318)
val DarkSurfaceBright = Color(0xFF37393E)
val DarkSurfaceContainerLowest = Color(0xFF0C0E13)
val DarkSurfaceContainerLow = Color(0xFF191C20)
val DarkSurfaceContainer = Color(0xFF1D2024)
val DarkSurfaceContainerHigh = Color(0xFF282A2F)
val DarkSurfaceContainerHighest = Color(0xFF33353A)

val DarkOutline = Color(0xFF8D9199)
val DarkOutlineVariant = Color(0xFF43474E)
val DarkScrim = Color(0xFF000000)

// ========== Prayer-Specific Tint Colors ==========
// Subtle accent tints that shift the theme based on next prayer

object PrayerTints {
    val Imsak = Color(0xFF1A2E4A)    // Pre-dawn deep blue
    val Gunes = Color(0xFFE8983A)    // Sunrise warm amber
    val Ogle = Color(0xFFE6C840)     // Noon golden yellow
    val Ikindi = Color(0xFFD97340)   // Afternoon terracotta
    val Aksam = Color(0xFFC04A4A)    // Sunset rose-red
    val Yatsi = Color(0xFF2A1B4A)    // Night deep indigo

    // Dark mode tints (lighter, more vibrant)
    val DarkImsak = Color(0xFF4A72A0)
    val DarkGunes = Color(0xFFF0B050)
    val DarkOgle = Color(0xFFF0D860)
    val DarkIkindi = Color(0xFFE09860)
    val DarkAksam = Color(0xFFE07070)
    val DarkYatsi = Color(0xFF6A5AC0)
}

// ========== Legacy names for backward compatibility ==========

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
