package co.gorkem.namaz.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
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
    val compassRotation by animateFloatAsState(
        targetValue = (-deviceAzimuth).toFloat(),
        label = "compass_rotation"
    )

    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(220.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 12.dp.toPx()

            drawCircle(
                color = outlineVariant.copy(alpha = 0.3f),
                radius = radius,
                style = Stroke(width = 2.dp.toPx())
            )

            drawCircle(
                color = outlineVariant.copy(alpha = 0.15f),
                radius = radius * 0.7f,
                style = Stroke(width = 1.dp.toPx())
            )

            for (i in 0 until 4) {
                val angle = i * 90f
                rotate(angle, center) {
                    drawLine(
                        color = onSurfaceVariant.copy(alpha = 0.6f),
                        start = Offset(center.x, center.y - radius),
                        end = Offset(center.x, center.y - radius + 16.dp.toPx()),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            for (i in 0 until 12) {
                if (i % 3 != 0) {
                    val angle = i * 30f
                    rotate(angle, center) {
                        drawLine(
                            color = outlineVariant.copy(alpha = 0.25f),
                            start = Offset(center.x, center.y - radius + 4.dp.toPx()),
                            end = Offset(center.x, center.y - radius + 10.dp.toPx()),
                            strokeWidth = 1.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            val qiblaAngle = qiblaBearing.toFloat()
            rotate(qiblaAngle, center) {
                val arcRadius = radius * 0.85f
                val arcPath = Path()
                arcPath.addArc(
                    oval = androidx.compose.ui.geometry.Rect(
                        center.x - arcRadius,
                        center.y - arcRadius,
                        center.x + arcRadius,
                        center.y + arcRadius
                    ),
                    startAngleDegrees = -15f,
                    sweepAngleDegrees = 30f
                )
                drawPath(
                    path = arcPath,
                    color = if (isAligned) primary else primary.copy(alpha = 0.5f),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                val arrowLen = radius * 0.6f
                val arrowY = center.y - arrowLen
                drawLine(
                    color = primary,
                    start = center,
                    end = Offset(center.x, arrowY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                val headSize = 8.dp.toPx()
                drawLine(
                    color = primary,
                    start = Offset(center.x, arrowY),
                    end = Offset(center.x - headSize, arrowY + headSize),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = primary,
                    start = Offset(center.x, arrowY),
                    end = Offset(center.x + headSize, arrowY + headSize),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            rotate(compassRotation, center) {
                drawLine(
                    color = Color(0xFFB3261E),
                    start = center,
                    end = Offset(center.x, center.y - radius * 0.5f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = onSurfaceVariant.copy(alpha = 0.4f),
                    start = center,
                    end = Offset(center.x, center.y + radius * 0.4f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            drawCircle(
                color = primary,
                radius = 5.dp.toPx()
            )
        }
    }
}
