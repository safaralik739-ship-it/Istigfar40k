package com.istighfar.app40k

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.istighfar.app40k.data.CounterRepository
import com.istighfar.app40k.data.SettingsDataStore
import com.istighfar.app40k.data.local.AppDatabase

class IstighfarApplication : Application() {

    lateinit var repository: CounterRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val database = AppDatabase.getInstance(this)
        val settingsDataStore = SettingsDataStore(this)
        repository = CounterRepository(settingsDataStore, database.historyDao())

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                REMINDER_CHANNEL_ID,
                getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = getString(R.string.reminder_channel_desc)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val REMINDER_CHANNEL_ID = "istighfar_reminder_channel"
    }
}
