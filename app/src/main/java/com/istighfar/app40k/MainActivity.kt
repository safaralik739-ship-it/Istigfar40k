package com.istighfar.app40k

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.istighfar.app40k.data.ThemeMode
import com.istighfar.app40k.notification.ReminderScheduler
import com.istighfar.app40k.ui.ViewModelFactory
import com.istighfar.app40k.ui.navigation.AppNavGraph
import com.istighfar.app40k.ui.theme.Istighfar40KTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* результат не критичен для работы приложения */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as IstighfarApplication
        val viewModelFactory = ViewModelFactory(app.repository)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        lifecycleScope.launch {
            val settings = app.repository.settingsFlow.first()
            if (settings.reminderCount > 0) {
                ReminderScheduler.schedule(this@MainActivity, settings.reminderCount)
            }
        }

        setContent {
            val settings by app.repository.settingsFlow.collectAsState(
                initial = com.istighfar.app40k.data.AppSettings()
            )

            val systemDark = isSystemInDarkTheme()
            val useDarkTheme = when (settings.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> systemDark
            }

            Istighfar40KTheme(darkTheme = useDarkTheme) {
                AppNavGraph(viewModelFactory = viewModelFactory)
            }
        }
    }
}
