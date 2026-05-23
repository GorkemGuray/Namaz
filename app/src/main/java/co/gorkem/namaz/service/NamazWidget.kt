package co.gorkem.namaz.service

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import co.gorkem.namaz.data.local.datastore.UserPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.background
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.ColorFilter
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import co.gorkem.namaz.MainActivity
import co.gorkem.namaz.R
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerDashboardUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import co.gorkem.namaz.ui.theme.AppThemeSelection
import co.gorkem.namaz.ui.theme.EmeraldLightSurface
import co.gorkem.namaz.ui.theme.EmeraldLightPrimary
import co.gorkem.namaz.ui.theme.EmeraldLightSecondaryContainer
import co.gorkem.namaz.ui.theme.EmeraldLightOnSurface
import co.gorkem.namaz.ui.theme.EmeraldLightOnSurfaceVariant
import co.gorkem.namaz.ui.theme.EmeraldDarkSurface
import co.gorkem.namaz.ui.theme.EmeraldDarkPrimary
import co.gorkem.namaz.ui.theme.EmeraldDarkSecondaryContainer
import co.gorkem.namaz.ui.theme.EmeraldDarkOnSurface
import co.gorkem.namaz.ui.theme.EmeraldDarkOnSurfaceVariant
import co.gorkem.namaz.ui.theme.SapphireLightSurface
import co.gorkem.namaz.ui.theme.SapphireLightPrimary
import co.gorkem.namaz.ui.theme.SapphireLightSecondaryContainer
import co.gorkem.namaz.ui.theme.SapphireLightOnSurface
import co.gorkem.namaz.ui.theme.SapphireLightOnSurfaceVariant
import co.gorkem.namaz.ui.theme.SapphireDarkSurface
import co.gorkem.namaz.ui.theme.SapphireDarkPrimary
import co.gorkem.namaz.ui.theme.SapphireDarkSecondaryContainer
import co.gorkem.namaz.ui.theme.SapphireDarkOnSurface
import co.gorkem.namaz.ui.theme.SapphireDarkOnSurfaceVariant
import co.gorkem.namaz.ui.theme.AmberLightSurface
import co.gorkem.namaz.ui.theme.AmberLightPrimary
import co.gorkem.namaz.ui.theme.AmberLightSecondaryContainer
import co.gorkem.namaz.ui.theme.AmberLightOnSurface
import co.gorkem.namaz.ui.theme.AmberLightOnSurfaceVariant
import co.gorkem.namaz.ui.theme.AmberDarkSurface
import co.gorkem.namaz.ui.theme.AmberDarkPrimary
import co.gorkem.namaz.ui.theme.AmberDarkSecondaryContainer
import co.gorkem.namaz.ui.theme.AmberDarkOnSurface
import co.gorkem.namaz.ui.theme.AmberDarkOnSurfaceVariant
import co.gorkem.namaz.ui.theme.MonochromeLightSurface
import co.gorkem.namaz.ui.theme.MonochromeLightPrimary
import co.gorkem.namaz.ui.theme.MonochromeLightSecondaryContainer
import co.gorkem.namaz.ui.theme.MonochromeLightOnSurface
import co.gorkem.namaz.ui.theme.MonochromeLightOnSurfaceVariant
import co.gorkem.namaz.ui.theme.MonochromeDarkSurface
import co.gorkem.namaz.ui.theme.MonochromeDarkPrimary
import co.gorkem.namaz.ui.theme.MonochromeDarkSecondaryContainer
import co.gorkem.namaz.ui.theme.MonochromeDarkOnSurface
import co.gorkem.namaz.ui.theme.MonochromeDarkOnSurfaceVariant
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NamazWidgetEntryPoint {
    fun userPreferencesRepository(): UserPreferencesRepository
    fun prayerTimeRepository(): PrayerTimeRepositoryImpl
    fun getPrayerTimesUseCase(): GetPrayerTimesUseCase
    fun getNextPrayerUseCase(): GetNextPrayerUseCase
    fun getPrayerDashboardUseCase(): GetPrayerDashboardUseCase
}

data class NamazWidgetState(
    val cityName: String,
    val language: String,
    val todayPrayerTimes: List<Pair<PrayerName, String>>,
    val nextPrayerName: PrayerName?,
    val nextPrayerTime: String?,
    val remainingMinutes: Long?,
    val currentPrayerName: PrayerName?,
    val layoutPreference: Int,
    val isKerahat: Boolean = false,
    val kerahatPrayerName: String? = null,
    val kerahatRemainingMinutes: Long = 0,
    val error: String? = null
)

class NamazWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            NamazWidgetEntryPoint::class.java
        )

        val appWidgetId = try {
            val resolvedId = GlanceAppWidgetManager(context).getAppWidgetId(id)
            android.util.Log.d("NamazWidget", "provideGlance: resolved appWidgetId = $resolvedId for GlanceId: $id")
            resolvedId
        } catch (e: Exception) {
            android.util.Log.e("NamazWidget", "provideGlance: failed to resolve appWidgetId for GlanceId: $id", e)
            AppWidgetManager.INVALID_APPWIDGET_ID
        }

        provideContent {
            val ctx = LocalContext.current

            val prefsFlow = remember { entryPoint.userPreferencesRepository().preferences }
            val prefsState by prefsFlow.collectAsState(initial = null)

            val layoutPreference = NamazWidgetPrefs.getWidgetLayout(ctx, appWidgetId)

            val updateTrigger = System.currentTimeMillis()

            val widgetState by produceState<NamazWidgetState?>(
                initialValue = null,
                prefsState,
                layoutPreference,
                updateTrigger
            ) {
                val currentPrefs = prefsState
                if (currentPrefs != null) {
                    val loadedState = loadWidgetState(ctx, entryPoint, currentPrefs, layoutPreference)
                    android.util.Log.d("NamazWidget", "provideContent: loaded state layout=$layoutPreference: $loadedState")
                    value = loadedState
                }
            }

            val state = widgetState
            if (state == null) {
                LoadingContent()
            } else {
                val appTheme = AppThemeSelection.values().getOrElse(
                    prefsState?.appTheme ?: 0
                ) { AppThemeSelection.EMERALD_OASIS }

                val widgetColors = themeToColorProviders(appTheme)

                GlanceTheme(colors = widgetColors) {
                    WidgetContent(state, appWidgetId)
                }
            }
        }
    }
}

private fun themeToColorProviders(theme: AppThemeSelection): androidx.glance.color.ColorProviders {
    return when (theme) {
        AppThemeSelection.EMERALD_OASIS -> ColorProviders(
            light = lightColorScheme(
                primary = EmeraldLightPrimary, onPrimary = Color(0xFFFFFFFF),
                secondaryContainer = EmeraldLightSecondaryContainer,
                onSecondaryContainer = EmeraldLightOnSurface,
                background = EmeraldLightSurface, surface = EmeraldLightSurface,
                onSurface = EmeraldLightOnSurface,
                surfaceVariant = Color(0xFFF4F3EF), onSurfaceVariant = EmeraldLightOnSurfaceVariant,
                onBackground = EmeraldLightOnSurface, onSecondary = Color(0xFFFFFFFF),
                secondary = Color(0xFF3D5A3F),
                tertiary = Color(0xFF8A6500), onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFFFE08A), onTertiaryContainer = Color(0xFF271900),
                error = Color(0xFFB3261E), onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFF9DEDC), onErrorContainer = Color(0xFF410E0B),
                primaryContainer = EmeraldLightSecondaryContainer,
                onPrimaryContainer = EmeraldLightPrimary,
                surfaceTint = EmeraldLightPrimary,
                inverseSurface = Color(0xFF313033), inverseOnSurface = Color(0xFFF4EFF4),
                inversePrimary = Color(0xFF9BBFB3),
                surfaceDim = Color(0xFFDDD8DD), surfaceBright = EmeraldLightSurface,
                surfaceContainerLowest = Color(0xFFFFFFFF),
                surfaceContainerLow = Color(0xFFF7F2FA),
                surfaceContainer = Color(0xFFF3EDF7),
                surfaceContainerHigh = Color(0xFFECE6F0),
                surfaceContainerHighest = Color(0xFFE6E0E9),
                outline = Color(0xFF7A8B85), outlineVariant = Color(0xFFC8D3CE),
                scrim = Color(0xFF000000)
            ),
            dark = darkColorScheme(
                primary = EmeraldDarkPrimary, onPrimary = Color(0xFFFFFFFF),
                secondaryContainer = EmeraldDarkSecondaryContainer,
                onSecondaryContainer = EmeraldDarkOnSurface,
                background = EmeraldDarkSurface, surface = EmeraldDarkSurface,
                onSurface = EmeraldDarkOnSurface,
                surfaceVariant = Color(0xFF1A1D1D), onSurfaceVariant = EmeraldDarkOnSurfaceVariant,
                onBackground = EmeraldDarkOnSurface, onSecondary = Color(0xFF1F3021),
                secondary = Color(0xFFB4C7B3),
                tertiary = Color(0xFFE8C462), onTertiary = Color(0xFF402D00),
                tertiaryContainer = Color(0xFF5C4300), onTertiaryContainer = Color(0xFFFFDEA1),
                error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
                primaryContainer = EmeraldDarkSecondaryContainer,
                onPrimaryContainer = EmeraldDarkOnSurface,
                surfaceTint = EmeraldDarkPrimary,
                inverseSurface = Color(0xFFE3E2E6), inverseOnSurface = Color(0xFF1C1B1F),
                inversePrimary = EmeraldLightPrimary,
                surfaceDim = EmeraldDarkSurface, surfaceBright = Color(0xFF38383A),
                surfaceContainerLowest = Color(0xFF0D0E0F),
                surfaceContainerLow = Color(0xFF1A1A1C),
                surfaceContainer = Color(0xFF1E1E20),
                surfaceContainerHigh = Color(0xFF29292B),
                surfaceContainerHighest = Color(0xFF333335),
                outline = Color(0xFF5A6B64), outlineVariant = Color(0xFF3C4D46),
                scrim = Color(0xFF000000)
            )
        )
        AppThemeSelection.SAPPHIRE_NIGHT -> ColorProviders(
            light = lightColorScheme(
                primary = SapphireLightPrimary, onPrimary = Color(0xFFFFFFFF),
                secondaryContainer = SapphireLightSecondaryContainer,
                onSecondaryContainer = SapphireLightOnSurface,
                background = SapphireLightSurface, surface = SapphireLightSurface,
                onSurface = SapphireLightOnSurface,
                surfaceVariant = Color(0xFFF1F3F7), onSurfaceVariant = SapphireLightOnSurfaceVariant,
                onBackground = SapphireLightOnSurface, onSecondary = Color(0xFFFFFFFF),
                secondary = Color(0xFF3A5A6E),
                tertiary = Color(0xFFB4502A), onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFFFDBCE), onTertiaryContainer = Color(0xFF401204),
                error = Color(0xFFB3261E), onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFF9DEDC), onErrorContainer = Color(0xFF410E0B),
                primaryContainer = SapphireLightSecondaryContainer,
                onPrimaryContainer = SapphireLightPrimary,
                surfaceTint = SapphireLightPrimary,
                inverseSurface = Color(0xFF2D3138), inverseOnSurface = Color(0xFFF0F2F5),
                inversePrimary = Color(0xFF8BB0E8),
                surfaceDim = Color(0xFFDCDEE3), surfaceBright = SapphireLightSurface,
                surfaceContainerLowest = Color(0xFFFFFFFF),
                surfaceContainerLow = Color(0xFFF2F4F8),
                surfaceContainer = Color(0xFFEDEFF4),
                surfaceContainerHigh = Color(0xFFE7E9EE),
                surfaceContainerHighest = Color(0xFFE1E3E8),
                outline = Color(0xFF788596), outlineVariant = Color(0xFFC6CFDB),
                scrim = Color(0xFF000000)
            ),
            dark = darkColorScheme(
                primary = SapphireDarkPrimary, onPrimary = Color(0xFF001A33),
                secondaryContainer = SapphireDarkSecondaryContainer,
                onSecondaryContainer = SapphireDarkOnSurface,
                background = SapphireDarkSurface, surface = SapphireDarkSurface,
                onSurface = SapphireDarkOnSurface,
                surfaceVariant = Color(0xFF181D25), onSurfaceVariant = SapphireDarkOnSurfaceVariant,
                onBackground = SapphireDarkOnSurface, onSecondary = Color(0xFF003042),
                secondary = Color(0xFFA0C8E0),
                tertiary = Color(0xFFE8956A), onTertiary = Color(0xFF501E08),
                tertiaryContainer = Color(0xFF8E3820), onTertiaryContainer = Color(0xFFFFDBCE),
                error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
                primaryContainer = SapphireDarkSecondaryContainer,
                onPrimaryContainer = SapphireDarkOnSurface,
                surfaceTint = SapphireDarkPrimary,
                inverseSurface = Color(0xFFE0E4EA), inverseOnSurface = Color(0xFF111827),
                inversePrimary = SapphireLightPrimary,
                surfaceDim = SapphireDarkSurface, surfaceBright = Color(0xFF343842),
                surfaceContainerLowest = Color(0xFF090D12),
                surfaceContainerLow = Color(0xFF161A22),
                surfaceContainer = Color(0xFF1A1E26),
                surfaceContainerHigh = Color(0xFF25292F),
                surfaceContainerHighest = Color(0xFF30343A),
                outline = Color(0xFF6E7A8C), outlineVariant = Color(0xFF4A5566),
                scrim = Color(0xFF000000)
            )
        )
        AppThemeSelection.AMBER_SANDS -> ColorProviders(
            light = lightColorScheme(
                primary = AmberLightPrimary, onPrimary = Color(0xFFFFFFFF),
                secondaryContainer = AmberLightSecondaryContainer,
                onSecondaryContainer = AmberLightOnSurface,
                background = AmberLightSurface, surface = AmberLightSurface,
                onSurface = AmberLightOnSurface,
                surfaceVariant = Color(0xFFF5F0EA), onSurfaceVariant = AmberLightOnSurfaceVariant,
                onBackground = AmberLightOnSurface, onSecondary = Color(0xFFFFFFFF),
                secondary = Color(0xFF6B5840),
                tertiary = Color(0xFF2A6B6B), onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFCCF0F0), onTertiaryContainer = Color(0xFF002020),
                error = Color(0xFFB3261E), onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFF9DEDC), onErrorContainer = Color(0xFF410E0B),
                primaryContainer = AmberLightSecondaryContainer,
                onPrimaryContainer = AmberLightPrimary,
                surfaceTint = AmberLightPrimary,
                inverseSurface = Color(0xFF353230), inverseOnSurface = Color(0xFFF2F0EC),
                inversePrimary = Color(0xFFC8B090),
                surfaceDim = Color(0xFFDDDAD7), surfaceBright = AmberLightSurface,
                surfaceContainerLowest = Color(0xFFFFFFFF),
                surfaceContainerLow = Color(0xFFF5F2EE),
                surfaceContainer = Color(0xFFF0EDE9),
                surfaceContainerHigh = Color(0xFFEAE7E3),
                surfaceContainerHighest = Color(0xFFE4E1DD),
                outline = Color(0xFF857C75), outlineVariant = Color(0xFFD4CBC3),
                scrim = Color(0xFF000000)
            ),
            dark = darkColorScheme(
                primary = AmberDarkPrimary, onPrimary = Color(0xFF3A2410),
                secondaryContainer = AmberDarkSecondaryContainer,
                onSecondaryContainer = AmberDarkOnSurface,
                background = AmberDarkSurface, surface = AmberDarkSurface,
                onSurface = AmberDarkOnSurface,
                surfaceVariant = Color(0xFF1E1A18), onSurfaceVariant = AmberDarkOnSurfaceVariant,
                onBackground = AmberDarkOnSurface, onSecondary = Color(0xFF3A2818),
                secondary = Color(0xFFC4B098),
                tertiary = Color(0xFF6BB5B5), onTertiary = Color(0xFF003838),
                tertiaryContainer = Color(0xFF1A5454), onTertiaryContainer = Color(0xFFCCF0F0),
                error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
                primaryContainer = AmberDarkSecondaryContainer,
                onPrimaryContainer = AmberDarkOnSurface,
                surfaceTint = AmberDarkPrimary,
                inverseSurface = Color(0xFFE8E4E0), inverseOnSurface = Color(0xFF272524),
                inversePrimary = AmberLightPrimary,
                surfaceDim = AmberDarkSurface, surfaceBright = Color(0xFF3A3836),
                surfaceContainerLowest = Color(0xFF0F0E0D),
                surfaceContainerLow = Color(0xFF1C1A18),
                surfaceContainer = Color(0xFF201E1C),
                surfaceContainerHigh = Color(0xFF2B2927),
                surfaceContainerHighest = Color(0xFF363432),
                outline = Color(0xFF7E7670), outlineVariant = Color(0xFF524A42),
                scrim = Color(0xFF000000)
            )
        )
        AppThemeSelection.MONOCHROME_PLATINUM -> ColorProviders(
            light = lightColorScheme(
                primary = MonochromeLightPrimary, onPrimary = Color(0xFFFFFFFF),
                secondaryContainer = MonochromeLightSecondaryContainer,
                onSecondaryContainer = MonochromeLightOnSurface,
                background = MonochromeLightSurface, surface = MonochromeLightSurface,
                onSurface = MonochromeLightOnSurface,
                surfaceVariant = Color(0xFFF5F5F5), onSurfaceVariant = MonochromeLightOnSurfaceVariant,
                onBackground = MonochromeLightOnSurface, onSecondary = Color(0xFFFFFFFF),
                secondary = Color(0xFF4A4A4A),
                tertiary = Color(0xFF525252), onTertiary = Color(0xFFFFFFFF),
                tertiaryContainer = Color(0xFFE8E8E8), onTertiaryContainer = Color(0xFF1A1A1A),
                error = Color(0xFFB3261E), onError = Color(0xFFFFFFFF),
                errorContainer = Color(0xFFF9DEDC), onErrorContainer = Color(0xFF410E0B),
                primaryContainer = MonochromeLightSecondaryContainer,
                onPrimaryContainer = MonochromeLightPrimary,
                surfaceTint = MonochromeLightPrimary,
                inverseSurface = Color(0xFF2D2D2D), inverseOnSurface = Color(0xFFF0F0F0),
                inversePrimary = Color(0xFFB8B8B8),
                surfaceDim = Color(0xFFE0E0E0), surfaceBright = MonochromeLightSurface,
                surfaceContainerLowest = Color(0xFFFFFFFF),
                surfaceContainerLow = Color(0xFFF8F8F8),
                surfaceContainer = Color(0xFFF2F2F2),
                surfaceContainerHigh = Color(0xFFECECEC),
                surfaceContainerHighest = Color(0xFFE6E6E6),
                outline = Color(0xFF7A7A7A), outlineVariant = Color(0xFFCCCCCC),
                scrim = Color(0xFF000000)
            ),
            dark = darkColorScheme(
                primary = MonochromeDarkPrimary, onPrimary = Color(0xFF1A1A1A),
                secondaryContainer = MonochromeDarkSecondaryContainer,
                onSecondaryContainer = MonochromeDarkOnSurface,
                background = MonochromeDarkSurface, surface = MonochromeDarkSurface,
                onSurface = MonochromeDarkOnSurface,
                surfaceVariant = Color(0xFF141414), onSurfaceVariant = MonochromeDarkOnSurfaceVariant,
                onBackground = MonochromeDarkOnSurface, onSecondary = Color(0xFF2A2A2A),
                secondary = Color(0xFFB8B8B8),
                tertiary = Color(0xFFA3A3A3), onTertiary = Color(0xFF2A2A2A),
                tertiaryContainer = Color(0xFF3A3A3A), onTertiaryContainer = Color(0xFFE8E8E8),
                error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
                errorContainer = Color(0xFF93000A), onErrorContainer = Color(0xFFFFDAD6),
                primaryContainer = MonochromeDarkSecondaryContainer,
                onPrimaryContainer = MonochromeDarkOnSurface,
                surfaceTint = MonochromeDarkPrimary,
                inverseSurface = Color(0xFFE0E0E0), inverseOnSurface = Color(0xFF1A1A1A),
                inversePrimary = MonochromeLightPrimary,
                surfaceDim = MonochromeDarkSurface, surfaceBright = Color(0xFF303030),
                surfaceContainerLowest = Color(0xFF050505),
                surfaceContainerLow = Color(0xFF121212),
                surfaceContainer = Color(0xFF161616),
                surfaceContainerHigh = Color(0xFF212121),
                surfaceContainerHighest = Color(0xFF2C2C2C),
                outline = Color(0xFF6E6E6E), outlineVariant = Color(0xFF484848),
                scrim = Color(0xFF000000)
            )
        )
    }
}

private suspend fun loadWidgetState(
    context: Context,
    entryPoint: NamazWidgetEntryPoint,
    prefs: UserPreferences,
    layoutPreference: Int
): NamazWidgetState {
    return try {
        val getPrayerTimesUseCase = entryPoint.getPrayerTimesUseCase()
        val getNextPrayerUseCase = entryPoint.getNextPrayerUseCase()
        val getPrayerDashboardUseCase = entryPoint.getPrayerDashboardUseCase()

        val language = prefs.language

        val location = if (prefs.manualCityName.isNotEmpty()) {
            LocationInfo(
                latitude = prefs.manualLatitude,
                longitude = prefs.manualLongitude,
                timezone = prefs.manualTimezone.ifEmpty { "Europe/Istanbul" },
                cityName = prefs.manualCityName,
                districtName = prefs.manualDistrictName,
                districtId = prefs.manualDistrictId
            )
        } else if (prefs.lastAutoCityName.isNotEmpty()) {
            LocationInfo(
                latitude = prefs.lastAutoLatitude,
                longitude = prefs.lastAutoLongitude,
                timezone = prefs.lastAutoTimezone.ifEmpty { "Europe/Istanbul" },
                cityName = prefs.lastAutoCityName,
                districtName = prefs.lastAutoDistrictName,
                districtId = prefs.lastAutoDistrictId
            )
        } else {
            LocationInfo.DEFAULT
        }

        val today = LocalDate.now()
        val todayKey = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val tomorrowKey = today.plusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

        var widgetState: NamazWidgetState? = null

        getPrayerTimesUseCase(
            location = location,
            month = today.monthValue,
            year = today.year,
            method = prefs.calculationMethod,
            school = prefs.school
        ).first { result ->
            if (result is NetworkResult.Success) {
                val todayPrayer = result.data.find { it.date == todayKey }
                if (todayPrayer != null) {
                    val nextInfo = getNextPrayerUseCase(todayPrayer)
                    val nextPrayerName = nextInfo?.prayerName
                    val currentPrayerName = nextPrayerName?.let { getCurrentPrayer(it) }

                    val tomorrowPrayer = result.data.find { it.date == tomorrowKey }
                    val dashboard = getPrayerDashboardUseCase.execute(todayPrayer, tomorrowPrayer)
                    val kerahatInfo = dashboard?.kerahatInfo

                    widgetState = NamazWidgetState(
                        cityName = if (location.districtName.isNotEmpty()) location.districtName else location.cityName,
                        language = language,
                        todayPrayerTimes = todayPrayer.getAllPrayers(),
                        nextPrayerName = nextPrayerName,
                        nextPrayerTime = nextInfo?.prayerTime,
                        remainingMinutes = nextInfo?.remainingMinutes,
                        currentPrayerName = currentPrayerName,
                        layoutPreference = layoutPreference,
                        isKerahat = kerahatInfo?.isCurrentlyKerahat == true,
                        kerahatPrayerName = kerahatInfo?.prayerName?.name,
                        kerahatRemainingMinutes = kerahatInfo?.remainingToEndMinutes ?: 0
                    )
                } else {
                    widgetState = NamazWidgetState(
                        cityName = location.cityName,
                        language = language,
                        todayPrayerTimes = emptyList(),
                        nextPrayerName = null,
                        nextPrayerTime = null,
                        remainingMinutes = null,
                        currentPrayerName = null,
                        layoutPreference = layoutPreference,
                        error = "Bugünün vakitleri bulunamadı"
                    )
                }
                true
            } else if (result is NetworkResult.Error) {
                widgetState = NamazWidgetState(
                    cityName = location.cityName,
                    language = language,
                    todayPrayerTimes = emptyList(),
                    nextPrayerName = null,
                    nextPrayerTime = null,
                    remainingMinutes = null,
                    currentPrayerName = null,
                    layoutPreference = layoutPreference,
                    error = result.message
                )
                true
            } else {
                false
            }
        }

        widgetState ?: NamazWidgetState(
            cityName = location.cityName,
            language = language,
            todayPrayerTimes = emptyList(),
            nextPrayerName = null,
            nextPrayerTime = null,
            remainingMinutes = null,
            currentPrayerName = null,
            layoutPreference = layoutPreference,
            error = "Yükleniyor..."
        )
    } catch (e: Exception) {
        NamazWidgetState(
            cityName = "İstanbul",
            language = "tr",
            todayPrayerTimes = emptyList(),
            nextPrayerName = null,
            nextPrayerTime = null,
            remainingMinutes = null,
            currentPrayerName = null,
            layoutPreference = layoutPreference,
            error = e.localizedMessage ?: "Bilinmeyen hata"
        )
    }
}

private fun getCurrentPrayer(nextPrayerName: PrayerName): PrayerName {
    val list = PrayerName.values()
    val index = list.indexOf(nextPrayerName)
    return if (index == 0) {
        list.last()
    } else {
        list[index - 1]
    }
}

private fun formatRemainingTime(minutes: Long, language: String): String {
    val hrs = minutes / 60
    val mins = minutes % 60
    return if (language == "en") {
        if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"
    } else {
        if (hrs > 0) "${hrs} sa ${mins} dk" else "${mins} dk"
    }
}

private fun getRemainingLabel(language: String): String {
    return if (language == "en") "left" else "kaldı"
}

private fun getLocalizedString(context: Context, resId: Int, language: String): String {
    val locale = java.util.Locale.forLanguageTag(language)
    val config = android.content.res.Configuration(context.resources.configuration)
    config.setLocale(locale)
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.getString(resId)
}

@Composable
private fun WidgetContent(state: NamazWidgetState, appWidgetId: Int) {
    val context = LocalContext.current
    val size = LocalSize.current

    if (state.error != null) {
        ErrorContent(state.error)
        return
    }

    val nextPrayerName = state.nextPrayerName
    val nextPrayerTime = state.nextPrayerTime
    val remainingMinutes = state.remainingMinutes

    if (nextPrayerName == null || nextPrayerTime == null || remainingMinutes == null) {
        LoadingContent()
        return
    }

    val remainingStr = formatRemainingTime(remainingMinutes, state.language)
    val remainingLabel = getRemainingLabel(state.language)

    val kerahatRemainingStr = if (state.isKerahat && state.kerahatRemainingMinutes > 0) {
        formatRemainingTime(state.kerahatRemainingMinutes, state.language)
    } else {
        ""
    }

    val onSurfaceColor = GlanceTheme.colors.onSurface
    val onSurfaceVariantColor = GlanceTheme.colors.onSurfaceVariant

    val outerBackground = GlanceTheme.colors.surface
    val settingsAction = actionRunCallback<NamazWidgetSettingsAction>()

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(outerBackground)
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        if (size.width < 220.dp || size.height < 140.dp || state.layoutPreference == 1) {
            CompactLayout(
                cityName = state.cityName,
                nextPrayerName = nextPrayerName,
                nextPrayerTime = nextPrayerTime,
                remainingStr = remainingStr,
                remainingLabel = remainingLabel,
                language = state.language,
                onSurfaceColor = onSurfaceColor,
                onSurfaceVariantColor = onSurfaceVariantColor,
                settingsAction = settingsAction,
                isKerahat = state.isKerahat,
                kerahatPrayerName = state.kerahatPrayerName,
                kerahatRemainingStr = kerahatRemainingStr
            )
        } else {
            ExpandedLayout(
                cityName = state.cityName,
                nextPrayerName = nextPrayerName,
                nextPrayerTime = nextPrayerTime,
                remainingStr = remainingStr,
                remainingLabel = remainingLabel,
                todayPrayerTimes = state.todayPrayerTimes,
                currentPrayerName = state.currentPrayerName,
                language = state.language,
                onSurfaceColor = onSurfaceColor,
                onSurfaceVariantColor = onSurfaceVariantColor,
                settingsAction = settingsAction,
                isKerahat = state.isKerahat,
                kerahatPrayerName = state.kerahatPrayerName,
                kerahatRemainingStr = kerahatRemainingStr
            )
        }
    }
}

@Composable
private fun CompactLayout(
    cityName: String,
    nextPrayerName: PrayerName,
    nextPrayerTime: String,
    remainingStr: String,
    remainingLabel: String,
    language: String,
    onSurfaceColor: ColorProvider,
    onSurfaceVariantColor: ColorProvider,
    settingsAction: androidx.glance.action.Action,
    isKerahat: Boolean = false,
    kerahatPrayerName: String? = null,
    kerahatRemainingStr: String = ""
) {
    val context = LocalContext.current
    val size = LocalSize.current
    val localizedPrayerName = getLocalizedString(context, nextPrayerName.nameRes, language)

    val isKerahatActive = isKerahat && kerahatPrayerName != null
    val cardBg = if (isKerahatActive) GlanceTheme.colors.errorContainer else GlanceTheme.colors.secondaryContainer
    val cardOnBg = if (isKerahatActive) GlanceTheme.colors.onErrorContainer else GlanceTheme.colors.onSecondaryContainer
    val badgeBg = if (isKerahatActive) GlanceTheme.colors.error else GlanceTheme.colors.primary
    val badgeOnBg = if (isKerahatActive) GlanceTheme.colors.onError else GlanceTheme.colors.onPrimary
    val isNarrow = size.width < 180.dp

    val kerahatLabel = if (isKerahatActive) {
        mapKerahatPrayerName(context, kerahatPrayerName!!, language)
    } else {
        ""
    }

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val headerText = when {
                isKerahatActive && !isNarrow -> "Kerahat Vakti • $cityName"
                isKerahatActive && isNarrow -> cityName
                isNarrow -> cityName
                else -> "Namaz Vakitleri • $cityName"
            }
            Text(
                text = headerText,
                style = TextStyle(
                    color = if (isKerahatActive) GlanceTheme.colors.error else onSurfaceVariantColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = GlanceModifier.defaultWeight(),
                maxLines = 1
            )
            Image(
                provider = ImageProvider(R.drawable.ic_widget_settings),
                contentDescription = "Settings",
                modifier = GlanceModifier
                    .width(18.dp)
                    .height(18.dp)
                    .clickable(settingsAction),
                colorFilter = ColorFilter.tint(if (isKerahatActive) GlanceTheme.colors.error else onSurfaceVariantColor)
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        if (isNarrow) {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(cardBg)
                    .cornerRadius(12.dp)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isKerahatActive) "$kerahatLabel (Kerahat)" else localizedPrayerName,
                    style = TextStyle(
                        color = cardOnBg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = nextPrayerTime,
                    style = TextStyle(
                        color = cardOnBg,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.height(6.dp))
                Box(
                    modifier = GlanceModifier
                        .background(badgeBg)
                        .cornerRadius(16.dp)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isKerahatActive) "$kerahatRemainingStr $kerahatLabel bitiyor" else "$remainingStr $remainingLabel",
                        style = TextStyle(
                            color = badgeOnBg,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                }
            }
        } else {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .background(cardBg)
                    .cornerRadius(12.dp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = if (isKerahatActive) "$kerahatLabel (Kerahat)" else localizedPrayerName,
                            style = TextStyle(
                                color = cardOnBg,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Text(
                            text = nextPrayerTime,
                            style = TextStyle(
                                color = cardOnBg,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = GlanceModifier
                            .background(badgeBg)
                            .cornerRadius(16.dp)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isKerahatActive) "$kerahatRemainingStr bitiyor" else "$remainingStr $remainingLabel",
                            style = TextStyle(
                                color = badgeOnBg,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedLayout(
    cityName: String,
    nextPrayerName: PrayerName,
    nextPrayerTime: String,
    remainingStr: String,
    remainingLabel: String,
    todayPrayerTimes: List<Pair<PrayerName, String>>,
    currentPrayerName: PrayerName?,
    language: String,
    onSurfaceColor: ColorProvider,
    onSurfaceVariantColor: ColorProvider,
    settingsAction: androidx.glance.action.Action,
    isKerahat: Boolean = false,
    kerahatPrayerName: String? = null,
    kerahatRemainingStr: String = ""
) {
    val context = LocalContext.current
    val localizedPrayerName = getLocalizedString(context, nextPrayerName.nameRes, language)
    val isKerahatActive = isKerahat && kerahatPrayerName != null

    val kerahatLabel = if (isKerahatActive) {
        mapKerahatPrayerName(context, kerahatPrayerName!!, language)
    } else {
        ""
    }

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val headerText = if (isKerahatActive) "Kerahat Vakti • $cityName" else "Namaz Vakitleri • $cityName"
            Text(
                text = headerText,
                style = TextStyle(
                    color = if (isKerahatActive) GlanceTheme.colors.error else onSurfaceVariantColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = GlanceModifier.defaultWeight(),
                maxLines = 1
            )
            Image(
                provider = ImageProvider(R.drawable.ic_widget_settings),
                contentDescription = "Settings",
                modifier = GlanceModifier
                    .width(18.dp)
                    .height(18.dp)
                    .clickable(settingsAction),
                colorFilter = ColorFilter.tint(if (isKerahatActive) GlanceTheme.colors.error else onSurfaceVariantColor)
            )
        }

        Spacer(modifier = GlanceModifier.height(4.dp))

        val cardBg = if (isKerahatActive) GlanceTheme.colors.errorContainer else GlanceTheme.colors.secondaryContainer
        val cardOnBg = if (isKerahatActive) GlanceTheme.colors.onErrorContainer else GlanceTheme.colors.onSecondaryContainer
        val badgeBg = if (isKerahatActive) GlanceTheme.colors.error else GlanceTheme.colors.primary
        val badgeOnBg = if (isKerahatActive) GlanceTheme.colors.onError else GlanceTheme.colors.onPrimary

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(cardBg)
                .cornerRadius(12.dp)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = if (isKerahatActive) "$kerahatLabel (Kerahat)" else localizedPrayerName,
                    style = TextStyle(
                        color = cardOnBg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = nextPrayerTime,
                    style = TextStyle(
                        color = cardOnBg,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }

            Box(
                modifier = GlanceModifier
                    .background(badgeBg)
                    .cornerRadius(16.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isKerahatActive) "$kerahatRemainingStr bitiyor" else "$remainingStr $remainingLabel",
                    style = TextStyle(
                        color = badgeOnBg,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(6.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            todayPrayerTimes.forEachIndexed { index, (name, time) ->
                val isCurrent = name == currentPrayerName
                val localizedName = getLocalizedString(context, name.nameRes, language)

                val iconRes = when (name) {
                    PrayerName.IMSAK -> R.drawable.ic_prayer_imsak
                    PrayerName.GUNES -> R.drawable.ic_prayer_gunes
                    PrayerName.OGLE -> R.drawable.ic_prayer_ogle
                    PrayerName.IKINDI -> R.drawable.ic_prayer_ikindi
                    PrayerName.AKSAM -> R.drawable.ic_prayer_aksam
                    PrayerName.YATSI -> R.drawable.ic_prayer_yatsi
                }

                val textColor = if (isCurrent) GlanceTheme.colors.primary else onSurfaceColor
                val labelColor = if (isCurrent) GlanceTheme.colors.primary else onSurfaceVariantColor
                val iconColor = if (isCurrent) GlanceTheme.colors.primary else onSurfaceVariantColor
                val textWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal

                Column(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .padding(vertical = 2.dp, horizontal = 1.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = localizedName,
                        style = TextStyle(
                            color = labelColor,
                            fontSize = 10.sp,
                            fontWeight = textWeight
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Image(
                        provider = ImageProvider(iconRes),
                        contentDescription = localizedName,
                        modifier = GlanceModifier.width(18.dp).height(18.dp),
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = time,
                        style = TextStyle(
                            color = textColor,
                            fontSize = 11.sp,
                            fontWeight = textWeight
                        ),
                        maxLines = 1
                    )
                    if (isCurrent) {
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Box(
                            modifier = GlanceModifier
                                .width(10.dp)
                                .height(2.dp)
                                .background(iconColor)
                                .cornerRadius(1.dp)
                        ) {}
                    }
                }
            }
        }
    }
}

@Composable
private fun mapKerahatPrayerName(context: Context, prayerNameStr: String, language: String): String {
    val resId = when (prayerNameStr) {
        "GUNES" -> R.string.prayer_gunes
        "OGLE" -> R.string.prayer_ogle
        "AKSAM" -> R.string.prayer_aksam
        "IKINDI" -> R.string.prayer_ikindi
        else -> return prayerNameStr
    }
    return getLocalizedString(context, resId, language)
}

@Composable
private fun ErrorContent(error: String) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = error,
            style = TextStyle(
                color = GlanceTheme.colors.error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.widgetBackground)
            .cornerRadius(16.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "...",
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

class NamazWidgetSettingsAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        try {
            val manager = GlanceAppWidgetManager(context)
            val appWidgetId = manager.getAppWidgetId(glanceId)
            val intent = Intent(context, NamazWidgetConfigureActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            android.util.Log.e("NamazWidgetSettingsAction", "Failed to start settings activity", e)
        }
    }
}
