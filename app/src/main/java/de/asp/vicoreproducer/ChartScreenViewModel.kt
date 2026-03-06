package de.asp.vicoreproducer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class ChartScreenViewModel : ViewModel() {

    private val _state = MutableStateFlow(ChartScreenState())
    val state = _state.asStateFlow()

    init {
        collectAudioLevels()
    }

    fun onIntent(intent: ChartScreenIntent) {
        when (intent) {
            is ChartScreenIntent.DecreaseHorizontalY -> _state.update { it.copy(horizontalLineY = it.horizontalLineY.dec()) }
            is ChartScreenIntent.IncreaseHorizontalY -> _state.update { it.copy(horizontalLineY = it.horizontalLineY.inc()) }
            is ChartScreenIntent.HorizontalYValueChanged ->
                _state.update { existingState ->
                    intent.newValue.toIntOrNull()?.let { value ->
                        val clamped = value.coerceIn(0, 100)
                        existingState.copy(horizontalLineY = clamped)
                    } ?: existingState
                }
        }
    }


    private fun collectAudioLevels() {
        val peakPercentHistory = ArrayDeque<Float>()
        viewModelScope.launch {
            while (isActive) {
                val randomNumber = (0..100).random()
                if (peakPercentHistory.size >= 100) peakPercentHistory.removeFirst()
                peakPercentHistory.addLast(randomNumber.toFloat())
                _state.update { it.copy(dataSeries = peakPercentHistory.toList()) }
                delay(1.seconds)
            }
        }
    }
}

data class ChartScreenState(
    val horizontalLineY: Int = 10,
    val dataSeries: List<Float> = emptyList()
)

sealed interface ChartScreenIntent {
    data object IncreaseHorizontalY : ChartScreenIntent
    data object DecreaseHorizontalY : ChartScreenIntent

    data class HorizontalYValueChanged(val newValue: String) : ChartScreenIntent
}
