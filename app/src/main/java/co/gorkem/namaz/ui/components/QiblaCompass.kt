package co.gorkem.namaz.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaCompass(
    deviceAzimuth: Double,
    qiblaBearing: Double,
    alignmentDelta: Double,
    isAligned: Boolean,
    pitch: Double = 0.0,
    roll: Double = 0.0,
    modifier: Modifier = Modifier
) {
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val textMeasurer = rememberTextMeasurer()

    val isLevel = kotlin.math.abs(pitch) <= 8.0 && kotlin.math.abs(roll) <= 8.0

    // Kaaba icon: placed at Qibla bearing on the rotating face
    // When qiblaBearing == deviceAzimuth, Kaaba is at top → aligned
    val kaabaAngle = qiblaBearing - deviceAzimuth
    val animatedKaaba by animateFloatAsState(
        targetValue = kaabaAngle.toFloat(),
        animationSpec = tween(80),
        label = "kaaba"
    )

    Canvas(modifier = modifier.size(280.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = size.minDimension / 2 - 16.dp.toPx()
        val innerRadius = outerRadius * 0.72f

        // Fixed outer ring
        drawCircle(
            color = outlineVariant.copy(alpha = 0.25f),
            radius = outerRadius,
            style = Stroke(width = 1.5.dp.toPx())
        )

        drawCircle(
            color = outlineVariant.copy(alpha = 0.12f),
            radius = innerRadius,
            style = Stroke(width = 1.dp.toPx())
        )

        // Fixed top indicator (12 o'clock) - helps user aim
        drawTopIndicator(center, outerRadius, primary)

        // Crosshair at center
        drawCrosshair(center, outlineVariant)

        // Level indicator: moving bubble dot
        // roll > 0 (tilted right) → dot moves left
        // pitch > 0 (tilted forward) → dot moves down
        val maxDisp = 12.dp.toPx()
        val scale = (maxDisp / 45.0).toFloat() // 45° tilt = max displacement
        val bubbleX = center.x - (roll * scale).toFloat()
        val bubbleY = center.y + (pitch * scale).toFloat()
        drawLevelBubble(
            center = Offset(bubbleX, bubbleY),
            targetCenter = center,
            isLevel = isLevel,
            primary = primary,
            outline = outlineVariant
        )

        // Rotating compass face: ticks + degree labels + cardinal labels
        // When device faces North (0°), "K" is at top, 0° at top
        rotate(degrees = -deviceAzimuth.toFloat(), pivot = center) {
            drawDegreeTicks(center, outerRadius, onSurfaceVariant)
            drawDegreeLabels(center, outerRadius, onSurfaceVariant, textMeasurer)
            drawCardinalLabels(center, innerRadius, onSurfaceVariant, textMeasurer)
        }

        val kaabaRad = Math.toRadians(animatedKaaba.toDouble())
        val kaabaX = center.x + outerRadius * sin(kaabaRad).toFloat()
        val kaabaY = center.y - outerRadius * cos(kaabaRad).toFloat()

        drawKaabaIcon(Offset(kaabaX, kaabaY), primary, isAligned)
    }
}

private fun DrawScope.drawTopIndicator(
    center: Offset,
    radius: Float,
    color: Color
) {
    val indicatorLen = 14.dp.toPx()
    val startY = center.y - radius - 2.dp.toPx()
    drawLine(
        color = color.copy(alpha = 0.6f),
        start = Offset(center.x, startY),
        end = Offset(center.x, startY + indicatorLen),
        strokeWidth = 2.5.dp.toPx(),
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawCrosshair(
    center: Offset,
    color: Color
) {
    val len = 10.dp.toPx()
    val alpha = if (color.alpha < 1f) 0.25f else 0.5f
    drawLine(
        color = color.copy(alpha = alpha),
        start = Offset(center.x - len, center.y),
        end = Offset(center.x + len, center.y),
        strokeWidth = 1.dp.toPx()
    )
    drawLine(
        color = color.copy(alpha = alpha),
        start = Offset(center.x, center.y - len),
        end = Offset(center.x, center.y + len),
        strokeWidth = 1.dp.toPx()
    )
}

private fun DrawScope.drawLevelBubble(
    center: Offset,
    targetCenter: Offset,
    isLevel: Boolean,
    primary: Color,
    outline: Color
) {
    val dotRadius = 3.5.dp.toPx()
    val targetRadius = 8.dp.toPx()

    // Target zone circle at crosshair center
    drawCircle(
        color = if (isLevel) primary.copy(alpha = 0.3f) else outline.copy(alpha = 0.2f),
        radius = targetRadius,
        center = targetCenter,
        style = Stroke(width = 1.dp.toPx())
    )

    // Moving bubble dot
    val dotColor = if (isLevel) primary else outline
    drawCircle(
        color = dotColor.copy(alpha = 0.9f),
        radius = dotRadius,
        center = center
    )
}

private fun DrawScope.drawDegreeTicks(
    center: Offset,
    radius: Float,
    color: Color
) {
    for (i in 0 until 72) {
        val angle = i * 5f
        val isMajor = i % 6 == 0  // every 30°
        val tickLength = if (isMajor) 10.dp.toPx() else 5.dp.toPx()
        val tickWidth = if (isMajor) 1.5f.dp.toPx() else 1f.dp.toPx()
        val alpha = if (isMajor) 0.4f else 0.2f

        rotate(angle, center) {
            drawLine(
                color = color.copy(alpha = alpha),
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y - radius + tickLength),
                strokeWidth = tickWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawDegreeLabels(
    center: Offset,
    radius: Float,
    color: Color,
    textMeasurer: TextMeasurer
) {
    val style = TextStyle(
        fontSize = 10.sp,
        color = color.copy(alpha = 0.5f),
        fontWeight = FontWeight.Normal
    )
    val labelRadius = radius - 20.dp.toPx()

    for (deg in listOf(0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330)) {
        val rad = Math.toRadians(deg.toDouble())
        val x = center.x + labelRadius * sin(rad).toFloat()
        val y = center.y - labelRadius * cos(rad).toFloat()
        val text = deg.toString()
        val measured = textMeasurer.measure(text, style)
        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = style,
            topLeft = Offset(x - measured.size.width / 2, y - measured.size.height / 2)
        )
    }
}

private fun DrawScope.drawCardinalLabels(
    center: Offset,
    radius: Float,
    color: Color,
    textMeasurer: TextMeasurer
) {
    val style = TextStyle(
        fontSize = 14.sp,
        color = color.copy(alpha = 0.7f),
        fontWeight = FontWeight.SemiBold
    )
    val labels = listOf("K" to 0, "D" to 90, "G" to 180, "B" to 270)

    for ((text, deg) in labels) {
        val rad = Math.toRadians(deg.toDouble())
        val x = center.x + radius * sin(rad).toFloat()
        val y = center.y - radius * cos(rad).toFloat()
        val measured = textMeasurer.measure(text, style)
        drawText(
            textMeasurer = textMeasurer,
            text = text,
            style = style,
            topLeft = Offset(x - measured.size.width / 2, y - measured.size.height / 2)
        )
    }
}

private fun DrawScope.drawKaabaIcon(
    center: Offset,
    color: Color,
    isAligned: Boolean
) {
    val alpha = if (isAligned) 1.0f else 0.85f
    val iconColor = color.copy(alpha = alpha)
    val iconSize = 12.dp.toPx()
    val half = iconSize / 2

    // Square body
    drawRect(
        color = iconColor,
        topLeft = Offset(center.x - half, center.y - half),
        size = Size(iconSize, iconSize)
    )

    // Triangle roof
    val roof = Path().apply {
        moveTo(center.x - half - 2.dp.toPx(), center.y - half)
        lineTo(center.x, center.y - half * 2.2f - 2.dp.toPx())
        lineTo(center.x + half + 2.dp.toPx(), center.y - half)
        close()
    }
    drawPath(path = roof, color = iconColor)
}
