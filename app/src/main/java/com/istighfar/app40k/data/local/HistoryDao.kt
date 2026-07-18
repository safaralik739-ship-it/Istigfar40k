package com.istighfar.app40k.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert
    suspend fun insert(entry: HistoryEntry)

    @Query("SELECT * FROM history_entries ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryEntry>>

    @Query("SELECT * FROM history_entries WHERE timestamp >= :fromMillis ORDER BY timestamp DESC")
    fun getSince(fromMillis: Long): Flow<List<HistoryEntry>>

    @Query("SELECT * FROM history_entries WHERE timestamp >= :fromMillis AND timestamp < :toMillis ORDER BY timestamp DESC")
    suspend fun getBetween(fromMillis: Long, toMillis: Long): List<HistoryEntry>

    @Query("SELECT * FROM history_entries ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<HistoryEntry>>

    @Query("DELETE FROM history_entries")
    suspend fun clearAll()
}
