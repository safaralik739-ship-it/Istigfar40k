package com.istighfar.app40k.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.istighfar.app40k.data.CounterRepository
import com.istighfar.app40k.data.local.HistoryEntry
import com.istighfar.app40k.util.CounterMath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val today: Int = 0,
    val yesterday: Int = 0,
    val week: Int = 0,
    val month: Int = 0,
    val total: Int = 0,
    val averagePerDay: Double = 0.0,
    val dailyChartValues: List<Int> = emptyList(),
    val recentEntries: List<HistoryEntry> = emptyList()
)

class HistoryViewModel(private val repository: CounterRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadStats()
        viewModelScope.launch {
            repository.historyFlow.collect { entries ->
                _uiState.value = _uiState.value.copy(recentEntries = entries.take(50))
                loadStats()
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            val today = repository.getTodayTotal()
            val yesterday = repository.getYesterdayTotal()
            val week = repository.getStatsForPeriod(7)
            val month = repository.getStatsForPeriod(30)
            val total = repository.getStatsForPeriod(36500) // приблизительно "всё время"
            val dailyValues = repository.getDailyTotals(7)
            val average = CounterMath.averagePerDay(dailyValues)

            _uiState.value = _uiState.value.copy(
                today = today,
                yesterday = yesterday,
                week = week,
                month = month,
                total = total,
                averagePerDay = average,
                dailyChartValues = dailyValues
            )
        }
    }
}
