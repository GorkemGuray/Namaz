package co.gorkem.namaz.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import co.gorkem.namaz.domain.model.PrayerName

// ============================================================================
// ATMOSPHERIC PRAYER-TIME GRADIENTS
// Soft, muted, diffuse — like real sky light.
// Only 3 stops. Colors are close in tone so transitions feel misty/cloud-like.
// ============================================================================

/**
 * İmsak — Twilight before dawn.
 * Night indigo overhead melts into a whisper of warm horizon glow.
 * Feels like: cool air, silence, first hint of light beneath the clouds.
 */
private val ImsakGradient = listOf(
    Color(0xFF232336),   // top: deep muted indigo (night still above)
    Color(0xFF4A4055),   // middle: dusty purple-mauve
    Color(0xFF8A7A7A)    // bottom: warm dusty rose-grey (horizon glow)
)

/**
 * Güneş — Sunrise.
 * Cool slate sky above, soft warm sand at the horizon.
 * Feels like: fresh morning air, golden light touching rooftops.
 */
private val GunesGradient = listOf(
    Color(0xFF3A4A5A),   // top: cool slate blue
    Color(0xFF7A8A8A),   // middle: misty blue-grey
    Color(0xFFC9A87C)    // bottom: soft warm sand
)

/**
 * Öğle — Midday.
 * Clear cerulean melting into pale airy blue.
 * Feels like: open sky, calm, bright but not harsh.
 */
private val OgleGradient = listOf(
    Color(0xFF4A6A8A),   // top: soft cerulean
    Color(0xFF7AA0B8),   // middle: airy sky blue
    Color(0xFFB8D0D8)    // bottom: very pale blue-grey
)

/**
 * İkindi — Late afternoon.
 * Cool grey above, gentle golden warmth below.
 * Feels like: long shadows, warm light on walls, peaceful.
 */
private val IkindiGradient = listOf(
    Color(0xFF5A6A7A),   // top: cool grey-blue
    Color(0xFF8A8A7A),   // middle: warm grey-taupe
    Color(0xFFC4A882)    // bottom: soft muted gold
)

/**
 * Akşam — Sunset.
 * Deep dusty purple overhead, faded rose-taupe at horizon.
 * NOT neon orange. Real sunset is muted by atmosphere.
 * Feels like: the day's last breath, calm, melancholic beauty.
 */
private val AksamGradient = listOf(
    Color(0xFF3A2A3A),   // top: deep dusty purple-brown
    Color(0xFF6A4A5A),   // middle: dusty mauve
    Color(0xFF9A7A6A)    // bottom: dusty rose-taupe
)

/**
 * Yatsı — Night.
 * Near-black overhead, soft deep blue-grey below.
 * Feels like: quiet, starlight, cool air.
 */
private val YatsiGradient = listOf(
    Color(0xFF0A0A15),   // top: near-black
    Color(0xFF1A1A2A),   // middle: deep navy
    Color(0xFF2E2E40)    // bottom: soft deep blue-grey
)

private val DefaultGradient = listOf(
    Color(0xFF2A2A3A),
    Color(0xFF3A3A4A),
    Color(0xFF4A4A5A)
)

// ============================================================================
// Public APIs
// ============================================================================

/**
 * Returns a diffuse, atmospheric gradient for the hero card.
 * Diagonal angle (topStart → bottomEnd) so light feels natural, not mechanical.
 */
@Composable
fun prayerHeroGradient(prayer: PrayerName?): Brush {
    val target = when (prayer) {
        PrayerName.IMSAK -> ImsakGradient
        PrayerName.GUNES -> GunesGradient
        PrayerName.OGLE -> OgleGradient
        PrayerName.IKINDI -> IkindiGradient
        PrayerName.AKSAM -> AksamGradient
        PrayerName.YATSI -> YatsiGradient
        null -> DefaultGradient
    }

    val animatedColors = target.mapIndexed { index, color ->
        val animated by animateColorAsState(
            targetValue = color,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessVeryLow  // very slow, gentle drift
            ),
            label = "grad_$index"
        )
        animated
    }

    // Diagonal gradient: light flows from top-start to bottom-end
    // like real sky light hitting at an angle
    return Brush.linearGradient(
        colors = animatedColors,
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
}

/**
 * Extremely subtle ambient tint for the screen background.
 * Barely perceptible — only adds a faint mood.
 */
@Composable
fun prayerAmbientColor(prayer: PrayerName?): Color {
    val base = when (prayer) {
        PrayerName.IMSAK -> Color(0xFF4A4055)
        PrayerName.GUNES -> Color(0xFF8A7A6A)
        PrayerName.OGLE -> Color(0xFF6A9AB8)
        PrayerName.IKINDI -> Color(0xFF9A8A6A)
        PrayerName.AKSAM -> Color(0xFF7A5A5A)
        PrayerName.YATSI -> Color(0xFF2A2A3A)
        null -> Color.Transparent
    }

    val animated by animateColorAsState(
        targetValue = base.copy(alpha = 0.03f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "ambient"
    )
    return animated
}

/**
 * Countdown accent — still functional for urgency.
 * On the hero card this is overridden to white for readability.
 */
@Composable
fun countdownAccentColor(remainingMinutes: Long): Color {
    return when {
        remainingMinutes <= 5 -> MaterialTheme.colorScheme.error
        remainingMinutes <= 15 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }
}

/**
 * A soft highlight color used for small decorative elements.
 */
@Composable
fun prayerHighlightColor(prayer: PrayerName?): Color {
    return when (prayer) {
        PrayerName.IMSAK -> Color(0xFFAA9AAA)
        PrayerName.GUNES -> Color(0xFFD4B494)
        PrayerName.OGLE -> Color(0xFF94B8C8)
        PrayerName.IKINDI -> Color(0xFFC4A882)
        PrayerName.AKSAM -> Color(0xFFB09080)
        PrayerName.YATSI -> Color(0xFF8A8AA0)
        null -> MaterialTheme.colorScheme.primary
    }
}
