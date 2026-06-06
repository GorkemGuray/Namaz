package co.gorkem.namaz.wear.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material3.*
import co.gorkem.namaz.wear.ui.WearZikirmatikViewModel

@Composable
fun WearZikirmatikScreen(
    onNavigateBack: () -> Unit,
    viewModel: WearZikirmatikViewModel = hiltViewModel()
) {
    val mode by viewModel.currentMode.collectAsState()
    val interactiveStepIndex by viewModel.interactiveStepIndex.collectAsState()
    val interactiveCount by viewModel.interactiveCount.collectAsState()
    val freeCount by viewModel.freeCount.collectAsState()
    val freeTarget by viewModel.freeTarget.collectAsState()

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (mode) {
            0 -> {
                // Landing Screen / Mode Selector
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Zikirmatik",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.setMode(1) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Tesbihat",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Namaz Tesbihatı",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.setMode(2) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sayaç",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Serbest Sayaç",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            1 -> {
                // Interactive Namaz Tesbihatı
                val step = WearNamazTesbihatiSteps.steps.getOrNull(interactiveStepIndex)
                if (step != null) {
                    WearInteractiveFlow(
                        step = step,
                        currentStepIndex = interactiveStepIndex,
                        totalSteps = WearNamazTesbihatiSteps.steps.size,
                        currentCount = interactiveCount,
                        viewModel = viewModel,
                        context = context
                    )
                }
            }
            2 -> {
                // Free Counter
                WearFreeCounter(
                    count = freeCount,
                    target = freeTarget,
                    viewModel = viewModel,
                    context = context
                )
            }
        }
    }
}

@Composable
private fun WearInteractiveFlow(
    step: WearNamazTesbihatiStep,
    currentStepIndex: Int,
    totalSteps: Int,
    currentCount: Int,
    viewModel: WearZikirmatikViewModel,
    context: Context
) {
    if (step.type != WearStepType.COMPLETED) {
        if (step.type == WearStepType.PRAYER) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    onClick = { viewModel.nextInteractiveStep() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                    ) {
                        if (step.textArabic.isNotEmpty()) {
                            Text(
                                text = step.textArabic,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 16.sp,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Center
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Text(
                            text = step.textTurkish,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = step.meaning,
                            style = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.nextInteractiveStep() },
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Text("Devam Et", style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            val target = step.targetCount
            val progress = (currentCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
            val animatedProgress by animateFloatAsState(
                targetValue = progress,
                animationSpec = tween(300),
                label = "progress"
            )

            var scale by remember { mutableStateOf(1f) }
            LaunchedEffect(currentCount) {
                if (currentCount > 0) {
                    scale = 1.2f
                    kotlinx.coroutines.delay(70)
                    scale = 1f
                }
            }
            val animatedScale by animateFloatAsState(
                targetValue = scale,
                animationSpec = tween(70),
                label = "scale"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { viewModel.previousInteractiveStep() },
                        enabled = currentStepIndex > 0,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            modifier = Modifier.size(16.dp),
                            tint = if (currentStepIndex > 0) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray.copy(alpha = 0.3f)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                vibrate(context, isTargetReached = false)
                                viewModel.incrementInteractive {
                                    vibrate(context, isTargetReached = true)
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(5.dp)) {
                            drawCircle(
                                color = Color.Gray.copy(alpha = 0.2f),
                                style = Stroke(width = 4.dp.toPx())
                            )
                        }

                        val primaryColor = MaterialTheme.colorScheme.primary
                        Canvas(modifier = Modifier.fillMaxSize().padding(5.dp)) {
                            drawArc(
                                color = primaryColor,
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
                        ) {
                            Text(
                                text = currentCount.toString(),
                                style = MaterialTheme.typography.displayMedium.copy(fontSize = 26.sp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "/ $target",
                                style = MaterialTheme.typography.titleSmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.nextInteractiveStep() },
                        enabled = currentStepIndex < totalSteps - 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Atla",
                            modifier = Modifier.size(16.dp),
                            tint = if (currentStepIndex < totalSteps - 1) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray.copy(alpha = 0.3f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                FilledTonalButton(
                    onClick = { viewModel.setMode(0) },
                    modifier = Modifier.height(28.dp).wrapContentWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Menü (${currentStepIndex + 1}/$totalSteps)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Başarı",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = step.textTurkish,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = step.meaning,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.resetInteractive()
                    viewModel.setMode(0)
                },
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text("Bitir", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun WearFreeCounter(
    count: Int,
    target: Int,
    viewModel: WearZikirmatikViewModel,
    context: Context
) {
    val progress = if (target > 0) count.toFloat() / target.toFloat() else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300),
        label = "progress"
    )

    var scale by remember { mutableStateOf(1f) }
    LaunchedEffect(count) {
        if (count > 0) {
            scale = 1.2f
            kotlinx.coroutines.delay(70)
            scale = 1f
        }
    }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(70),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Serbest Sayaç",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.setMode(0) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Menü",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        val newCount = count + 1
                        vibrate(context, isTargetReached = (target > 0 && newCount == target))
                        viewModel.incrementFree()
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(5.dp)) {
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.2f),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }

                if (target > 0) {
                    val primaryColor = MaterialTheme.colorScheme.primary
                    Canvas(modifier = Modifier.fillMaxSize().padding(5.dp)) {
                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 26.sp),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    if (target > 0) {
                        Text(
                            text = "/ $target",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = { viewModel.resetFree() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Sıfırla",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        FilledTonalButton(
            onClick = {
                val nextTarget = when (target) {
                    0 -> 33
                    33 -> 99
                    99 -> 100
                    100 -> 1000
                    else -> 0
                }
                viewModel.setFreeTarget(nextTarget)
            },
            modifier = Modifier.height(28.dp).wrapContentWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (target == 0) "Sınırsız" else "Hedef: $target",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
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

