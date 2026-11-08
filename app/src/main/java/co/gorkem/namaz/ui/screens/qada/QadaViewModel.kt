package co.gorkem.namaz.ui.screens.qada

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.gorkem.namaz.domain.model.PrayerName
import co.gorkem.namaz.domain.usecase.GetQadaCountsUseCase
import co.gorkem.namaz.domain.usecase.ToggleQadaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QadaViewModel @Inject constructor(
    getQadaCountsUseCase: GetQadaCountsUseCase,
    private val toggleQadaUseCase: ToggleQadaUseCase
) : ViewModel() {

    val qadaCounts: StateFlow<Map<PrayerName, Int>> = getQadaCountsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PrayerName.values().associateWith { 0 }
        )

    fun increment(prayerName: PrayerName) {
        viewModelScope.launch {
            toggleQadaUseCase.increment(prayerName.name)
        }
    }

    fun decrement(prayerName: PrayerName) {
        viewModelScope.launch {
            toggleQadaUseCase.decrement(prayerName.name)
        }
    }
}
