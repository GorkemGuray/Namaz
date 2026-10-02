package co.gorkem.namaz.ui.screens.zikirmatik

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.gorkem.namaz.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZikirmatikScreen(
    modifier: Modifier = Modifier,
    viewModel: ZikirmatikViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_tesbihat_vakti), style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NamazTesbihatiFlow(
                viewModel = viewModel,
                context = context
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NamazTesbihatiFlow(
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
                    Text(
                        text = "$totalSteps Adım",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(activeSteps) { index, item ->
                        val isCurrent = index == currentStepIndex
                        val isPassed = index < currentStepIndex
                        Card(
                            onClick = {
                                viewModel.jumpToStep(index)
                                showStepsSheet = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            if (isCurrent) MaterialTheme.colorScheme.primary
                                            else if (isPassed) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isPassed) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isCurrent) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        if (item.handGesture != null) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = if (item.handGesture.contains("aşağı") || item.handGesture.contains("yere")) "🫳" else "🤲",
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                    Text(
                                        text = when (item.type) {
                                            StepType.COUNTER -> "Sayaç: ${item.targetCount}x"
                                            StepType.PRAYER -> "Dua / Okuma"
                                            StepType.COMPLETED -> "Bitiş"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (item.type == StepType.COUNTER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
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
                                SelectionContainer {
                                    Text(
                                        text = currentStep.textArabic,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontSize = 24.sp,
                                            lineHeight = 42.sp,
                                            textAlign = TextAlign.Center
                                        ),
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface,
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

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.65f)
                ) {
                    when (currentStep.type) {
                        StepType.COUNTER -> {
                            val target = currentStep.targetCount
                            val progress = if (target > 0) {
                                (currentCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                            } else 0f
                            val animatedProgress by animateFloatAsState(
                                targetValue = progress,
                                animationSpec = tween(250),
                                label = "progress"
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                FilledTonalIconButton(
                                    onClick = { viewModel.previousTesbihatStep() },
                                    enabled = stepIdx > 0,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Geri",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.resetCurrentTesbihatStep() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Sıfırla",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            vibrate(context, isTargetReached = false)
                                            viewModel.incrementTesbihat {
                                                vibrate(context, isTargetReached = true)
                                            }
                                        }
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawCircle(
                                            color = Color.Gray.copy(alpha = 0.2f),
                                            style = Stroke(width = 5.dp.toPx())
                                        )
                                    }

                                    val primaryColor = MaterialTheme.colorScheme.primary
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawArc(
                                            color = primaryColor,
                                            startAngle = -90f,
                                            sweepAngle = 360f * animatedProgress,
                                            useCenter = false,
                                            style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(4.dp)
                                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = currentCount.toString(),
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                            Text(
                                                text = "/ $target",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }

                                FilledTonalIconButton(
                                    onClick = { viewModel.nextTesbihatStep() },
                                    enabled = stepIdx < totalSteps - 1,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "İleri",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        StepType.PRAYER -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                FilledTonalIconButton(
                                    onClick = { viewModel.previousTesbihatStep() },
                                    enabled = stepIdx > 0,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Geri",
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Button(
                                    onClick = { viewModel.nextTesbihatStep() },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .padding(horizontal = 8.dp),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "Devam Et",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.resetTesbihat() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Başa Dön",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                        StepType.COMPLETED -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { viewModel.resetTesbihat() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "Başa Dön",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun vibrate(context: Context, isTargetReached: Boolean) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val duration = if (isTargetReached) 300L else 30L
        val amplitude = if (isTargetReached) 255 else 50
        vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(if (isTargetReached) 300L else 30L)
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
        val afterText = if (afterStart < afterEnd) gesture.substring(afterStart, afterEnd).lowercase() else ""

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

