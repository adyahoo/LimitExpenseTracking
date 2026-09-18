package com.example.myexpensetracking.data.local.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myexpensetracking.data.local.entities.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Query("UPDATE expenses SET amount=:amount, isReachedLimit=:isLimit WHERE id=:id")
    suspend fun updateAmount(id: Int, amount: Int, isLimit: Boolean)

    @Query("UPDATE expenses SET amount=initialAmount, isReachedLimit=0 WHERE type=:type")
    suspend fun resetBudget(type: String)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    // Flow for auto update when new data added
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id=:id")
    fun getExpense(id: Int): Flow<ExpenseEntity>
}