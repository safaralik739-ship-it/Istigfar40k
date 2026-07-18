package com.istighfar.app40k.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class EntryType {
    INCREMENT,   // +1
    DECREMENT,   // -1
    MANUAL_ADD,  // добавление количества вручную (тасбих)
    RESET        // сброс счётчика
}

@Entity(tableName = "history_entries")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Int,          // например +350, +1, -1
    val resultingTotal: Int,  // итоговое значение счётчика после этой операции
    val type: EntryType,
    val timestamp: Long       // System.currentTimeMillis()
)
