package com.example.myexpensetracking.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val initialAmount: Long,
    val amount: Long,
    val type: String,
    val amountType: String,
    val isReachedLimit: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)