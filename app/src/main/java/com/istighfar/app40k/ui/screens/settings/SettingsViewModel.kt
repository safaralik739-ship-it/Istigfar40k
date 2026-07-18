package com.istighfar.app40k.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istighfar.app40k.data.AppSettings
import com.istighfar.app40k.data.CounterRepository
import com.istighfar.app40k.data.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: CounterRepository) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch { repository.setVibrationEnabled(enabled) }
    }

    fun setSound(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundEnabled(enabled) }
    }

    fun setReminderCount(count: Int) {
        viewModelScope.launch { repository.setReminderCount(count) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun setGoal(goal: Int) {
        viewModelScope.launch { repository.setGoal(goal) }
    }

    fun resetCounter() {
        viewModelScope.launch { repository.resetCounter(settings.value) }
    }

    fun exportData(onResult: (List<com.istighfar.app40k.data.local.HistoryEntry>) -> Unit) {
        viewModelScope.launch {
            val result = repository.historyFlow.first()
            onResult(result)
        }
    }
}
