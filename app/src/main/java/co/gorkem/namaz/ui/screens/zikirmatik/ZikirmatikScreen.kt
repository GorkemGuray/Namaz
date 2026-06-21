package co.gorkem.namaz.ui.screens.zikirmatik

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.EaseInOut
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.gorkem.namaz.data.local.room.ZikirEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZikirmatikScreen(
    modifier: Modifier = Modifier,
    viewModel: ZikirmatikViewModel = hiltViewModel()
) {
    val currentZikir by viewModel.currentZikir.collectAsState()
    val zikirs by viewModel.zikirs.collectAsState()
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    // Dialog state for adding custom zikir
    var showAddDialog by remember { mutableStateOf(false) }
    var zikirTitle by remember { mutableStateOf("") }
    var zikirTarget by remember { mutableStateOf("") }
    var isUnlimited by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                zikirTitle = ""
                zikirTarget = ""
                isUnlimited = false
            },
            title = { Text("Yeni Zikir Ekle") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = zikirTitle,
                        onValueChange = { zikirTitle = it },
                        label = { Text("Zikir Adı") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("Sınırsız (Hedef Yok)", style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = isUnlimited,
                            onCheckedChange = { isUnlimited = it }
                        )
                    }

                    if (!isUnlimited) {
                        OutlinedTextField(
                            value = zikirTarget,
                            onValueChange = { zikirTarget = it },
                            label = { Text("Hedef Sayı") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (zikirTitle.isNotBlank()) {
                            val targetVal = if (isUnlimited) 0 else zikirTarget.toIntOrNull() ?: 33
                            viewModel.addCustomZikir(zikirTitle, targetVal)
                            showAddDialog = false
                            zikirTitle = ""
                            zikirTarget = ""
                            isUnlimited = false
                        }
                    },
                    enabled = zikirTitle.isNotBlank() && (isUnlimited || zikirTarget.toIntOrNull() != null)
                ) {
                    Text("Ekle")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        zikirTitle = ""
                        zikirTarget = ""
                        isUnlimited = false
                    }
                ) {
                    Text("İptal")
                }
            }
        )
    }

    if (expanded) {
        ModalBottomSheet(
            onDismissRequest = { expanded = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            ZikirSelectionBottomSheetContent(
                zikirs = zikirs,
                currentZikir = currentZikir,
                onZikirSelect = { zikir ->
                    viewModel.selectZikir(zikir)
                    expanded = false
                },
                onDeleteClick = { zikir ->
                    viewModel.deleteCustomZikir(zikir)
                },
                onAddClick = {
                    showAddDialog = true
                    expanded = false
                }
            )
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Zikirmatik & Tesbihat", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mode Toggle TabRow
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text("Zikirmatik", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("Namaz Tesbihatı", fontWeight = FontWeight.Bold) }
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                if (page == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Dropdown Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 32.dp)
                        ) {
                            ElevatedCard(
                                onClick = { expanded = true },
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = CircleShape
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = currentZikir?.title ?: "Zikir Seçiniz",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Seç",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        currentZikir?.let { zikir ->
                            val target = if (zikir.isNamazTesbihati) 33 else zikir.targetCount
                            val progress = if (target > 0) {
                                (zikir.currentCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                            } else {
                                0f
                            }

                            val animatedProgress by animateFloatAsState(
                                targetValue = progress,
                                animationSpec = tween(300),
                                label = "progress"
                            )

                            // Current phase label (for Namaz Tesbihati)
                            if (zikir.isNamazTesbihati) {
                                Text(
                                    text = viewModel.namazTesbihatPhases.getOrElse(zikir.currentPhase) { "" },
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                )
                            }

                            // Big Tap Area
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(280.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        viewModel.increment()
                                        val newCount = zikir.currentCount + 1
                                        vibrate(context, isTargetReached = (target > 0 && newCount == target))
                                    }
                            ) {
                                // Background circle
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color.Gray.copy(alpha = 0.2f),
                                        style = Stroke(width = 16.dp.toPx())
                                    )
                                }

                                // Progress circle (Only if not unlimited)
                                if (target > 0) {
                                    val primaryColor = MaterialTheme.colorScheme.primary
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        drawArc(
                                            color = primaryColor,
                                            startAngle = -90f,
                                            sweepAngle = 360f * animatedProgress,
                                            useCenter = false,
                                            style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                                        )
                                    }
                                }

                                // Inner circle background
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = zikir.currentCount.toString(),
                                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp),
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        if (target > 0) {
                                            Text(
                                                text = "/ $target",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Bottom Actions
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                        ) {
                            FilledTonalIconButton(
                                onClick = { viewModel.reset() },
                                modifier = Modifier.size(64.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Sıfırla",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Interactive Namaz Tesbihatı Flow
                    NamazTesbihatiFlow(viewModel = viewModel, context = context)
                }
            }
        }
    }
}

@Composable
private fun NamazTesbihatiFlow(
    viewModel: ZikirmatikViewModel,
    context: Context
) {
    val currentStepIndex by viewModel.currentTesbihatStepIndex.collectAsState()
    val currentCount by viewModel.currentTesbihatCount.collectAsState()
    val step = NamazTesbihatiSteps.steps.getOrNull(currentStepIndex) ?: return
    val totalSteps = NamazTesbihatiSteps.steps.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Step Indicators (Instagram Stories style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 0 until totalSteps) {
                val color = if (i <= currentStepIndex) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .background(color, CircleShape)
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
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { stepIdx ->
            val currentStep = NamazTesbihatiSteps.steps.getOrNull(stepIdx) ?: return@AnimatedContent
            
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Title
                Text(
                    text = currentStep.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (currentStep.type != StepType.COMPLETED) {
                    // Content Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (currentStep.textArabic.isNotEmpty()) {
                                Text(
                                    text = currentStep.textArabic,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontSize = 28.sp,
                                        lineHeight = 44.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            if (currentStep.textTurkish.isNotEmpty()) {
                                Text(
                                    text = currentStep.textTurkish,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = 18.sp,
                                        lineHeight = 26.sp,
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }
                            if (currentStep.meaning.isNotEmpty()) {
                                Text(
                                    text = currentStep.meaning,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Completion Layout
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(120.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                .padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Başarı",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = currentStep.textTurkish, // "Kabûl Olsun"
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentStep.meaning,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Interactive Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (currentStep.type) {
                        StepType.PRAYER -> {
                            Button(
                                onClick = { viewModel.nextTesbihatStep() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "Devam Et",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        StepType.COUNTER -> {
                            val target = currentStep.targetCount
                            val progress = (currentCount.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                            val animatedProgress by animateFloatAsState(
                                targetValue = progress,
                                animationSpec = tween(300),
                                label = "progress"
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(220.dp)
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
                                        style = Stroke(width = 12.dp.toPx())
                                    )
                                }

                                val primaryColor = MaterialTheme.colorScheme.primary
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawArc(
                                        color = primaryColor,
                                        startAngle = -90f,
                                        sweepAngle = 360f * animatedProgress,
                                        useCenter = false,
                                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = currentCount.toString(),
                                            style = MaterialTheme.typography.displayMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "/ $target",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                        StepType.COMPLETED -> {
                            Button(
                                onClick = { viewModel.resetTesbihat() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
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

                // Bottom Navigation Bar for Card (only when not completed)
                if (currentStep.type != StepType.COMPLETED) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        TextButton(
                            onClick = { viewModel.previousTesbihatStep() },
                            enabled = stepIdx > 0
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Geri"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Geri")
                        }

                        IconButton(
                            onClick = {
                                if (currentStep.type == StepType.COUNTER) {
                                    viewModel.resetCurrentTesbihatStep()
                                } else {
                                    viewModel.resetTesbihat()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sıfırla"
                            )
                        }

                        TextButton(
                            onClick = { viewModel.nextTesbihatStep() },
                            enabled = stepIdx < totalSteps - 1
                        ) {
                            Text("Atla")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "İleri"
                            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZikirSelectionBottomSheetContent(
    zikirs: List<ZikirEntity>,
    currentZikir: ZikirEntity?,
    onZikirSelect: (ZikirEntity) -> Unit,
    onDeleteClick: (ZikirEntity) -> Unit,
    onAddClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Zikir Seçin",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(zikirs, key = { it.id }) { zikir ->
                val isSelected = currentZikir?.id == zikir.id
                Card(
                    onClick = { onZikirSelect(zikir) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
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
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = zikir.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Sayaç: ${zikir.currentCount}" + if (zikir.targetCount > 0) " / ${zikir.targetCount}" else " (Sınırsız)",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!zikir.isNamazTesbihati) {
                            IconButton(
                                onClick = { onDeleteClick(zikir) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Sil",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Yeni Özel Zikir Ekle")
        }
    }
}
