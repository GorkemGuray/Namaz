package co.gorkem.namaz.ui.screens.qibla

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import kotlinx.coroutines.delay
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.gorkem.namaz.R
import co.gorkem.namaz.ui.components.QiblaCompass
import co.gorkem.namaz.ui.components.QiblaDegreeDisplay
import co.gorkem.namaz.ui.state.QiblaUiState
import co.gorkem.namaz.ui.theme.LocalPrayerContext
import co.gorkem.namaz.ui.theme.prayerAmbientColor
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QiblaScreen(
    modifier: Modifier = Modifier,
    onSetLocationClick: (() -> Unit)? = null,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val prayerContext = LocalPrayerContext.current
    val ambientTint = prayerAmbientColor(prayerContext)
    
    var showMapBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ambientTint)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        val crossfadeKey = when (uiState) {
            is QiblaUiState.Loading -> 0
            is QiblaUiState.CompassActive -> 1
            is QiblaUiState.StaticDirection -> 2
            is QiblaUiState.Error -> 3
            is QiblaUiState.LocationRequired -> 4
        }
        Crossfade(
            targetState = crossfadeKey,
            animationSpec = tween(400),
            label = "qibla_state"
        ) {
            when (val state = uiState) {
                is QiblaUiState.Loading -> {
                    val infiniteTransition = rememberInfiniteTransition(label = "")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 0.6f,
                        animationSpec = InfiniteRepeatableSpec(
                            animation = tween(1500),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = ""
                    )
                    val rotationAngle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = InfiniteRepeatableSpec(
                            animation = tween(2000, easing = androidx.compose.animation.core.LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = ""
                    )
                    val primary = MaterialTheme.colorScheme.primary
                    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Canvas(modifier = Modifier.size(160.dp)) {
                            val center = Offset(size.width / 2, size.height / 2)
                            val radius = size.minDimension / 2 - 12.dp.toPx()

                            drawCircle(
                                color = primary.copy(alpha = pulseAlpha * 0.3f),
                                radius = radius,
                                style = Stroke(width = 2.dp.toPx())
                            )

                            drawCircle(
                                color = primary.copy(alpha = pulseAlpha * 0.15f),
                                radius = radius * 0.65f,
                                style = Stroke(width = 1.dp.toPx())
                            )

                            drawCircle(
                                color = primary.copy(alpha = pulseAlpha * 0.2f),
                                radius = radius * 0.3f
                            )

                            rotate(rotationAngle, center) {
                                drawArc(
                                    color = primary,
                                    startAngle = -90f,
                                    sweepAngle = 90f,
                                    useCenter = false,
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                )

                                val angleRad = Math.toRadians(-90.0)
                                drawCircle(
                                    color = primary,
                                    radius = 4.dp.toPx(),
                                    center = Offset(
                                        center.x + radius * sin(angleRad).toFloat(),
                                        center.y - radius * cos(angleRad).toFloat()
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = stringResource(R.string.qibla_loading),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = onSurfaceVariant.copy(alpha = 0.5f + pulseAlpha * 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }

                is QiblaUiState.CompassActive -> {
                    var isAligned by remember { mutableStateOf(false) }

                    val targetAligned = if (isAligned) {
                        state.alignmentDelta <= 6.5
                    } else {
                        state.alignmentDelta <= 4.0
                    }

                    LaunchedEffect(targetAligned) {
                        if (targetAligned != isAligned) {
                            delay(200)
                            isAligned = targetAligned
                        }
                    }

                    val vibrator = remember { getVibrator(context) }
                    var wasAligned by remember { mutableStateOf(false) }
                    var showCompass by remember { mutableStateOf(false) }
                    var showDisplay by remember { mutableStateOf(false) }

                    LaunchedEffect(isAligned) {
                        if (isAligned && !wasAligned) {
                            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        }
                        wasAligned = isAligned
                    }

                    LaunchedEffect(Unit) {
                        delay(400)
                        showCompass = true
                        delay(200)
                        showDisplay = true
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnimatedVisibility(
                            visible = showCompass,
                            enter = fadeIn(tween(600)) + scaleIn(
                                animationSpec = tween(600),
                                initialScale = 0.8f
                            )
                        ) {
                            QiblaCompass(
                                deviceAzimuth = state.deviceAzimuth,
                                qiblaBearing = state.qiblaDirection.bearing,
                                alignmentDelta = state.alignmentDelta,
                                isAligned = isAligned,
                                pitch = state.pitch,
                                roll = state.roll
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        AnimatedVisibility(
                            visible = showDisplay,
                            enter = fadeIn(tween(400))
                        ) {
                            QiblaDegreeDisplay(
                                bearing = state.qiblaDirection.bearing,
                                cardinalDirection = state.qiblaDirection.cardinalDirection,
                                alignmentDelta = state.alignmentDelta,
                                isAligned = isAligned,
                                needsCalibration = state.needsCalibration,
                                isMagneticDistorted = state.isMagneticDistorted,
                                sensorAccuracy = state.sensorAccuracy
                            )
                        }
                    }
                }

                is QiblaUiState.StaticDirection -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.qibla_static_instruction),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        QiblaStaticArrow(
                            bearing = state.qiblaDirection.bearing
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        QiblaDegreeDisplay(
                            bearing = state.qiblaDirection.bearing,
                            cardinalDirection = state.qiblaDirection.cardinalDirection,
                            alignmentDelta = 0.0,
                            isAligned = false,
                            needsCalibration = false,
                            isMagneticDistorted = false
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.qibla_no_magnetometer),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                is QiblaUiState.Error -> {
                    var isRetrying by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = stringResource(R.string.qibla_error_prefix),
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.qibla_error_prefix),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        FilledTonalButton(
                            onClick = {
                                if (!isRetrying) {
                                    isRetrying = true
                                    viewModel.retry()
                                }
                            },
                            enabled = !isRetrying,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            if (isRetrying) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(R.string.qibla_retrying))
                            } else {
                                Text(text = stringResource(R.string.qibla_retry))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.qibla_error_fix),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }

                is QiblaUiState.LocationRequired -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.qibla_location_required_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.qibla_location_required),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )

                        if (onSetLocationClick != null) {
                            Spacer(modifier = Modifier.height(32.dp))

                            FilledTonalButton(
                                onClick = onSetLocationClick
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(R.string.qibla_set_location))
                            }
                        }
                    }
                }
            }
        }

        if (uiState is QiblaUiState.CompassActive || uiState is QiblaUiState.StaticDirection) {
            val userLocation = when (val state = uiState) {
                is QiblaUiState.CompassActive -> state.userLocation
                is QiblaUiState.StaticDirection -> state.userLocation
                else -> null
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(onClick = { showMapBottomSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Haritada Gör",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (showMapBottomSheet && userLocation != null) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    onDismissRequest = { showMapBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)) {
                        QiblaMap(
                            userLocation = userLocation,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QiblaStaticArrow(
    bearing: Double,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary

    Canvas(modifier = modifier.size(160.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 - 8.dp.toPx()
        val angleRad = Math.toRadians(bearing)

        drawCircle(
            color = outlineVariantColor,
            radius = radius,
            style = Stroke(width = 1.5.dp.toPx())
        )

        drawCircle(
            color = outlineVariantColor.copy(alpha = 0.05f),
            radius = radius
        )

        for (i in 0 until 12) {
            val tickAngle = i * 30.0
            val tickRad = Math.toRadians(tickAngle)
            val tickLen = 6.dp.toPx()
            val start = Offset(
                center.x + (radius - tickLen) * sin(tickRad).toFloat(),
                center.y - (radius - tickLen) * cos(tickRad).toFloat()
            )
            val end = Offset(
                center.x + radius * sin(tickRad).toFloat(),
                center.y - radius * cos(tickRad).toFloat()
            )
            drawLine(
                color = outlineVariantColor.copy(alpha = 0.5f),
                start = start,
                end = end,
                strokeWidth = 1.dp.toPx()
            )
        }

        val needleLength = radius - 16.dp.toPx()
        val endX = center.x + needleLength * sin(angleRad).toFloat()
        val endY = center.y - needleLength * cos(angleRad).toFloat()

        val backAngleRad = angleRad + Math.PI
        val backX = center.x + 12.dp.toPx() * sin(backAngleRad).toFloat()
        val backY = center.y - 12.dp.toPx() * cos(backAngleRad).toFloat()

        val leftAngleRad = angleRad - Math.PI / 2
        val leftX = center.x + 8.dp.toPx() * sin(leftAngleRad).toFloat()
        val leftY = center.y - 8.dp.toPx() * cos(leftAngleRad).toFloat()

        val rightAngleRad = angleRad + Math.PI / 2
        val rightX = center.x + 8.dp.toPx() * sin(rightAngleRad).toFloat()
        val rightY = center.y - 8.dp.toPx() * cos(rightAngleRad).toFloat()

        val leftHalf = Path().apply {
            moveTo(endX, endY)
            lineTo(leftX, leftY)
            lineTo(backX, backY)
            close()
        }

        val rightHalf = Path().apply {
            moveTo(endX, endY)
            lineTo(rightX, rightY)
            lineTo(backX, backY)
            close()
        }

        drawPath(path = leftHalf, color = primaryColor)
        drawPath(path = rightHalf, color = primaryColor.copy(alpha = 0.75f))

        drawCircle(
            color = onPrimaryColor,
            radius = 3.dp.toPx(),
            center = center
        )

        val goldColor = Color(0xFFFFD700)
        drawCircle(
            color = goldColor,
            radius = 4.dp.toPx(),
            center = Offset(
                center.x + radius * sin(angleRad).toFloat(),
                center.y - radius * cos(angleRad).toFloat()
            )
        )
    }
}

private fun getVibrator(context: Context): Vibrator? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
}
