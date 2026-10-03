package co.gorkem.namaz.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.gorkem.namaz.LocaleManager
import co.gorkem.namaz.MainActivity
import co.gorkem.namaz.R
import co.gorkem.namaz.ui.components.UpdateCheckButton
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import co.gorkem.namaz.util.NotificationPermissionHelper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import co.gorkem.namaz.ui.theme.AppThemeSelection
import co.gorkem.namaz.ui.theme.PlayfairDisplayFontFamily
import co.gorkem.namaz.ui.theme.isDarkFixed
import co.gorkem.namaz.ui.theme.toColorScheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }

    val isSystemPermissionGranted = remember(preferences.notificationsEnabled) {
        NotificationPermissionHelper.checkNotificationPermission(context)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setNotificationPermissionRequested(true)
        if (isGranted) {
            viewModel.setNotificationsEnabled(true)
        } else {
            viewModel.setNotificationsEnabled(false)
            val shouldShowRationale = NotificationPermissionHelper.shouldShowRationale(context)
            if (shouldShowRationale) {
                showRationaleDialog = true
            } else {
                showSettingsDialog = true
            }
        }
    }

    val handleNotificationToggle: (Boolean) -> Unit = { enabled ->
        if (enabled) {
            val systemGranted = NotificationPermissionHelper.checkNotificationPermission(context)
            if (systemGranted) {
                viewModel.setNotificationsEnabled(true)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!preferences.notificationPermissionRequested) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        val shouldShowRationale = NotificationPermissionHelper.shouldShowRationale(context)
                        if (shouldShowRationale) {
                            showRationaleDialog = true
                        } else {
                            showSettingsDialog = true
                        }
                    }
                } else {
                    viewModel.setNotificationsEnabled(true)
                }
            }
        } else {
            viewModel.setNotificationsEnabled(false)
        }
    }

    LaunchedEffect(preferences.notificationsEnabled, isSystemPermissionGranted) {
        if (preferences.notificationsEnabled && !isSystemPermissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (!preferences.notificationPermissionRequested) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    val shouldShowRationale = NotificationPermissionHelper.shouldShowRationale(context)
                    if (shouldShowRationale) {
                        showRationaleDialog = true
                    } else {
                        showSettingsDialog = true
                    }
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val systemGranted = NotificationPermissionHelper.checkNotificationPermission(context)
                if (systemGranted) {
                    if (!preferences.notificationsEnabled) {
                        viewModel.setNotificationsEnabled(true)
                    }
                    showRationaleDialog = false
                    showSettingsDialog = false
                } else {
                    if (showSettingsDialog || showRationaleDialog) {
                        showRationaleDialog = false
                        showSettingsDialog = false
                        viewModel.setNotificationsEnabled(false)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Bildirimler Section
            SettingsSection(title = stringResource(R.string.settings_notifications)) {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.settings_enable_notifications),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.settings_notifications_desc),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingContent = {
                        Switch(
                            checked = preferences.notificationsEnabled,
                            onCheckedChange = handleNotificationToggle
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    modifier = Modifier.clip(MaterialTheme.shapes.large)
                )

                AnimatedVisibility(
                    visible = preferences.notificationsEnabled,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.settings_notification_time),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                        )

                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.selectableGroup()) {
                                viewModel.notificationMinutesOptions.forEachIndexed { index, minutes ->
                                    val selected = preferences.notificationMinutesBefore == minutes

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .selectable(
                                                selected = selected,
                                                onClick = {
                                                    viewModel.setNotificationMinutesBefore(minutes)
                                                },
                                                role = Role.RadioButton
                                            )
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selected,
                                            onClick = null
                                        )
                                        Spacer(modifier = Modifier.padding(start = 16.dp))
                                        val optionText = if (minutes == 0) {
                                            stringResource(R.string.settings_notification_on_time)
                                        } else {
                                            minutes.toString() + stringResource(R.string.settings_minutes_before)
                                        }
                                        Text(
                                            text = optionText,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    if (index < viewModel.notificationMinutesOptions.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(start = 52.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Dil Section
            SettingsSection(title = stringResource(R.string.settings_language)) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.selectableGroup()) {
                        val languages = listOf(
                            "tr" to R.string.settings_language_turkish,
                            "en" to R.string.settings_language_english
                        )
                        val context = LocalContext.current
                        languages.forEachIndexed { index, (code, nameRes) ->
                            val selected = preferences.language == code
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selected,
                                        onClick = {
                                            if (preferences.language != code) {
                                                LocaleManager.setLanguage(context, code)
                                                viewModel.setLanguage(code)
                                                (context as? MainActivity)?.recreate()
                                            }
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.padding(start = 16.dp))
                                Text(
                                    text = stringResource(nameRes),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (index < languages.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 52.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // Veri Kaynağı Section
            SettingsSection(title = stringResource(R.string.settings_data_source)) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.selectableGroup()) {
                        val sources = listOf(
                            0 to (R.string.settings_source_aladhan to R.string.settings_source_aladhan_desc),
                            1 to (R.string.settings_source_imsakiyem to R.string.settings_source_imsakiyem_desc),
                            2 to (R.string.settings_source_local to R.string.settings_source_local_desc)
                        )
                        sources.forEachIndexed { index, (sourceId, strings) ->
                            val (titleRes, descRes) = strings
                            val selected = preferences.dataSource == sourceId

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selected,
                                        onClick = {
                                            viewModel.setDataSource(sourceId)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.padding(start = 16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(titleRes),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = stringResource(descRes),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (index < sources.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 52.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // Hesaplama Yöntemi Section
            SettingsSection(title = stringResource(R.string.settings_calculation_method)) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.selectableGroup()) {
                        viewModel.calculationMethods.forEachIndexed { index, method ->
                            val selected = preferences.calculationMethod == method
                            val nameRes = when (method) {
                                13 -> R.string.method_diyanet
                                3 -> R.string.method_mwl
                                4 -> R.string.method_umm_al_qura
                                2 -> R.string.method_isna
                                else -> R.string.method_diyanet
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selected,
                                        onClick = {
                                            viewModel.setCalculationMethod(method)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.padding(start = 16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(nameRes),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (method == 13) {
                                        Text(
                                            text = stringResource(R.string.settings_default),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            if (index < viewModel.calculationMethods.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 52.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            SettingsSection(title = stringResource(R.string.settings_theme)) {
                val currentThemeNameRes = viewModel.themeOptions.find { it.first == preferences.appTheme }?.second
                    ?: R.string.theme_emerald_oasis
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.settings_theme),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(currentThemeNameRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .clickable { showThemeSheet = true }
                )
            }

            // Mezhep Section
            SettingsSection(title = stringResource(R.string.settings_school)) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.selectableGroup()) {
                        viewModel.schoolOptions.forEachIndexed { index, (school, nameRes) ->
                            val selected = preferences.school == school
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selected,
                                        onClick = {
                                            viewModel.setSchool(school)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.padding(start = 16.dp))
                                Text(
                                    text = stringResource(nameRes),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (school == 0) {
                                    Text(
                                        text = stringResource(R.string.settings_default),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            if (index < viewModel.schoolOptions.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 52.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            UpdateCheckButton()

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            Text(
                text = "Namaz Vakitleri v${co.gorkem.namaz.BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )
        }
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { 
                showRationaleDialog = false 
                viewModel.setNotificationsEnabled(false)
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.notification_permission_rationale_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.notification_permission_rationale_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRationaleDialog = false
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_grant),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRationaleDialog = false
                        viewModel.setNotificationsEnabled(false)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_cancel),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            iconContentColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSettingsDialog = false 
                viewModel.setNotificationsEnabled(false)
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = stringResource(R.string.notification_permission_settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.notification_permission_settings_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSettingsDialog = false
                        viewModel.setNotificationsEnabled(false)
                        NotificationPermissionHelper.openAppSettings(context)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_settings_action),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSettingsDialog = false
                        viewModel.setNotificationsEnabled(false)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.notification_permission_cancel),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            iconContentColor = MaterialTheme.colorScheme.error,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showThemeSheet) {
        val themeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showThemeSheet = false },
            sheetState = themeSheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_theme),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.theme_picker_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                themeSheetState.hide()
                                showThemeSheet = false
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.theme_picker_close),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        viewModel.themeOptions.forEachIndexed { index, (themeOrdinal, nameRes) ->
                            val themeSelection = AppThemeSelection.entries.getOrElse(themeOrdinal) { AppThemeSelection.EMERALD_OASIS }
                            val isSelected = preferences.appTheme == themeOrdinal
                            val previewScheme = themeSelection.toColorScheme(darkTheme = false)

                            ThemePreviewRow(
                                themeName = stringResource(nameRes),
                                previewScheme = previewScheme,
                                isSelected = isSelected,
                                isDark = themeSelection.isDarkFixed,
                                onClick = {
                                    viewModel.setAppTheme(themeOrdinal)
                                    coroutineScope.launch {
                                        themeSheetState.hide()
                                        showThemeSheet = false
                                    }
                                }
                            )

                            if (index < viewModel.themeOptions.lastIndex) {
                                HorizontalDivider(
                                    color = if (themeSelection.isDarkFixed) {
                                        Color.White.copy(alpha = 0.08f)
                                    } else {
                                        Color.Black.copy(alpha = 0.08f)
                                    },
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemePreviewRow(
    themeName: String,
    previewScheme: ColorScheme,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val samplePrefix = stringResource(R.string.theme_sample_prefix)
    val sampleAccent1 = stringResource(R.string.theme_sample_accent1)
    val sampleMiddle = stringResource(R.string.theme_sample_middle)
    val sampleAccent2 = stringResource(R.string.theme_sample_accent2)

    val sampleText = remember(samplePrefix, sampleAccent1, sampleMiddle, sampleAccent2, previewScheme) {
        buildAnnotatedString {
            withStyle(SpanStyle(color = previewScheme.onSurface.copy(alpha = 0.75f))) {
                append(samplePrefix)
            }
            withStyle(SpanStyle(color = previewScheme.primary)) {
                append(sampleAccent1)
            }
            withStyle(SpanStyle(color = previewScheme.onSurface.copy(alpha = 0.75f))) {
                append(sampleMiddle)
            }
            withStyle(SpanStyle(color = previewScheme.secondary)) {
                append(sampleAccent2)
            }
        }
    }

    val checkmarkColor = if (isDark) Color(0xFFE06D53) else Color(0xFFB83A2E)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(previewScheme.surface)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = themeName,
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 22.sp
                ),
                color = previewScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = sampleText,
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = checkmarkColor,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(22.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        content()
    }
}
