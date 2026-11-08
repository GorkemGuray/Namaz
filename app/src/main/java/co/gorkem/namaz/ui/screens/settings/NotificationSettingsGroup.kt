package co.gorkem.namaz.ui.screens.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.UserPreferences
import co.gorkem.namaz.util.NotificationPermissionHelper

@Composable
fun NotificationSettingsGroup(
    preferences: UserPreferences,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

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

    Column(modifier = modifier) {
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

                Spacer(modifier = Modifier.height(12.dp))

                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.settings_prayer_voice),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(R.string.settings_prayer_voice_desc),
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
                            checked = preferences.prayerVoiceEnabled,
                            onCheckedChange = { viewModel.setPrayerVoiceEnabled(it) }
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    ),
                    modifier = Modifier.clip(MaterialTheme.shapes.large)
                )
            }
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
}
