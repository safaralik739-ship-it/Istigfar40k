package com.istighfar.app40k.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private val presetGoals = listOf(1000, 5000, 10000, 40000, 100000)

@Composable
fun GoalPickerDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedPreset by remember {
        mutableStateOf(if (presetGoals.contains(currentGoal)) currentGoal else -1)
    }
    var customText by remember {
        mutableStateOf(if (!presetGoals.contains(currentGoal)) currentGoal.toString() else "")
    }

    val finalGoal = if (selectedPreset > 0) selectedPreset else customText.toIntOrNull()
    val isValid = finalGoal != null && finalGoal > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите цель") },
        text = {
            Column {
                presetGoals.forEach { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedPreset == goal,
                                onClick = {
                                    selectedPreset = goal
                                    customText = ""
                                }
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedPreset == goal,
                            onClick = {
                                selectedPreset = goal
                                customText = ""
                            }
                        )
                        Text("$goal", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                OutlinedTextField(
                    value = customText,
                    onValueChange = {
                        if (it.length <= 8 && it.all { c -> c.isDigit() }) {
                            customText = it
                            selectedPreset = -1
                        }
                    },
                    placeholder = { Text("Своя цель") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = { finalGoal?.let { onConfirm(it) } }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
