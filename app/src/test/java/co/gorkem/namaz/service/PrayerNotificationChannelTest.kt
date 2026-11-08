package co.gorkem.namaz.service

import co.gorkem.namaz.domain.model.UserPreferences
import co.gorkem.namaz.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerNotificationChannelTest {

    private class FakeUserPreferencesRepository(
        initialVoiceEnabled: Boolean = false
    ) : UserPreferencesRepository {
        private val _preferences = MutableStateFlow(
            UserPreferences(prayerVoiceEnabled = initialVoiceEnabled)
        )
        override val preferences: Flow<UserPreferences> = _preferences

        override suspend fun setPrayerVoiceEnabled(enabled: Boolean) {
            _preferences.value = _preferences.value.copy(prayerVoiceEnabled = enabled)
        }

        override suspend fun setNotificationsEnabled(enabled: Boolean) {}
        override suspend fun setNotificationPermissionRequested(requested: Boolean) {}
        override suspend fun setNotificationMinutesBefore(minutes: Int) {}
        override suspend fun setCalculationMethod(method: Int) {}
        override suspend fun setSchool(school: Int) {}
        override suspend fun setUseLocalCalculation(enabled: Boolean) {}
        override suspend fun setDataSource(source: Int) {}
        override suspend fun setLanguage(language: String) {}
        override suspend fun setAppTheme(theme: Int) {}
        override suspend fun setManualCity(
            name: String,
            districtName: String,
            latitude: Double,
            longitude: Double,
            districtId: String
        ) {}
        override suspend fun clearManualCity() {}
        override suspend fun clearRecentCities() {}
        override suspend fun setLastAutoLocation(
            name: String,
            districtName: String,
            latitude: Double,
            longitude: Double,
            districtId: String,
            timezone: String
        ) {}
        override suspend fun updateManualTimezone(timezone: String) {}
        override suspend fun setLastUpdateCheck() {}
    }

    @Test
    fun voiceDisabled_selectsDefaultChannelId() = runTest {
        val repository = FakeUserPreferencesRepository(initialVoiceEnabled = false)
        val voiceEnabled = repository.preferences.first().prayerVoiceEnabled

        val channelId = NotificationHelper.resolveNotificationChannelId(voiceEnabled)

        assertEquals(NotificationHelper.CHANNEL_ID, channelId)
        assertEquals("namaz_prayer_times", channelId)
    }

    @Test
    fun voiceEnabled_selectsVoiceChannelId() = runTest {
        val repository = FakeUserPreferencesRepository(initialVoiceEnabled = true)
        val voiceEnabled = repository.preferences.first().prayerVoiceEnabled

        val channelId = NotificationHelper.resolveNotificationChannelId(voiceEnabled)

        assertEquals(NotificationHelper.VOICE_CHANNEL_ID, channelId)
        assertEquals("namaz_prayer_times_voice", channelId)
    }

    @Test
    fun togglingVoicePreference_switchesChannelId() = runTest {
        val repository = FakeUserPreferencesRepository(initialVoiceEnabled = false)

        var voiceEnabled = repository.preferences.first().prayerVoiceEnabled
        assertEquals(NotificationHelper.CHANNEL_ID, NotificationHelper.resolveNotificationChannelId(voiceEnabled))

        repository.setPrayerVoiceEnabled(true)
        voiceEnabled = repository.preferences.first().prayerVoiceEnabled
        assertEquals(NotificationHelper.VOICE_CHANNEL_ID, NotificationHelper.resolveNotificationChannelId(voiceEnabled))

        repository.setPrayerVoiceEnabled(false)
        voiceEnabled = repository.preferences.first().prayerVoiceEnabled
        assertEquals(NotificationHelper.CHANNEL_ID, NotificationHelper.resolveNotificationChannelId(voiceEnabled))
    }
}
