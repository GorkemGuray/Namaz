package co.gorkem.namaz.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R

@Composable
fun QiblaDegreeDisplay(
    bearing: Double,
    cardinalDirection: String,
    alignmentDelta: Double,
    isAligned: Boolean,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isAligned) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

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
                    fontFeatureSettings = "tnum"
                ),
                color = contentColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = cardinalDirection,
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isAligned) {
                stringResource(R.string.qibla_aligned)
            } else {
                "${alignmentDelta.toInt()}${stringResource(R.string.qibla_deviation)}"
            },
            style = MaterialTheme.typography.labelLarge.copy(
                fontFeatureSettings = "tnum"
            ),
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}
