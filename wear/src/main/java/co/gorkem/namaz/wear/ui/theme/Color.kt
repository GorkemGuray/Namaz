package co.gorkem.namaz.wear.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme

// Helper to create OLED-optimized color schemes
private fun buildOledColorScheme(
    primary: Color,
    onPrimary: Color,
    primaryContainer: Color,
    onPrimaryContainer: Color,
    primaryDim: Color,
    secondary: Color,
    onSecondary: Color,
    secondaryContainer: Color,
    onSecondaryContainer: Color,
    secondaryDim: Color,
    tertiary: Color,
    onTertiary: Color,
    tertiaryContainer: Color,
    onTertiaryContainer: Color,
    tertiaryDim: Color,
    surface: Color,
    onSurface: Color,
    onSurfaceVariant: Color,
    outline: Color,
    outlineVariant: Color
) = ColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    primaryDim = primaryDim,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    secondaryDim = secondaryDim,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    tertiaryDim = tertiaryDim,
    background = Color(0xFF000000), // PURE BLACK for OLED
    onBackground = Color(0xFFFFFFFF),
    surfaceContainer = surface,
    surfaceContainerLow = Color(0xFF000000),
    surfaceContainerHigh = surface,
    onSurface = onSurface,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outlineVariant,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    errorDim = Color(0xFF93000A)
)

// 0: Emerald Oasis
val EmeraldColorScheme = buildOledColorScheme(
    primary = Color(0xFF3B6053),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1F2E2A),
    onPrimaryContainer = Color(0xFFE6E6E6),
    primaryDim = Color(0xFF9BBFB3),
    secondary = Color(0xFFB4C7B3),
    onSecondary = Color(0xFF1F3021),
    secondaryContainer = Color(0xFF1F2E2A),
    onSecondaryContainer = Color(0xFFE6E6E6),
    secondaryDim = Color(0xFFB4C7B3),
    tertiary = Color(0xFFE8C462),
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = Color(0xFF5C4300),
    onTertiaryContainer = Color(0xFFFFDEA1),
    tertiaryDim = Color(0xFFE8C462),
    surface = Color(0xFF1A1D1D),
    onSurface = Color(0xFFE6E6E6),
    onSurfaceVariant = Color(0xFF909090),
    outline = Color(0xFF5A6B64),
    outlineVariant = Color(0xFF3C4D46)
)

// 1: Sapphire Night
val SapphireColorScheme = buildOledColorScheme(
    primary = Color(0xFF5C93E2),
    onPrimary = Color(0xFF001A33),
    primaryContainer = Color(0xFF192538),
    onPrimaryContainer = Color(0xFFF9FAFB),
    primaryDim = Color(0xFF8BB0E8),
    secondary = Color(0xFFA0C8E0),
    onSecondary = Color(0xFF003042),
    secondaryContainer = Color(0xFF192538),
    onSecondaryContainer = Color(0xFFF9FAFB),
    secondaryDim = Color(0xFFA0BCD8),
    tertiary = Color(0xFFE8956A),
    onTertiary = Color(0xFF501E08),
    tertiaryContainer = Color(0xFF8E3820),
    onTertiaryContainer = Color(0xFFFFDBCE),
    tertiaryDim = Color(0xFFE8A890),
    surface = Color(0xFF181D25),
    onSurface = Color(0xFFF9FAFB),
    onSurfaceVariant = Color(0xFF9CA3AF),
    outline = Color(0xFF6E7A8C),
    outlineVariant = Color(0xFF4A5566)
)

// 2: Amber Sands
val AmberColorScheme = buildOledColorScheme(
    primary = Color(0xFFD4B996),
    onPrimary = Color(0xFF3A2410),
    primaryContainer = Color(0xFF29231D),
    onPrimaryContainer = Color(0xFFF5F5F4),
    primaryDim = Color(0xFFC8B090),
    secondary = Color(0xFFC4B098),
    onSecondary = Color(0xFF3A2818),
    secondaryContainer = Color(0xFF29231D),
    onSecondaryContainer = Color(0xFFF5F5F4),
    secondaryDim = Color(0xFFB8A080),
    tertiary = Color(0xFF6BB5B5),
    onTertiary = Color(0xFF003838),
    tertiaryContainer = Color(0xFF1A5454),
    onTertiaryContainer = Color(0xFFCCF0F0),
    tertiaryDim = Color(0xFFA0D4D4),
    surface = Color(0xFF1E1A18),
    onSurface = Color(0xFFF5F5F4),
    onSurfaceVariant = Color(0xFFA8A29E),
    outline = Color(0xFF7E7670),
    outlineVariant = Color(0xFF524A42)
)

// 3: Monochrome Platinum
val MonochromeColorScheme = buildOledColorScheme(
    primary = Color(0xFFE5E5E5),
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFF222222),
    onPrimaryContainer = Color(0xFFFFFFFF),
    primaryDim = Color(0xFFB8B8B8),
    secondary = Color(0xFFB8B8B8),
    onSecondary = Color(0xFF2A2A2A),
    secondaryContainer = Color(0xFF222222),
    onSecondaryContainer = Color(0xFFFFFFFF),
    secondaryDim = Color(0xFFA8A8A8),
    tertiary = Color(0xFFA3A3A3),
    onTertiary = Color(0xFF2A2A2A),
    tertiaryContainer = Color(0xFF3A3A3A),
    onTertiaryContainer = Color(0xFFE8E8E8),
    tertiaryDim = Color(0xFFB8B8B8),
    surface = Color(0xFF141414),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFA3A3A3),
    outline = Color(0xFF6E6E6E),
    outlineVariant = Color(0xFF484848)
)
