package com.example.myexpensetracking.ui.screen.expense_edit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.myexpensetracking.core.router.ExpenseEditRoute
import com.example.myexpensetracking.data.local.daos.ExpenseDao
import com.example.myexpensetracking.data.local.entities.ExpenseEntity
import com.example.myexpensetracking.ui.screen.add_item.AddItemEffect
import com.example.myexpensetracking.ui.screen.add_item.AddItemFormEvent
import com.example.myexpensetracking.ui.screen.add_item.AddItemFormState
import com.example.myexpensetracking.ui.screen.add_item.AddItemUIState
import com.example.myexpensetracking.ui.screen.add_item.AmountType
import com.example.myexpensetracking.ui.screen.add_item.ExpenseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseEditViewModel @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val stateHandle: SavedStateHandle
) : ViewModel() {
    private lateinit var currentExpense: ExpenseEntity
    private val routeState = stateHandle.toRoute<ExpenseEditRoute>()
    private val _formState = MutableStateFlow(AddItemFormState())
    private val _uiState = MutableStateFlow(AddItemUIState())

    val formState = _formState.asStateFlow()
    val uiState = _uiState.asStateFlow()
    private val _effect = Channel<AddItemEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        getExpense()
    }

    private fun getExpense() {
        val id = routeState.id

        viewModelScope.launch {
            try {
                expenseDao.getExpense(id).collect { expense ->
                    currentExpense = expense
                    _formState.update {
                        it.copy(
                            name = expense.name,
                            type = ExpenseType.entries.filter { it.displayName == expense.type }[0],
                            amountType = AmountType.entries.filter { it.displayName == expense.amountType }[0],
                            amount = expense.amount.toInt()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("Error Get Expense", e.message ?: "Error Not Found")
            }
        }
    }

    fun onUpdateField(event: AddItemFormEvent) {
        when (event) {
            is AddItemFormEvent.AmountTypeUpdated -> _formState.update { it.copy(amountType = event.value) }
            is AddItemFormEvent.AmountUpdated -> _formState.update { it.copy(amount = event.value) }
            is AddItemFormEvent.NameUpdated -> _formState.update { it.copy(name = event.value) }
            is AddItemFormEvent.TypeUpdated -> _formState.update { it.copy(type = event.value) }
        }
    }

    fun submit() {
        val form = _formState.value
        _uiState.update {
            it.copy(isLoading = true)
        }

        if (form.name.isBlank() || form.amount == 0) {
            return
        }

        viewModelScope.launch {
            try {
                val entity = currentExpense.copy(
                    name = form.name,
                    amount = form.amount.toLong(),
                    initialAmount = form.amount.toLong(),
                    type = form.type.displayName,
                    amountType = form.amountType.displayName,
                )
                Log.d("edit", entity.toString())
                expenseDao.updateExpense(entity)

                _effect.send(AddItemEffect.onNavigateBack)
            } catch (e: Exception) {
                Log.e("Error Insert Expense", e.message ?: "Error Not Found")
                _effect.send(AddItemEffect.showError(e.message ?: "Error Not Found"))
            } finally {
                _uiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}