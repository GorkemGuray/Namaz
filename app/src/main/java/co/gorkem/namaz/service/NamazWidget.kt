package co.gorkem.namaz.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
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
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.AndroidRemoteViews
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
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.ColorFilter
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import co.gorkem.namaz.MainActivity
import co.gorkem.namaz.R
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.local.datastore.getLocationInfo
import co.gorkem.namaz.data.remote.NetworkResult
import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.util.alarmManager
import co.gorkem.namaz.util.DateTimeUtils
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerDashboardUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import co.gorkem.namaz.ui.theme.AppThemeSelection
import co.gorkem.namaz.ui.theme.AppThemeUtils
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

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
    val targetEpochMillis: Long?,
    val currentPrayerName: PrayerName?,
    val layoutPreference: Int,
    val isKerahat: Boolean = false,
    val kerahatPrayerName: String? = null,
    val kerahatRemainingMinutes: Long = 0,
    val nextKerahatStartEpochMillis: Long? = null,
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

            val updateTrigger = remember { System.currentTimeMillis() }

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
                    // En erken güncelleme zamanını bul: ya transition alarm (namaz vakti/kerahat bitişi) ya da kerahat başlangıcı
                    val nextAlarmEpoch = when {
                        loadedState.nextKerahatStartEpochMillis != null && loadedState.targetEpochMillis != null ->
                            minOf(loadedState.nextKerahatStartEpochMillis, loadedState.targetEpochMillis)
                        loadedState.nextKerahatStartEpochMillis != null -> loadedState.nextKerahatStartEpochMillis
                        else -> loadedState.targetEpochMillis
                    }
                    nextAlarmEpoch?.let { epoch ->
                        schedulePrayerTransitionAlarm(ctx, epoch)
                    }
                }
            }

            val state = widgetState
            if (state == null) {
                LoadingContent()
            } else {
                val appTheme = AppThemeSelection.values().getOrElse(
                    prefsState?.appTheme ?: 0
                ) { AppThemeSelection.EMERALD_OASIS }

                val widgetColors = AppThemeUtils.getGlanceColors(appTheme)

                GlanceTheme(colors = widgetColors) {
                    WidgetContent(state, appWidgetId, appTheme)
                }
            }
        }
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

        val location = prefs.getLocationInfo()

        val today = LocalDate.now()
        val todayKey = today.format(DateTimeUtils.DATE_FORMATTER)
        val tomorrowKey = today.plusDays(1).format(DateTimeUtils.DATE_FORMATTER)

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
                    val nextKerahatInfo = dashboard?.nextKerahatInfo

                    val targetEpoch = if (kerahatInfo?.isCurrentlyKerahat == true) {
                        val targetDateTime = LocalDateTime.of(today, kerahatInfo.endTime)
                        targetDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    } else {
                        nextInfo?.targetPrayerTimeInMillis
                    }

                    // Bir sonraki kerahat başlangıcını hesapla (kerahat aktif değilken)
                    val nextKerahatStartEpoch = if (kerahatInfo?.isCurrentlyKerahat != true && nextKerahatInfo != null) {
                        val kerahatStart = LocalDateTime.of(today, nextKerahatInfo.startTime)
                        kerahatStart.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    } else {
                        null
                    }

                    widgetState = NamazWidgetState(
                        cityName = if (location.districtName.isNotEmpty()) location.districtName else location.cityName,
                        language = language,
                        todayPrayerTimes = todayPrayer.getAllPrayers(),
                        nextPrayerName = nextPrayerName,
                        nextPrayerTime = nextInfo?.prayerTime,
                        remainingMinutes = nextInfo?.remainingMinutes,
                        targetEpochMillis = targetEpoch,
                        currentPrayerName = currentPrayerName,
                        layoutPreference = layoutPreference,
                        isKerahat = kerahatInfo?.isCurrentlyKerahat == true,
                        kerahatPrayerName = kerahatInfo?.prayerName?.name,
                        kerahatRemainingMinutes = kerahatInfo?.remainingToEndMinutes ?: 0,
                        nextKerahatStartEpochMillis = nextKerahatStartEpoch
                    )
                } else {
                    widgetState = NamazWidgetState(
                        cityName = location.cityName,
                        language = language,
                        todayPrayerTimes = emptyList(),
                        nextPrayerName = null,
                        nextPrayerTime = null,
                        remainingMinutes = null,
                        targetEpochMillis = null,
                        currentPrayerName = null,
                        layoutPreference = layoutPreference,
                        error = context.getString(R.string.error_today_times_not_found)
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
                    targetEpochMillis = null,
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
            targetEpochMillis = null,
            currentPrayerName = null,
            layoutPreference = layoutPreference,
            error = context.getString(R.string.loading)
        )
    } catch (e: Exception) {
        NamazWidgetState(
            cityName = context.getString(R.string.default_city),
            language = "tr",
            todayPrayerTimes = emptyList(),
            nextPrayerName = null,
            nextPrayerTime = null,
            remainingMinutes = null,
            targetEpochMillis = null,
            currentPrayerName = null,
            layoutPreference = layoutPreference,
            error = e.localizedMessage ?: context.getString(R.string.error_unknown)
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
private fun WidgetContent(state: NamazWidgetState, appWidgetId: Int, appTheme: AppThemeSelection) {
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

    val targetEpochMillis = state.targetEpochMillis ?: 0L

    val remainingLabel = getRemainingLabel(state.language)

    val kerahatRemainingStr = if (state.isKerahat && state.kerahatRemainingMinutes > 0) {
        formatRemainingTime(state.kerahatRemainingMinutes, state.language)
    } else {
        ""
    }

    val onSurfaceColor = GlanceTheme.colors.onSurface
    val onSurfaceVariantColor = GlanceTheme.colors.onSurfaceVariant

    val outerBackground = GlanceTheme.colors.surface

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(outerBackground)
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .padding(8.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        if (size.width < 220.dp || size.height < 140.dp || state.layoutPreference == 1) {
            CompactLayout(
                cityName = state.cityName,
                nextPrayerName = nextPrayerName,
                nextPrayerTime = nextPrayerTime,
                targetEpochMillis = targetEpochMillis,
                language = state.language,
                onSurfaceColor = onSurfaceColor,
                onSurfaceVariantColor = onSurfaceVariantColor,
                isKerahat = state.isKerahat,
                kerahatPrayerName = state.kerahatPrayerName,
                kerahatRemainingStr = kerahatRemainingStr,
                remainingLabel = remainingLabel,
                appTheme = appTheme
            )
        } else {
            ExpandedLayout(
                cityName = state.cityName,
                nextPrayerName = nextPrayerName,
                nextPrayerTime = nextPrayerTime,
                targetEpochMillis = targetEpochMillis,
                todayPrayerTimes = state.todayPrayerTimes,
                currentPrayerName = state.currentPrayerName,
                language = state.language,
                onSurfaceColor = onSurfaceColor,
                onSurfaceVariantColor = onSurfaceVariantColor,
                isKerahat = state.isKerahat,
                kerahatPrayerName = state.kerahatPrayerName,
                kerahatRemainingStr = kerahatRemainingStr,
                remainingLabel = remainingLabel,
                appTheme = appTheme
            )
        }
    }
}



internal fun getChronometerLayoutRes(appTheme: AppThemeSelection, isKerahatActive: Boolean): Int {
    if (isKerahatActive) return R.layout.widget_chronometer_kerahat
    return when (appTheme) {
        AppThemeSelection.EMERALD_OASIS -> R.layout.widget_chronometer_emerald
        AppThemeSelection.SAPPHIRE_NIGHT -> R.layout.widget_chronometer_sapphire
        AppThemeSelection.AMBER_SANDS -> R.layout.widget_chronometer_amber
        AppThemeSelection.MONOCHROME_PLATINUM -> R.layout.widget_chronometer_monochrome
        AppThemeSelection.YUMUSAK_AYDINLIK -> R.layout.widget_chronometer_yumusak
        AppThemeSelection.TAM_AYDINLIK -> R.layout.widget_chronometer_tam
        AppThemeSelection.SAMAN_KAGIDI -> R.layout.widget_chronometer_saman
        AppThemeSelection.KARANLIK -> R.layout.widget_chronometer_karanlik
        AppThemeSelection.GALAKSI -> R.layout.widget_chronometer_galaksi
        AppThemeSelection.GUN_BATIMI -> R.layout.widget_chronometer_gun_batimi
        AppThemeSelection.AMBER_GECE -> R.layout.widget_chronometer_amber_gece
        else -> R.layout.widget_chronometer_emerald
    }
}

@Composable
private fun CompactLayout(
    cityName: String,
    nextPrayerName: PrayerName,
    nextPrayerTime: String,
    targetEpochMillis: Long,
    language: String,
    onSurfaceColor: ColorProvider,
    onSurfaceVariantColor: ColorProvider,
    isKerahat: Boolean = false,
    kerahatPrayerName: String? = null,
    kerahatRemainingStr: String = "",
    remainingLabel: String = "",
    appTheme: AppThemeSelection
) {
    val context = LocalContext.current
    val size = LocalSize.current
    val localizedPrayerName = getLocalizedString(context, nextPrayerName.nameRes, language)

    val isKerahatActive = isKerahat && kerahatPrayerName != null
    val cardBg = if (isKerahatActive) GlanceTheme.colors.errorContainer else GlanceTheme.colors.secondaryContainer
    val cardOnBg = if (isKerahatActive) GlanceTheme.colors.onErrorContainer else GlanceTheme.colors.onSecondaryContainer
    val badgeBg = if (isKerahatActive) GlanceTheme.colors.error else GlanceTheme.colors.primary
    val stackVertically = size.height >= 120.dp || size.width < 220.dp

    val kerahatLabel = if (isKerahatActive) {
        mapKerahatPrayerName(context, kerahatPrayerName!!, language)
    } else {
        ""
    }

    val chronometerFormat = if (isKerahatActive) {
        if (language == "en") "%s Kerahat ends" else "%s Kerahat bitiyor"
    } else {
        if (language == "en") "%s left" else "%s kaldı"
    }

    val chronometerRemoteViews = remember(targetEpochMillis, appTheme, isKerahatActive, chronometerFormat) {
        val layoutRes = getChronometerLayoutRes(appTheme, isKerahatActive)
        val rv = RemoteViews(context.packageName, layoutRes)
        val nowElapsed = SystemClock.elapsedRealtime()
        val nowWall = System.currentTimeMillis()
        val remainingWall = maxOf(targetEpochMillis - nowWall, 0)
        val targetElapsed = nowElapsed + remainingWall
        rv.setChronometer(R.id.widget_chronometer, targetElapsed, chronometerFormat, true)
        rv.setChronometerCountDown(R.id.widget_chronometer, true)
        rv
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
                isKerahatActive && stackVertically -> cityName
                isKerahatActive -> "${context.getString(R.string.kerahat_vakti)} • $cityName"
                stackVertically -> cityName
                else -> "${context.getString(R.string.home_title)} • $cityName"
            }
            Text(
                text = headerText,
                style = TextStyle(
                    color = if (isKerahatActive) GlanceTheme.colors.error else onSurfaceVariantColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                modifier = GlanceModifier.defaultWeight()
            )
            Image(
                provider = ImageProvider(R.drawable.ic_widget_refresh),
                contentDescription = context.getString(R.string.refresh),
                modifier = GlanceModifier
                    .size(20.dp)
                    .clickable(actionRunCallback<RefreshActionCallback>()),
                colorFilter = ColorFilter.tint(onSurfaceVariantColor)
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        if (stackVertically) {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .background(cardBg)
                    .cornerRadius(16.dp)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isKerahatActive) {
                        if (language == "en") "$kerahatLabel Kerahat" else "$kerahatLabel Kerahatı"
                    } else localizedPrayerName,
                    style = TextStyle(
                        color = cardOnBg,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.height(6.dp))
                Text(
                    text = nextPrayerTime,
                    style = TextStyle(
                        color = cardOnBg,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.height(12.dp))
                Box(
                    modifier = GlanceModifier
                        .background(badgeBg)
                        .cornerRadius(16.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidRemoteViews(chronometerRemoteViews)
                }
            }
        } else {
            Column(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .defaultWeight()
                    .background(cardBg)
                    .cornerRadius(16.dp)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = GlanceModifier.defaultWeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = if (isKerahatActive) {
                                if (language == "en") "$kerahatLabel Kerahat" else "$kerahatLabel Kerahatı"
                            } else localizedPrayerName,
                            style = TextStyle(
                                color = cardOnBg,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Text(
                            text = nextPrayerTime,
                            style = TextStyle(
                                color = cardOnBg,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1
                        )
                    }

                    Box(
                        modifier = GlanceModifier
                            .background(badgeBg)
                            .cornerRadius(16.dp)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AndroidRemoteViews(chronometerRemoteViews)
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
    targetEpochMillis: Long,
    todayPrayerTimes: List<Pair<PrayerName, String>>,
    currentPrayerName: PrayerName?,
    language: String,
    onSurfaceColor: ColorProvider,
    onSurfaceVariantColor: ColorProvider,
    isKerahat: Boolean = false,
    kerahatPrayerName: String? = null,
    kerahatRemainingStr: String = "",
    remainingLabel: String = "",
    appTheme: AppThemeSelection
) {
    val context = LocalContext.current
    val localizedPrayerName = getLocalizedString(context, nextPrayerName.nameRes, language)
    val isKerahatActive = isKerahat && kerahatPrayerName != null

    val kerahatLabel = if (isKerahatActive) {
        mapKerahatPrayerName(context, kerahatPrayerName!!, language)
    } else {
        ""
    }

    val cardBg = if (isKerahatActive) GlanceTheme.colors.errorContainer else GlanceTheme.colors.secondaryContainer
    val cardOnBg = if (isKerahatActive) GlanceTheme.colors.onErrorContainer else GlanceTheme.colors.onSecondaryContainer
    val badgeBg = if (isKerahatActive) GlanceTheme.colors.error else GlanceTheme.colors.primary

    val chronometerFormat = if (isKerahatActive) {
        if (language == "en") "%s Kerahat ends" else "%s Kerahat bitiyor"
    } else {
        if (language == "en") "%s left" else "%s kaldı"
    }

    val chronometerRemoteViews = remember(targetEpochMillis, appTheme, isKerahatActive, chronometerFormat) {
        val layoutRes = getChronometerLayoutRes(appTheme, isKerahatActive)
        val rv = RemoteViews(context.packageName, layoutRes)
        val nowElapsed = SystemClock.elapsedRealtime()
        val nowWall = System.currentTimeMillis()
        val remainingWall = maxOf(targetEpochMillis - nowWall, 0)
        val targetElapsed = nowElapsed + remainingWall
        rv.setChronometer(R.id.widget_chronometer, targetElapsed, chronometerFormat, true)
        rv.setChronometerCountDown(R.id.widget_chronometer, true)
        rv
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
            val headerText = if (isKerahatActive) "${context.getString(R.string.kerahat_vakti)} • $cityName" else "${context.getString(R.string.home_title)} • $cityName"
            Text(
                text = headerText,
                style = TextStyle(
                    color = if (isKerahatActive) GlanceTheme.colors.error else onSurfaceVariantColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = 1,
                modifier = GlanceModifier.defaultWeight()
            )
            Image(
                provider = ImageProvider(R.drawable.ic_widget_refresh),
                contentDescription = context.getString(R.string.refresh),
                modifier = GlanceModifier
                    .size(20.dp)
                    .clickable(actionRunCallback<RefreshActionCallback>()),
                colorFilter = ColorFilter.tint(onSurfaceVariantColor)
            )
        }

        Spacer(modifier = GlanceModifier.height(4.dp))

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .defaultWeight()
                .background(cardBg)
                .cornerRadius(16.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier.defaultWeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = if (isKerahatActive) {
                        if (language == "en") "$kerahatLabel Kerahat" else "$kerahatLabel Kerahatı"
                    } else localizedPrayerName,
                    style = TextStyle(
                        color = cardOnBg,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = nextPrayerTime,
                    style = TextStyle(
                        color = cardOnBg,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
            }

            Box(
                modifier = GlanceModifier
                    .background(badgeBg)
                    .cornerRadius(16.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                AndroidRemoteViews(chronometerRemoteViews)
            }
        }

        Spacer(modifier = GlanceModifier.height(6.dp))

        Row(
            modifier = GlanceModifier.fillMaxWidth().defaultWeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val now = java.time.LocalTime.now()
            val formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")

            todayPrayerTimes.forEachIndexed { index, (name, time) ->
                val isCurrent = name == currentPrayerName
                val localizedName = getLocalizedString(context, name.nameRes, language)

                val prayerTime = try {
                    java.time.LocalTime.parse(time, formatter)
                } catch (e: Exception) {
                    java.time.LocalTime.MAX
                }
                val isPassed = !isCurrent && now.isAfter(prayerTime)

                val fadedTextColor = GlanceTheme.colors.outline
                val fadedLabelColor = GlanceTheme.colors.outline

                val textColor = if (isCurrent) GlanceTheme.colors.primary else if (isPassed) fadedTextColor else onSurfaceColor
                val labelColor = if (isCurrent) GlanceTheme.colors.primary else if (isPassed) fadedLabelColor else onSurfaceVariantColor
                val iconColor = if (isCurrent) GlanceTheme.colors.primary else if (isPassed) fadedLabelColor else onSurfaceVariantColor
                val textWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal

                val iconRes = when (name) {
                    PrayerName.IMSAK -> R.drawable.ic_prayer_imsak
                    PrayerName.GUNES -> R.drawable.ic_prayer_gunes
                    PrayerName.OGLE -> R.drawable.ic_prayer_ogle
                    PrayerName.IKINDI -> R.drawable.ic_prayer_ikindi
                    PrayerName.AKSAM -> R.drawable.ic_prayer_aksam
                    PrayerName.YATSI -> R.drawable.ic_prayer_yatsi
                }

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
                            fontSize = 11.sp,
                            fontWeight = textWeight
                        ),
                        maxLines = 1
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Image(
                        provider = ImageProvider(iconRes),
                        contentDescription = localizedName,
                        modifier = GlanceModifier.width(22.dp).height(22.dp),
                        colorFilter = ColorFilter.tint(iconColor)
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = time,
                        style = TextStyle(
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = textWeight
                        ),
                        maxLines = 1
                    )
                    if (isCurrent) {
                        Spacer(modifier = GlanceModifier.height(4.dp))
                        Box(
                            modifier = GlanceModifier
                                .width(16.dp)
                                .height(3.dp)
                                .background(iconColor)
                                .cornerRadius(1.5.dp)
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

private fun schedulePrayerTransitionAlarm(context: Context, targetEpochMillis: Long) {
    val triggerTime = targetEpochMillis + 1500 // Add a 1.5-second buffer to ensure the transition time has safely passed
    if (triggerTime <= System.currentTimeMillis()) return

    val alarmManager = context.alarmManager ?: return

    val intent = Intent(context, NamazWidgetReceiver::class.java).apply {
        action = NamazWidgetReceiver.ACTION_PRAYER_TIME_TRANSITION
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        9002,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Safely cancel the previous transition alarm
    alarmManager.cancel(pendingIntent)

    val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        try {
            alarmManager.canScheduleExactAlarms()
        } catch (e: Exception) {
            false
        }
    } else {
        true
    }

    try {
        if (canScheduleExact) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        }
    } catch (e: SecurityException) {
        Log.e("NamazWidget", "SecurityException scheduling exact alarm, falling back to inexact", e)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e2: Exception) {
            Log.e("NamazWidget", "Failed to schedule inexact fallback alarm", e2)
        }
    } catch (e: Exception) {
        Log.e("NamazWidget", "Failed to schedule transition alarm", e)
    }
}

class RefreshActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        LocationSyncWorker.executeNow(context)
    }
}


