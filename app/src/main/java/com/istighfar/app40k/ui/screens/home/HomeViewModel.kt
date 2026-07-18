package com.istighfar.app40k.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istighfar.app40k.data.AppSettings
import com.istighfar.app40k.data.CounterRepository
import com.istighfar.app40k.util.CounterMath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val currentCount: Int = 0,
    val goal: Int = 40000,
    val percent: Int = 0,
    val remaining: Int = 40000,
    val vibrationEnabled: Boolean = true,
    val celebratingMilestone: Int? = null
)

class HomeViewModel(private val repository: CounterRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var latestSettings = AppSettings()

    init {
        viewModelScope.launch {
            repository.settingsFlow.collect { settings ->
                latestSettings = settings
                _uiState.value = _uiState.value.copy(
                    currentCount = settings.currentCount,
                    goal = settings.goal,
                    percent = CounterMath.percentage(settings.currentCount, settings.goal),
                    remaining = CounterMath.remaining(settings.currentCount, settings.goal),
                    vibrationEnabled = settings.vibrationEnabled
                )
            }
        }
    }

    fun onIncrement() {
        viewModelScope.launch {
            val result = repository.increment(latestSettings)
            handleMutationResult(result.newlyUnlocked)
        }
    }

    fun onDecrement() {
        viewModelScope.launch {
            repository.decrement(latestSettings)
        }
    }

    fun onAddAmount(amount: Int) {
        viewModelScope.launch {
            val result = repository.addAmount(latestSettings, amount)
            handleMutationResult(result.newlyUnlocked)
        }
    }

    fun onGoalSelected(goal: Int) {
        viewModelScope.launch {
            repository.setGoal(goal)
        }
    }

    fun onResetCounter() {
        viewModelScope.launch {
            repository.resetCounter(latestSettings)
        }
    }

    fun dismissCelebration() {
        _uiState.value = _uiState.value.copy(celebratingMilestone = null)
    }

    private fun handleMutationResult(newlyUnlocked: List<Int>) {
        if (newlyUnlocked.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(celebratingMilestone = newlyUnlocked.max())
        }
    }
}
