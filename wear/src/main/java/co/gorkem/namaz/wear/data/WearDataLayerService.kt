package co.gorkem.namaz.wear.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject
import javax.inject.Singleton

val Context.wearPrayerDataStore: DataStore<Preferences> by preferencesDataStore(name = "wear_prayer_prefs")

@Singleton
class WearDataLayerService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataClient: DataClient = Wearable.getDataClient(context)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val PRAYER_DATA_KEY = stringPreferencesKey("prayer_data_json")

    private val _prayerData = MutableStateFlow<WearPrayerData?>(null)
    val prayerData: StateFlow<WearPrayerData?> = _prayerData.asStateFlow()

    private val _isPhoneConnected = MutableStateFlow(false)
    val isPhoneConnected: StateFlow<Boolean> = _isPhoneConnected.asStateFlow()

    private val dataListener = DataClient.OnDataChangedListener { events ->
        updateFromDataItems(events)
    }

    companion object {
        fun parsePrayerData(json: String): WearPrayerData? {
            return try {
                WearPrayerData.fromJson(json)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun startListening() {
        dataClient.addListener(dataListener)
        
        scope.launch {
            try {
                context.wearPrayerDataStore.data.first().let { prefs ->
                    val cachedJson = prefs[PRAYER_DATA_KEY]
                    if (cachedJson != null) {
                        val data = parsePrayerData(cachedJson)
                        if (data != null) {
                            _prayerData.value = data
                        }
                    }
                }
            } catch (e: Exception) {
            }
        }

        dataClient.dataItems.addOnSuccessListener { items ->
            for (item in items) {
                if (item.uri.path == "/namaz_prayer_data") {
                    val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
                    val json = dataMap.getString("prayer_json") ?: continue
                    val data = parsePrayerData(json)
                    if (data != null) {
                        _prayerData.value = data
                        _isPhoneConnected.value = true
                        savePrayerDataToDisk(json)
                        requestComplicationUpdate()
                    }
                }
            }
        }
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
                    val data = parsePrayerData(json)
                    if (data != null) {
                        _prayerData.value = data
                        _isPhoneConnected.value = true
                        savePrayerDataToDisk(json)
                        requestComplicationUpdate()
                    }
                }
            }
        }
        events.release()
    }

    private fun savePrayerDataToDisk(json: String) {
        scope.launch {
            try {
                context.wearPrayerDataStore.edit { prefs ->
                    prefs[PRAYER_DATA_KEY] = json
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun requestComplicationUpdate() {
        try {
            val requester1 = androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester.create(
                context = context,
                complicationDataSourceComponent = android.content.ComponentName(context, co.gorkem.namaz.wear.complication.PrayerComplicationService::class.java)
            )
            requester1.requestUpdateAll()
            
            val requester2 = androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester.create(
                context = context,
                complicationDataSourceComponent = android.content.ComponentName(context, co.gorkem.namaz.wear.complication.PrayerCountdownComplicationService::class.java)
            )
            requester2.requestUpdateAll()
        } catch (e: Exception) {
            // Ignore
        }
    }

    suspend fun getLatestPrayerData(): WearPrayerData? {
        val current = _prayerData.value
        if (current != null) return current

        try {
            val prefs = context.wearPrayerDataStore.data.first()
            val cachedJson = prefs[PRAYER_DATA_KEY]
            if (cachedJson != null) {
                val data = parsePrayerData(cachedJson)
                if (data != null) {
                    _prayerData.value = data
                    return data
                }
            }
        } catch (e: Exception) {
        }

        return suspendCancellableCoroutine { cont ->
            dataClient.dataItems.addOnSuccessListener { items ->
                for (item in items) {
                    if (item.uri.path == "/namaz_prayer_data") {
                        val dataMap = com.google.android.gms.wearable.DataMapItem.fromDataItem(item).dataMap
                        val json = dataMap.getString("prayer_json") ?: continue
                        val data = parsePrayerData(json)
                        if (data != null) {
                            _prayerData.value = data
                            _isPhoneConnected.value = true
                            savePrayerDataToDisk(json)
                            if (cont.isActive) cont.resume(data)
                            return@addOnSuccessListener
                        }
                    }
                }
                if (cont.isActive) cont.resume(null)
            }.addOnFailureListener {
                if (cont.isActive) cont.resume(null)
            }
        }
    }
}
