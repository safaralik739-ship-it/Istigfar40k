package com.istighfar.app40k.ui.screens.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istighfar.app40k.data.AppSettings
import com.istighfar.app40k.data.CounterRepository
import com.istighfar.app40k.util.CounterMath
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class AchievementsViewModel(repository: CounterRepository) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val milestones: List<Int> = CounterMath.MILESTONES
}
