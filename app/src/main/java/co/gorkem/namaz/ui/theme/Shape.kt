package co.gorkem.namaz.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * M3 Expressive Shapes
 *
 * Uses bolder, more organic corner radii:
 * - Cards: extra-large rounded (32dp) for a premium, welcoming feel
 * - Buttons: fully rounded pill shapes
 * - Small components: medium-large (16dp)
 */

val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Premium card shape — dramatic asymmetric emphasis
 */
val HeroCardShape = RoundedCornerShape(
    topStart = 36.dp,
    topEnd = 24.dp,
    bottomStart = 24.dp,
    bottomEnd = 36.dp
)

/**
 * Pill shape for chips and compact buttons
 */
val PillShape = RoundedCornerShape(percent = 50)

/**
 * Soft container shape for list items
 */
val ListItemShape = RoundedCornerShape(16.dp)
