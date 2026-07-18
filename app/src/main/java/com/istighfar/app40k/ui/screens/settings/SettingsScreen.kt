package com.istighfar.app40k.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.istighfar.app40k.data.ThemeMode
import com.istighfar.app40k.ui.ViewModelFactory
import com.istighfar.app40k.ui.components.GoalPickerDialog
import com.istighfar.app40k.util.DataExporter

@Composable
fun SettingsScreen(viewModelFactory: ViewModelFactory) {
    val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current

    var showGoalDialog by remember { mutableStateOf(false) }
    var showResetConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { androidx.compose.material3.Text("Настройки") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item { SectionCard(title = "Основные") {
                SettingRow("Вибрация") {
                    Switch(checked = settings.vibrationEnabled, onCheckedChange = { viewModel.setVibration(it) })
                }
                SettingRow("Звук") {
                    Switch(checked = settings.soundEnabled, onCheckedChange = { viewModel.setSound(it) })
                }
            }}

            item { SectionCard(title = "Напоминания") {
                Text(
                    "Сколько раз в день напоминать сделать зикр",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column {
                    listOf(0 to "Отключено", 1 to "1 раз", 2 to "2 раза", 3 to "3 раза").forEach { (value, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = settings.reminderCount == value,
                                onClick = {
                                    viewModel.setReminderCount(value)
                                    com.istighfar.app40k.notification.ReminderScheduler.schedule(context, value)
                                }
                            )
                            Text(label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }}

            item { SectionCard(title = "Тема оформления") {
                listOf(
                    ThemeMode.LIGHT to "Светлая",
                    ThemeMode.DARK to "Тёмная",
                    ThemeMode.SYSTEM to "Следовать системе"
                ).forEach { (mode, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = settings.themeMode == mode,
                            onClick = { viewModel.setThemeMode(mode) }
                        )
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }}

            item { SectionCard(title = "Цель") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Текущая цель: ${settings.goal}", style = MaterialTheme.typography.bodyLarge)
                    OutlinedButton(onClick = { showGoalDialog = true }) {
                        Text("Изменить")
                    }
                }
            }}

            item { SectionCard(title = "Данные") {
                OutlinedButton(
                    onClick = {
                        viewModel.exportData { entries ->
                            DataExporter.exportToCsvAndShare(context, entries)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Экспортировать данные (CSV)")
                }
                OutlinedButton(
                    onClick = { showResetConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Сбросить счётчик", color = MaterialTheme.colorScheme.error)
                }
            }}
        }
    }

    if (showGoalDialog) {
        GoalPickerDialog(
            currentGoal = settings.goal,
            onDismiss = { showGoalDialog = false },
            onConfirm = {
                viewModel.setGoal(it)
                showGoalDialog = false
            }
        )
    }

    if (showResetConfirm) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Сбросить счётчик?") },
            text = { Text("Текущий прогресс (${settings.currentCount}) будет обнулён. История сохранится.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetCounter()
                    showResetConfirm = false
                }) { Text("Сбросить", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScopeAlias.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            content()
        }
    }
}

private typealias ColumnScopeAlias = androidx.compose.foundation.layout.ColumnScope

@Composable
private fun SettingRow(label: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        control()
    }
}
