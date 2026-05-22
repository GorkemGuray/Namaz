package co.gorkem.namaz.ui.components

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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

    val animatedAzimuth by animateFloatAsState(
        targetValue = deviceAzimuth.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "azimuth"
    )

    val kaabaAngle = qiblaBearing - deviceAzimuth
    val animatedKaaba by animateFloatAsState(
        targetValue = kaabaAngle.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "kaaba"
    )

    val animatedPitch by animateFloatAsState(
        targetValue = pitch.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "pitch"
    )
    val animatedRoll by animateFloatAsState(
        targetValue = roll.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "roll"
    )

    val glowTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 0.5f,
        animationSpec = InfiniteRepeatableSpec(
            animation = androidx.compose.animation.core.tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowRadius by glowTransition.animateFloat(
        initialValue = 18f,
        targetValue = 26f,
        animationSpec = InfiniteRepeatableSpec(
            animation = androidx.compose.animation.core.tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowRadius"
    )

    val alignedAlpha by animateFloatAsState(
        targetValue = if (isAligned) 1.0f else 0.0f,
        animationSpec = tween(500),
        label = "alignedAlpha"
    )

    val alignedRingAlpha by animateFloatAsState(
        targetValue = if (isAligned) 0.35f else 0.0f,
        animationSpec = tween(500),
        label = "alignedRingAlpha"
    )

    Canvas(modifier = modifier.size(280.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = size.minDimension / 2 - 16.dp.toPx()
        val innerRadius = outerRadius * 0.72f

        drawCircle(
            color = if (isAligned) primary.copy(alpha = 0.8f) else outlineVariant.copy(alpha = 0.3f),
            radius = outerRadius,
            style = Stroke(width = if (isAligned) 3.dp.toPx() else 2.dp.toPx())
        )

        drawCircle(
            color = if (isAligned) primary.copy(alpha = 0.4f) else outlineVariant.copy(alpha = 0.15f),
            radius = innerRadius,
            style = Stroke(width = 1.5.dp.toPx())
        )

        drawTopIndicator(center, outerRadius, primary)

        drawCrosshair(center, outlineVariant)

        val maxDisp = 12.dp.toPx()
        val tiltScale = (maxDisp / 45.0).toFloat()
        val bubbleX = center.x - (animatedRoll * tiltScale)
        val bubbleY = center.y + (animatedPitch * tiltScale)
        drawLevelBubble(
            center = Offset(bubbleX, bubbleY),
            targetCenter = center,
            isLevel = isLevel,
            primary = primary,
            outline = outlineVariant
        )

        rotate(degrees = -animatedAzimuth, pivot = center) {
            drawDegreeTicks(center, outerRadius, onSurfaceVariant)
            drawDegreeLabels(center, outerRadius, onSurfaceVariant, textMeasurer)
            drawCardinalLabels(center, innerRadius, onSurfaceVariant, textMeasurer, primary)
        }

        if (!isAligned) {
            val sweep = animatedKaaba
            var normalizedSweep = sweep % 360
            if (normalizedSweep > 180) normalizedSweep -= 360
            if (normalizedSweep < -180) normalizedSweep += 360

            drawArc(
                color = primary.copy(alpha = 0.6f),
                startAngle = -90f,
                sweepAngle = normalizedSweep,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        val kaabaRad = Math.toRadians(animatedKaaba.toDouble())
        val kaabaX = center.x + outerRadius * sin(kaabaRad).toFloat()
        val kaabaY = center.y - outerRadius * cos(kaabaRad).toFloat()

        if (isAligned) {
            if (alignedRingAlpha > 0f) {
                drawCircle(
                    color = primary.copy(alpha = alignedRingAlpha),
                    radius = outerRadius,
                    style = Stroke(width = 5.dp.toPx())
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primary.copy(alpha = glowAlpha * 0.8f),
                        primary.copy(alpha = 0.0f)
                    ),
                    center = Offset(kaabaX, kaabaY),
                    radius = glowRadius.dp.toPx() * 1.5f
                ),
                radius = glowRadius.dp.toPx() * 1.5f,
                center = Offset(kaabaX, kaabaY)
            )
        }

        drawKaabaIcon(Offset(kaabaX, kaabaY), primary, isAligned)
    }
}

private fun DrawScope.drawTopIndicator(
    center: Offset,
    radius: Float,
    color: Color
) {
    val size = 12.dp.toPx()
    val topY = center.y - radius - 8.dp.toPx()
    val path = Path().apply {
        moveTo(center.x, topY + size)
        lineTo(center.x - size / 2, topY)
        lineTo(center.x + size / 2, topY)
        close()
    }
    drawPath(path = path, color = color)
}

private fun DrawScope.drawCrosshair(
    center: Offset,
    color: Color
) {
    val len = 16.dp.toPx()
    drawLine(
        color = color.copy(alpha = 0.25f),
        start = Offset(center.x - len, center.y),
        end = Offset(center.x + len, center.y),
        strokeWidth = 1.dp.toPx()
    )
    drawLine(
        color = color.copy(alpha = 0.25f),
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
    val dotRadius = 6.dp.toPx()
    val targetRadius = 14.dp.toPx()

    drawCircle(
        color = if (isLevel) primary.copy(alpha = 0.4f) else outline.copy(alpha = 0.25f),
        radius = targetRadius,
        center = targetCenter,
        style = Stroke(width = 1.5.dp.toPx())
    )

    drawCircle(
        color = if (isLevel) primary.copy(alpha = 0.1f) else outline.copy(alpha = 0.04f),
        radius = targetRadius - 1.dp.toPx(),
        center = targetCenter
    )

    val dotColor = if (isLevel) primary else outline.copy(alpha = 0.8f)
    drawCircle(
        color = dotColor,
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
        val isMajor = i % 6 == 0
        val tickLength = if (isMajor) 12.dp.toPx() else 6.dp.toPx()
        val tickWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
        val alpha = if (isMajor) 0.5f else 0.3f

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
        fontSize = 11.sp,
        color = color.copy(alpha = 0.5f),
        fontWeight = FontWeight.Normal
    )
    val labelRadius = radius - 22.dp.toPx()

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
    textMeasurer: TextMeasurer,
    primaryColor: Color
) {
    val labels = listOf("K" to 0, "D" to 90, "G" to 180, "B" to 270)

    for ((text, deg) in labels) {
        val labelColor = if (text == "K") {
            Color(0xFFE53935)
        } else {
            color.copy(alpha = 0.8f)
        }
        val style = TextStyle(
            fontSize = 16.sp,
            color = labelColor,
            fontWeight = FontWeight.Bold
        )
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
    val scale = if (isAligned) 1.2f else 1.0f
    val baseSize = 20.dp.toPx() * scale
    val half = baseSize / 2

    val goldColor = Color(0xFFFFD700)
    val blackColor = Color(0xFF1E1E1E)
    val blackShadowColor = Color(0xFF121212)
    val topColor = Color(0xFF2C2C2C)

    val leftFace = Path().apply {
        moveTo(center.x, center.y - half * 0.2f)
        lineTo(center.x - half, center.y - half * 0.6f)
        lineTo(center.x - half, center.y + half * 0.2f)
        lineTo(center.x, center.y + half * 0.6f)
        close()
    }
    
    val rightFace = Path().apply {
        moveTo(center.x, center.y - half * 0.2f)
        lineTo(center.x + half, center.y - half * 0.6f)
        lineTo(center.x + half, center.y + half * 0.2f)
        lineTo(center.x, center.y + half * 0.6f)
        close()
    }

    val topFace = Path().apply {
        moveTo(center.x, center.y - half * 0.2f)
        lineTo(center.x - half, center.y - half * 0.6f)
        lineTo(center.x, center.y - half)
        lineTo(center.x + half, center.y - half * 0.6f)
        close()
    }

    drawPath(path = leftFace, color = blackShadowColor)
    drawPath(path = rightFace, color = blackColor)
    drawPath(path = topFace, color = topColor)

    val bandHeight = 2.dp.toPx()
    val bandOffset = half * 0.15f

    val leftBand = Path().apply {
        moveTo(center.x, center.y - half * 0.2f + bandOffset)
        lineTo(center.x - half, center.y - half * 0.6f + bandOffset)
        lineTo(center.x - half, center.y - half * 0.6f + bandOffset + bandHeight)
        lineTo(center.x, center.y - half * 0.2f + bandOffset + bandHeight)
        close()
    }
    
    val rightBand = Path().apply {
        moveTo(center.x, center.y - half * 0.2f + bandOffset)
        lineTo(center.x + half, center.y - half * 0.6f + bandOffset)
        lineTo(center.x + half, center.y - half * 0.6f + bandOffset + bandHeight)
        lineTo(center.x, center.y - half * 0.2f + bandOffset + bandHeight)
        close()
    }

    drawPath(path = leftBand, color = goldColor)
    drawPath(path = rightBand, color = goldColor)

    val doorWidth = half * 0.3f
    val doorHeight = half * 0.45f
    val doorX1 = center.x + half * 0.25f
    val doorY1 = center.y + half * 0.1f
    val doorX2 = center.x + half * 0.55f
    val doorY2 = doorY1 - doorWidth * 0.4f

    val doorPath = Path().apply {
        moveTo(doorX1, doorY1)
        lineTo(doorX2, doorY2)
        lineTo(doorX2, doorY2 + doorHeight)
        lineTo(doorX1, doorY1 + doorHeight)
        close()
    }
    drawPath(path = doorPath, color = goldColor)
}
