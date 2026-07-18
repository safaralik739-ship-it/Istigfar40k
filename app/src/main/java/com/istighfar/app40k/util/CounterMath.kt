package com.istighfar.app40k.util

/**
 * Чистая (без зависимостей от Android) логика подсчёта.
 * Вынесена отдельно, чтобы её можно было покрыть быстрыми unit-тестами.
 */
object CounterMath {

    val MILESTONES = listOf(1000, 5000, 10000, 40000, 100000)

    /** Увеличение на 1 (без верхнего предела). */
    fun increment(current: Int): Int = current + 1

    /** Уменьшение на 1, но не ниже нуля. */
    fun decrement(current: Int): Int = if (current > 0) current - 1 else 0

    /** Добавление произвольного (в т.ч. отрицательного при коррекции) количества, не ниже нуля. */
    fun addAmount(current: Int, amount: Int): Int {
        val result = current + amount
        return if (result < 0) 0 else result
    }

    /** Процент выполнения цели, от 0 до 100. */
    fun percentage(current: Int, goal: Int): Int {
        if (goal <= 0) return 0
        val pct = (current.toDouble() / goal.toDouble() * 100).toInt()
        return pct.coerceIn(0, 100)
    }

    /** Сколько осталось до цели (не может быть отрицательным). */
    fun remaining(current: Int, goal: Int): Int {
        val rem = goal - current
        return if (rem < 0) 0 else rem
    }

    /**
     * Возвращает список вех (milestones), которые были только что пройдены
     * при переходе счётчика с oldCount на newCount.
     */
    fun newlyUnlockedAchievements(
        oldCount: Int,
        newCount: Int,
        milestones: List<Int> = MILESTONES
    ): List<Int> {
        return milestones.filter { milestone -> oldCount < milestone && newCount >= milestone }
    }

    /** Среднее количество в день по списку дневных сумм. */
    fun averagePerDay(dailyTotals: List<Int>): Double {
        if (dailyTotals.isEmpty()) return 0.0
        return dailyTotals.sum().toDouble() / dailyTotals.size
    }
}
