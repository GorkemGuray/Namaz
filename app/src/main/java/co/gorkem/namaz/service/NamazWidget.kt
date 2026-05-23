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
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import co.gorkem.namaz.ui.theme.DarkColorScheme
import co.gorkem.namaz.ui.theme.LightColorScheme
import androidx.glance.material3.ColorProviders
import androidx.glance.unit.ColorProvider
import androidx.glance.color.ColorProvider as DayNightColorProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
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
}

data class NamazWidgetState(
    val cityName: String,
    val language: String,
    val todayPrayerTimes: List<Pair<PrayerName, String>>,
    val nextPrayerName: PrayerName?,
    val nextPrayerTime: String?,
    val remainingMinutes: Long?,
    val currentPrayerName: PrayerName?,
    val widgetTheme: Int,
    val layoutPreference: Int,
    val error: String? = null
)

class NamazWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            NamazWidgetEntryPoint::class.java
        )

        // Resolve appWidgetId to load config
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

            // 1. Collect user preferences from DataStore dynamically
            val prefsFlow = remember { entryPoint.userPreferencesRepository().preferences }
            val prefsState by prefsFlow.collectAsState(initial = null)

            // 2. Read widget-specific preferences from SharedPreferences
            val themePreference = NamazWidgetPrefs.getWidgetTheme(ctx, appWidgetId)
            val layoutPreference = NamazWidgetPrefs.getWidgetLayout(ctx, appWidgetId)

            // 3. Track recomposition/updates via timestamp key
            val updateTrigger = System.currentTimeMillis()

            // 4. Produce widgetState reactively when settings or trigger changes
            val widgetState by produceState<NamazWidgetState?>(
                initialValue = null,
                prefsState,
                themePreference,
                layoutPreference,
                updateTrigger
            ) {
                val currentPrefs = prefsState
                if (currentPrefs != null) {
                    val loadedState = loadWidgetState(ctx, entryPoint, currentPrefs, themePreference, layoutPreference)
                    android.util.Log.d("NamazWidget", "provideContent: loaded state theme=$themePreference, layout=$layoutPreference: $loadedState")
                    value = loadedState
                }
            }

            val state = widgetState
            if (state == null) {
                LoadingContent()
            } else {
                // Setup theme colors based on preferences
                val widgetColors = when (themePreference) {
                    1 -> { // Glassmorphism Theme (with semi-transparent colors)
                        ColorProviders(
                            light = LightColorScheme.copy(
                                primary = Color(0xFF1E3A34),
                                onPrimary = Color(0xFFFFFFFF),
                                secondaryContainer = Color(0x281E3A34),
                                onSecondaryContainer = Color(0xFF1C1B1F),
                                background = Color(0xD0FBFBFA),
                                surface = Color(0xD0FBFBFA),
                                onSurface = Color(0xFF1C1B1F),
                                surfaceVariant = Color(0xE0E8F0EC),
                                onSurfaceVariant = Color(0xFF6A6A6A)
                            ),
                            dark = DarkColorScheme.copy(
                                primary = Color(0xFF3B6053),
                                onPrimary = Color(0xFFFFFFFF),
                                secondaryContainer = Color(0x323B6053),
                                onSecondaryContainer = Color(0xFFE6E6E6),
                                background = Color(0x3C121414),
                                surface = Color(0x3C121414),
                                onSurface = Color(0xFFE6E6E6),
                                surfaceVariant = Color(0x501F2E2A),
                                onSurfaceVariant = Color(0xFF909090)
                            )
                        )
                    }
                    else -> { // Classic App Theme (dynamic colors fallback is now locked custom theme)
                        ColorProviders(
                            light = LightColorScheme,
                            dark = DarkColorScheme
                        )
                    }
                }

                GlanceTheme(colors = widgetColors) {
                    WidgetContent(state, appWidgetId)
                }
            }
        }
    }
}

private suspend fun loadWidgetState(
    context: Context,
    entryPoint: NamazWidgetEntryPoint,
    prefs: UserPreferences,
    widgetTheme: Int,
    layoutPreference: Int
): NamazWidgetState {
    return try {
        val getPrayerTimesUseCase = entryPoint.getPrayerTimesUseCase()
        val getNextPrayerUseCase = entryPoint.getNextPrayerUseCase()

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

                    widgetState = NamazWidgetState(
                        cityName = if (location.districtName.isNotEmpty()) location.districtName else location.cityName,
                        language = language,
                        todayPrayerTimes = todayPrayer.getAllPrayers(),
                        nextPrayerName = nextPrayerName,
                        nextPrayerTime = nextInfo?.prayerTime,
                        remainingMinutes = nextInfo?.remainingMinutes,
                        currentPrayerName = currentPrayerName,
                        widgetTheme = widgetTheme,
                        layoutPreference = layoutPreference
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
                        widgetTheme = widgetTheme,
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
                    widgetTheme = widgetTheme,
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
            widgetTheme = widgetTheme,
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
            widgetTheme = widgetTheme,
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

    // Customize typography colors using GlanceTheme colors
    val onSurfaceColor = GlanceTheme.colors.onSurface
    val onSurfaceVariantColor = GlanceTheme.colors.onSurfaceVariant
    val primaryTextColor = GlanceTheme.colors.primary

    // Outer background color mapping
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
        // Detailed layout requires sufficient space (width >= 220dp, height >= 140dp) and layout selection 0
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
                primaryTextColor = primaryTextColor,
                settingsAction = settingsAction,
                state = state
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
                primaryTextColor = primaryTextColor,
                settingsAction = settingsAction,
                state = state
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
    primaryTextColor: ColorProvider,
    settingsAction: androidx.glance.action.Action,
    state: NamazWidgetState
) {
    val context = LocalContext.current
    val size = LocalSize.current
    val localizedPrayerName = getLocalizedString(context, nextPrayerName.nameRes, language)

    val cardBg = GlanceTheme.colors.secondaryContainer
    val cardOnBg = GlanceTheme.colors.onSecondaryContainer

    val badgeBg = GlanceTheme.colors.primary
    val badgeOnBg = GlanceTheme.colors.onPrimary

    val isNarrow = size.width < 180.dp

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // 1. Header Bar
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isNarrow) cityName else "Namaz Vakitleri • $cityName",
                style = TextStyle(
                    color = onSurfaceVariantColor,
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
                colorFilter = ColorFilter.tint(onSurfaceVariantColor)
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // 2. Next Prayer Card
        if (isNarrow) {
            // Vertical Layout for narrow sizes
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
                    text = localizedPrayerName,
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
                        text = "$remainingStr $remainingLabel",
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
            // Horizontal Layout for standard compact sizes
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
                            text = localizedPrayerName,
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

                    // Badge
                    Box(
                        modifier = GlanceModifier
                            .background(badgeBg)
                            .cornerRadius(16.dp)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$remainingStr $remainingLabel",
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
    primaryTextColor: ColorProvider,
    settingsAction: androidx.glance.action.Action,
    state: NamazWidgetState
) {
    val context = LocalContext.current
    val localizedPrayerName = getLocalizedString(context, nextPrayerName.nameRes, language)

    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // 1. Header Bar
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Namaz Vakitleri • $cityName",
                style = TextStyle(
                    color = onSurfaceVariantColor,
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
                colorFilter = ColorFilter.tint(onSurfaceVariantColor)
            )
        }

        Spacer(modifier = GlanceModifier.height(4.dp))

        // 2. Next Prayer Card (Full Width)
        val cardBg = GlanceTheme.colors.secondaryContainer
        val cardOnBg = GlanceTheme.colors.onSecondaryContainer

        val badgeBg = GlanceTheme.colors.primary
        val badgeOnBg = GlanceTheme.colors.onPrimary

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
                    text = localizedPrayerName,
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

            // Countdown Badge
            Box(
                modifier = GlanceModifier
                    .background(badgeBg)
                    .cornerRadius(16.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$remainingStr $remainingLabel",
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

        // 3. Today's Prayer Times Row (Full Width - equal division prevents wrapping)
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
                    // Chain: Label -> Icon -> Time String
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
