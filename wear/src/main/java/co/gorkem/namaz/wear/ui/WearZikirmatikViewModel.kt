package co.gorkem.namaz.wear.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.wearZikirDataStore: DataStore<Preferences> by preferencesDataStore(name = "wear_zikir_prefs")

@HiltViewModel
class WearZikirmatikViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val COUNT_KEY = intPreferencesKey("zikir_count")
    private val PHASE_KEY = intPreferencesKey("zikir_phase")

    private val _currentCount = MutableStateFlow(0)
    val currentCount: StateFlow<Int> = _currentCount.asStateFlow()

    val namazTesbihatPhases = listOf("Sübhanallah", "Elhamdülillah", "Allahuekber")

    private val _currentPhase = MutableStateFlow(0)
    val currentPhase: StateFlow<Int> = _currentPhase.asStateFlow()

    init {
        viewModelScope.launch {
            context.wearZikirDataStore.data.collect { prefs ->
                _currentCount.value = prefs[COUNT_KEY] ?: 0
                _currentPhase.value = prefs[PHASE_KEY] ?: 0
            }
        }
    }

    fun increment() {
        var newCount = _currentCount.value + 1
        var newPhase = _currentPhase.value

        if (newCount > 33) {
            if (newPhase < 2) {
                newPhase += 1
                newCount = 1
            } else {
                newCount = 33
            }
        }
        
        viewModelScope.launch {
            context.wearZikirDataStore.edit { prefs ->
                prefs[COUNT_KEY] = newCount
                prefs[PHASE_KEY] = newPhase
            }
        }
    }

    fun reset() {
        viewModelScope.launch {
            context.wearZikirDataStore.edit { prefs ->
                prefs[COUNT_KEY] = 0
                prefs[PHASE_KEY] = 0
            }
        }
    }
}
