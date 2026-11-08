package co.gorkem.namaz.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.R
import co.gorkem.namaz.data.local.datastore.UserPreferences
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
import co.gorkem.namaz.ui.theme.AppThemeSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _preferences = MutableStateFlow(UserPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    val notificationMinutesOptions = listOf(0, 15, 30, 45)
    val calculationMethods = listOf(13, 3, 4, 2)
    val schoolOptions: List<Pair<Int, Int>> = listOf(
        Pair(1, R.string.school_hanafi),
        Pair(0, R.string.school_shafi)
    )

    val themeOptions: List<Pair<Int, Int>> = AppThemeSelection.entries.map { selection ->
        val nameRes = when (selection) {
            AppThemeSelection.EMERALD_OASIS -> R.string.theme_emerald_oasis
            AppThemeSelection.SAPPHIRE_NIGHT -> R.string.theme_sapphire_night
            AppThemeSelection.AMBER_SANDS -> R.string.theme_amber_sands
            AppThemeSelection.MONOCHROME_PLATINUM -> R.string.theme_monochrome_platinum
            AppThemeSelection.YUMUSAK_AYDINLIK -> R.string.theme_yumusak_aydinlik
            AppThemeSelection.TAM_AYDINLIK -> R.string.theme_tam_aydinlik
            AppThemeSelection.SAMAN_KAGIDI -> R.string.theme_saman_kagidi
            AppThemeSelection.KARANLIK -> R.string.theme_karanlik
            AppThemeSelection.GALAKSI -> R.string.theme_galaksi
            AppThemeSelection.GUN_BATIMI -> R.string.theme_gun_batimi
            AppThemeSelection.AMBER_GECE -> R.string.theme_amber_gece
        }
        Pair(selection.ordinal, nameRes)
    }

    init {
        viewModelScope.launch {
            preferencesRepository.preferences.collect { prefs ->
                _preferences.value = prefs
            }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setNotificationPermissionRequested(requested: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationPermissionRequested(requested)
        }
    }

    fun setNotificationMinutesBefore(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.setNotificationMinutesBefore(minutes)
        }
    }

    fun setPrayerVoiceEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setPrayerVoiceEnabled(enabled)
        }
    }

    fun setCalculationMethod(method: Int) {
        viewModelScope.launch {
            preferencesRepository.setCalculationMethod(method)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesRepository.setLanguage(language)
        }
    }

    fun setSchool(school: Int) {
        viewModelScope.launch {
            preferencesRepository.setSchool(school)
        }
    }

    fun setUseLocalCalculation(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setUseLocalCalculation(enabled)
        }
    }

    fun setDataSource(source: Int) {
        viewModelScope.launch {
            preferencesRepository.setDataSource(source)
        }
    }

    fun setAppTheme(theme: Int) {
        viewModelScope.launch {
            preferencesRepository.setAppTheme(theme)
        }
    }
}
