package co.gorkem.namaz.ui.screens.zikirmatik

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.data.local.room.ZikirEntity
import co.gorkem.namaz.domain.usecase.GetNamazTesbihatiUseCase
import co.gorkem.namaz.domain.usecase.GetZikirsUseCase
import co.gorkem.namaz.domain.usecase.ManageZikirUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ZikirmatikViewModel @Inject constructor(
    private val getZikirsUseCase: GetZikirsUseCase,
    private val getNamazTesbihatiUseCase: GetNamazTesbihatiUseCase,
    private val manageZikirUseCase: ManageZikirUseCase
) : ViewModel() {

    private val _zikirs = MutableStateFlow<List<ZikirEntity>>(emptyList())
    val zikirs: StateFlow<List<ZikirEntity>> = _zikirs.asStateFlow()

    private val _currentZikir = MutableStateFlow<ZikirEntity?>(null)
    val currentZikir: StateFlow<ZikirEntity?> = _currentZikir.asStateFlow()

    private val _namazTesbihati = MutableStateFlow<ZikirEntity?>(null)

    val namazTesbihatPhases = listOf("Sübhanallah", "Elhamdülillah", "Allahuekber")

    private val _currentTesbihatStepIndex = MutableStateFlow(0)
    val currentTesbihatStepIndex: StateFlow<Int> = _currentTesbihatStepIndex.asStateFlow()

    private val _currentTesbihatCount = MutableStateFlow(0)
    val currentTesbihatCount: StateFlow<Int> = _currentTesbihatCount.asStateFlow()

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
                        title = "Namaz Tesbihatı",
                        targetCount = 33,
                        isNamazTesbihati = true
                    )
                    // Pre-populate popular default zikirs
                    manageZikirUseCase.addZikir(title = "Sübhanallah", targetCount = 33, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = "Elhamdülillah", targetCount = 33, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = "Allahu ekber", targetCount = 33, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = "Estağfirullah", targetCount = 99, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = "Kelime-i Tevhid", targetCount = 99, isNamazTesbihati = false)
                    manageZikirUseCase.addZikir(title = "Salavat", targetCount = 0, isNamazTesbihati = false)
                }
            }
        }
    }

    fun selectZikir(zikir: ZikirEntity) {
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
    
    fun deleteCustomZikir(zikir: ZikirEntity) {
        viewModelScope.launch {
            manageZikirUseCase.deleteZikir(zikir)
            if (_currentZikir.value?.id == zikir.id) {
                _currentZikir.value = _namazTesbihati.value
            }
        }
    }

    fun nextTesbihatStep() {
        val nextIndex = _currentTesbihatStepIndex.value + 1
        if (nextIndex < NamazTesbihatiSteps.steps.size) {
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
        val currentStep = NamazTesbihatiSteps.steps.getOrNull(_currentTesbihatStepIndex.value) ?: return
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
