package com.istighfar.app40k.data

import com.istighfar.app40k.data.local.EntryType
import com.istighfar.app40k.data.local.HistoryDao
import com.istighfar.app40k.data.local.HistoryEntry
import com.istighfar.app40k.util.CounterMath
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

/**
 * Единая точка правды (single source of truth) для состояния счётчика и истории.
 * ViewModel'и работают только через этот репозиторий (Repository Pattern).
 */
class CounterRepository(
    private val settingsDataStore: SettingsDataStore,
    private val historyDao: HistoryDao
) {

    val settingsFlow: Flow<AppSettings> = settingsDataStore.settingsFlow
    val historyFlow: Flow<List<HistoryEntry>> = historyDao.getAll()

    /** Результат операции изменения счётчика: новое значение + список новых достижений. */
    data class MutationResult(val newTotal: Int, val newlyUnlocked: List<Int>)

    suspend fun increment(oldSettings: AppSettings): MutationResult {
        val newTotal = CounterMath.increment(oldSettings.currentCount)
        return applyMutation(oldSettings, newTotal, amount = 1, type = EntryType.INCREMENT)
    }

    suspend fun decrement(oldSettings: AppSettings): MutationResult {
        val newTotal = CounterMath.decrement(oldSettings.currentCount)
        val amount = newTotal - oldSettings.currentCount
        return applyMutation(oldSettings, newTotal, amount = amount, type = EntryType.DECREMENT)
    }

    suspend fun addAmount(oldSettings: AppSettings, amount: Int): MutationResult {
        val newTotal = CounterMath.addAmount(oldSettings.currentCount, amount)
        return applyMutation(oldSettings, newTotal, amount = amount, type = EntryType.MANUAL_ADD)
    }

    suspend fun resetCounter(oldSettings: AppSettings) {
        historyDao.insert(
            HistoryEntry(
                amount = -oldSettings.currentCount,
                resultingTotal = 0,
                type = EntryType.RESET,
                timestamp = System.currentTimeMillis()
            )
        )
        settingsDataStore.resetAll()
    }

    suspend fun setGoal(goal: Int) = settingsDataStore.setGoal(goal)
    suspend fun setVibrationEnabled(enabled: Boolean) = settingsDataStore.setVibrationEnabled(enabled)
    suspend fun setSoundEnabled(enabled: Boolean) = settingsDataStore.setSoundEnabled(enabled)
    suspend fun setReminderCount(count: Int) = settingsDataStore.setReminderCount(count)
    suspend fun setThemeMode(mode: ThemeMode) = settingsDataStore.setThemeMode(mode)

    private suspend fun applyMutation(
        oldSettings: AppSettings,
        newTotal: Int,
        amount: Int,
        type: EntryType
    ): MutationResult {
        settingsDataStore.setCurrentCount(newTotal)

        if (amount != 0) {
            historyDao.insert(
                HistoryEntry(
                    amount = amount,
                    resultingTotal = newTotal,
                    type = type,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        val newlyUnlocked = CounterMath.newlyUnlockedAchievements(oldSettings.currentCount, newTotal)
        for (milestone in newlyUnlocked) {
            settingsDataStore.addUnlockedAchievement(milestone)
        }

        return MutationResult(newTotal, newlyUnlocked)
    }

    /** Статистика: сумма приращений (только положительные INCREMENT/MANUAL_ADD) за период. */
    suspend fun getStatsForPeriod(daysBack: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, -daysBack)
        val from = cal.timeInMillis
        val to = System.currentTimeMillis()
        val entries = historyDao.getBetween(from, to)
        return entries.filter { it.amount > 0 }.sumOf { it.amount }
    }

    suspend fun getTodayTotal(): Int = getStatsForPeriod(0)
    suspend fun getYesterdayTotal(): Int {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val from = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val to = cal.timeInMillis
        val entries = historyDao.getBetween(from, to)
        return entries.filter { it.amount > 0 }.sumOf { it.amount }
    }

    /** Суммы за последние [days] дней, от старого к новому — удобно для графика. */
    suspend fun getDailyTotals(days: Int): List<Int> {
        val result = mutableListOf<Int>()
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        for (i in (days - 1) downTo 0) {
            val dayStart = cal.clone() as Calendar
            dayStart.add(Calendar.DAY_OF_YEAR, -i)
            val from = dayStart.timeInMillis
            val dayEnd = dayStart.clone() as Calendar
            dayEnd.add(Calendar.DAY_OF_YEAR, 1)
            val to = dayEnd.timeInMillis
            val entries = historyDao.getBetween(from, to)
            result.add(entries.filter { it.amount > 0 }.sumOf { it.amount })
        }
        return result
    }

    suspend fun clearHistory() = historyDao.clearAll()
}
