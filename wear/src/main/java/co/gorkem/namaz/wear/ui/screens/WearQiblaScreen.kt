package co.gorkem.namaz.wear.ui.screens

import android.content.Context
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.gorkem.namaz.wear.data.QiblaDirection
import co.gorkem.namaz.wear.ui.WearQiblaViewModel
import co.gorkem.namaz.wear.ui.state.QiblaUiState
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WearQiblaScreen(
    onNavigateBack: () -> Unit,
    viewModel: WearQiblaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    var lastAlignedState by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is QiblaUiState.Loading -> {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            is QiblaUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is QiblaUiState.LocationRequired -> {
                Text(
                    text = "Konum bilgisi bekleniyor...",
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
            is QiblaUiState.StaticDirection -> {
                WearStaticQiblaCompass(
                    qiblaDirection = state.qiblaDirection,
                    modifier = Modifier.size(160.dp)
                )
            }
            is QiblaUiState.CompassActive -> {
                val isAligned = state.alignmentDelta <= 5.0
                
                LaunchedEffect(isAligned) {
                    if (isAligned && !lastAlignedState) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    lastAlignedState = isAligned
                }

                WearActiveQiblaCompass(
                    deviceAzimuth = state.deviceAzimuth,
                    qiblaBearing = state.qiblaDirection.bearing,
                    alignmentDelta = state.alignmentDelta,
                    isAligned = isAligned,
                    modifier = Modifier.size(160.dp)
                )

                // Calibration or Distorted Warning overlay
                if (state.needsCalibration || state.isMagneticDistorted) {
                    Icon(
                        imageVector = Icons.Default.CompassCalibration,
                        contentDescription = "Kalibrasyon Gerekli",
                        tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .size(16.dp)
                    )
                }

                // Degree display at the top
                Text(
                    text = "${state.alignmentDelta.toInt()}°",
                    color = if (isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )
            }
        }

        // Vakitlere dön butonu (Sol tarafta dairesel kenarda)
        IconButton(
            onClick = onNavigateBack,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 8.dp)
                .size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Geri Dön",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun WearActiveQiblaCompass(
    deviceAzimuth: Double,
    qiblaBearing: Double,
    alignmentDelta: Double,
    isAligned: Boolean,
    modifier: Modifier = Modifier
) {
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val textMeasurer = rememberTextMeasurer()

    var accumulatedAzimuth by remember { mutableStateOf(deviceAzimuth) }
    LaunchedEffect(deviceAzimuth) {
        var diff = (deviceAzimuth - accumulatedAzimuth) % 360
        if (diff < 0) diff += 360
        val shortestDiff = if (diff > 180) diff - 360 else diff
        accumulatedAzimuth += shortestDiff
    }

    val animatedAzimuth by animateFloatAsState(
        targetValue = accumulatedAzimuth.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "azimuth"
    )

    val targetKaaba = qiblaBearing - deviceAzimuth
    var accumulatedKaaba by remember { mutableStateOf(targetKaaba) }
    LaunchedEffect(targetKaaba) {
        var diff = (targetKaaba - accumulatedKaaba) % 360
        if (diff < 0) diff += 360
        val shortestDiff = if (diff > 180) diff - 360 else diff
        accumulatedKaaba += shortestDiff
    }

    val animatedKaaba by animateFloatAsState(
        targetValue = accumulatedKaaba.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "kaaba"
    )

    val glowTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by glowTransition.animateFloat(
        initialValue = 0.0f,
        targetValue = 0.5f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowRadius by glowTransition.animateFloat(
        initialValue = 10f,
        targetValue = 16f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowRadius"
    )

    val alignedAlpha by animateFloatAsState(
        targetValue = if (isAligned) 1.0f else 0.0f,
        animationSpec = tween(500),
        label = "alignedAlpha"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = size.minDimension / 2 - 8.dp.toPx()
        val innerRadius = outerRadius * 0.75f

        // Outer ring
        drawCircle(
            color = if (isAligned) primary.copy(alpha = 0.8f) else outlineVariant.copy(alpha = 0.3f),
            radius = outerRadius,
            style = Stroke(width = if (isAligned) 2.5.dp.toPx() else 1.5.dp.toPx())
        )

        // Inner ring
        drawCircle(
            color = if (isAligned) primary.copy(alpha = 0.4f) else outlineVariant.copy(alpha = 0.15f),
            radius = innerRadius,
            style = Stroke(width = 1.dp.toPx())
        )

        // Top pointer indicator
        drawTopPointer(center, outerRadius, primary)

        // Rotate degree markings and cardinal points
        rotate(degrees = -animatedAzimuth, pivot = center) {
            drawDegreeTicks(center, outerRadius, onSurfaceVariant)
            drawCardinalLabels(center, innerRadius, onSurfaceVariant, textMeasurer)
        }

        // Draw Kaaba arc indicator if not aligned
        if (!isAligned) {
            val sweep = animatedKaaba
            var normalizedSweep = sweep % 360
            if (normalizedSweep > 180) normalizedSweep -= 360
            if (normalizedSweep < -180) normalizedSweep += 360

            drawArc(
                color = primary.copy(alpha = 0.5f),
                startAngle = -90f,
                sweepAngle = normalizedSweep,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Kaaba icon coordinates
        val kaabaRad = Math.toRadians(animatedKaaba.toDouble())
        val kaabaX = center.x + outerRadius * sin(kaabaRad).toFloat()
        val kaabaY = center.y - outerRadius * cos(kaabaRad).toFloat()

        // Glow effect
        if (alignedAlpha > 0f) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primary.copy(alpha = glowAlpha * 0.8f * alignedAlpha),
                        Color.Transparent
                    ),
                    center = Offset(kaabaX, kaabaY),
                    radius = glowRadius.dp.toPx() * 1.5f
                ),
                radius = glowRadius.dp.toPx() * 1.5f,
                center = Offset(kaabaX, kaabaY)
            )
        }

        // Kaaba Cube drawing
        drawWearKaabaIcon(
            center = Offset(kaabaX, kaabaY),
            scale = 0.7f + 0.15f * alignedAlpha
        )
    }
}

@Composable
fun WearStaticQiblaCompass(
    qiblaDirection: QiblaDirection,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val outerRadius = size.minDimension / 2 - 8.dp.toPx()
        val innerRadius = outerRadius * 0.75f

        // Outer ring
        drawCircle(
            color = outlineVariant.copy(alpha = 0.4f),
            radius = outerRadius,
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Inner ring
        drawCircle(
            color = outlineVariant.copy(alpha = 0.2f),
            radius = innerRadius,
            style = Stroke(width = 1.dp.toPx())
        )

        // Static arrow pointing to Qibla bearing (North is at 0 degrees, top)
        rotate(degrees = qiblaDirection.bearing.toFloat(), pivot = center) {
            // Draw a pointer arrow pointing to the top of canvas (which represents the relative bearing)
            val arrowPath = Path().apply {
                moveTo(center.x, center.y - outerRadius)
                lineTo(center.x - 8.dp.toPx(), center.y - innerRadius)
                lineTo(center.x + 8.dp.toPx(), center.y - innerRadius)
                close()
            }
            drawPath(path = arrowPath, color = primary)
        }

        // Draw compass labels (North at top)
        drawCardinalLabels(center, innerRadius, outlineVariant, textMeasurer)
        
        // Show kaaba icon at center of the static compass
        drawWearKaabaIcon(center, scale = 0.9f)
    }
}

private fun DrawScope.drawTopPointer(
    center: Offset,
    radius: Float,
    color: Color
) {
    val size = 8.dp.toPx()
    val topY = center.y - radius - 4.dp.toPx()
    val path = Path().apply {
        moveTo(center.x, topY + size)
        lineTo(center.x - size / 2, topY)
        lineTo(center.x + size / 2, topY)
        close()
    }
    drawPath(path = path, color = color)
}

private fun DrawScope.drawDegreeTicks(
    center: Offset,
    radius: Float,
    color: Color
) {
    for (i in 0 until 24) {
        val angle = i * 15f
        val isMajor = i % 6 == 0
        val tickLength = if (isMajor) 8.dp.toPx() else 4.dp.toPx()
        val tickWidth = if (isMajor) 1.5.dp.toPx() else 1.dp.toPx()
        val alpha = if (isMajor) 0.5f else 0.25f

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

private fun DrawScope.drawCardinalLabels(
    center: Offset,
    radius: Float,
    color: Color,
    textMeasurer: TextMeasurer
) {
    val labels = listOf("K" to 0, "D" to 90, "G" to 180, "B" to 270)

    for ((text, deg) in labels) {
        val labelColor = if (text == "K") {
            Color(0xFFE53935)
        } else {
            color.copy(alpha = 0.7f)
        }
        val style = TextStyle(
            fontSize = 11.sp,
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

private fun DrawScope.drawWearKaabaIcon(
    center: Offset,
    scale: Float
) {
    val baseSize = 14.dp.toPx() * scale
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

    // Gold band
    val bandHeight = 1.5.dp.toPx()
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
}
