package co.gorkem.namaz.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppThemeSelection {
    EMERALD_OASIS,
    SAPPHIRE_NIGHT,
    AMBER_SANDS,
    MONOCHROME_PLATINUM
}

private data class ThemeTokens(
    val lightPrimary: Color, val lightOnPrimary: Color,
    val lightPrimaryContainer: Color, val lightOnPrimaryContainer: Color,
    val lightPrimaryFixed: Color, val lightPrimaryFixedDim: Color,
    val lightOnPrimaryFixed: Color, val lightOnPrimaryFixedVariant: Color,
    val lightSecondary: Color, val lightOnSecondary: Color,
    val lightSecondaryContainer: Color, val lightOnSecondaryContainer: Color,
    val lightSecondaryFixed: Color, val lightSecondaryFixedDim: Color,
    val lightOnSecondaryFixed: Color, val lightOnSecondaryFixedVariant: Color,
    val lightTertiary: Color, val lightOnTertiary: Color,
    val lightTertiaryContainer: Color, val lightOnTertiaryContainer: Color,
    val lightTertiaryFixed: Color, val lightTertiaryFixedDim: Color,
    val lightOnTertiaryFixed: Color, val lightOnTertiaryFixedVariant: Color,
    val lightError: Color, val lightOnError: Color,
    val lightErrorContainer: Color, val lightOnErrorContainer: Color,
    val lightBackground: Color, val lightOnBackground: Color,
    val lightSurface: Color, val lightOnSurface: Color,
    val lightSurfaceVariant: Color, val lightOnSurfaceVariant: Color,
    val lightSurfaceTint: Color,
    val lightInverseSurface: Color, val lightInverseOnSurface: Color, val lightInversePrimary: Color,
    val lightSurfaceDim: Color, val lightSurfaceBright: Color,
    val lightSurfaceContainerLowest: Color, val lightSurfaceContainerLow: Color,
    val lightSurfaceContainer: Color, val lightSurfaceContainerHigh: Color, val lightSurfaceContainerHighest: Color,
    val lightOutline: Color, val lightOutlineVariant: Color, val lightScrim: Color,
    val darkPrimary: Color, val darkOnPrimary: Color,
    val darkPrimaryContainer: Color, val darkOnPrimaryContainer: Color,
    val darkPrimaryFixed: Color, val darkPrimaryFixedDim: Color,
    val darkOnPrimaryFixed: Color, val darkOnPrimaryFixedVariant: Color,
    val darkSecondary: Color, val darkOnSecondary: Color,
    val darkSecondaryContainer: Color, val darkOnSecondaryContainer: Color,
    val darkSecondaryFixed: Color, val darkSecondaryFixedDim: Color,
    val darkOnSecondaryFixed: Color, val darkOnSecondaryFixedVariant: Color,
    val darkTertiary: Color, val darkOnTertiary: Color,
    val darkTertiaryContainer: Color, val darkOnTertiaryContainer: Color,
    val darkTertiaryFixed: Color, val darkTertiaryFixedDim: Color,
    val darkOnTertiaryFixed: Color, val darkOnTertiaryFixedVariant: Color,
    val darkError: Color, val darkOnError: Color,
    val darkErrorContainer: Color, val darkOnErrorContainer: Color,
    val darkBackground: Color, val darkOnBackground: Color,
    val darkSurface: Color, val darkOnSurface: Color,
    val darkSurfaceVariant: Color, val darkOnSurfaceVariant: Color,
    val darkSurfaceTint: Color,
    val darkInverseSurface: Color, val darkInverseOnSurface: Color, val darkInversePrimary: Color,
    val darkSurfaceDim: Color, val darkSurfaceBright: Color,
    val darkSurfaceContainerLowest: Color, val darkSurfaceContainerLow: Color,
    val darkSurfaceContainer: Color, val darkSurfaceContainerHigh: Color, val darkSurfaceContainerHighest: Color,
    val darkOutline: Color, val darkOutlineVariant: Color, val darkScrim: Color
) {
    fun buildLightColorScheme() = lightColorScheme(
        primary = lightPrimary, onPrimary = lightOnPrimary,
        primaryContainer = lightPrimaryContainer, onPrimaryContainer = lightOnPrimaryContainer,
        secondary = lightSecondary, onSecondary = lightOnSecondary,
        secondaryContainer = lightSecondaryContainer, onSecondaryContainer = lightOnSecondaryContainer,
        tertiary = lightTertiary, onTertiary = lightOnTertiary,
        tertiaryContainer = lightTertiaryContainer, onTertiaryContainer = lightOnTertiaryContainer,
        error = lightError, onError = lightOnError,
        errorContainer = lightErrorContainer, onErrorContainer = lightOnErrorContainer,
        background = lightBackground, onBackground = lightOnBackground,
        surface = lightSurface, onSurface = lightOnSurface,
        surfaceVariant = lightSurfaceVariant, onSurfaceVariant = lightOnSurfaceVariant,
        surfaceTint = lightSurfaceTint,
        inverseSurface = lightInverseSurface, inverseOnSurface = lightInverseOnSurface,
        inversePrimary = lightInversePrimary,
        surfaceDim = lightSurfaceDim, surfaceBright = lightSurfaceBright,
        surfaceContainerLowest = lightSurfaceContainerLowest,
        surfaceContainerLow = lightSurfaceContainerLow,
        surfaceContainer = lightSurfaceContainer,
        surfaceContainerHigh = lightSurfaceContainerHigh,
        surfaceContainerHighest = lightSurfaceContainerHighest,
        outline = lightOutline, outlineVariant = lightOutlineVariant, scrim = lightScrim
    )

    fun buildDarkColorScheme() = darkColorScheme(
        primary = darkPrimary, onPrimary = darkOnPrimary,
        primaryContainer = darkPrimaryContainer, onPrimaryContainer = darkOnPrimaryContainer,
        secondary = darkSecondary, onSecondary = darkOnSecondary,
        secondaryContainer = darkSecondaryContainer, onSecondaryContainer = darkOnSecondaryContainer,
        tertiary = darkTertiary, onTertiary = darkOnTertiary,
        tertiaryContainer = darkTertiaryContainer, onTertiaryContainer = darkOnTertiaryContainer,
        error = darkError, onError = darkOnError,
        errorContainer = darkErrorContainer, onErrorContainer = darkOnErrorContainer,
        background = darkBackground, onBackground = darkOnBackground,
        surface = darkSurface, onSurface = darkOnSurface,
        surfaceVariant = darkSurfaceVariant, onSurfaceVariant = darkOnSurfaceVariant,
        surfaceTint = darkSurfaceTint,
        inverseSurface = darkInverseSurface, inverseOnSurface = darkInverseOnSurface,
        inversePrimary = darkInversePrimary,
        surfaceDim = darkSurfaceDim, surfaceBright = darkSurfaceBright,
        surfaceContainerLowest = darkSurfaceContainerLowest,
        surfaceContainerLow = darkSurfaceContainerLow,
        surfaceContainer = darkSurfaceContainer,
        surfaceContainerHigh = darkSurfaceContainerHigh,
        surfaceContainerHighest = darkSurfaceContainerHighest,
        outline = darkOutline, outlineVariant = darkOutlineVariant, scrim = darkScrim
    )
}

// ==================================================================
// EMERALD OASIS — Default Modern Islamic Aesthetic
// Surface: #FBFBFA | Primary: #1E3A34 | SecondaryContainer: #E8F0EC
// OnSurface: #1C1B1F | OnSurfaceVariant: #6A6A6A
// Dark Surface: #121414 | Dark Primary: #3B6053 | Dark SecondaryContainer: #1F2E2A
// Dark OnSurface: #E6E6E6 | Dark OnSurfaceVariant: #909090
// ==================================================================
private val EmeraldTokens = ThemeTokens(
    lightPrimary = Color(0xFF1E3A34), lightOnPrimary = Color(0xFFFFFFFF),
    lightPrimaryContainer = Color(0xFFE8F0EC), lightOnPrimaryContainer = Color(0xFF1E3A34),
    lightPrimaryFixed = Color(0xFFD0E8DD), lightPrimaryFixedDim = Color(0xFF9BBFB3),
    lightOnPrimaryFixed = Color(0xFF002019), lightOnPrimaryFixedVariant = Color(0xFF0E4A3E),
    lightSecondary = Color(0xFF3D5A3F), lightOnSecondary = Color(0xFFFFFFFF),
    lightSecondaryContainer = Color(0xFFE8F0EC), lightOnSecondaryContainer = Color(0xFF1E3A34),
    lightSecondaryFixed = Color(0xFFC2E0C0), lightSecondaryFixedDim = Color(0xFFA4C8A5),
    lightOnSecondaryFixed = Color(0xFF0A1B0F), lightOnSecondaryFixedVariant = Color(0xFF2A4A2C),
    lightTertiary = Color(0xFF8A6500), lightOnTertiary = Color(0xFFFFFFFF),
    lightTertiaryContainer = Color(0xFFFFE08A), lightOnTertiaryContainer = Color(0xFF271900),
    lightTertiaryFixed = Color(0xFFFFE08A), lightTertiaryFixedDim = Color(0xFFE8C050),
    lightOnTertiaryFixed = Color(0xFF271900), lightOnTertiaryFixedVariant = Color(0xFF6A4E00),
    lightError = Color(0xFFB3261E), lightOnError = Color(0xFFFFFFFF),
    lightErrorContainer = Color(0xFFF9DEDC), lightOnErrorContainer = Color(0xFF410E0B),
    lightBackground = Color(0xFFFBFBFA), lightOnBackground = Color(0xFF1C1B1F),
    lightSurface = Color(0xFFFBFBFA), lightOnSurface = Color(0xFF1C1B1F),
    lightSurfaceVariant = Color(0xFFF4F3EF), lightOnSurfaceVariant = Color(0xFF6A6A6A),
    lightSurfaceTint = Color(0xFF1E3A34),
    lightInverseSurface = Color(0xFF313033), lightInverseOnSurface = Color(0xFFF4EFF4),
    lightInversePrimary = Color(0xFF9BBFB3),
    lightSurfaceDim = Color(0xFFDDD8DD), lightSurfaceBright = Color(0xFFFBFBFA),
    lightSurfaceContainerLowest = Color(0xFFFFFFFF),
    lightSurfaceContainerLow = Color(0xFFF7F2FA),
    lightSurfaceContainer = Color(0xFFF3EDF7),
    lightSurfaceContainerHigh = Color(0xFFECE6F0),
    lightSurfaceContainerHighest = Color(0xFFE6E0E9),
    lightOutline = Color(0xFF7A8B85), lightOutlineVariant = Color(0xFFC8D3CE),
    lightScrim = Color(0xFF000000),

    darkPrimary = Color(0xFF3B6053), darkOnPrimary = Color(0xFFFFFFFF),
    darkPrimaryContainer = Color(0xFF1F2E2A), darkOnPrimaryContainer = Color(0xFFE6E6E6),
    darkPrimaryFixed = Color(0xFFD0E8DD), darkPrimaryFixedDim = Color(0xFF9BBFB3),
    darkOnPrimaryFixed = Color(0xFF002019), darkOnPrimaryFixedVariant = Color(0xFF0E4A3E),
    darkSecondary = Color(0xFFB4C7B3), darkOnSecondary = Color(0xFF1F3021),
    darkSecondaryContainer = Color(0xFF1F2E2A), darkOnSecondaryContainer = Color(0xFFE6E6E6),
    darkSecondaryFixed = Color(0xFFCFE3CE), darkSecondaryFixedDim = Color(0xFFB4C7B3),
    darkOnSecondaryFixed = Color(0xFF0A1B0F), darkOnSecondaryFixedVariant = Color(0xFF364638),
    darkTertiary = Color(0xFFE8C462), darkOnTertiary = Color(0xFF402D00),
    darkTertiaryContainer = Color(0xFF5C4300), darkOnTertiaryContainer = Color(0xFFFFDEA1),
    darkTertiaryFixed = Color(0xFFFFDEA1), darkTertiaryFixedDim = Color(0xFFE8C462),
    darkOnTertiaryFixed = Color(0xFF271900), darkOnTertiaryFixedVariant = Color(0xFF5C4300),
    darkError = Color(0xFFFFB4AB), darkOnError = Color(0xFF690005),
    darkErrorContainer = Color(0xFF93000A), darkOnErrorContainer = Color(0xFFFFDAD6),
    darkBackground = Color(0xFF121414), darkOnBackground = Color(0xFFE6E6E6),
    darkSurface = Color(0xFF121414), darkOnSurface = Color(0xFFE6E6E6),
    darkSurfaceVariant = Color(0xFF1A1D1D), darkOnSurfaceVariant = Color(0xFF909090),
    darkSurfaceTint = Color(0xFF3B6053),
    darkInverseSurface = Color(0xFFE3E2E6), darkInverseOnSurface = Color(0xFF1C1B1F),
    darkInversePrimary = Color(0xFF1E3A34),
    darkSurfaceDim = Color(0xFF121414), darkSurfaceBright = Color(0xFF38383A),
    darkSurfaceContainerLowest = Color(0xFF0D0E0F),
    darkSurfaceContainerLow = Color(0xFF1A1A1C),
    darkSurfaceContainer = Color(0xFF1E1E20),
    darkSurfaceContainerHigh = Color(0xFF29292B),
    darkSurfaceContainerHighest = Color(0xFF333335),
    darkOutline = Color(0xFF5A6B64), darkOutlineVariant = Color(0xFF3C4D46),
    darkScrim = Color(0xFF000000)
)

// ==================================================================
// SAPPHIRE NIGHT — Deep Spiritual Sky
// Surface: #F8F9FA | Primary: #1A365D | SecondaryContainer: #EBF2FA
// OnSurface: #111827 | OnSurfaceVariant: #6B7280
// Dark Surface: #0E121A | Dark Primary: #5C93E2 | Dark SecondaryContainer: #192538
// Dark OnSurface: #F9FAFB | Dark OnSurfaceVariant: #9CA3AF
// ==================================================================
private val SapphireTokens = ThemeTokens(
    lightPrimary = Color(0xFF1A365D), lightOnPrimary = Color(0xFFFFFFFF),
    lightPrimaryContainer = Color(0xFFEBF2FA), lightOnPrimaryContainer = Color(0xFF1A365D),
    lightPrimaryFixed = Color(0xFFD0E2FF), lightPrimaryFixedDim = Color(0xFF8BB0E8),
    lightOnPrimaryFixed = Color(0xFF001A33), lightOnPrimaryFixedVariant = Color(0xFF0E3E63),
    lightSecondary = Color(0xFF3A5A6E), lightOnSecondary = Color(0xFFFFFFFF),
    lightSecondaryContainer = Color(0xFFEBF2FA), lightOnSecondaryContainer = Color(0xFF1A365D),
    lightSecondaryFixed = Color(0xFFC2D8F0), lightSecondaryFixedDim = Color(0xFFA0BCD8),
    lightOnSecondaryFixed = Color(0xFF001D33), lightOnSecondaryFixedVariant = Color(0xFF2A4A5E),
    lightTertiary = Color(0xFFB4502A), lightOnTertiary = Color(0xFFFFFFFF),
    lightTertiaryContainer = Color(0xFFFFDBCE), lightOnTertiaryContainer = Color(0xFF401204),
    lightTertiaryFixed = Color(0xFFFFDBCE), lightTertiaryFixedDim = Color(0xFFE8A890),
    lightOnTertiaryFixed = Color(0xFF401204), lightOnTertiaryFixedVariant = Color(0xFF8E3820),
    lightError = Color(0xFFB3261E), lightOnError = Color(0xFFFFFFFF),
    lightErrorContainer = Color(0xFFF9DEDC), lightOnErrorContainer = Color(0xFF410E0B),
    lightBackground = Color(0xFFF8F9FA), lightOnBackground = Color(0xFF111827),
    lightSurface = Color(0xFFF8F9FA), lightOnSurface = Color(0xFF111827),
    lightSurfaceVariant = Color(0xFFF1F3F7), lightOnSurfaceVariant = Color(0xFF6B7280),
    lightSurfaceTint = Color(0xFF1A365D),
    lightInverseSurface = Color(0xFF2D3138), lightInverseOnSurface = Color(0xFFF0F2F5),
    lightInversePrimary = Color(0xFF8BB0E8),
    lightSurfaceDim = Color(0xFFDCDEE3), lightSurfaceBright = Color(0xFFF8F9FA),
    lightSurfaceContainerLowest = Color(0xFFFFFFFF),
    lightSurfaceContainerLow = Color(0xFFF2F4F8),
    lightSurfaceContainer = Color(0xFFEDEFF4),
    lightSurfaceContainerHigh = Color(0xFFE7E9EE),
    lightSurfaceContainerHighest = Color(0xFFE1E3E8),
    lightOutline = Color(0xFF788596), lightOutlineVariant = Color(0xFFC6CFDB),
    lightScrim = Color(0xFF000000),

    darkPrimary = Color(0xFF5C93E2), darkOnPrimary = Color(0xFF001A33),
    darkPrimaryContainer = Color(0xFF192538), darkOnPrimaryContainer = Color(0xFFF9FAFB),
    darkPrimaryFixed = Color(0xFFD0E2FF), darkPrimaryFixedDim = Color(0xFF8BB0E8),
    darkOnPrimaryFixed = Color(0xFF001A33), darkOnPrimaryFixedVariant = Color(0xFF0E3E63),
    darkSecondary = Color(0xFFA0C8E0), darkOnSecondary = Color(0xFF003042),
    darkSecondaryContainer = Color(0xFF192538), darkOnSecondaryContainer = Color(0xFFF9FAFB),
    darkSecondaryFixed = Color(0xFFC2D8F0), darkSecondaryFixedDim = Color(0xFFA0BCD8),
    darkOnSecondaryFixed = Color(0xFF001D33), darkOnSecondaryFixedVariant = Color(0xFF3A5060),
    darkTertiary = Color(0xFFE8956A), darkOnTertiary = Color(0xFF501E08),
    darkTertiaryContainer = Color(0xFF8E3820), darkOnTertiaryContainer = Color(0xFFFFDBCE),
    darkTertiaryFixed = Color(0xFFFFDBCE), darkTertiaryFixedDim = Color(0xFFE8A890),
    darkOnTertiaryFixed = Color(0xFF401204), darkOnTertiaryFixedVariant = Color(0xFF8E3820),
    darkError = Color(0xFFFFB4AB), darkOnError = Color(0xFF690005),
    darkErrorContainer = Color(0xFF93000A), darkOnErrorContainer = Color(0xFFFFDAD6),
    darkBackground = Color(0xFF0E121A), darkOnBackground = Color(0xFFF9FAFB),
    darkSurface = Color(0xFF0E121A), darkOnSurface = Color(0xFFF9FAFB),
    darkSurfaceVariant = Color(0xFF181D25), darkOnSurfaceVariant = Color(0xFF9CA3AF),
    darkSurfaceTint = Color(0xFF5C93E2),
    darkInverseSurface = Color(0xFFE0E4EA), darkInverseOnSurface = Color(0xFF111827),
    darkInversePrimary = Color(0xFF1A365D),
    darkSurfaceDim = Color(0xFF0E121A), darkSurfaceBright = Color(0xFF343842),
    darkSurfaceContainerLowest = Color(0xFF090D12),
    darkSurfaceContainerLow = Color(0xFF161A22),
    darkSurfaceContainer = Color(0xFF1A1E26),
    darkSurfaceContainerHigh = Color(0xFF25292F),
    darkSurfaceContainerHighest = Color(0xFF30343A),
    darkOutline = Color(0xFF6E7A8C), darkOutlineVariant = Color(0xFF4A5566),
    darkScrim = Color(0xFF000000)
)

// ==================================================================
// AMBER SANDS — Warm Heritage & Kaaba Shines
// Surface: #FAF8F5 | Primary: #634832 | SecondaryContainer: #F3ECE3
// OnSurface: #272524 | OnSurfaceVariant: #78716C
// Dark Surface: #141312 | Dark Primary: #D4B996 | Dark SecondaryContainer: #29231D
// Dark OnSurface: #F5F5F4 | Dark OnSurfaceVariant: #A8A29E
// ==================================================================
private val AmberTokens = ThemeTokens(
    lightPrimary = Color(0xFF634832), lightOnPrimary = Color(0xFFFFFFFF),
    lightPrimaryContainer = Color(0xFFF3ECE3), lightOnPrimaryContainer = Color(0xFF634832),
    lightPrimaryFixed = Color(0xFFE8D8C4), lightPrimaryFixedDim = Color(0xFFC8B090),
    lightOnPrimaryFixed = Color(0xFF201208), lightOnPrimaryFixedVariant = Color(0xFF4E3822),
    lightSecondary = Color(0xFF6B5840), lightOnSecondary = Color(0xFFFFFFFF),
    lightSecondaryContainer = Color(0xFFF3ECE3), lightOnSecondaryContainer = Color(0xFF634832),
    lightSecondaryFixed = Color(0xFFD8C8B0), lightSecondaryFixedDim = Color(0xFFB8A080),
    lightOnSecondaryFixed = Color(0xFF2A1A08), lightOnSecondaryFixedVariant = Color(0xFF524230),
    lightTertiary = Color(0xFF2A6B6B), lightOnTertiary = Color(0xFFFFFFFF),
    lightTertiaryContainer = Color(0xFFCCF0F0), lightOnTertiaryContainer = Color(0xFF002020),
    lightTertiaryFixed = Color(0xFFCCF0F0), lightTertiaryFixedDim = Color(0xFFA0D4D4),
    lightOnTertiaryFixed = Color(0xFF002020), lightOnTertiaryFixedVariant = Color(0xFF1A5454),
    lightError = Color(0xFFB3261E), lightOnError = Color(0xFFFFFFFF),
    lightErrorContainer = Color(0xFFF9DEDC), lightOnErrorContainer = Color(0xFF410E0B),
    lightBackground = Color(0xFFFAF8F5), lightOnBackground = Color(0xFF272524),
    lightSurface = Color(0xFFFAF8F5), lightOnSurface = Color(0xFF272524),
    lightSurfaceVariant = Color(0xFFF5F0EA), lightOnSurfaceVariant = Color(0xFF78716C),
    lightSurfaceTint = Color(0xFF634832),
    lightInverseSurface = Color(0xFF353230), lightInverseOnSurface = Color(0xFFF2F0EC),
    lightInversePrimary = Color(0xFFC8B090),
    lightSurfaceDim = Color(0xFFDDDAD7), lightSurfaceBright = Color(0xFFFAF8F5),
    lightSurfaceContainerLowest = Color(0xFFFFFFFF),
    lightSurfaceContainerLow = Color(0xFFF5F2EE),
    lightSurfaceContainer = Color(0xFFF0EDE9),
    lightSurfaceContainerHigh = Color(0xFFEAE7E3),
    lightSurfaceContainerHighest = Color(0xFFE4E1DD),
    lightOutline = Color(0xFF857C75), lightOutlineVariant = Color(0xFFD4CBC3),
    lightScrim = Color(0xFF000000),

    darkPrimary = Color(0xFFD4B996), darkOnPrimary = Color(0xFF3A2410),
    darkPrimaryContainer = Color(0xFF29231D), darkOnPrimaryContainer = Color(0xFFF5F5F4),
    darkPrimaryFixed = Color(0xFFE8D8C4), darkPrimaryFixedDim = Color(0xFFC8B090),
    darkOnPrimaryFixed = Color(0xFF201208), darkOnPrimaryFixedVariant = Color(0xFF4E3822),
    darkSecondary = Color(0xFFC4B098), darkOnSecondary = Color(0xFF3A2818),
    darkSecondaryContainer = Color(0xFF29231D), darkOnSecondaryContainer = Color(0xFFF5F5F4),
    darkSecondaryFixed = Color(0xFFD8C8B0), darkSecondaryFixedDim = Color(0xFFB8A080),
    darkOnSecondaryFixed = Color(0xFF2A1A08), darkOnSecondaryFixedVariant = Color(0xFF524230),
    darkTertiary = Color(0xFF6BB5B5), darkOnTertiary = Color(0xFF003838),
    darkTertiaryContainer = Color(0xFF1A5454), darkOnTertiaryContainer = Color(0xFFCCF0F0),
    darkTertiaryFixed = Color(0xFFCCF0F0), darkTertiaryFixedDim = Color(0xFFA0D4D4),
    darkOnTertiaryFixed = Color(0xFF002020), darkOnTertiaryFixedVariant = Color(0xFF1A5454),
    darkError = Color(0xFFFFB4AB), darkOnError = Color(0xFF690005),
    darkErrorContainer = Color(0xFF93000A), darkOnErrorContainer = Color(0xFFFFDAD6),
    darkBackground = Color(0xFF141312), darkOnBackground = Color(0xFFF5F5F4),
    darkSurface = Color(0xFF141312), darkOnSurface = Color(0xFFF5F5F4),
    darkSurfaceVariant = Color(0xFF1E1A18), darkOnSurfaceVariant = Color(0xFFA8A29E),
    darkSurfaceTint = Color(0xFFD4B996),
    darkInverseSurface = Color(0xFFE8E4E0), darkInverseOnSurface = Color(0xFF272524),
    darkInversePrimary = Color(0xFF634832),
    darkSurfaceDim = Color(0xFF141312), darkSurfaceBright = Color(0xFF3A3836),
    darkSurfaceContainerLowest = Color(0xFF0F0E0D),
    darkSurfaceContainerLow = Color(0xFF1C1A18),
    darkSurfaceContainer = Color(0xFF201E1C),
    darkSurfaceContainerHigh = Color(0xFF2B2927),
    darkSurfaceContainerHighest = Color(0xFF363432),
    darkOutline = Color(0xFF7E7670), darkOutlineVariant = Color(0xFF524A42),
    darkScrim = Color(0xFF000000)
)

// ==================================================================
// MONOCHROME PLATINUM — Ultra-Minimalist Executive
// Surface: #FFFFFF | Primary: #1A1A1A | SecondaryContainer: #F0F0F0
// OnSurface: #000000 | OnSurfaceVariant: #737373
// Dark Surface: #0A0A0A | Dark Primary: #E5E5E5 | Dark SecondaryContainer: #222222
// Dark OnSurface: #FFFFFF | Dark OnSurfaceVariant: #A3A3A3
// ==================================================================
private val MonochromeTokens = ThemeTokens(
    lightPrimary = Color(0xFF1A1A1A), lightOnPrimary = Color(0xFFFFFFFF),
    lightPrimaryContainer = Color(0xFFF0F0F0), lightOnPrimaryContainer = Color(0xFF1A1A1A),
    lightPrimaryFixed = Color(0xFFE0E0E0), lightPrimaryFixedDim = Color(0xFFB8B8B8),
    lightOnPrimaryFixed = Color(0xFF0A0A0A), lightOnPrimaryFixedVariant = Color(0xFF2A2A2A),
    lightSecondary = Color(0xFF4A4A4A), lightOnSecondary = Color(0xFFFFFFFF),
    lightSecondaryContainer = Color(0xFFF0F0F0), lightOnSecondaryContainer = Color(0xFF1A1A1A),
    lightSecondaryFixed = Color(0xFFD0D0D0), lightSecondaryFixedDim = Color(0xFFA8A8A8),
    lightOnSecondaryFixed = Color(0xFF0A0A0A), lightOnSecondaryFixedVariant = Color(0xFF3A3A3A),
    lightTertiary = Color(0xFF525252), lightOnTertiary = Color(0xFFFFFFFF),
    lightTertiaryContainer = Color(0xFFE8E8E8), lightOnTertiaryContainer = Color(0xFF1A1A1A),
    lightTertiaryFixed = Color(0xFFE0E0E0), lightTertiaryFixedDim = Color(0xFFB8B8B8),
    lightOnTertiaryFixed = Color(0xFF0A0A0A), lightOnTertiaryFixedVariant = Color(0xFF3A3A3A),
    lightError = Color(0xFFB3261E), lightOnError = Color(0xFFFFFFFF),
    lightErrorContainer = Color(0xFFF9DEDC), lightOnErrorContainer = Color(0xFF410E0B),
    lightBackground = Color(0xFFFFFFFF), lightOnBackground = Color(0xFF000000),
    lightSurface = Color(0xFFFFFFFF), lightOnSurface = Color(0xFF000000),
    lightSurfaceVariant = Color(0xFFF5F5F5), lightOnSurfaceVariant = Color(0xFF737373),
    lightSurfaceTint = Color(0xFF1A1A1A),
    lightInverseSurface = Color(0xFF2D2D2D), lightInverseOnSurface = Color(0xFFF0F0F0),
    lightInversePrimary = Color(0xFFB8B8B8),
    lightSurfaceDim = Color(0xFFE0E0E0), lightSurfaceBright = Color(0xFFFFFFFF),
    lightSurfaceContainerLowest = Color(0xFFFFFFFF),
    lightSurfaceContainerLow = Color(0xFFF8F8F8),
    lightSurfaceContainer = Color(0xFFF2F2F2),
    lightSurfaceContainerHigh = Color(0xFFECECEC),
    lightSurfaceContainerHighest = Color(0xFFE6E6E6),
    lightOutline = Color(0xFF7A7A7A), lightOutlineVariant = Color(0xFFCCCCCC),
    lightScrim = Color(0xFF000000),

    darkPrimary = Color(0xFFE5E5E5), darkOnPrimary = Color(0xFF1A1A1A),
    darkPrimaryContainer = Color(0xFF222222), darkOnPrimaryContainer = Color(0xFFFFFFFF),
    darkPrimaryFixed = Color(0xFFE0E0E0), darkPrimaryFixedDim = Color(0xFFB8B8B8),
    darkOnPrimaryFixed = Color(0xFF0A0A0A), darkOnPrimaryFixedVariant = Color(0xFF2A2A2A),
    darkSecondary = Color(0xFFB8B8B8), darkOnSecondary = Color(0xFF2A2A2A),
    darkSecondaryContainer = Color(0xFF222222), darkOnSecondaryContainer = Color(0xFFFFFFFF),
    darkSecondaryFixed = Color(0xFFD0D0D0), darkSecondaryFixedDim = Color(0xFFA8A8A8),
    darkOnSecondaryFixed = Color(0xFF0A0A0A), darkOnSecondaryFixedVariant = Color(0xFF3A3A3A),
    darkTertiary = Color(0xFFA3A3A3), darkOnTertiary = Color(0xFF2A2A2A),
    darkTertiaryContainer = Color(0xFF3A3A3A), darkOnTertiaryContainer = Color(0xFFE8E8E8),
    darkTertiaryFixed = Color(0xFFE0E0E0), darkTertiaryFixedDim = Color(0xFFB8B8B8),
    darkOnTertiaryFixed = Color(0xFF0A0A0A), darkOnTertiaryFixedVariant = Color(0xFF3A3A3A),
    darkError = Color(0xFFFFB4AB), darkOnError = Color(0xFF690005),
    darkErrorContainer = Color(0xFF93000A), darkOnErrorContainer = Color(0xFFFFDAD6),
    darkBackground = Color(0xFF0A0A0A), darkOnBackground = Color(0xFFFFFFFF),
    darkSurface = Color(0xFF0A0A0A), darkOnSurface = Color(0xFFFFFFFF),
    darkSurfaceVariant = Color(0xFF141414), darkOnSurfaceVariant = Color(0xFFA3A3A3),
    darkSurfaceTint = Color(0xFFE5E5E5),
    darkInverseSurface = Color(0xFFE0E0E0), darkInverseOnSurface = Color(0xFF1A1A1A),
    darkInversePrimary = Color(0xFF1A1A1A),
    darkSurfaceDim = Color(0xFF0A0A0A), darkSurfaceBright = Color(0xFF303030),
    darkSurfaceContainerLowest = Color(0xFF050505),
    darkSurfaceContainerLow = Color(0xFF121212),
    darkSurfaceContainer = Color(0xFF161616),
    darkSurfaceContainerHigh = Color(0xFF212121),
    darkSurfaceContainerHighest = Color(0xFF2C2C2C),
    darkOutline = Color(0xFF6E6E6E), darkOutlineVariant = Color(0xFF484848),
    darkScrim = Color(0xFF000000)
)

private val themeTokens = mapOf(
    AppThemeSelection.EMERALD_OASIS to EmeraldTokens,
    AppThemeSelection.SAPPHIRE_NIGHT to SapphireTokens,
    AppThemeSelection.AMBER_SANDS to AmberTokens,
    AppThemeSelection.MONOCHROME_PLATINUM to MonochromeTokens
)

fun AppThemeSelection.toColorScheme(darkTheme: Boolean): ColorScheme {
    val tokens = themeTokens[this] ?: EmeraldTokens
    return if (darkTheme) tokens.buildDarkColorScheme() else tokens.buildLightColorScheme()
}

// ========== Prayer-Specific Tint Colors ==========

object PrayerTints {
    val Imsak = Color(0xFF1A3A5C)
    val Gunes = Color(0xFFD48820)
    val Ogle = Color(0xFFC4A000)
    val Ikindi = Color(0xFFB86030)
    val Aksam = Color(0xFF9C3A3A)
    val Yatsi = Color(0xFF3A2A6A)

    val DarkImsak = Color(0xFF5A90C8)
    val DarkGunes = Color(0xFFF0B84A)
    val DarkOgle = Color(0xFFE8D060)
    val DarkIkindi = Color(0xFFE09050)
    val DarkAksam = Color(0xFFE88080)
    val DarkYatsi = Color(0xFF7A6AD0)
}

// ========== Legacy names for backward compatibility ==========

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// ========== Direct color references for Glance widget ==========

val EmeraldLightSurface = Color(0xFFFBFBFA)
val EmeraldLightPrimary = Color(0xFF1E3A34)
val EmeraldLightSecondaryContainer = Color(0xFFE8F0EC)
val EmeraldLightOnSurface = Color(0xFF1C1B1F)
val EmeraldLightOnSurfaceVariant = Color(0xFF6A6A6A)

val EmeraldDarkSurface = Color(0xFF121414)
val EmeraldDarkPrimary = Color(0xFF3B6053)
val EmeraldDarkSecondaryContainer = Color(0xFF1F2E2A)
val EmeraldDarkOnSurface = Color(0xFFE6E6E6)
val EmeraldDarkOnSurfaceVariant = Color(0xFF909090)

val SapphireLightSurface = Color(0xFFF8F9FA)
val SapphireLightPrimary = Color(0xFF1A365D)
val SapphireLightSecondaryContainer = Color(0xFFEBF2FA)
val SapphireLightOnSurface = Color(0xFF111827)
val SapphireLightOnSurfaceVariant = Color(0xFF6B7280)

val SapphireDarkSurface = Color(0xFF0E121A)
val SapphireDarkPrimary = Color(0xFF5C93E2)
val SapphireDarkSecondaryContainer = Color(0xFF192538)
val SapphireDarkOnSurface = Color(0xFFF9FAFB)
val SapphireDarkOnSurfaceVariant = Color(0xFF9CA3AF)

val AmberLightSurface = Color(0xFFFAF8F5)
val AmberLightPrimary = Color(0xFF634832)
val AmberLightSecondaryContainer = Color(0xFFF3ECE3)
val AmberLightOnSurface = Color(0xFF272524)
val AmberLightOnSurfaceVariant = Color(0xFF78716C)

val AmberDarkSurface = Color(0xFF141312)
val AmberDarkPrimary = Color(0xFFD4B996)
val AmberDarkSecondaryContainer = Color(0xFF29231D)
val AmberDarkOnSurface = Color(0xFFF5F5F4)
val AmberDarkOnSurfaceVariant = Color(0xFFA8A29E)

val MonochromeLightSurface = Color(0xFFFFFFFF)
val MonochromeLightPrimary = Color(0xFF1A1A1A)
val MonochromeLightSecondaryContainer = Color(0xFFF0F0F0)
val MonochromeLightOnSurface = Color(0xFF000000)
val MonochromeLightOnSurfaceVariant = Color(0xFF737373)

val MonochromeDarkSurface = Color(0xFF0A0A0A)
val MonochromeDarkPrimary = Color(0xFFE5E5E5)
val MonochromeDarkSecondaryContainer = Color(0xFF222222)
val MonochromeDarkOnSurface = Color(0xFFFFFFFF)
val MonochromeDarkOnSurfaceVariant = Color(0xFFA3A3A3)
