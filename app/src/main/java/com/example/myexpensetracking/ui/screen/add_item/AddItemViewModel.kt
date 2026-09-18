package com.example.myexpensetracking.ui.screen.add_item

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myexpensetracking.data.local.daos.ExpenseDao
import com.example.myexpensetracking.data.local.entities.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ExpenseType(val displayName: String) {
    HARIAN("Harian"),
    MINGGUAN("Mingguan"),
    BULANAN("Bulanan"),
}

enum class AmountType(val displayName: String) {
    FREQ("Frequency"),
    BUDGET("Budget")
}

data class AddItemFormState(
    val name: String = "",
    val type: ExpenseType = ExpenseType.MINGGUAN,
    val amountType: AmountType = AmountType.FREQ,
    val amount: Int = 0,
)

data class AddItemUIState(
    val isLoading: Boolean = false
)

sealed interface AddItemFormEvent {
    data class NameUpdated(val value: String) : AddItemFormEvent
    data class TypeUpdated(val value: ExpenseType) : AddItemFormEvent
    data class AmountTypeUpdated(val value: AmountType) : AddItemFormEvent
    data class AmountUpdated(val value: Int) : AddItemFormEvent
}

sealed interface AddItemEffect {
    object onNavigateBack : AddItemEffect
    data class showError(val msg: String) : AddItemEffect
}

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val expenseDao: ExpenseDao
) : ViewModel() {
    private val _state = MutableStateFlow(AddItemFormState())
    private val _uiState = MutableStateFlow(AddItemUIState())
    val state = _state.asStateFlow()
    val uiState = _uiState.asStateFlow()

    val _effect = Channel<AddItemEffect>()
    val effect = _effect.receiveAsFlow()

    fun onUpdateField(event: AddItemFormEvent) {
        when (event) {
            is AddItemFormEvent.AmountTypeUpdated -> _state.update { it.copy(amountType = event.value) }
            is AddItemFormEvent.AmountUpdated -> _state.update { it.copy(amount = event.value) }
            is AddItemFormEvent.NameUpdated -> _state.update { it.copy(name = event.value) }
            is AddItemFormEvent.TypeUpdated -> _state.update { it.copy(type = event.value) }
        }
    }

    fun submit() {
        val form = _state.value
        _uiState.update {
            it.copy(isLoading = true)
        }

        if (form.name.isBlank() || form.amount == 0) {
            return
        }

        viewModelScope.launch {
            try {
                val entity = ExpenseEntity(
                    name = form.name,
                    amount = form.amount.toLong(),
                    initialAmount = form.amount.toLong(),
                    type = form.type.displayName,
                    amountType = form.amountType.displayName,
                )
                expenseDao.insertExpense(entity)

                _state.value = AddItemFormState()
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