package co.gorkem.namaz.wear.data

import android.content.Context
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearDataLayerService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataClient: DataClient = Wearable.getDataClient(context)

    private val _prayerData = MutableStateFlow<WearPrayerData?>(null)
    val prayerData: StateFlow<WearPrayerData?> = _prayerData.asStateFlow()

    private val _isPhoneConnected = MutableStateFlow(false)
    val isPhoneConnected: StateFlow<Boolean> = _isPhoneConnected.asStateFlow()

    private val dataListener = DataClient.OnDataChangedListener { events ->
        updateFromDataItems(events)
    }

    fun startListening() {
        dataClient.addListener(dataListener)
    }

    fun stopListening() {
        dataClient.removeListener(dataListener)
    }

    private fun updateFromDataItems(events: DataEventBuffer) {
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/namaz_prayer_data") {
                    val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(dataItem).dataMap
                    val json = dataMap.getString("prayer_json") ?: continue

                    try {
                        val data = WearNextPrayer.fromJson(json)
                        _prayerData.value = data
                        _isPhoneConnected.value = true
                    } catch (e: Exception) {
                        // Invalid data
                    }
                }
            }
        }
        events.release()
    }
}
