package com.istighfar.app40k.ui.screens.home

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.istighfar.app40k.ui.ViewModelFactory
import com.istighfar.app40k.ui.components.AddAmountDialog
import com.istighfar.app40k.ui.components.BigCounterButton
import com.istighfar.app40k.ui.components.ConfettiOverlay
import com.istighfar.app40k.ui.components.GoalPickerDialog
import com.istighfar.app40k.ui.components.ProgressRing

private fun vibrate(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(35)
        }
    }
}

@Composable
fun HomeScreen(viewModelFactory: ViewModelFactory) {
    val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var showGoalDialog by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "أستغفر الله",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Астагфируллах",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    ProgressRing(percent = uiState.percent, modifier = Modifier.fillMaxSize()) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${uiState.currentCount}",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "/ ${uiState.goal}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${uiState.percent}%",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "До цели осталось",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${uiState.remaining}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(28.dp))

                BigCounterButton(onClick = {
                    if (uiState.vibrationEnabled) vibrate(context)
                    viewModel.onIncrement()
                })

                Spacer(modifier = Modifier.height(20.dp))

                ActionButtonsRow(
                    onMinusClick = { viewModel.onDecrement() },
                    onAddClick = { showAddDialog = true },
                    onGoalClick = { showGoalDialog = true }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            uiState.celebratingMilestone?.let { milestone ->
                ConfettiOverlay(milestone = milestone, onDismiss = { viewModel.dismissCelebration() })
            }
        }
    }

    if (showAddDialog) {
        AddAmountDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amount ->
                viewModel.onAddAmount(amount)
                showAddDialog = false
            }
        )
    }

    if (showGoalDialog) {
        GoalPickerDialog(
            currentGoal = uiState.goal,
            onDismiss = { showGoalDialog = false },
            onConfirm = { goal ->
                viewModel.onGoalSelected(goal)
                showGoalDialog = false
            }
        )
    }
}

@Composable
private fun ActionButtonsRow(
    onMinusClick: () -> Unit,
    onAddClick: () -> Unit,
    onGoalClick: () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onMinusClick,
            modifier = Modifier.height(48.dp)
        ) {
            Text("−1")
        }
        FilledTonalButton(
            onClick = onAddClick,
            modifier = Modifier
                .height(48.dp)
                .weight(1f)
        ) {
            Text("Добавить количество")
        }
        OutlinedButton(
            onClick = onGoalClick,
            modifier = Modifier.height(48.dp)
        ) {
            Text("Цель")
        }
    }
}
