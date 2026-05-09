package co.gorkem.namaz.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.ui.theme.HeroCardShape
import co.gorkem.namaz.ui.theme.countdownAccentColor
import co.gorkem.namaz.ui.theme.prayerHeroGradient
import kotlinx.coroutines.delay

@Composable
fun PrayerHeroCard(
    nextPrayer: GetNextPrayerUseCase.NextPrayerInfo,
    locationName: String,
    onLocationClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "hero_scale"
    )

    val gradient = prayerHeroGradient(nextPrayer.prayerName)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(HeroCardShape)
            .background(gradient)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White.copy(alpha = 0.3f)),
                onClick = { /* Hero tap action if needed */ }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Location chip — semi-transparent dark background so it
            // reads well over any gradient tone
            if (locationName.isNotBlank()) {
                HeroLocationChip(
                    locationName = locationName,
                    onClick = onLocationClick
                )
            }

            // Label
            Text(
                text = "Sıradaki Vakit",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.75f)
            )

            // Prayer name with animated transition
            AnimatedContent(
                targetState = nextPrayer.prayerName.turkishName,
                transitionSpec = {
                    scaleIn(
                        initialScale = 0.85f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    ) + fadeIn() togetherWith
                    scaleOut(
                        targetScale = 1.15f,
                        animationSpec = spring()
                    ) + fadeOut()
                },
                label = "prayer_name_anim"
            ) { name ->
                Text(
                    text = name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            // Prayer time
            Text(
                text = nextPrayer.prayerTime,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Countdown — pure white for guaranteed readability on any gradient
            CountdownDisplay(
                remainingMinutes = nextPrayer.remainingMinutes,
                textColor = Color.White
            )
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
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_my_location),
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = Color.White.copy(alpha = 0.85f)
        )
        Text(
            text = locationName,
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun CountdownDisplay(
    remainingMinutes: Long,
    textColor: Color
) {
    var remainingSeconds by remember {
        mutableLongStateOf(remainingMinutes * 60L)
    }

    LaunchedEffect(remainingMinutes) {
        remainingSeconds = remainingMinutes * 60L
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
    }

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    val displayText = when {
        hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
        else -> String.format("%02ds", seconds)
    }

    Crossfade(
        targetState = displayText,
        animationSpec = tween(durationMillis = 150, easing = EaseInOut),
        label = "countdown_anim"
    ) { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.displayMedium.copy(
                fontFeatureSettings = "tnum"
            ),
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 140.dp)
        )
    }

    // Sub-label
    val subLabel = when {
        hours > 0 -> "saat kaldı"
        minutes > 0 -> "dakika kaldı"
        else -> "saniye kaldı"
    }
    Text(
        text = subLabel,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
    )
}
