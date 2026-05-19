package co.gorkem.namaz.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
    modifier: Modifier = Modifier
) {
    val needleAngle = qiblaBearing - deviceAzimuth

    val animatedNeedle by animateFloatAsState(
        targetValue = needleAngle.toFloat(),
        animationSpec = tween(80),
        label = "needle_rotation"
    )

    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(250.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 8.dp.toPx()

            drawOuterRing(center, radius, outlineVariant)
            drawCardinalTickMarks(center, radius, onSurfaceVariant, outlineVariant)

            rotate(animatedNeedle, center) {
                drawQiblaNeedle(center, radius, primary, isAligned)
            }

            drawCircle(
                color = primary,
                radius = 6.dp.toPx()
            )
        }

        val labelRadius = 100.dp
        CardinalLabel("K", 0f, labelRadius, onSurfaceVariant.copy(alpha = 0.7f))
        CardinalLabel("D", 90f, labelRadius, onSurfaceVariant.copy(alpha = 0.7f))
        CardinalLabel("G", 180f, labelRadius, onSurfaceVariant.copy(alpha = 0.7f))
        CardinalLabel("B", 270f, labelRadius, onSurfaceVariant.copy(alpha = 0.7f))

        val northAngleRad = Math.toRadians(-deviceAzimuth)
        val northX = (sin(northAngleRad) * 70).dp
        val northY = (-cos(northAngleRad) * 70).dp
        Text(
            text = "KUZEY",
            fontSize = 9.sp,
            color = Color(0xFFB3261E),
            modifier = Modifier.offset(x = northX, y = northY)
        )
    }
}

@Composable
private fun CardinalLabel(
    text: String,
    angleDeg: Float,
    radius: androidx.compose.ui.unit.Dp,
    color: Color
) {
    val angleRad = Math.toRadians(angleDeg.toDouble())
    val x = (sin(angleRad) * radius.value).dp
    val y = (-cos(angleRad) * radius.value).dp
    Text(
        text = text,
        fontSize = 13.sp,
        color = color,
        modifier = Modifier.offset(x = x, y = y)
    )
}

private fun DrawScope.drawOuterRing(
    center: Offset,
    radius: Float,
    color: Color
) {
    drawCircle(
        color = color.copy(alpha = 0.3f),
        radius = radius,
        style = Stroke(width = 2.dp.toPx())
    )
    drawCircle(
        color = color.copy(alpha = 0.12f),
        radius = radius * 0.78f,
        style = Stroke(width = 1.dp.toPx())
    )
}

private fun DrawScope.drawCardinalTickMarks(
    center: Offset,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    for (i in 0 until 4) {
        val angle = i * 90f
        rotate(angle, center) {
            drawLine(
                color = primaryColor.copy(alpha = 0.5f),
                start = Offset(center.x, center.y - radius),
                end = Offset(center.x, center.y - radius + 18.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }

    for (i in 0 until 36) {
        if (i % 9 != 0) {
            val angle = i * 10f
            rotate(angle, center) {
                drawLine(
                    color = secondaryColor.copy(alpha = if (i % 3 == 0) 0.3f else 0.15f),
                    start = Offset(center.x, center.y - radius + 4.dp.toPx()),
                    end = Offset(center.x, center.y - radius + 10.dp.toPx()),
                    strokeWidth = 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

private fun DrawScope.drawQiblaNeedle(
    center: Offset,
    radius: Float,
    color: Color,
    isAligned: Boolean
) {
    val needleLen = radius * 0.62f
    val needleTip = Offset(center.x, center.y - needleLen)
    val halfBase = 6.dp.toPx()

    val needlePath = Path().apply {
        moveTo(center.x, center.y + 5.dp.toPx())
        lineTo(center.x - halfBase, center.y)
        lineTo(needleTip.x, needleTip.y)
        lineTo(center.x + halfBase, center.y)
        close()
    }

    val needleColor = if (isAligned) color else color.copy(alpha = 0.7f)

    drawPath(
        path = needlePath,
        color = needleColor
    )

    val tipRadius = 5.dp.toPx()
    drawCircle(
        color = needleColor,
        radius = tipRadius,
        center = needleTip
    )

    val kaabaSize = 4.dp.toPx()
    drawRect(
        color = Color.White,
        topLeft = Offset(needleTip.x - kaabaSize, needleTip.y - kaabaSize),
        size = Size(kaabaSize * 2, kaabaSize * 2)
    )
    val kaabaRoof = Path().apply {
        moveTo(needleTip.x - kaabaSize - 1.dp.toPx(), needleTip.y - kaabaSize)
        lineTo(needleTip.x, needleTip.y - kaabaSize * 2 - 1.dp.toPx())
        lineTo(needleTip.x + kaabaSize + 1.dp.toPx(), needleTip.y - kaabaSize)
        close()
    }
    drawPath(path = kaabaRoof, color = Color.White)
}
