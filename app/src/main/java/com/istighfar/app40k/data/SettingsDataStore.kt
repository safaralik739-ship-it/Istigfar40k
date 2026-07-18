package com.istighfar.app40k.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "istighfar_settings")

enum class ThemeMode { LIGHT, DARK, SYSTEM }

data class AppSettings(
    val currentCount: Int = 0,
    val goal: Int = 40000,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val reminderCount: Int = 1, // 0 = выкл, 1..3 раз в день
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val unlockedAchievements: Set<Int> = emptySet()
)

class SettingsDataStore(private val context: Context) {

    private object Keys {
        val CURRENT_COUNT = intPreferencesKey("current_count")
        val GOAL = intPreferencesKey("goal")
        val VIBRATION = booleanPreferencesKey("vibration_enabled")
        val SOUND = booleanPreferencesKey("sound_enabled")
        val REMINDER_COUNT = intPreferencesKey("reminder_count")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val UNLOCKED_ACHIEVEMENTS = stringSetPreferencesKey("unlocked_achievements")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            currentCount = prefs[Keys.CURRENT_COUNT] ?: 0,
            goal = prefs[Keys.GOAL] ?: 40000,
            vibrationEnabled = prefs[Keys.VIBRATION] ?: true,
            soundEnabled = prefs[Keys.SOUND] ?: false,
            reminderCount = prefs[Keys.REMINDER_COUNT] ?: 1,
            themeMode = prefs[Keys.THEME_MODE]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.SYSTEM,
            unlockedAchievements = prefs[Keys.UNLOCKED_ACHIEVEMENTS]?.map { it.toInt() }?.toSet() ?: emptySet()
        )
    }

    suspend fun setCurrentCount(value: Int) {
        context.dataStore.edit { it[Keys.CURRENT_COUNT] = value }
    }

    suspend fun setGoal(value: Int) {
        context.dataStore.edit { it[Keys.GOAL] = value }
    }

    suspend fun setVibrationEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.VIBRATION] = value }
    }

    suspend fun setSoundEnabled(value: Boolean) {
        context.dataStore.edit { it[Keys.SOUND] = value }
    }

    suspend fun setReminderCount(value: Int) {
        context.dataStore.edit { it[Keys.REMINDER_COUNT] = value }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun addUnlockedAchievement(milestone: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.UNLOCKED_ACHIEVEMENTS]?.toMutableSet() ?: mutableSetOf()
            current.add(milestone.toString())
            prefs[Keys.UNLOCKED_ACHIEVEMENTS] = current
        }
    }

    suspend fun resetAll() {
        context.dataStore.edit { prefs ->
            prefs[Keys.CURRENT_COUNT] = 0
            prefs[Keys.UNLOCKED_ACHIEVEMENTS] = emptySet()
        }
    }
}
