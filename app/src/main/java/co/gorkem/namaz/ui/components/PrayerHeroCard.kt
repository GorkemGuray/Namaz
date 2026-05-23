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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.usecase.PrayerDashboardData
import co.gorkem.namaz.domain.usecase.PeriodType
import co.gorkem.namaz.ui.theme.HeroCardShape
import co.gorkem.namaz.ui.theme.prayerHeroGradient
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
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
                        onClick = onLocationClick,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
                if (hijriDate.isNotBlank()) {
                    Text(
                        text = hijriDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.65f),
                        maxLines = 1,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val periodLabel = when (dashboard.currentPeriod.periodType) {
                    PeriodType.SABAH -> stringResource(R.string.period_sabah)
                    PeriodType.KUSLUK -> stringResource(R.string.period_kusluk)
                    else -> stringResource(dashboard.currentPeriod.prayerName.nameRes)
                } + " " + stringResource(R.string.period_vakti)

                Text(
                    text = periodLabel,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(R.string.home_in_prayer_time),
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
                        text = stringResource(R.string.home_next_prayer),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
            AnimatedContent(
                targetState = dashboard.nextPrayer.prayerName,
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
                    text = stringResource(name.nameRes),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
                }

                CountdownDisplay(
                    prayerTime = dashboard.nextPrayer.prayerTime,
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
            color = Color.White.copy(alpha = 0.95f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CountdownDisplay(
    prayerTime: String,
    textColor: Color,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center
) {
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    var remainingSeconds by remember { mutableLongStateOf(0L) }

    LaunchedEffect(prayerTime) {
        val now = LocalTime.now()
        val parsedTime = LocalTime.parse(prayerTime, timeFormatter)
        val today = LocalDate.now()

        val targetDateTime = if (parsedTime.isAfter(now)) {
            LocalDateTime.of(today, parsedTime)
        } else {
            LocalDateTime.of(today.plusDays(1), parsedTime)
        }

        val targetMillis = targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        while (true) {
            val remaining = ((targetMillis - System.currentTimeMillis()) / 1000).coerceAtLeast(0)
            remainingSeconds = remaining
            delay(1000L)
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
        hours > 0 -> stringResource(R.string.home_hours_left)
        minutes > 0 -> stringResource(R.string.home_minutes_left)
        else -> stringResource(R.string.home_seconds_left)
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
