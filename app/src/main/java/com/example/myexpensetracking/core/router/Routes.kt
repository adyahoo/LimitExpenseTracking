package com.example.myexpensetracking.core.router

import kotlinx.serialization.Serializable

@Serializable
object ExpenseListRoute

@Serializable
object AddItemRoute

@Serializable
object ResetHistoryRoute

@Serializable
data class ExpenseEditRoute(val id: Int)