package com.example.myexpensetracking.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myexpensetracking.data.local.entities.ResetHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResetHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHistory(hist: ResetHistoryEntity)

    @Query("SELECT * FROM resethistories ORDER BY createdAt DESC")
    fun getAllHistories(): Flow<List<ResetHistoryEntity>>
}