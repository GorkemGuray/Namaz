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
import kotlinx.coroutines.launch
import javax.inject.Inject

val Context.wearZikirDataStore: DataStore<Preferences> by preferencesDataStore(name = "wear_zikir_prefs")

@HiltViewModel
class WearZikirmatikViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val MODE_KEY = intPreferencesKey("wear_mode")
    private val INTERACTIVE_STEP_KEY = intPreferencesKey("wear_interactive_step")
    private val INTERACTIVE_COUNT_KEY = intPreferencesKey("wear_interactive_count")
    private val FREE_COUNT_KEY = intPreferencesKey("wear_free_count")
    private val FREE_TARGET_KEY = intPreferencesKey("wear_free_target")

    private val _currentMode = MutableStateFlow(0)
    val currentMode: StateFlow<Int> = _currentMode.asStateFlow()

    private val _interactiveStepIndex = MutableStateFlow(0)
    val interactiveStepIndex: StateFlow<Int> = _interactiveStepIndex.asStateFlow()

    private val _interactiveCount = MutableStateFlow(0)
    val interactiveCount: StateFlow<Int> = _interactiveCount.asStateFlow()

    private val _freeCount = MutableStateFlow(0)
    val freeCount: StateFlow<Int> = _freeCount.asStateFlow()

    private val _freeTarget = MutableStateFlow(0)
    val freeTarget: StateFlow<Int> = _freeTarget.asStateFlow()

    init {
        viewModelScope.launch {
            context.wearZikirDataStore.data.collect { prefs ->
                val savedMode = prefs[MODE_KEY] ?: 0
                // Backward compatibility: Old Serbest Sayaç was 2, now 0.
                _currentMode.value = if (savedMode == 2) 0 else savedMode.coerceIn(0, 1)
                _interactiveStepIndex.value = prefs[INTERACTIVE_STEP_KEY] ?: 0
                _interactiveCount.value = prefs[INTERACTIVE_COUNT_KEY] ?: 0
                _freeCount.value = prefs[FREE_COUNT_KEY] ?: 0
                _freeTarget.value = prefs[FREE_TARGET_KEY] ?: 0
            }
        }
    }

    fun setMode(mode: Int) {
        viewModelScope.launch {
            context.wearZikirDataStore.edit { prefs ->
                prefs[MODE_KEY] = mode.coerceIn(0, 1)
            }
        }
    }

    fun nextInteractiveStep() {
        val nextIndex = _interactiveStepIndex.value + 1
        if (nextIndex < co.gorkem.namaz.wear.ui.screens.WearNamazTesbihatiSteps.steps.size) {
            viewModelScope.launch {
                context.wearZikirDataStore.edit { prefs ->
                    prefs[INTERACTIVE_STEP_KEY] = nextIndex
                    prefs[INTERACTIVE_COUNT_KEY] = 0
                }
            }
        }
    }

    fun previousInteractiveStep() {
        val prevIndex = _interactiveStepIndex.value - 1
        if (prevIndex >= 0) {
            viewModelScope.launch {
                context.wearZikirDataStore.edit { prefs ->
                    prefs[INTERACTIVE_STEP_KEY] = prevIndex
                    prefs[INTERACTIVE_COUNT_KEY] = 0
                }
            }
        }
    }

    fun incrementInteractive(onTargetReached: () -> Unit) {
        val currentStep = co.gorkem.namaz.wear.ui.screens.WearNamazTesbihatiSteps.steps.getOrNull(_interactiveStepIndex.value) ?: return
        if (currentStep.type != co.gorkem.namaz.wear.ui.screens.WearStepType.COUNTER) return

        val newCount = _interactiveCount.value + 1
        if (newCount <= currentStep.targetCount) {
            viewModelScope.launch {
                context.wearZikirDataStore.edit { prefs ->
                    prefs[INTERACTIVE_COUNT_KEY] = newCount
                }
                if (newCount == currentStep.targetCount) {
                    onTargetReached()
                    kotlinx.coroutines.delay(250)
                    nextInteractiveStep()
                }
            }
        }
    }

    fun resetInteractive() {
        viewModelScope.launch {
            context.wearZikirDataStore.edit { prefs ->
                prefs[INTERACTIVE_STEP_KEY] = 0
                prefs[INTERACTIVE_COUNT_KEY] = 0
            }
        }
    }

    fun resetCurrentInteractiveStep() {
        viewModelScope.launch {
            context.wearZikirDataStore.edit { prefs ->
                prefs[INTERACTIVE_COUNT_KEY] = 0
            }
        }
    }

    fun incrementFree() {
        val target = _freeTarget.value
        val newCount = _freeCount.value + 1
        if (target == 0 || newCount <= target) {
            viewModelScope.launch {
                context.wearZikirDataStore.edit { prefs ->
                    prefs[FREE_COUNT_KEY] = newCount
                }
            }
        }
    }

    fun resetFree() {
        viewModelScope.launch {
            context.wearZikirDataStore.edit { prefs ->
                prefs[FREE_COUNT_KEY] = 0
            }
        }
    }

    fun setFreeTarget(target: Int) {
        viewModelScope.launch {
            context.wearZikirDataStore.edit { prefs ->
                prefs[FREE_TARGET_KEY] = target
                prefs[FREE_COUNT_KEY] = 0 // reset count when target changes
            }
        }
    }
}
