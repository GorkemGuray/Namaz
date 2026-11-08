package co.gorkem.namaz.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import co.gorkem.namaz.R
import co.gorkem.namaz.ui.components.UpdateCheckButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val preferences by viewModel.preferences.collectAsState()

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

            SettingsSection(title = stringResource(R.string.settings_notifications)) {
                NotificationSettingsGroup(
                    preferences = preferences,
                    viewModel = viewModel
                )
            }

            LanguageSettingsGroup(
                preferences = preferences,
                viewModel = viewModel
            )

            SettingsSection(title = stringResource(R.string.qada_tracker_title)) {
                QadaSettingsGroup(viewModel = viewModel)
            }

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

            ThemeSettingsGroup(
                preferences = preferences,
                viewModel = viewModel
            )

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
}

@Composable
internal fun SettingsSection(
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
