package co.gorkem.namaz.service

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.provideContent
import co.gorkem.namaz.R
import co.gorkem.namaz.data.local.datastore.UserPreferences
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.data.local.datastore.getLocationInfo
import co.gorkem.namaz.data.repository.PrayerTimeRepositoryImpl
import co.gorkem.namaz.domain.model.NetworkResult
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerDashboardUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerTimesUseCase
import co.gorkem.namaz.ui.theme.AppThemeSelection
import co.gorkem.namaz.ui.theme.AppThemeUtils
import co.gorkem.namaz.util.DateTimeUtils
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
                    val nextAlarmEpoch = when {
                        loadedState.nextKerahatStartEpochMillis != null && loadedState.targetEpochMillis != null ->
                            minOf(loadedState.nextKerahatStartEpochMillis, loadedState.targetEpochMillis)
                        loadedState.nextKerahatStartEpochMillis != null -> loadedState.nextKerahatStartEpochMillis
                        else -> loadedState.targetEpochMillis
                    }
                    nextAlarmEpoch?.let { epoch ->
                        WidgetAlarm.schedulePrayerTransitionAlarm(ctx, epoch)
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

class RefreshActionCallback : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        LocationSyncWorker.executeNow(context)
    }
}
