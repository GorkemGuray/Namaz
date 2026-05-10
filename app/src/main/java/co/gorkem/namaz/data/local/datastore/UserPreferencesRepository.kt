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

data class RecentCity(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val notificationMinutesBefore: Int = 15,
    val calculationMethod: Int = 13,
    val school: Int = 1,
    val language: String = "tr",
    val manualCityName: String = "",
    val manualLatitude: Double = 0.0,
    val manualLongitude: Double = 0.0,
    val recentCities: List<RecentCity> = emptyList()
)

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
        val LANGUAGE = stringPreferencesKey("language")
        val MANUAL_CITY_NAME = stringPreferencesKey("manual_city_name")
        val MANUAL_LATITUDE = stringPreferencesKey("manual_latitude")
        val MANUAL_LONGITUDE = stringPreferencesKey("manual_longitude")
        val RECENT_CITIES = stringPreferencesKey("recent_cities")
    }

    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        val recentCitiesJson = prefs[RECENT_CITIES] ?: "[]"
        val recentCities: List<RecentCity> = try {
            val type = object : TypeToken<List<RecentCity>>() {}.type
            gson.fromJson(recentCitiesJson, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        UserPreferences(
            notificationsEnabled = prefs[NOTIFICATIONS_ENABLED] ?: true,
            notificationMinutesBefore = prefs[NOTIFICATION_MINUTES_BEFORE] ?: 15,
            calculationMethod = prefs[CALCULATION_METHOD] ?: 13,
            school = prefs[SCHOOL] ?: 1,
            language = prefs[LANGUAGE] ?: "tr",
            manualCityName = prefs[MANUAL_CITY_NAME] ?: "",
            manualLatitude = (prefs[MANUAL_LATITUDE] ?: "0.0").toDouble(),
            manualLongitude = (prefs[MANUAL_LONGITUDE] ?: "0.0").toDouble(),
            recentCities = recentCities
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
    }

    suspend fun setSchool(school: Int) {
        dataStore.edit { prefs -> prefs[SCHOOL] = school }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { prefs -> prefs[LANGUAGE] = language }
    }

    suspend fun setManualCity(
        name: String,
        latitude: Double,
        longitude: Double
    ) {
        dataStore.edit { prefs ->
            prefs[MANUAL_CITY_NAME] = name
            prefs[MANUAL_LATITUDE] = latitude.toString()
            prefs[MANUAL_LONGITUDE] = longitude.toString()
        }
        addRecentCity(RecentCity(name, latitude, longitude))
    }

    suspend fun clearManualCity() {
        dataStore.edit { prefs ->
            prefs.remove(MANUAL_CITY_NAME)
            prefs.remove(MANUAL_LATITUDE)
            prefs.remove(MANUAL_LONGITUDE)
        }
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

            // Remove duplicate if exists, add to front, keep max 5
            val updated = listOf(city) + current.filter {
                it.name != city.name
            }.take(4)

            prefs[RECENT_CITIES] = gson.toJson(updated)
        }
    }

    suspend fun clearRecentCities() {
        dataStore.edit { prefs -> prefs[RECENT_CITIES] = "[]" }
    }
}
