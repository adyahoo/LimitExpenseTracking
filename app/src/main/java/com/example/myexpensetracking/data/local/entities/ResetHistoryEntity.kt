package com.example.myexpensetracking.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ResetHistories")
data class ResetHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String = "",
    val time: String = "",
    val type: String = "",
    val errMsg: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
