package co.gorkem.namaz.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * M3 Expressive Shapes
 *
 * Uses bolder, more organic corner radii:
 * - Cards: extra-large rounded (28dp) for a premium, welcoming feel
 * - Buttons: fully rounded pill shapes
 * - Small components: medium-large (16dp)
 */

val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/**
 * Premium card shape — asymmetric top-left and bottom-right emphasis
 */
val HeroCardShape = RoundedCornerShape(
    topStart = 32.dp,
    topEnd = 20.dp,
    bottomStart = 20.dp,
    bottomEnd = 32.dp
)

/**
 * Pill shape for chips and compact buttons
 */
val PillShape = RoundedCornerShape(percent = 50)
