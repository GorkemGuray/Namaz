package co.gorkem.namaz.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import co.gorkem.namaz.BuildConfig
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.util.UpdateCheckResult
import co.gorkem.namaz.util.UpdateChecker
import kotlinx.coroutines.flow.first

@Composable
fun UpdateBanner(
    modifier: Modifier = Modifier,
    manualCheck: Boolean = false,
    onCheckCompleted: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val currentVersion = BuildConfig.VERSION_NAME

    var result by remember { mutableStateOf<UpdateCheckResult?>(null) }
    var dismissed by remember { mutableStateOf(false) }
    var checking by remember { mutableStateOf(false) }
    var shouldCheck by remember { mutableStateOf(true) }

    LaunchedEffect(shouldCheck) {
        if (!shouldCheck) return@LaunchedEffect

        val prefsRepo = UserPreferencesRepository(context.applicationContext)
        val prefs = prefsRepo.preferences.first()

        if (!manualCheck) {
            val oneWeekMs = 7 * 24 * 60 * 60 * 1000L
            if (System.currentTimeMillis() - prefs.lastUpdateCheckMs < oneWeekMs) {
                shouldCheck = false
                onCheckCompleted?.invoke()
                return@LaunchedEffect
            }
        }

        checking = true
        val updateChecker = UpdateChecker()
        val checkResult = updateChecker.check(currentVersion)
        prefsRepo.setLastUpdateCheck()
        checking = false
        result = checkResult
        shouldCheck = false
        onCheckCompleted?.invoke()
    }

    if (!manualCheck && (result == null || !result!!.updateAvailable || dismissed)) return

    AnimatedVisibility(
        visible = result != null && (manualCheck || (result?.updateAvailable == true && !dismissed)),
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        val res = result ?: return@AnimatedVisibility

        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = if (res.updateAvailable) MaterialTheme.colorScheme.primaryContainer
                   else MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.SystemUpdate,
                    contentDescription = null,
                    tint = if (res.updateAvailable) MaterialTheme.colorScheme.onPrimaryContainer
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(8.dp))

                val message = if (checking) {
                    "Güncellemeler kontrol ediliyor..."
                } else if (res.isError) {
                    "Güncelleme kontrolü yapılamadı"
                } else if (res.updateAvailable) {
                    "Yeni sürüm mevcut: v${res.latestVersion} (Şu an: v${res.currentVersion})"
                } else {
                    "Uygulamanız güncel (v${res.currentVersion})"
                }

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                if (res.updateAvailable) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.releaseUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("İndir", style = MaterialTheme.typography.labelMedium)
                    }
                }

                if (manualCheck || !res.updateAvailable) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = {
                        dismissed = true
                        result = null
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Kapat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (res.updateAvailable) {
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = { dismissed = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Kapat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
