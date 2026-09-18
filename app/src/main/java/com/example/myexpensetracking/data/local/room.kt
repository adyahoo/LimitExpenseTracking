package com.example.myexpensetracking.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myexpensetracking.data.local.daos.ExpenseDao
import com.example.myexpensetracking.data.local.daos.ResetHistoryDao
import com.example.myexpensetracking.data.local.entities.ExpenseEntity
import com.example.myexpensetracking.data.local.entities.ResetHistoryEntity

@Database(
    entities = [ExpenseEntity::class, ResetHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun resetHistory(): ResetHistoryDao
}