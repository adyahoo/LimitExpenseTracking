package com.example.myexpensetracking.ui.screen.expense_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpensetracking.core.router.ExpenseEditRoute
import com.example.myexpensetracking.ui.component.Dropdown
import com.example.myexpensetracking.ui.component.TextField
import com.example.myexpensetracking.ui.component.TopBar
import com.example.myexpensetracking.ui.screen.add_item.AddItemEffect
import com.example.myexpensetracking.ui.screen.add_item.AddItemFormEvent
import com.example.myexpensetracking.ui.screen.add_item.AddItemFormState
import com.example.myexpensetracking.ui.screen.add_item.AddItemUIState
import com.example.myexpensetracking.ui.screen.add_item.AmountType
import com.example.myexpensetracking.ui.screen.add_item.ExpenseType

@Composable
fun ExpenseEditScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ExpenseEditViewModel = hiltViewModel()
) {
    val formState by vm.formState.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { e ->
            when (e) {
                AddItemEffect.onNavigateBack -> onBack()
                is AddItemEffect.showError -> {}
            }
        }
    }

    ExpenseEditScreenContent(
        onBack,
        vm,
        formState,
        uiState,
        modifier = modifier
    )
}

@Composable
fun ExpenseEditScreenContent(
    onBack: () -> Unit,
    vm: ExpenseEditViewModel,
    formState: AddItemFormState,
    uiState: AddItemUIState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack,
                title = "Edit Item",
                modifier = Modifier.statusBarsPadding()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
        ) {
            TextField(
                label = "Name",
                value = formState.name,
                onValueChange = { vm.onUpdateField(AddItemFormEvent.NameUpdated(it)) }
            )
            Dropdown(
                label = "Type",
                items = ExpenseType.entries,
                selectedValue = formState.type,
                onValueSelected = { vm.onUpdateField(AddItemFormEvent.TypeUpdated(it)) },
                itemToString = { it.displayName },
            )
            Dropdown(
                label = "Amount Type",
                items = AmountType.entries,
                selectedValue = formState.amountType,
                onValueSelected = { vm.onUpdateField(AddItemFormEvent.AmountTypeUpdated(it)) },
                itemToString = { it.displayName }
            )
            TextField(
                label = "Amount",
                value = formState.amount.toString(),
                keyboardType = KeyboardType.Number,
                onValueChange = {
                    if (it.isNotBlank()) vm.onUpdateField(AddItemFormEvent.AmountUpdated(it.toInt()))
                }
            )
            Box(modifier = Modifier.weight(1f))
            Button(
                onClick = { vm.submit() },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Submit",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}