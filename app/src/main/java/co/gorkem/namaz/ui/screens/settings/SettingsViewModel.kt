package co.gorkem.namaz.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.R
import co.gorkem.namaz.data.local.datastore.UserPreferences
import co.gorkem.namaz.data.local.datastore.UserPreferencesRepository
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

    val notificationMinutesOptions = listOf(15, 30, 45)
    val calculationMethods = listOf(13, 3, 4, 2)
    val schoolOptions: List<Pair<Int, Int>> = listOf(
        Pair(1, R.string.school_hanafi),
        Pair(0, R.string.school_shafi)
    )

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

    fun setNotificationMinutesBefore(minutes: Int) {
        viewModelScope.launch {
            preferencesRepository.setNotificationMinutesBefore(minutes)
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
}
