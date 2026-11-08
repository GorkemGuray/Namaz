package co.gorkem.namaz.wear.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.ScreenScaffold
import co.gorkem.namaz.wear.ui.WearQiblaViewModel
import co.gorkem.namaz.wear.ui.state.QiblaUiState

@Composable
fun WearQiblaScreen(
    onNavigateBack: () -> Unit,
    viewModel: WearQiblaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var lastAlignedState by remember { mutableStateOf(false) }

    ScreenScaffold(
        timeText = {}, // Pusula modunda saat rozetle çakışmaması için gizlenir
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    WearStaticQiblaCompass(
                        qiblaDirection = state.qiblaDirection,
                        modifier = Modifier.fillMaxSize()
                    )

                    WearQiblaCityDisplay(
                        cityName = state.cityName,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 12.dp)
                    )

                    WearQiblaDegreeDisplay(
                        text = "${state.qiblaDirection.bearing.toInt()}°",
                        isAligned = false,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                    )
                }
            }
            is QiblaUiState.CompassActive -> {
                val targetAligned = state.alignmentDelta <= 5.0
                
                LaunchedEffect(targetAligned) {
                    if (targetAligned && !lastAlignedState) {
                        vibrate(context, true)
                    }
                    lastAlignedState = targetAligned
                }

                WearActiveQiblaCompass(
                    deviceAzimuth = state.deviceAzimuth,
                    qiblaBearing = state.qiblaDirection.bearing,
                    alignmentDelta = state.alignmentDelta,
                    isAligned = targetAligned,
                    pitch = state.pitch,
                    roll = state.roll,
                    modifier = Modifier.fillMaxSize()
                )

                WearQiblaCalibrationIndicator(
                    visible = state.needsCalibration || state.isMagneticDistorted,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                )

                WearQiblaCityDisplay(
                    cityName = state.cityName,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                )

                WearQiblaDegreeDisplay(
                    text = if (targetAligned) "KIBLE" else "${state.alignmentDelta.toInt()}°",
                    isAligned = targetAligned,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                )
            }
        }
    }
}
}

private fun vibrate(context: Context, isTargetReached: Boolean) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val duration = if (isTargetReached) 100L else 30L
        val amplitude = if (isTargetReached) 200 else 50
        vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(if (isTargetReached) 100L else 30L)
    }
}
