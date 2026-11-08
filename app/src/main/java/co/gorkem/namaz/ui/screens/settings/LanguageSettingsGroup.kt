package co.gorkem.namaz.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.LocaleManager
import co.gorkem.namaz.MainActivity
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.UserPreferences

@Composable
fun LanguageSettingsGroup(
    preferences: UserPreferences,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    SettingsSection(title = stringResource(R.string.settings_language), modifier = modifier) {
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
}
