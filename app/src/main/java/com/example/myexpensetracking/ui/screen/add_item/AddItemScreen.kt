package com.example.myexpensetracking.ui.screen.add_item

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myexpensetracking.ui.component.Dropdown
import com.example.myexpensetracking.ui.component.TextField
import com.example.myexpensetracking.ui.component.TopBar
import com.example.myexpensetracking.ui.theme.MyExpenseTrackingTheme

@Composable
fun AddItemScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: AddItemViewModel = hiltViewModel()
) {
    val formState by vm.state.collectAsStateWithLifecycle()
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { e ->
            when (e) {
                AddItemEffect.onNavigateBack -> onBack()
                is AddItemEffect.showError -> {}
            }
        }
    }

    AddItemScreenContent(
        onBack,
        vm,
        formState,
        uiState,
        modifier = modifier
    )
}

@Composable
fun AddItemScreenContent(
    onBack: () -> Unit,
    vm: AddItemViewModel,
    formState: AddItemFormState,
    uiState: AddItemUIState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack,
                title = "Add Item",
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
                itemToString = { it.displayName }
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

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun ExpenseListScreenPreview() {
//    MyExpenseTrackingTheme {
//        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//            AddItemScreen(
//                {},
//                modifier = Modifier.padding(innerPadding)
//            )
//        }
//    }
//}