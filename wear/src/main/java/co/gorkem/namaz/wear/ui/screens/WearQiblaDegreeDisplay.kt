package co.gorkem.namaz.wear.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WearQiblaDegreeDisplay(
    text: String,
    isAligned: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (isAligned) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.6f)
                },
                shape = CircleShape
            )
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = if (isAligned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WearQiblaCityDisplay(
    cityName: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = cityName,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        modifier = modifier
    )
}

@Composable
fun WearQiblaCalibrationIndicator(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (visible) {
        Icon(
            imageVector = Icons.Default.CompassCalibration,
            contentDescription = "Kalibrasyon Gerekli",
            tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
            modifier = modifier.size(16.dp)
        )
    }
}
