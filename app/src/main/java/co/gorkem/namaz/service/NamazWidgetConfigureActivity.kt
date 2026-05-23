package co.gorkem.namaz.service

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import co.gorkem.namaz.R
import co.gorkem.namaz.ui.theme.NamazTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NamazWidgetConfigureActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: android.content.Context) {
        super.attachBaseContext(co.gorkem.namaz.LocaleManager.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        android.util.Log.d("NamazWidgetConfigure", "onCreate: intent = $intent, extras = ${intent?.extras}, appWidgetId = $appWidgetId")

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            android.util.Log.e("NamazWidgetConfigure", "onCreate: INVALID_APPWIDGET_ID, finishing")
            finish()
            return
        }

        // Default to CANCELED in case user presses back key
        setResult(RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId))

        // Read current values if we are reconfiguring
        val currentTheme = NamazWidgetPrefs.getWidgetTheme(this, appWidgetId)
        val currentLayout = NamazWidgetPrefs.getWidgetLayout(this, appWidgetId)
        android.util.Log.d("NamazWidgetConfigure", "onCreate: current values for $appWidgetId: theme = $currentTheme, layout = $currentLayout")

        setContent {
            NamazTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConfigurationScreen(
                        initialTheme = currentTheme,
                        initialLayout = currentLayout,
                        onSave = { selectedTheme, selectedLayout ->
                            android.util.Log.d("NamazWidgetConfigure", "onSave: saving settings for $appWidgetId: theme = $selectedTheme, layout = $selectedLayout")
                            NamazWidgetPrefs.setWidgetTheme(this@NamazWidgetConfigureActivity, appWidgetId, selectedTheme)
                            NamazWidgetPrefs.setWidgetLayout(this@NamazWidgetConfigureActivity, appWidgetId, selectedLayout)

                            lifecycleScope.launch {
                                try {
                                    val manager = GlanceAppWidgetManager(this@NamazWidgetConfigureActivity)
                                    val glanceId = manager.getGlanceIdBy(appWidgetId)
                                    NamazWidget().update(this@NamazWidgetConfigureActivity, glanceId)
                                } catch (e: Exception) {
                                    android.util.Log.e("NamazWidgetConfigure", "Error updating specific widget, falling back to updateAll", e)
                                    try {
                                        NamazWidget().updateAll(this@NamazWidgetConfigureActivity)
                                    } catch (e2: Exception) {
                                        android.util.Log.e("NamazWidgetConfigure", "Error in updateAll", e2)
                                    }
                                }

                                val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                setResult(RESULT_OK, resultValue)
                                finish()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfigurationScreen(
    initialTheme: Int,
    initialLayout: Int,
    onSave: (theme: Int, layout: Int) -> Unit
) {
    var selectedTheme by remember { mutableIntStateOf(initialTheme) }
    var selectedLayout by remember { mutableIntStateOf(initialLayout) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.widget_settings_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(R.string.widget_settings_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Widget Theme Selection
        Text(
            text = stringResource(R.string.widget_setting_theme),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        ThemeOptionCard(
            title = stringResource(R.string.widget_theme_app),
            description = stringResource(R.string.widget_theme_app_desc),
            selected = selectedTheme == 0,
            onClick = { selectedTheme = 0 }
        )
        Spacer(modifier = Modifier.height(8.dp))

        ThemeOptionCard(
            title = stringResource(R.string.widget_theme_glass),
            description = stringResource(R.string.widget_theme_glass_desc),
            selected = selectedTheme == 1,
            onClick = { selectedTheme = 1 }
        )
        Spacer(modifier = Modifier.height(8.dp))

        ThemeOptionCard(
            title = stringResource(R.string.widget_theme_dynamic),
            description = stringResource(R.string.widget_theme_dynamic_desc),
            selected = selectedTheme == 2,
            onClick = { selectedTheme = 2 }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Layout Selector
        Text(
            text = stringResource(R.string.widget_setting_layout),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        LayoutOptionRow(
            title = stringResource(R.string.widget_layout_detailed),
            selected = selectedLayout == 0,
            onClick = { selectedLayout = 0 }
        )
        LayoutOptionRow(
            title = stringResource(R.string.widget_layout_compact),
            selected = selectedLayout == 1,
            onClick = { selectedLayout = 1 }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onSave(selectedTheme, selectedLayout) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.widget_settings_save),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun LayoutOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
