package com.istighfar.app40k.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.istighfar.app40k.data.local.EntryType
import com.istighfar.app40k.data.local.HistoryEntry
import com.istighfar.app40k.ui.ViewModelFactory
import com.istighfar.app40k.ui.components.SimpleBarChart
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HistoryScreen(viewModelFactory: ViewModelFactory) {
    val viewModel: HistoryViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Статистика") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatCard(title = "Сегодня", value = uiState.today, modifier = Modifier.weight(1f))
                    StatCard(title = "Вчера", value = uiState.yesterday, modifier = Modifier.weight(1f))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    StatCard(title = "Неделя", value = uiState.week, modifier = Modifier.weight(1f))
                    StatCard(title = "Месяц", value = uiState.month, modifier = Modifier.weight(1f))
                }
            }
            item {
                StatCard(title = "Всего", value = uiState.total, modifier = Modifier.fillMaxWidth(), big = true)
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Последние 7 дней",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "В среднем ${"%.0f".format(uiState.averagePerDay)} в день",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        SimpleBarChart(
                            values = uiState.dailyChartValues,
                            labels = lastNDayLabels(uiState.dailyChartValues.size),
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
            item {
                Text(
                    "История действий",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(uiState.recentEntries) { entry ->
                HistoryEntryRow(entry)
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: Int, modifier: Modifier = Modifier, big: Boolean = false) {
    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                "$value",
                style = if (big) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun HistoryEntryRow(entry: HistoryEntry) {
    val sdf = remember(entry.id) { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = labelFor(entry),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = sdf.format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Итого: ${entry.resultingTotal}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun labelFor(entry: HistoryEntry): String {
    val sign = if (entry.amount > 0) "+" else ""
    return when (entry.type) {
        EntryType.RESET -> "Сброс счётчика"
        else -> "$sign${entry.amount}"
    }
}

private fun lastNDayLabels(n: Int): List<String> {
    val labels = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
    val cal = java.util.Calendar.getInstance()
    val result = mutableListOf<String>()
    for (i in (n - 1) downTo 0) {
        val c = cal.clone() as java.util.Calendar
        c.add(java.util.Calendar.DAY_OF_YEAR, -i)
        val dayIndex = (c.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7 // приводим к Пн=0
        result.add(labels[dayIndex])
    }
    return result
}
