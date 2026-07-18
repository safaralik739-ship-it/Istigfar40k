package com.istighfar.app40k.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

/**
 * Планирует напоминания через AlarmManager (просто и предсказуемо, без ограничений
 * WorkManager по минимальному интервалу).
 * Часы для напоминаний распределены по бодрствующей части дня: 9:00, 15:00, 20:00.
 */
object ReminderScheduler {

    private val REMINDER_HOURS_BY_COUNT = mapOf(
        0 to emptyList(),
        1 to listOf(9),
        2 to listOf(9, 18),
        3 to listOf(9, 15, 20)
    )

    fun schedule(context: Context, reminderCount: Int) {
        cancelAll(context)
        val hours = REMINDER_HOURS_BY_COUNT[reminderCount] ?: emptyList()
        hours.forEachIndexed { index, hour ->
            scheduleAt(context, hour, requestCode = index)
        }
    }

    fun rescheduleAfterFiring(context: Context) {
        // AlarmManager.setExact не повторяется сам по себе — переустановим тот же слот на завтра.
        // Для простоты и надёжности достаточно повторного вызова schedule() при следующем запуске
        // приложения/onCreate, а также через AlarmManager.setRepeating ниже.
    }

    private fun scheduleAt(context: Context, hour: Int, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Отменяем максимум 3 возможных слота (0,1,2)
        for (requestCode in 0..2) {
            val intent = Intent(context, ReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
