package com.istighfar.app40k.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.istighfar.app40k.data.CounterRepository
import com.istighfar.app40k.ui.screens.achievements.AchievementsViewModel
import com.istighfar.app40k.ui.screens.history.HistoryViewModel
import com.istighfar.app40k.ui.screens.home.HomeViewModel
import com.istighfar.app40k.ui.screens.settings.SettingsViewModel

/**
 * Простая фабрика ViewModel'ей без Hilt/Dagger — репозиторий передаётся вручную
 * из Application (Repository Pattern + Clean Architecture без лишних зависимостей).
 */
class ViewModelFactory(private val repository: CounterRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) ->
                HomeViewModel(repository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(repository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(repository) as T
            modelClass.isAssignableFrom(AchievementsViewModel::class.java) ->
                AchievementsViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
