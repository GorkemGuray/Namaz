package co.gorkem.namaz.service

import android.content.Context
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import co.gorkem.namaz.domain.model.LocationInfo
import co.gorkem.namaz.domain.model.PrayerTime
import co.gorkem.namaz.domain.usecase.GetNextPrayerUseCase
import co.gorkem.namaz.domain.usecase.GetPrayerDashboardUseCase
import co.gorkem.namaz.util.DateTimeUtils
import java.time.LocalDate

@Singleton
class WearSyncManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataClient: DataClient by lazy { Wearable.getDataClient(context) }

    suspend fun syncPrayerData(data: WearPrayerData) {
        return withContext(Dispatchers.IO) {
            try {
                val putDataRequest = PutDataMapRequest.create(SYNC_PATH).apply {
                    dataMap.putString(KEY_PRAYER_JSON, WearPrayerData.toJson(data))
                    dataMap.putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                }.setUrgent()
                    .asPutDataRequest()

                Tasks.await(dataClient.putDataItem(putDataRequest))
            } catch (_: Exception) {
                // No connected wear device — silently ignore
            }
        }
    }

    suspend fun syncData(
        prayerTimes: List<PrayerTime>,
        location: LocationInfo,
        themeId: Int,
        getNextPrayerUseCase: GetNextPrayerUseCase,
        getPrayerDashboardUseCase: GetPrayerDashboardUseCase
    ) {
        val today = LocalDate.now()
        val todayData = prayerTimes.find { it.date == today.format(DateTimeUtils.DATE_FORMATTER) } ?: return
        
        val localizedContext = co.gorkem.namaz.LocaleManager.applyLocale(context)
        
        val nextPrayerInfo = getNextPrayerUseCase(todayData)
        val tomorrow = prayerTimes.find { it.date == today.plusDays(1).format(DateTimeUtils.DATE_FORMATTER) }
        val dashboard = getPrayerDashboardUseCase.execute(todayData, tomorrow)
        
        val kerahatInfo = dashboard?.kerahatInfo
        val wearKerahat = kerahatInfo?.let { k ->
            WearKerahatInfo(
                isCurrentlyKerahat = k.isCurrentlyKerahat,
                prayerName = localizedContext.getString(k.prayerName.nameRes),
                remainingMinutes = if (k.isCurrentlyKerahat) k.remainingToEndMinutes else k.remainingToStartMinutes
            )
        }

        val wearData = WearPrayerData(
            cityName = location.cityName,
            prayers = todayData.getAllPrayers().map { (name, time) ->
                WearPrayerItem(
                    name = localizedContext.getString(name.nameRes),
                    time = time
                )
            },
            nextPrayer = nextPrayerInfo?.let { nextPrayer ->
                WearNextPrayer(
                    name = localizedContext.getString(nextPrayer.prayerName.nameRes),
                    time = nextPrayer.prayerTime,
                    remainingMinutes = nextPrayer.remainingMinutes,
                    targetTimeMillis = nextPrayer.targetPrayerTimeInMillis
                )
            },
            kerahat = wearKerahat,
            themeId = themeId,
            latitude = location.latitude,
            longitude = location.longitude
        )
        syncPrayerData(wearData)
    }

    companion object {
        const val SYNC_PATH = "/namaz_prayer_data"
        const val KEY_PRAYER_JSON = "prayer_json"
        const val KEY_TIMESTAMP = "timestamp"
    }
}
