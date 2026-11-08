package co.gorkem.namaz.domain.repository

import co.gorkem.namaz.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setNotificationPermissionRequested(requested: Boolean)
    suspend fun setNotificationMinutesBefore(minutes: Int)
    suspend fun setCalculationMethod(method: Int)
    suspend fun setSchool(school: Int)
    suspend fun setUseLocalCalculation(enabled: Boolean)
    suspend fun setDataSource(source: Int)
    suspend fun setLanguage(language: String)
    suspend fun setAppTheme(theme: Int)
    suspend fun setManualCity(
        name: String,
        districtName: String = "",
        latitude: Double,
        longitude: Double,
        districtId: String = ""
    )
    suspend fun clearManualCity()
    suspend fun clearRecentCities()
    suspend fun setLastAutoLocation(
        name: String,
        districtName: String = "",
        latitude: Double,
        longitude: Double,
        districtId: String,
        timezone: String
    )
    suspend fun updateManualTimezone(timezone: String)
    suspend fun setLastUpdateCheck()
}
