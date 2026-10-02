package co.gorkem.namaz.ui.components

import android.hardware.SensorManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class GuidanceTone { NEUTRAL, WARNING }

enum class CompassGuidance(
    val tone: GuidanceTone,
    val showsFigureEight: Boolean,
    val showsMetalHint: Boolean
) {
    NONE(GuidanceTone.NEUTRAL, false, false),
    CALIBRATION(GuidanceTone.NEUTRAL, true, false),
    MAGNETIC_INTERFERENCE(GuidanceTone.WARNING, false, true)
}

fun resolveCompassGuidance(
    needsCalibration: Boolean,
    isMagneticDistorted: Boolean,
    sensorAccuracy: Int
): CompassGuidance {
    if (isMagneticDistorted) return CompassGuidance.MAGNETIC_INTERFERENCE
    if (needsCalibration) return CompassGuidance.CALIBRATION
    return when (sensorAccuracy) {
        SensorManager.SENSOR_STATUS_UNRELIABLE,
        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> CompassGuidance.CALIBRATION
        else -> CompassGuidance.NONE
    }
}

@Composable
fun QiblaDegreeDisplay(
    bearing: Double,
    cardinalDirection: String,
    alignmentDelta: Double,
    isAligned: Boolean,
    needsCalibration: Boolean = false,
    isMagneticDistorted: Boolean = false,
    sensorAccuracy: Int? = null,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceContainerLow = MaterialTheme.colorScheme.surfaceContainerLow

    val guidance = resolveCompassGuidance(
        needsCalibration = needsCalibration,
        isMagneticDistorted = isMagneticDistorted,
        sensorAccuracy = sensorAccuracy ?: SensorManager.SENSOR_STATUS_NO_CONTACT
    )

    val contentColor by animateColorAsState(
        targetValue = if (isAligned) primary else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(400),
        label = "contentColor"
    )

    val overlayAlpha by animateFloatAsState(
        targetValue = if (isAligned) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "overlayAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "alignAnim")

    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    val trailAngle2 by infiniteTransition.animateFloat(
        initialValue = 120f,
        targetValue = 480f,
        animationSpec = infiniteRepeatable(
            animation = tween(3100, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "trailAngle2"
    )

    val breatheAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheAlpha"
    )

    val cornerRadiusDp = 32.dp

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val alpha = overlayAlpha
            val shouldDraw = alpha > 0.01f
            if (shouldDraw) {
                val cornerPx = cornerRadiusDp.toPx()
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val cy = h / 2f

                val roundedPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = 0f,
                            top = 0f,
                            right = w,
                            bottom = h,
                            cornerRadius = CornerRadius(cornerPx, cornerPx)
                        )
                    )
                }

                drawPath(
                    path = roundedPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            primaryContainer.copy(alpha = 0.22f * alpha),
                            primaryContainer.copy(alpha = 0.04f * alpha)
                        )
                    )
                )

                val diagHalf = sqrt((w * w + h * h).toDouble()).toFloat() / 2f

                clipPath(roundedPath) {
                    val sweepRad = Math.toRadians(sweepAngle.toDouble())
                    val trailSpread = Math.toRadians(18.0)

                    val ex1 = cx + diagHalf * sin(sweepRad).toFloat()
                    val ey1 = cy - diagHalf * cos(sweepRad).toFloat()
                    val lx1 = cx + diagHalf * sin(sweepRad - trailSpread).toFloat()
                    val ly1 = cy - diagHalf * cos(sweepRad - trailSpread).toFloat()

                    drawPath(
                        path = Path().apply {
                            moveTo(cx, cy)
                            lineTo(lx1, ly1)
                            lineTo(ex1, ey1)
                            close()
                        },
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primary.copy(alpha = 0.55f * alpha),
                                primary.copy(alpha = 0f)
                            ),
                            center = Offset(cx, cy),
                            radius = diagHalf
                        )
                    )

                    val trail2Rad = Math.toRadians(trailAngle2.toDouble())
                    val ex2 = cx + diagHalf * sin(trail2Rad).toFloat()
                    val ey2 = cy - diagHalf * cos(trail2Rad).toFloat()
                    val lx2 = cx + diagHalf * sin(trail2Rad - trailSpread).toFloat()
                    val ly2 = cy - diagHalf * cos(trail2Rad - trailSpread).toFloat()

                    drawPath(
                        path = Path().apply {
                            moveTo(cx, cy)
                            lineTo(lx2, ly2)
                            lineTo(ex2, ey2)
                            close()
                        },
                        brush = Brush.radialGradient(
                            colors = listOf(
                                primary.copy(alpha = 0.35f * alpha),
                                primary.copy(alpha = 0f)
                            ),
                            center = Offset(cx, cy),
                            radius = diagHalf
                        )
                    )
                }

                drawArc(
                    color = primary.copy(alpha = breatheAlpha * alpha),
                    startAngle = sweepAngle - 90f,
                    sweepAngle = 60f,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(w, h),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                drawArc(
                    color = primary.copy(alpha = (breatheAlpha * 0.55f) * alpha),
                    startAngle = trailAngle2 - 90f,
                    sweepAngle = 40f,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(w, h),
                    style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                )

                drawRoundRect(
                    color = primary.copy(alpha = 0.18f * alpha),
                    size = Size(w, h),
                    cornerRadius = CornerRadius(cornerPx),
                    style = Stroke(width = 1.dp.toPx())
                )

                val dotAngles = listOf(
                    sweepAngle to 0.85f,
                    sweepAngle + 90f to 0.5f,
                    sweepAngle + 200f to 0.3f,
                    trailAngle2 to 0.7f,
                    trailAngle2 + 130f to 0.35f
                )
                dotAngles.forEach { (angleDeg, dotAlphaMult) ->
                    val dotRad = Math.toRadians(angleDeg.toDouble())
                    val dotX = cx + (w / 2f - 1f) * sin(dotRad).toFloat()
                    val dotY = cy - (h / 2f - 1f) * cos(dotRad).toFloat()
                    drawCircle(
                        color = primary.copy(alpha = dotAlphaMult.toFloat() * alpha),
                        radius = 3.5.dp.toPx(),
                        center = Offset(dotX, dotY)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(cornerRadiusDp),
            colors = CardDefaults.cardColors(
                containerColor = if (isAligned) Color.Transparent else surfaceContainerLow
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${bearing.toInt()}°",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFeatureSettings = "tnum",
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = cardinalDirection,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = contentColor.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = isAligned,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250))
                    },
                    label = "status_text"
                ) { aligned ->
                    Text(
                        text = if (aligned) {
                            stringResource(R.string.qibla_aligned)
                        } else {
                            "${alignmentDelta.toInt()}${stringResource(R.string.qibla_deviation)}"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFeatureSettings = "tnum",
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )
                }

                if (guidance != CompassGuidance.NONE) {
                    Spacer(modifier = Modifier.height(16.dp))

                    CompassGuidanceCard(
                        guidance = guidance,
                        sensorAccuracy = sensorAccuracy
                    )
                }

                sensorAccuracy?.let { accuracy ->
                    Spacer(modifier = Modifier.height(12.dp))

                    val accuracyText = when (accuracy) {
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> stringResource(R.string.qibla_accuracy_high)
                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> stringResource(R.string.qibla_accuracy_medium)
                        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> stringResource(R.string.qibla_accuracy_low)
                        else -> stringResource(R.string.qibla_accuracy_unreliable)
                    }

                    val dotColor = when (accuracy) {
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> Color(0xFF4CAF50)
                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(dotColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.qibla_sensor_accuracy, accuracyText),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompassGuidanceCard(
    guidance: CompassGuidance,
    sensorAccuracy: Int?
) {
    var expanded by remember(guidance) { mutableStateOf(true) }

    val isInterference = guidance.tone == GuidanceTone.WARNING
    val containerColor = if (isInterference) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    val contentColor = if (isInterference) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onTertiaryContainer
    }
    val accentColor = if (isInterference) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isInterference) Icons.Default.Warning else Icons.Default.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(
                        if (isInterference) R.string.qibla_interference_title
                        else R.string.qibla_calibration_title
                    ),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = contentColor,
                    modifier = Modifier.weight(1f)
                )
                if (!isInterference) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = stringResource(
                                if (expanded) R.string.qibla_calibration_collapse
                                else R.string.qibla_calibration_expand
                            ),
                            tint = contentColor
                        )
                    }
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (guidance.showsFigureEight) {
                        stringResource(R.string.qibla_calibration_figure_eight)
                    } else {
                        stringResource(R.string.qibla_interference_move_away)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )

                if (guidance == CompassGuidance.CALIBRATION) {
                    Spacer(modifier = Modifier.height(12.dp))

                    val progress = when (sensorAccuracy) {
                        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> 1f
                        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> 0.66f
                        else -> 0.33f
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = accentColor,
                        trackColor = accentColor.copy(alpha = 0.18f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.qibla_calibration_waiting),
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
