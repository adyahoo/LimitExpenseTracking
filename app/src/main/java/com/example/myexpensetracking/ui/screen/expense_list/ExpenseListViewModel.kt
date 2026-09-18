package com.example.myexpensetracking.ui.screen.expense_list

import android.util.Log
import androidx.compose.runtime.snapshots.toInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpensetracking.data.local.daos.ExpenseDao
import com.example.myexpensetracking.data.local.entities.ExpenseEntity
import com.example.myexpensetracking.ui.screen.add_item.AmountType
import com.example.myexpensetracking.ui.screen.add_item.ExpenseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.exp

data class ExpenseListState(
    val items: List<ExpenseEntity> = emptyList(),
    val groupedItems: Map<String, List<ExpenseEntity>> = emptyMap(),
    val selectedItem: ExpenseEntity? = null,
    val actionType: ExpenseActionType = ExpenseActionType.SET_LIMIT,
)

enum class ExpenseActionType { SET_LIMIT, EDIT, DELETE }

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val expenseDao: ExpenseDao
) : ViewModel() {
    private val _selectedItem = MutableStateFlow<ExpenseEntity?>(null)
    private val _selectedAction = MutableStateFlow<ExpenseActionType>(ExpenseActionType.SET_LIMIT)

    val state: StateFlow<ExpenseListState> = combine(
        expenseDao.getAllExpenses(),
        _selectedItem,
        _selectedAction,
    ) { dbExpense, selectedItem, selectedAction ->
        val grouped = dbExpense.groupBy { it.type }
        val itemOrder = ExpenseType.entries.map { it.displayName }
        val orderedGroup = itemOrder.associateWith { grouped[it] ?: emptyList() }

        ExpenseListState(
            items = dbExpense,
            groupedItems = orderedGroup,
            selectedItem = selectedItem,
            actionType = selectedAction,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3000),
        initialValue = ExpenseListState()
    )

    fun itemClicked(item: ExpenseEntity, type: ExpenseActionType) {
        _selectedItem.value = item
        _selectedAction.value = type
    }

    fun onDialogClosed() {
        _selectedItem.value = null
    }

    fun onSubmitLimit(item: ExpenseEntity, newAmount: Int) {
        viewModelScope.launch {
            val updatedAmount: Int = if (item.amountType == AmountType.FREQ.displayName) {
                (item.amount.toInt() - 1).coerceAtLeast(0)
            } else {
                (item.amount.toInt() - newAmount).coerceAtLeast(0)
            }

            expenseDao.updateAmount(item.id, updatedAmount, updatedAmount == 0)
        }
        onDialogClosed()
    }

    fun onDelete(item: ExpenseEntity) {
        viewModelScope.launch {
            expenseDao.deleteExpense(item)
        }
        onDialogClosed()
    }
}
