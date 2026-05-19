package co.gorkem.namaz.ui.screens.qibla

import android.annotation.SuppressLint
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.gorkem.namaz.R
import co.gorkem.namaz.ui.components.QiblaCompass
import co.gorkem.namaz.ui.components.QiblaDegreeDisplay
import co.gorkem.namaz.ui.state.QiblaUiState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaScreen(
    modifier: Modifier = Modifier,
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is QiblaUiState.Loading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(R.string.qibla_loading),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is QiblaUiState.CompassActive -> {
                val isAligned = state.alignmentDelta <= 5.0
                val vibrator = remember { getVibrator(context) }
                var wasAligned by remember { mutableStateOf(false) }

                LaunchedEffect(isAligned) {
                    if (isAligned && !wasAligned) {
                        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    }
                    wasAligned = isAligned
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                        QiblaCompass(
                            deviceAzimuth = state.deviceAzimuth,
                            qiblaBearing = state.qiblaDirection.bearing,
                            alignmentDelta = state.alignmentDelta,
                            isAligned = isAligned,
                            pitch = state.pitch,
                            roll = state.roll
                        )

                    Spacer(modifier = Modifier.height(32.dp))

                    QiblaDegreeDisplay(
                        bearing = state.qiblaDirection.bearing,
                        cardinalDirection = state.qiblaDirection.cardinalDirection,
                        alignmentDelta = state.alignmentDelta,
                        isAligned = isAligned
                    )

                    if (state.needsCalibration || state.isMagneticDistorted) {
                        Spacer(modifier = Modifier.height(16.dp))
                        val microText = when {
                            state.needsCalibration -> stringResource(R.string.qibla_calibration_needed)
                            state.isMagneticDistorted -> stringResource(R.string.qibla_magnetic_distortion)
                            else -> ""
                        }
                        Text(
                            text = microText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            is QiblaUiState.StaticDirection -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.qibla_static_instruction),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    QiblaStaticArrow(
                        bearing = state.qiblaDirection.bearing
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    QiblaDegreeDisplay(
                        bearing = state.qiblaDirection.bearing,
                        cardinalDirection = state.qiblaDirection.cardinalDirection,
                        alignmentDelta = 0.0,
                        isAligned = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.qibla_no_magnetometer),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            is QiblaUiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is QiblaUiState.LocationRequired -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.qibla_location_required),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
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
    androidx.compose.foundation.Canvas(modifier = modifier.padding(16.dp)) {
        val center = androidx.compose.ui.geometry.Offset(size.width / 2, size.height / 2)
        val length = size.minDimension / 2 - 16.dp.toPx()
        val angleRad = Math.toRadians(bearing)
        val endX = center.x + length * sin(angleRad).toFloat()
        val endY = center.y - length * cos(angleRad).toFloat()

        drawLine(
            color = primaryColor,
            start = center,
            end = androidx.compose.ui.geometry.Offset(endX, endY),
            strokeWidth = 4.dp.toPx(),
            cap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        drawCircle(
            color = primaryColor,
            radius = 8.dp.toPx(),
            center = androidx.compose.ui.geometry.Offset(endX, endY)
        )
    }
}

@Suppress("DEPRECATION")
private fun getVibrator(context: Context): Vibrator? {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
}
