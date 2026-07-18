package com.istighfar.app40k.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.istighfar.app40k.IstighfarApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val app = context.applicationContext as IstighfarApplication

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = app.repository.settingsFlow.first()
                ReminderScheduler.schedule(context, settings.reminderCount)
            } finally {
                pendingResult.finish()
            }
        }
    }
}
