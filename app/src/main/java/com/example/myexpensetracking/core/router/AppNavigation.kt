package com.example.myexpensetracking.core.router

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myexpensetracking.ui.screen.add_item.AddItemScreen
import com.example.myexpensetracking.ui.screen.expense_edit.ExpenseEditScreen
import com.example.myexpensetracking.ui.screen.expense_list.ExpenseListScreen
import com.example.myexpensetracking.ui.screen.reset_history.ResetHistoryScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = ExpenseListRoute) {
        composable<ExpenseListRoute> {
            ExpenseListScreen(
                onNavigateToAddItem = { navController.navigate(AddItemRoute) },
                onNavigateEdit = { id -> navController.navigate(ExpenseEditRoute(id)) },
                onNavigateHistory = { navController.navigate(ResetHistoryRoute) },
            )
        }
        composable<AddItemRoute> {
            AddItemScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<ResetHistoryRoute> {
            ResetHistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable<ExpenseEditRoute> {
            ExpenseEditScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
