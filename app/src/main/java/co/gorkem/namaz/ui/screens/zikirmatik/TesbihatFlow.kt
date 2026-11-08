package co.gorkem.namaz.ui.screens.zikirmatik

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.gorkem.namaz.R
import co.gorkem.namaz.ui.theme.arabicText
import co.gorkem.namaz.ui.theme.serifTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NamazTesbihatiFlow(
    viewModel: ZikirmatikViewModel,
    context: Context
) {
    val selectedMode by viewModel.selectedTesbihatMode.collectAsState()
    val selectedPrayerTime by viewModel.selectedPrayerTime.collectAsState()
    val activeSteps by viewModel.activeTesbihatSteps.collectAsState()
    val currentStepIndex by viewModel.currentTesbihatStepIndex.collectAsState()
    val currentCount by viewModel.currentTesbihatCount.collectAsState()

    var showStepsSheet by remember { mutableStateOf(false) }

    val step = activeSteps.getOrNull(currentStepIndex) ?: return
    val totalSteps = activeSteps.size

    if (showStepsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showStepsSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedMode == TesbihatMode.KISA) "Kısa Tesbihat Adımları" else "${selectedPrayerTime.displayName} Namazı Tesbihatı Adımları",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showStepsSheet = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(activeSteps) { index, item ->
                        val isCurrent = index == currentStepIndex
                        Card(
                            onClick = {
                                viewModel.jumpToStep(index)
                                showStepsSheet = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.width(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                        if (item.type == StepType.COUNTER) {
                                            Text(
                                                text = "${item.targetCount}x tekrar",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Mevcut",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedMode == TesbihatMode.UZUN,
                onClick = { viewModel.setTesbihatMode(TesbihatMode.UZUN) },
                label = {
                    Text(
                        text = "Uzun Tesbihat",
                        fontWeight = if (selectedMode == TesbihatMode.UZUN) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            FilterChip(
                selected = selectedMode == TesbihatMode.KISA,
                onClick = { viewModel.setTesbihatMode(TesbihatMode.KISA) },
                label = {
                    Text(
                        text = "Kısa Tesbihat",
                        fontWeight = if (selectedMode == TesbihatMode.KISA) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        if (selectedMode == TesbihatMode.UZUN) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TesbihatPrayerTime.values().forEach { prayer ->
                    val isSelected = selectedPrayerTime == prayer
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setPrayerTime(prayer) },
                        label = {
                            Text(
                                text = prayer.displayName,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Adım ${currentStepIndex + 1} / $totalSteps",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (step.type == StepType.COUNTER) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Sayaç: ${step.targetCount}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                LinearProgressIndicator(
                    progress = { (currentStepIndex + 1).toFloat() / totalSteps.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }

            FilledTonalIconButton(
                onClick = { showStepsSheet = true },
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = "Tüm Adımlar",
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedContent(
            targetState = currentStepIndex,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                slideInHorizontally(
                    animationSpec = tween(300, easing = EaseInOut),
                    initialOffsetX = { fullWidth -> fullWidth / 2 * direction }
                ) + fadeIn(tween(300)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(300, easing = EaseInOut),
                    targetOffsetX = { fullWidth -> fullWidth / 2 * -direction }
                ) + fadeOut(tween(300))
            },
            label = "tesbihat_step_transition",
            modifier = Modifier.fillMaxWidth()
        ) { stepIdx ->
            val currentStep = activeSteps.getOrNull(stepIdx) ?: return@AnimatedContent
            var showMeaning by remember(stepIdx) { mutableStateOf(false) }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentStep.title,
                    style = MaterialTheme.typography.serifTitle,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 2.dp),
                    textAlign = TextAlign.Center
                )

                if (currentStep.type != StepType.COMPLETED) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 120.dp)
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            if (currentStep.textArabic.isNotEmpty()) {
                                val redColor = MaterialTheme.colorScheme.error
                                val baseColor = MaterialTheme.colorScheme.onSurface
                                val annotatedArabic = remember(currentStep.textArabic, redColor, baseColor) {
                                    annotateSacredWords(
                                        arabic = currentStep.textArabic,
                                        redColor = redColor,
                                        baseColor = baseColor
                                    )
                                }
                                SelectionContainer {
                                    Text(
                                        text = annotatedArabic,
                                        style = MaterialTheme.typography.arabicText.copy(
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )
                                }
                            }

                            val gestureTriggers = remember(currentStep.handGesture) {
                                currentStep.handGesture?.let { extractGestureTriggers(it) } ?: emptyList()
                            }
                            val triggerMatches = remember(currentStep.textTurkish, gestureTriggers) {
                                if (gestureTriggers.isNotEmpty()) {
                                    findTriggerMatches(currentStep.textTurkish, gestureTriggers)
                                } else emptyList()
                            }

                            if (currentStep.textTurkish.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        Text(
                                            text = "Okunuşu",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                        )
                                    }
                                    if (triggerMatches.isNotEmpty()) {
                                        val highlightColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.65f)
                                        val onHighlightColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        val annotatedText = remember(currentStep.textTurkish, triggerMatches, highlightColor, onHighlightColor) {
                                            buildAnnotatedReading(
                                                reading = currentStep.textTurkish,
                                                matches = triggerMatches,
                                                highlightColor = highlightColor,
                                                onHighlightColor = onHighlightColor
                                            )
                                        }
                                        Text(
                                            text = annotatedText,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        Text(
                                            text = currentStep.textTurkish,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontSize = 16.sp,
                                                lineHeight = 24.sp,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            if (currentStep.handGesture != null && triggerMatches.isEmpty()) {
                                Row(
                                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    val gestureEmoji = if (currentStep.handGesture.contains("aşağı") || currentStep.handGesture.contains("yere")) "🫳" else "🤲"
                                    Text(
                                        text = "$gestureEmoji ${currentStep.handGesture}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            if (currentStep.meaning.isNotEmpty()) {
                                TextButton(
                                    onClick = { showMeaning = !showMeaning },
                                    modifier = Modifier.padding(top = 2.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showMeaning) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = stringResource(if (showMeaning) R.string.zikirmatik_hide_meaning else R.string.zikirmatik_show_meaning),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                    )
                                }

                                if (showMeaning) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                                        ),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(
                                                text = "Anlamı",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(bottom = 4.dp)
                                            )
                                            Text(
                                                text = currentStep.meaning,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontSize = 14.sp,
                                                    lineHeight = 20.sp
                                                ),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(88.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Başarı",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = currentStep.textTurkish,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (currentStep.meaning.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))

                            TextButton(
                                onClick = { showMeaning = !showMeaning },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = if (showMeaning) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(if (showMeaning) R.string.zikirmatik_hide_meaning else R.string.zikirmatik_show_meaning),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }

                            if (showMeaning) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = currentStep.meaning,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }

                ZikirCounter(
                    currentStep = currentStep,
                    stepIdx = stepIdx,
                    totalSteps = totalSteps,
                    currentCount = currentCount,
                    onPrevious = { viewModel.previousTesbihatStep() },
                    onNext = { viewModel.nextTesbihatStep() },
                    onResetStep = { viewModel.resetCurrentTesbihatStep() },
                    onResetAll = { viewModel.resetTesbihat() },
                    onIncrement = {
                        viewModel.incrementTesbihat {
                            vibrate(context, isTargetReached = true)
                        }
                    },
                    context = context
                )
            }
        }
    }
}

data class GestureTrigger(
    val phrase: String,
    val isPrefix: Boolean,
    val emoji: String
)

data class GestureTriggerMatch(
    val trigger: GestureTrigger,
    val range: IntRange
)

fun extractGestureTriggers(gesture: String): List<GestureTrigger> {
    if (gesture.isBlank()) return emptyList()
    val matches = Regex("""["“]([^"”]+)["”]""").findAll(gesture).toList()
    if (matches.isEmpty()) return emptyList()

    return matches.mapIndexed { index, match ->
        val rawContent = match.groupValues[1]
        val isPrefix = rawContent.endsWith("...") || rawContent.endsWith("…")
        val phrase = rawContent.removeSuffix("...").removeSuffix("…").trim()

        val afterStart = match.range.last + 1
        val afterEnd = if (index < matches.size - 1) matches[index + 1].range.first else gesture.length
        val afterText = if (afterStart < afterEnd) gesture.substring(afterStart, afterEnd).lowercase(java.util.Locale.forLanguageTag("tr")) else ""

        val emoji = when {
            afterText.contains("yere") || afterText.contains("aşağı") -> "🫳"
            afterText.contains("yukarı") || afterText.contains("semaya") -> "🤲"
            else -> "🤲"
        }

        GestureTrigger(
            phrase = phrase,
            isPrefix = isPrefix,
            emoji = emoji
        )
    }
}

fun findTriggerMatches(reading: String, triggers: List<GestureTrigger>): List<GestureTriggerMatch> {
    if (reading.isBlank() || triggers.isEmpty()) return emptyList()

    val normalizedReading = normalizeForMatching(reading)
    if (normalizedReading.text.isEmpty()) return emptyList()

    val result = mutableListOf<GestureTriggerMatch>()
    var searchFromNormIdx = 0

    for (trigger in triggers) {
        val normPhrase = normalizePhrase(trigger.phrase)
        if (normPhrase.isEmpty()) continue

        val matchNormStart = normalizedReading.text.indexOf(normPhrase, searchFromNormIdx)
        if (matchNormStart == -1) continue

        val matchNormEnd = matchNormStart + normPhrase.length - 1
        val origStart = normalizedReading.origIndices[matchNormStart]
        val phraseEndOrig = normalizedReading.origIndices[matchNormEnd]

        val origEnd = if (trigger.isPrefix) {
            minOf(reading.length - 1, maxOf(phraseEndOrig, origStart + 23))
        } else {
            phraseEndOrig
        }

        result.add(GestureTriggerMatch(trigger, origStart..origEnd))
        searchFromNormIdx = matchNormEnd + 1
    }

    return result
}

fun findTriggerRanges(reading: String, triggers: List<GestureTrigger>): List<IntRange> {
    return findTriggerMatches(reading, triggers).map { it.range }
}

fun buildAnnotatedReading(
    reading: String,
    matches: List<GestureTriggerMatch>,
    highlightColor: Color,
    onHighlightColor: Color
): AnnotatedString {
    if (matches.isEmpty()) return AnnotatedString(reading)

    val sortedMatches = matches.sortedBy { it.range.first }
    val builder = AnnotatedString.Builder()
    var cursor = 0

    for (match in sortedMatches) {
        if (match.range.first > cursor) {
            builder.append(reading.substring(cursor, match.range.first))
        }

        val spanStart = maxOf(cursor, match.range.first)
        val spanEnd = minOf(reading.length, match.range.last + 1)
        if (spanStart < spanEnd) {
            builder.withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = highlightColor,
                    color = onHighlightColor
                )
            ) {
                builder.append(reading.substring(spanStart, spanEnd))
            }
            builder.append(" ${match.trigger.emoji}")
            cursor = spanEnd
        }
    }

    if (cursor < reading.length) {
        builder.append(reading.substring(cursor))
    }

    return builder.toAnnotatedString()
}

private data class NormalizedString(
    val text: String,
    val origIndices: IntArray
)

private fun isStrippedChar(c: Char): Boolean = when (c) {
    '-', '‐', '‑', '‒', '–', '—', '\u00AD' -> true
    '\'', '’', '‘', 'ʻ', 'ʼ', 'ʿ', 'ʾ', '`' -> true
    '"', '“', '”', '«', '»' -> true
    '^', '~', '´' -> true
    else -> false
}

private fun foldChar(c: Char): Char = when (c) {
    'â', 'Â' -> 'a'
    'î', 'Î' -> 'i'
    'û', 'Û' -> 'u'
    'ḥ', 'Ḥ', 'ḫ', 'Ḫ' -> 'h'
    'ḳ', 'Ḳ' -> 'k'
    'š', 'Š' -> 's'
    'ḍ', 'Ḍ' -> 'd'
    'ẓ', 'Ẓ', 'ẕ', 'Ẕ' -> 'z'
    'ṭ', 'Ṭ' -> 't'
    'İ', 'I' -> 'i'
    'ı' -> 'i'
    else -> c.lowercaseChar()
}

private fun normalizeForMatching(input: String): NormalizedString {
    val sb = StringBuilder(input.length)
    val indices = ArrayList<Int>(input.length)
    var lastWasSpace = false

    for (i in input.indices) {
        val c = input[i]
        if (c.isWhitespace()) {
            if (!lastWasSpace && sb.isNotEmpty()) {
                sb.append(' ')
                indices.add(i)
                lastWasSpace = true
            }
        } else if (isStrippedChar(c)) {
            continue
        } else {
            sb.append(foldChar(c))
            indices.add(i)
            lastWasSpace = false
        }
    }

    return NormalizedString(sb.toString(), indices.toIntArray())
}

private fun normalizePhrase(phrase: String): String {
    val sb = StringBuilder(phrase.length)
    var lastWasSpace = false

    for (c in phrase) {
        if (c.isWhitespace()) {
            if (!lastWasSpace && sb.isNotEmpty()) {
                sb.append(' ')
                lastWasSpace = true
            }
        } else if (isStrippedChar(c)) {
            continue
        } else {
            sb.append(foldChar(c))
            lastWasSpace = false
        }
    }

    return sb.toString().trim()
}

private const val ARABIC_COMBINING_MARKS = "[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]"
private const val ARABIC_LETTERS = "[\\u0621-\\u064A\\u0671-\\u06D3]"
private val ALLAH_SACRED_WORD_REGEX = Regex(
    "(?:[\\u0627\\u0671\\u0625\\u0623\\u0622]$ARABIC_COMBINING_MARKS*\\u0644$ARABIC_COMBINING_MARKS*\\u0644$ARABIC_COMBINING_MARKS*\\u0647$ARABIC_COMBINING_MARKS*|\\uFDF2$ARABIC_COMBINING_MARKS*)(?!$ARABIC_COMBINING_MARKS*$ARABIC_LETTERS)"
)

fun annotateSacredWords(
    arabic: String,
    redColor: Color,
    baseColor: Color
): AnnotatedString {
    if (arabic.isEmpty()) return AnnotatedString("")

    val matches = ALLAH_SACRED_WORD_REGEX.findAll(arabic).toList()
    if (matches.isEmpty()) {
        val builder = AnnotatedString.Builder(arabic)
        builder.addStyle(SpanStyle(color = baseColor), 0, arabic.length)
        return builder.toAnnotatedString()
    }

    val builder = AnnotatedString.Builder()
    var cursor = 0

    for (match in matches) {
        val start = match.range.first
        val end = match.range.last + 1

        if (start > cursor) {
            builder.pushStyle(SpanStyle(color = baseColor))
            builder.append(arabic.substring(cursor, start))
            builder.pop()
        }

        builder.pushStyle(SpanStyle(color = redColor))
        builder.append(match.value)
        builder.pop()

        cursor = end
    }

    if (cursor < arabic.length) {
        builder.pushStyle(SpanStyle(color = baseColor))
        builder.append(arabic.substring(cursor))
        builder.pop()
    }

    return builder.toAnnotatedString()
}
