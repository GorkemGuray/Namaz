package co.gorkem.namaz.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.usecase.PrayerDashboardData
import co.gorkem.namaz.ui.theme.HeroCardShape
import co.gorkem.namaz.ui.theme.prayerHeroGradient
import java.time.format.DateTimeFormatter

@Composable
fun PrayerHeroCard(
    dashboard: PrayerDashboardData,
    locationName: String,
    hijriDate: String = "",
    onLocationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "hero_scale"
    )

    val gradient = prayerHeroGradient(dashboard.currentPeriod.prayerName)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(HeroCardShape)
            .background(gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                onClick = { }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (locationName.isNotBlank()) {
                    HeroLocationChip(
                        locationName = locationName,
                        onClick = onLocationClick
                    )
                }
                if (hijriDate.isNotBlank()) {
                    Text(
                        text = hijriDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = dashboard.currentPeriod.label,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "vaktindesiniz",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dashboard.currentPeriod.startTime.format(TIME_FORMAT),
                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
                        color = Color.White.copy(alpha = 0.65f)
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(Color.White.copy(alpha = 0.4f))
                    )
                    Text(
                        text = dashboard.currentPeriod.endTime.format(TIME_FORMAT),
                        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
                        color = Color.White.copy(alpha = 0.65f)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.15f))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Sıradaki Vakit",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    AnimatedContent(
                        targetState = dashboard.nextPrayer.prayerName.turkishName,
                        transitionSpec = {
                            scaleIn(
                                initialScale = 0.85f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                            ) + fadeIn() togetherWith
                            scaleOut(targetScale = 1.15f) + fadeOut()
                        },
                        label = "next_prayer_name"
                    ) { name ->
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    }
                }

                CountdownDisplay(
                    remainingMinutes = dashboard.nextPrayer.remainingMinutes,
                    textColor = Color.White,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun HeroLocationChip(
    locationName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = modifier
            .scale(scale)
            .clip(MaterialTheme.shapes.small)
            .background(Color.Black.copy(alpha = 0.25f))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_my_location),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.White.copy(alpha = 0.9f)
        )
        Text(
            text = locationName,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.95f)
        )
    }
}

@Composable
fun CountdownDisplay(
    remainingMinutes: Long,
    textColor: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center
) {
    val remainingSeconds = remember { androidx.compose.runtime.mutableStateOf(remainingMinutes * 60L) }

    androidx.compose.runtime.LaunchedEffect(remainingMinutes) {
        remainingSeconds.value = remainingMinutes * 60L
        while (remainingSeconds.value > 0) {
            kotlinx.coroutines.delay(1000L)
            remainingSeconds.value--
        }
    }

    val hours = remainingSeconds.value / 3600
    val minutes = (remainingSeconds.value % 3600) / 60
    val seconds = remainingSeconds.value % 60

    val displayText = when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
        else -> String.format("%02ds", seconds)
    }

    Text(
        text = displayText,
        style = MaterialTheme.typography.displaySmall.copy(
            fontFeatureSettings = "tnum"
        ),
        color = textColor,
        textAlign = textAlign,
        modifier = modifier
    )

    val subLabel = when {
        hours > 0 -> "saat kaldı"
        minutes > 0 -> "dakika kaldı"
        else -> "saniye kaldı"
    }
    Text(
        text = subLabel,
        style = MaterialTheme.typography.labelSmall,
        color = textColor.copy(alpha = 0.6f),
        textAlign = textAlign,
        modifier = Modifier.padding(top = 2.dp)
    )
}

private val TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm")
