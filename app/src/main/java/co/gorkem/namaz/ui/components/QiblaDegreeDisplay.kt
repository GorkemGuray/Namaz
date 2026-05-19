package co.gorkem.namaz.ui.components

import android.hardware.SensorManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.gorkem.namaz.R

@Composable
fun QiblaDegreeDisplay(
    bearing: Double,
    cardinalDirection: String,
    alignmentDelta: Double,
    isAligned: Boolean,
    sensorAccuracy: Int = SensorManager.SENSOR_STATUS_NO_CONTACT,
    magneticFieldStrength: Float = 0f,
    isMagneticDistorted: Boolean = false,
    needsCalibration: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${bearing.toInt()}°",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFeatureSettings = "tnum"
                ),
                color = if (isAligned)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = cardinalDirection,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isAligned) {
            Text(
                text = stringResource(R.string.qibla_aligned),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "${alignmentDelta.toInt()}${stringResource(R.string.qibla_deviation)}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        val accuracyLabel = when (sensorAccuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "\u2713 Yüksek"
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Orta"
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "\u26A0 Düşük"
            SensorManager.SENSOR_STATUS_UNRELIABLE -> "\u26A0 Güvenilmez"
            else -> null
        }
        if (accuracyLabel != null) {
            Text(
                text = stringResource(R.string.qibla_sensor_accuracy, accuracyLabel),
                fontSize = 11.sp,
                color = when (sensorAccuracy) {
                    SensorManager.SENSOR_STATUS_ACCURACY_LOW,
                    SensorManager.SENSOR_STATUS_UNRELIABLE ->
                        MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    else ->
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                },
                textAlign = TextAlign.Center
            )
        }

        if (isMagneticDistorted) {
            Text(
                text = "${magneticFieldStrength.toInt()} μT",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}
