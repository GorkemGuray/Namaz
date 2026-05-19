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

    companion object {
        const val SYNC_PATH = "/namaz_prayer_data"
        const val KEY_PRAYER_JSON = "prayer_json"
        const val KEY_TIMESTAMP = "timestamp"
    }
}
