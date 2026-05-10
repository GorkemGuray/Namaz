package co.gorkem.namaz.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.ui.theme.ListItemShape
import co.gorkem.namaz.ui.theme.PillShape

@Composable
fun PrayerTimesList(
    prayerTime: PrayerTime,
    nextPrayer: GetNextPrayerUseCase.NextPrayerInfo?,
    currentPrayerName: PrayerName?,
    modifier: Modifier = Modifier
) {
    val prayers = prayerTime.getAllPrayers()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            prayers.forEachIndexed { index, (name, time) ->
                val isNext = name == nextPrayer?.prayerName
                val isCurrent = name == currentPrayerName

                PrayerListRow(
                    name = name,
                    time = time,
                    isNext = isNext,
                    isCurrent = isCurrent,
                    modifier = Modifier.fillMaxWidth()
                )

                if (index < prayers.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerListRow(
    name: PrayerName,
    time: String,
    isNext: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isNext) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "row_scale"
    )

    val containerColor = when {
        isNext -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
        isCurrent -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
        else -> Color.Transparent
    }

    val nameColor = when {
        isNext -> MaterialTheme.colorScheme.onPrimaryContainer
        isCurrent -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    val timeColor = when {
        isNext -> MaterialTheme.colorScheme.primary
        isCurrent -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .clip(ListItemShape)
            .background(containerColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isNext -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        isCurrent -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            PrayerIcon(
                name = name,
                modifier = Modifier.size(18.dp),
                tint = when {
                    isNext -> MaterialTheme.colorScheme.primary
                    isCurrent -> MaterialTheme.colorScheme.tertiary
                    else -> prayerIconTint(name).copy(alpha = 0.8f)
                }
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = name.turkishName,
            style = if (isNext) {
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            } else {
                MaterialTheme.typography.titleMedium
            },
            color = nameColor,
            modifier = Modifier.weight(1f)
        )

        if (isNext) {
            AnimatedContent(
                targetState = true,
                transitionSpec = {
                    scaleIn(
                        initialScale = 0.7f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeIn() togetherWith
                    scaleOut(targetScale = 1.3f) + fadeOut()
                },
                label = "next_badge"
            ) {
                Box(
                    modifier = Modifier
                        .clip(PillShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Sıradaki",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Text(
            text = time,
                style = if (isNext) {
                    MaterialTheme.typography.titleLarge.copy(
                        fontFeatureSettings = "tnum"
                    )
                } else {
                    MaterialTheme.typography.titleMedium.copy(
                        fontFeatureSettings = "tnum"
                    )
                },
            color = timeColor
        )
    }
}
