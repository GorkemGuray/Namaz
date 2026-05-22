package co.gorkem.namaz.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
        MaterialTheme.colorScheme.onSurface
    }
    val contentColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "contentColor"
    )

    val cardBgColor by animateColorAsState(
        targetValue = if (isAligned) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardBgColor"
    )

    val cardBorderColor by animateColorAsState(
        targetValue = if (isAligned) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardBorderColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor
        ),
        border = BorderStroke(1.5.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isAligned) 4.dp else 1.dp
        )
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
                    text = "${animatedBearing.toInt()}°",
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

            Text(
                text = if (isAligned) {
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

            if (needsCalibration || isMagneticDistorted) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.width(16.dp).height(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            needsCalibration -> stringResource(R.string.qibla_calibration_needed)
                            isMagneticDistorted -> stringResource(R.string.qibla_magnetic_distortion)
                            else -> ""
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
