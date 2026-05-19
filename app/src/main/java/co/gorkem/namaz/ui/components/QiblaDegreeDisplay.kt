package co.gorkem.namaz.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R

@Composable
fun QiblaDegreeDisplay(
    bearing: Double,
    cardinalDirection: String,
    alignmentDelta: Double,
    isAligned: Boolean,
    needsCalibration: Boolean = false,
    isMagneticDistorted: Boolean = false,
    modifier: Modifier = Modifier
) {
    val animatedBearing by animateFloatAsState(
        targetValue = bearing.toFloat(),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bearing"
    )

    val targetColor = if (isAligned) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val contentColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "contentColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${animatedBearing.toInt()}°",
            style = MaterialTheme.typography.displayLarge.copy(
                fontFeatureSettings = "tnum",
                fontWeight = FontWeight.ExtraBold
            ),
            color = contentColor,
            textAlign = TextAlign.Center
        )

        Text(
            text = cardinalDirection,
            style = MaterialTheme.typography.headlineSmall,
            color = contentColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (isAligned) {
                stringResource(R.string.qibla_aligned)
            } else {
                "${alignmentDelta.toInt()}${stringResource(R.string.qibla_deviation)}"
            },
            style = MaterialTheme.typography.labelLarge.copy(
                fontFeatureSettings = "tnum",
                fontWeight = FontWeight.Medium
            ),
            color = contentColor,
            textAlign = TextAlign.Center
        )

        if (needsCalibration || isMagneticDistorted) {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.height(14.dp).width(14.dp),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
                Text(
                    text = when {
                        needsCalibration -> stringResource(R.string.qibla_calibration_needed)
                        isMagneticDistorted -> stringResource(R.string.qibla_magnetic_distortion)
                        else -> ""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
