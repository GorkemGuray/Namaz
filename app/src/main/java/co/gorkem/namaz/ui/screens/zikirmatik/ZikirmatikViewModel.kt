package co.gorkem.namaz.ui.screens.zikirmatik

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.R
import co.gorkem.namaz.domain.model.Zikir
import co.gorkem.namaz.domain.usecase.GetNamazTesbihatiUseCase
import co.gorkem.namaz.domain.usecase.GetZikirsUseCase
import co.gorkem.namaz.domain.usecase.ManageZikirUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZikirmatikViewModel @Inject constructor(
    private val getZikirsUseCase: GetZikirsUseCase,
    private val getNamazTesbihatiUseCase: GetNamazTesbihatiUseCase,
    private val manageZikirUseCase: ManageZikirUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _zikirs = MutableStateFlow<List<Zikir>>(emptyList())
    val zikirs: StateFlow<List<Zikir>> = _zikirs.asStateFlow()

    private val _currentZikir = MutableStateFlow<Zikir?>(null)
    val currentZikir: StateFlow<Zikir?> = _currentZikir.asStateFlow()

    private val _namazTesbihati = MutableStateFlow<Zikir?>(null)

    val namazTesbihatPhases = listOf(
        context.getString(R.string.zikir_subhanallah),
        context.getString(R.string.zikir_elhamdulillah),
        context.getString(R.string.zikir_allahu_ekber)
    )

    private val _selectedTesbihatMode = MutableStateFlow(TesbihatMode.UZUN)
    val selectedTesbihatMode: StateFlow<TesbihatMode> = _selectedTesbihatMode.asStateFlow()

    private val _selectedPrayerTime = MutableStateFlow(getDefaultPrayerTime())
    val selectedPrayerTime: StateFlow<TesbihatPrayerTime> = _selectedPrayerTime.asStateFlow()

    private val _activeTesbihatSteps = MutableStateFlow<List<NamazTesbihatiStep>>(
        LongTesbihatSteps.getSteps(_selectedPrayerTime.value)
    )
    val activeTesbihatSteps: StateFlow<List<NamazTesbihatiStep>> = _activeTesbihatSteps.asStateFlow()

    private val _currentTesbihatStepIndex = MutableStateFlow(0)
    val currentTesbihatStepIndex: StateFlow<Int> = _currentTesbihatStepIndex.asStateFlow()

    private val _currentTesbihatCount = MutableStateFlow(0)
    val currentTesbihatCount: StateFlow<Int> = _currentTesbihatCount.asStateFlow()

    companion object {
        private fun getDefaultPrayerTime(): TesbihatPrayerTime {
            val hour = java.time.LocalTime.now().hour
            return when (hour) {
                in 5..10 -> TesbihatPrayerTime.SABAH
                in 11..15 -> TesbihatPrayerTime.OGLE
                in 16..18 -> TesbihatPrayerTime.IKINDI
                in 19..20 -> TesbihatPrayerTime.AKSAM
                else -> TesbihatPrayerTime.YATSI
            }
        }
    }

    init {
        viewModelScope.launch {
            getZikirsUseCase().collect { list ->
                _zikirs.value = list
                if (_currentZikir.value == null && list.isNotEmpty()) {
                    _currentZikir.value = list.firstOrNull { it.isNamazTesbihati } ?: list.first()
                } else if (_currentZikir.value != null) {
                    val updated = list.find { it.id == _currentZikir.value?.id }
                    if (updated != null) {
                        _currentZikir.value = updated
                    }
                }
            }
        }

        viewModelScope.launch {
            getNamazTesbihatiUseCase().collect { tesbihat ->
                _namazTesbihati.value = tesbihat
                if (tesbihat == null) {
                    // Initialize Namaz Tesbihati if it doesn't exist
                    manageZikirUseCase.addZikir(
                        title = context.getString(R.string.zikir_namaz_tesbihati),
                        targetCount = 33,
                        isNamazTesbihati = true
                    )
                    // Pre-populate popular default zikirs
                    manageZikirUseCase.addZikir(title = context.getString(R.string.zikir_subhanallah), targetCount = 33, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = context.getString(R.string.zikir_elhamdulillah), targetCount = 33, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = context.getString(R.string.zikir_allahu_ekber), targetCount = 33, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = context.getString(R.string.zikir_estagfirullah), targetCount = 99, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = context.getString(R.string.zikir_kelime_i_tevhid), targetCount = 99, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = context.getString(R.string.zikir_salavat), targetCount = 0, isNamazTesbihati = false)
                }
            }
        }
    }

    fun selectZikir(zikir: Zikir) {
        _currentZikir.value = zikir
    }

    fun increment() {
        val zikir = _currentZikir.value ?: return
        var newCount = zikir.currentCount + 1
        var newPhase = zikir.currentPhase

        if (zikir.isNamazTesbihati) {
            if (newCount > 33) {
                if (newPhase < 2) {
                    newPhase += 1
                    newCount = 1
                } else {
                    // Finished all phases, keep at 33 or reset? Let's just keep at 33
                    newCount = 33
                }
            }
        } else if (zikir.targetCount > 0 && newCount > zikir.targetCount) {
            newCount = zikir.targetCount
        }

        viewModelScope.launch {
            manageZikirUseCase.updateCount(zikir.id, newCount, newPhase)
        }
    }

    fun reset() {
        val zikir = _currentZikir.value ?: return
        viewModelScope.launch {
            manageZikirUseCase.resetZikir(zikir.id)
        }
    }

    fun addCustomZikir(title: String, targetCount: Int) {
        viewModelScope.launch {
            manageZikirUseCase.addZikir(title, targetCount, false)
        }
    }
    
    fun deleteCustomZikir(zikir: Zikir) {
        viewModelScope.launch {
            manageZikirUseCase.deleteZikir(zikir)
            if (_currentZikir.value?.id == zikir.id) {
                _currentZikir.value = _namazTesbihati.value
            }
        }
    }

    private fun updateActiveSteps() {
        _activeTesbihatSteps.value = if (_selectedTesbihatMode.value == TesbihatMode.KISA) {
            NamazTesbihatiSteps.steps
        } else {
            LongTesbihatSteps.getSteps(_selectedPrayerTime.value)
        }
        _currentTesbihatStepIndex.value = 0
        _currentTesbihatCount.value = 0
    }

    fun setTesbihatMode(mode: TesbihatMode) {
        if (_selectedTesbihatMode.value != mode) {
            _selectedTesbihatMode.value = mode
            updateActiveSteps()
        }
    }

    fun setPrayerTime(prayerTime: TesbihatPrayerTime) {
        if (_selectedPrayerTime.value != prayerTime) {
            _selectedPrayerTime.value = prayerTime
            updateActiveSteps()
        }
    }

    fun jumpToStep(index: Int) {
        if (index in 0 until _activeTesbihatSteps.value.size) {
            _currentTesbihatStepIndex.value = index
            _currentTesbihatCount.value = 0
        }
    }

    fun nextTesbihatStep() {
        val nextIndex = _currentTesbihatStepIndex.value + 1
        if (nextIndex < _activeTesbihatSteps.value.size) {
            _currentTesbihatStepIndex.value = nextIndex
            _currentTesbihatCount.value = 0
        }
    }

    fun previousTesbihatStep() {
        val prevIndex = _currentTesbihatStepIndex.value - 1
        if (prevIndex >= 0) {
            _currentTesbihatStepIndex.value = prevIndex
            _currentTesbihatCount.value = 0
        }
    }

    fun incrementTesbihat(onTargetReached: () -> Unit) {
        val currentStep = _activeTesbihatSteps.value.getOrNull(_currentTesbihatStepIndex.value) ?: return
        if (currentStep.type != StepType.COUNTER) return

        val newCount = _currentTesbihatCount.value + 1
        if (newCount <= currentStep.targetCount) {
            _currentTesbihatCount.value = newCount
            if (newCount == currentStep.targetCount) {
                onTargetReached()
                viewModelScope.launch {
                    kotlinx.coroutines.delay(250)
                    nextTesbihatStep()
                }
            }
        }
    }

    fun resetTesbihat() {
        _currentTesbihatStepIndex.value = 0
        _currentTesbihatCount.value = 0
    }

    fun resetCurrentTesbihatStep() {
        _currentTesbihatCount.value = 0
    }
}
