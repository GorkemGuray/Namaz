package co.gorkem.namaz.service

import android.content.Context
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import co.gorkem.namaz.MainActivity
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.ui.theme.AppThemeSelection

internal fun getCurrentPrayer(nextPrayerName: PrayerName): PrayerName {
    val list = PrayerName.values()
    val index = list.indexOf(nextPrayerName)
    return if (index == 0) {
        list.last()
    } else {
        list[index - 1]
    }
}

internal fun formatRemainingTime(minutes: Long, language: String): String {
    val hrs = minutes / 60
    val mins = minutes % 60
    return if (language == "en") {
        if (hrs > 0) "${hrs}h ${mins}m" else "${mins}m"
    } else {
        if (hrs > 0) "${hrs} sa ${mins} dk" else "${mins} dk"
    }
}

internal fun getRemainingLabel(language: String): String {
    return if (language == "en") "left" else "kaldı"
}

internal fun getLocalizedString(context: Context, resId: Int, language: String): String {
    val locale = java.util.Locale.forLanguageTag(language)
    val config = android.content.res.Configuration(context.resources.configuration)
    config.setLocale(locale)
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.getString(resId)
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
    }
}

enum class WidgetLayoutType {
    COMPACT,
    EXPANDED
}

object WidgetLayoutResolver {
    private data class LayoutRecord(
        var activeLayout: WidgetLayoutType,
        var pendingBucket: WidgetLayoutType? = null
    )

    private val layoutCache = java.util.concurrent.ConcurrentHashMap<Int, LayoutRecord>()

    fun clearMemoryCache() {
        layoutCache.clear()
    }

    fun clear(appWidgetId: Int) {
        layoutCache.remove(appWidgetId)
    }

    fun setActiveLayoutForTesting(appWidgetId: Int, layout: WidgetLayoutType) {
        layoutCache[appWidgetId] = LayoutRecord(activeLayout = layout, pendingBucket = null)
    }

    @Synchronized
    fun resolve(
        appWidgetId: Int,
        layoutPreference: Int,
        width: androidx.compose.ui.unit.Dp,
        height: androidx.compose.ui.unit.Dp
    ): WidgetLayoutType {
        if (layoutPreference == NamazWidgetPrefs.LAYOUT_COMPACT) {
            layoutCache.remove(appWidgetId)
            return WidgetLayoutType.COMPACT
        }
        if (layoutPreference == NamazWidgetPrefs.LAYOUT_EXPANDED) {
            layoutCache.remove(appWidgetId)
            return WidgetLayoutType.EXPANDED
        }

        val currentBucket = if (width < 220.dp || height < 140.dp) {
            WidgetLayoutType.COMPACT
        } else {
            WidgetLayoutType.EXPANDED
        }

        val record = layoutCache[appWidgetId]
        if (record == null) {
            layoutCache[appWidgetId] = LayoutRecord(activeLayout = currentBucket, pendingBucket = null)
            return currentBucket
        }

        if (currentBucket == record.activeLayout) {
            record.pendingBucket = null
            return record.activeLayout
        }

        if (record.pendingBucket == currentBucket) {
            record.activeLayout = currentBucket
            record.pendingBucket = null
            return record.activeLayout
        } else {
            record.pendingBucket = currentBucket
            return record.activeLayout
        }
    }
}

internal fun resolveWidgetLayout(
    appWidgetId: Int,
    layoutPreference: Int,
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp
): WidgetLayoutType = WidgetLayoutResolver.resolve(appWidgetId, layoutPreference, width, height)

@Composable
internal fun WidgetContent(state: NamazWidgetState, appWidgetId: Int, appTheme: AppThemeSelection) {
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
        val layoutType = resolveWidgetLayout(
            appWidgetId = appWidgetId,
            layoutPreference = state.layoutPreference,
            width = size.width,
            height = size.height
        )

        if (layoutType == WidgetLayoutType.COMPACT) {
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

@Composable
internal fun CompactLayout(
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
internal fun ExpandedLayout(
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
                } catch (_: Exception) {
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
internal fun ErrorContent(error: String) {
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
internal fun LoadingContent() {
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
