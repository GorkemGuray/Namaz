package co.gorkem.namaz.service

import co.gorkem.namaz.domain.model.UserPreferences
import co.gorkem.namaz.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerNotificationChannelTest {

    private val testPackage = "co.gorkem.namaz"

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
    fun voiceDisabled_selectsDefaultChannelId_andHasNoSound() = runTest {
        val repository = FakeUserPreferencesRepository(initialVoiceEnabled = false)
        val voiceEnabled = repository.preferences.first().prayerVoiceEnabled

        val channelId = NotificationHelper.resolveNotificationChannelId(voiceEnabled)

        assertEquals(NotificationHelper.CHANNEL_ID, channelId)
        assertEquals("namaz_prayer_times", channelId)

        // Silent channel: no sound URI is associated
        val soundUriStr = NotificationHelper.getChannelSoundUriString(testPackage, channelId)
        assertNull("Default channel must be silent", soundUriStr)
        assertNull("Sound URI must be null when voice is disabled", NotificationHelper.getSoundUriString(testPackage, voiceEnabled))
    }

    @Test
    fun voiceEnabled_selectsVoiceChannelId_andMatchesEzanSound() = runTest {
        val repository = FakeUserPreferencesRepository(initialVoiceEnabled = true)
        val voiceEnabled = repository.preferences.first().prayerVoiceEnabled

        val channelId = NotificationHelper.resolveNotificationChannelId(voiceEnabled)

        assertEquals(NotificationHelper.VOICE_CHANNEL_ID, channelId)
        assertEquals("namaz_prayer_times_voice", channelId)

        // Voice channel: must point to raw ezan audio resource
        val soundUriStr = NotificationHelper.getChannelSoundUriString(testPackage, channelId)
        assertNotNull("Voice channel must have sound URI", soundUriStr)
        assertEquals("android.resource://$testPackage/${co.gorkem.namaz.R.raw.ezan}", soundUriStr)
        assertEquals(NotificationHelper.getEzanSoundUriString(testPackage), soundUriStr)
    }

    @Test
    fun togglingVoicePreference_switchesChannelIdAndSound() = runTest {
        val repository = FakeUserPreferencesRepository(initialVoiceEnabled = false)

        var voiceEnabled = repository.preferences.first().prayerVoiceEnabled
        assertEquals(NotificationHelper.CHANNEL_ID, NotificationHelper.resolveNotificationChannelId(voiceEnabled))
        assertNull(NotificationHelper.getSoundUriString(testPackage, voiceEnabled))

        repository.setPrayerVoiceEnabled(true)
        voiceEnabled = repository.preferences.first().prayerVoiceEnabled
        assertEquals(NotificationHelper.VOICE_CHANNEL_ID, NotificationHelper.resolveNotificationChannelId(voiceEnabled))
        assertNotNull(NotificationHelper.getSoundUriString(testPackage, voiceEnabled))
        assertEquals(NotificationHelper.getEzanSoundUriString(testPackage), NotificationHelper.getSoundUriString(testPackage, voiceEnabled))

        repository.setPrayerVoiceEnabled(false)
        voiceEnabled = repository.preferences.first().prayerVoiceEnabled
        assertEquals(NotificationHelper.CHANNEL_ID, NotificationHelper.resolveNotificationChannelId(voiceEnabled))
        assertNull(NotificationHelper.getSoundUriString(testPackage, voiceEnabled))
    }

    @Test
    fun prayerAudioPlayer_volumeRampConfig_isGradualAndComfortable() {
        assertEquals("Ramp duration must be exactly 10 seconds", 10_000L, PrayerAudioPlayer.RAMP_DURATION_MS)
        assertEquals(20, PrayerAudioPlayer.RAMP_STEPS)
        assertTrue("Initial volume must start low (wake-up comfort)", PrayerAudioPlayer.INITIAL_VOLUME <= 0.1f)
        assertEquals(1.0f, PrayerAudioPlayer.TARGET_VOLUME, 0.001f)
        assertTrue(PrayerAudioPlayer.INITIAL_VOLUME < PrayerAudioPlayer.TARGET_VOLUME)
    }
}
