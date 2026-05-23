package co.gorkem.namaz.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import co.gorkem.namaz.service.NamazWidgetUpdater
import co.gorkem.namaz.domain.model.LocationInfo


data class RecentCity(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val districtId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val notificationMinutesBefore: Int = 15,
    val calculationMethod: Int = 13,
    val school: Int = 0,
    val useLocalCalculation: Boolean = false,
    val dataSource: Int = 0,
    val language: String = "tr",
    val appTheme: Int = 0,
    val manualCityName: String = "",
    val manualDistrictName: String = "",
    val manualDistrictId: String = "",
    val manualLatitude: Double = 0.0,
    val manualLongitude: Double = 0.0,
    val recentCities: List<RecentCity> = emptyList(),
    val lastAutoLatitude: Double = 0.0,
    val lastAutoLongitude: Double = 0.0,
    val lastAutoCityName: String = "",
    val lastAutoDistrictName: String = "",
    val lastAutoDistrictId: String = "",
    val lastAutoTimezone: String = "Europe/Istanbul",
    val manualTimezone: String = "Europe/Istanbul"
)

fun UserPreferences.getLocationInfo(): LocationInfo {
    return if (manualCityName.isNotEmpty()) {
        LocationInfo(
            latitude = manualLatitude,
            longitude = manualLongitude,
            timezone = manualTimezone.ifEmpty { "Europe/Istanbul" },
            cityName = manualCityName,
            districtName = manualDistrictName,
            districtId = manualDistrictId
        )
    } else if (lastAutoCityName.isNotEmpty()) {
        LocationInfo(
            latitude = lastAutoLatitude,
            longitude = lastAutoLongitude,
            timezone = lastAutoTimezone.ifEmpty { "Europe/Istanbul" },
            cityName = lastAutoCityName,
            districtName = lastAutoDistrictName,
            districtId = lastAutoDistrictId
        )
    } else {
        LocationInfo.DEFAULT
    }
}

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.userPreferencesDataStore
    private val gson = Gson()

    companion object Keys {
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_MINUTES_BEFORE = intPreferencesKey("notification_minutes_before")
        val CALCULATION_METHOD = intPreferencesKey("calculation_method")
        val SCHOOL = intPreferencesKey("school")
        val USE_LOCAL_CALCULATION = booleanPreferencesKey("use_local_calculation")
        val DATA_SOURCE = intPreferencesKey("data_source")
        val LANGUAGE = stringPreferencesKey("language")
        val MANUAL_CITY_NAME = stringPreferencesKey("manual_city_name")
        val MANUAL_DISTRICT_NAME = stringPreferencesKey("manual_district_name")
        val MANUAL_DISTRICT_ID = stringPreferencesKey("manual_district_id")
        val MANUAL_LATITUDE = stringPreferencesKey("manual_latitude")
        val MANUAL_LONGITUDE = stringPreferencesKey("manual_longitude")
        val RECENT_CITIES = stringPreferencesKey("recent_cities")
        val LAST_AUTO_LATITUDE = stringPreferencesKey("last_auto_latitude")
        val LAST_AUTO_LONGITUDE = stringPreferencesKey("last_auto_longitude")
        val LAST_AUTO_CITY_NAME = stringPreferencesKey("last_auto_city_name")
        val LAST_AUTO_DISTRICT_NAME = stringPreferencesKey("last_auto_district_name")
        val LAST_AUTO_DISTRICT_ID = stringPreferencesKey("last_auto_district_id")
        val LAST_AUTO_TIMEZONE = stringPreferencesKey("last_auto_timezone")
        val MANUAL_TIMEZONE = stringPreferencesKey("manual_timezone")
        val APP_THEME = intPreferencesKey("app_theme")
    }

    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        val recentCitiesJson = prefs[RECENT_CITIES] ?: "[]"
        val recentCities: List<RecentCity> = try {
            val type = object : TypeToken<List<RecentCity>>() {}.type
            val cities = gson.fromJson<List<RecentCity>>(recentCitiesJson, type) ?: emptyList()
            cities.map { it.copy(districtId = it.districtId ?: "") }
        } catch (_: Exception) {
            emptyList()
        }

        UserPreferences(
            notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true,
            notificationMinutesBefore = prefs[NOTIFICATION_MINUTES_BEFORE] ?: 15,
            calculationMethod = prefs[CALCULATION_METHOD] ?: 13,
            school = prefs[SCHOOL] ?: 0,
            useLocalCalculation = prefs[USE_LOCAL_CALCULATION] ?: false,
            dataSource = prefs[DATA_SOURCE] ?: 0,
            language = prefs[LANGUAGE] ?: "tr",
            appTheme = prefs[APP_THEME] ?: 0,
            manualCityName = prefs[MANUAL_CITY_NAME] ?: "",
            manualDistrictName = prefs[MANUAL_DISTRICT_NAME] ?: "",
            manualDistrictId = prefs[MANUAL_DISTRICT_ID] ?: "",
            manualLatitude = (prefs[MANUAL_LATITUDE] ?: "0.0").toDouble(),
            manualLongitude = (prefs[MANUAL_LONGITUDE] ?: "0.0").toDouble(),
            recentCities = recentCities,
            lastAutoLatitude = (prefs[LAST_AUTO_LATITUDE] ?: "0.0").toDouble(),
            lastAutoLongitude = (prefs[LAST_AUTO_LONGITUDE] ?: "0.0").toDouble(),
            lastAutoCityName = prefs[LAST_AUTO_CITY_NAME] ?: "",
            lastAutoDistrictName = prefs[LAST_AUTO_DISTRICT_NAME] ?: "",
            lastAutoDistrictId = prefs[LAST_AUTO_DISTRICT_ID] ?: "",
            lastAutoTimezone = prefs[LAST_AUTO_TIMEZONE] ?: "Europe/Istanbul",
            manualTimezone = prefs[MANUAL_TIMEZONE] ?: "Europe/Istanbul"
        )
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setNotificationMinutesBefore(minutes: Int) {
        dataStore.edit { prefs -> prefs[NOTIFICATION_MINUTES_BEFORE] = minutes }
    }

    suspend fun setCalculationMethod(method: Int) {
        dataStore.edit { prefs -> prefs[CALCULATION_METHOD] = method }
        updateWidget()
    }

    suspend fun setSchool(school: Int) {
        dataStore.edit { prefs -> prefs[SCHOOL] = school }
        updateWidget()
    }

    suspend fun setUseLocalCalculation(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[USE_LOCAL_CALCULATION] = enabled
            if (enabled) {
                prefs[DATA_SOURCE] = 2
            } else if ((prefs[DATA_SOURCE] ?: 0) == 2) {
                prefs[DATA_SOURCE] = 0
            }
        }
        updateWidget()
    }

    suspend fun setDataSource(source: Int) {
        dataStore.edit { prefs ->
            prefs[DATA_SOURCE] = source
            prefs[USE_LOCAL_CALCULATION] = (source == 2)
        }
        updateWidget()
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { prefs -> prefs[LANGUAGE] = language }
        updateWidget()
    }

    suspend fun setAppTheme(theme: Int) {
        dataStore.edit { prefs -> prefs[APP_THEME] = theme }
        updateWidget()
    }

    suspend fun setManualCity(
        name: String,
        districtName: String = "",
        latitude: Double,
        longitude: Double,
        districtId: String = ""
    ) {
        dataStore.edit { prefs ->
            prefs[MANUAL_CITY_NAME] = name
            prefs[MANUAL_DISTRICT_NAME] = districtName
            prefs[MANUAL_DISTRICT_ID] = districtId
            prefs[MANUAL_LATITUDE] = latitude.toString()
            prefs[MANUAL_LONGITUDE] = longitude.toString()
        }
        addRecentCity(RecentCity(name, latitude, longitude, districtId))
        updateWidget()
    }

    suspend fun clearManualCity() {
        dataStore.edit { prefs ->
            prefs.remove(MANUAL_CITY_NAME)
            prefs.remove(MANUAL_DISTRICT_NAME)
            prefs.remove(MANUAL_DISTRICT_ID)
            prefs.remove(MANUAL_LATITUDE)
            prefs.remove(MANUAL_LONGITUDE)
        }
        updateWidget()
    }

    private suspend fun addRecentCity(city: RecentCity) {
        dataStore.edit { prefs ->
            val currentJson = prefs[RECENT_CITIES] ?: "[]"
            val current: List<RecentCity> = try {
                val type = object : TypeToken<List<RecentCity>>() {}.type
                gson.fromJson(currentJson, type) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }

            val updated = listOf(city) + current.filter {
                it.name != city.name
            }.take(4)

            prefs[RECENT_CITIES] = gson.toJson(updated)
        }
    }

    suspend fun clearRecentCities() {
        dataStore.edit { prefs -> prefs[RECENT_CITIES] = "[]" }
    }

    suspend fun setLastAutoLocation(
        name: String,
        districtName: String = "",
        latitude: Double,
        longitude: Double,
        districtId: String,
        timezone: String
    ) {
        dataStore.edit { prefs ->
            prefs[LAST_AUTO_CITY_NAME] = name
            prefs[LAST_AUTO_DISTRICT_NAME] = districtName
            prefs[LAST_AUTO_LATITUDE] = latitude.toString()
            prefs[LAST_AUTO_LONGITUDE] = longitude.toString()
            prefs[LAST_AUTO_DISTRICT_ID] = districtId
            prefs[LAST_AUTO_TIMEZONE] = timezone
        }
        updateWidget()
    }

    suspend fun updateManualTimezone(timezone: String) {
        dataStore.edit { prefs ->
            prefs[MANUAL_TIMEZONE] = timezone
        }
        updateWidget()
    }

    private fun updateWidget() {
        NamazWidgetUpdater.update(context)
    }
}
