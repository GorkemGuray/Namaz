package co.gorkem.namaz.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.UserPreferences
import co.gorkem.namaz.ui.theme.AppThemeSelection
import co.gorkem.namaz.ui.theme.PlayfairDisplayFontFamily
import co.gorkem.namaz.ui.theme.isDarkFixed
import co.gorkem.namaz.ui.theme.toColorScheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsGroup(
    preferences: UserPreferences,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    var showThemeSheet by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    SettingsSection(title = stringResource(R.string.settings_theme), modifier = modifier) {
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
