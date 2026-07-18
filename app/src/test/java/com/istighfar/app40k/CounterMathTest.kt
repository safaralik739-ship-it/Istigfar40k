package com.istighfar.app40k

import com.istighfar.app40k.util.CounterMath
import org.junit.Assert.assertEquals
import org.junit.Test

class CounterMathTest {

    @Test
    fun increment_addsOne() {
        assertEquals(1, CounterMath.increment(0))
        assertEquals(371, CounterMath.increment(370))
    }

    @Test
    fun decrement_doesNotGoBelowZero() {
        assertEquals(0, CounterMath.decrement(0))
        assertEquals(4, CounterMath.decrement(5))
    }

    @Test
    fun addAmount_setsAndAccumulatesCorrectly() {
        // сценарий из ТЗ: ввели 350, потом +1 двадцать раз -> 370
        var count = CounterMath.addAmount(0, 350)
        assertEquals(350, count)
        repeat(20) { count = CounterMath.increment(count) }
        assertEquals(370, count)
    }

    @Test
    fun addAmount_neverGoesNegative() {
        assertEquals(0, CounterMath.addAmount(5, -100))
    }

    @Test
    fun percentage_isCorrectAndClamped() {
        assertEquals(0, CounterMath.percentage(0, 40000))
        assertEquals(25, CounterMath.percentage(10000, 40000))
        assertEquals(100, CounterMath.percentage(50000, 40000)) // clamp at 100
    }

    @Test
    fun percentage_withZeroGoal_returnsZero() {
        assertEquals(0, CounterMath.percentage(100, 0))
    }

    @Test
    fun remaining_isCorrectAndNeverNegative() {
        assertEquals(30000, CounterMath.remaining(10000, 40000))
        assertEquals(0, CounterMath.remaining(50000, 40000))
    }

    @Test
    fun newlyUnlockedAchievements_detectsCrossedMilestones() {
        val unlocked = CounterMath.newlyUnlockedAchievements(900, 1200)
        assertEquals(listOf(1000), unlocked)
    }

    @Test
    fun newlyUnlockedAchievements_detectsMultipleCrossedAtOnce() {
        // добавили сразу 6000, перескочив и 1000 и 5000
        val unlocked = CounterMath.newlyUnlockedAchievements(500, 6500)
        assertEquals(listOf(1000, 5000), unlocked)
    }

    @Test
    fun newlyUnlockedAchievements_emptyWhenNoNewMilestone() {
        val unlocked = CounterMath.newlyUnlockedAchievements(1500, 1600)
        assertEquals(emptyList<Int>(), unlocked)
    }

    @Test
    fun averagePerDay_computesMean() {
        assertEquals(200.0, CounterMath.averagePerDay(listOf(100, 200, 300)), 0.001)
        assertEquals(0.0, CounterMath.averagePerDay(emptyList()), 0.001)
    }
}
