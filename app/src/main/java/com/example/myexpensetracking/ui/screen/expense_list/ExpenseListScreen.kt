package com.example.myexpensetracking.ui.screen.expense_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpensetracking.data.local.entities.ExpenseEntity
import com.example.myexpensetracking.ui.component.ActionIcon
import com.example.myexpensetracking.ui.component.Spacer
import com.example.myexpensetracking.ui.component.SwipeableItemWithAction
import com.example.myexpensetracking.ui.screen.add_item.AmountType
import com.example.myexpensetracking.ui.screen.expense_list.dialog.DeleteDialog
import com.example.myexpensetracking.ui.screen.expense_list.dialog.LimitDialog
import com.example.myexpensetracking.utils.formatCurrency
import java.util.Locale

@Composable
fun ExpenseListScreen(
    onNavigateToAddItem: () -> Unit,
    onNavigateEdit: (id: Int) -> Unit,
    onNavigateHistory: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ExpenseListViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()

    ExpenseListScreenContent(
        state,
        onNavigateToAddItem = onNavigateToAddItem,
        onClickItem = { item, type -> vm.itemClicked(item, type) },
        onDialogClosed = { vm.onDialogClosed() },
        onSubmitLimit = { item, newAmount -> vm.onSubmitLimit(item, newAmount) },
        onDelete = { item -> vm.onDelete(item) },
        onNavigateEdit = onNavigateEdit,
        onNavigateHistory = onNavigateHistory,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseListScreenContent(
    state: ExpenseListState,
    onNavigateToAddItem: () -> Unit,
    onClickItem: (ExpenseEntity, ExpenseActionType) -> Unit,
    onDialogClosed: () -> Unit,
    onSubmitLimit: (ExpenseEntity, Int) -> Unit,
    onDelete: (ExpenseEntity) -> Unit,
    onNavigateEdit: (id: Int) -> Unit,
    onNavigateHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddItem,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "List Screen", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "history",
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable(
                                onClick = onNavigateHistory
                            )
                    )
                }
            }
            item {
                Spacer(8.dp)
            }

            if (state.items.isEmpty()) {
                item {
                    Text("No Data Found")
                }
            } else {
                state.groupedItems.forEach { (key, items) ->
                    item {
                        Text(
                            key,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    itemsIndexed(
                        items = items
                    ) { i, item ->
                        SwipeableItemWithAction(
                            actions = {
                                ActionIcon(
                                    onClick = { onNavigateEdit(item.id) },
                                    backgroundColor = Color.Blue,
                                    icon = Icons.Default.Edit,
                                )
                                ActionIcon(
                                    onClick = { onClickItem(item, ExpenseActionType.DELETE) },
                                    backgroundColor = Color.Red,
                                    icon = Icons.Default.Delete,
                                )
                            },
                            onExpanded = {},
                            onCollapsed = {},
                            enabled = !item.isReachedLimit
                        ) {
                            ExpenseCard(
                                item,
                                i,
                                { if (!item.isReachedLimit) onClickItem(item, ExpenseActionType.SET_LIMIT) },
                            )
                        }
                    }
                }
            }
        }
        if (state.selectedItem != null) {
            when (state.actionType) {
                ExpenseActionType.SET_LIMIT -> {
                    LimitDialog(
                        item = state.selectedItem,
                        onDismiss = onDialogClosed,
                        onSubmit = onSubmitLimit,
                    )
                }

                ExpenseActionType.EDIT -> {}
                ExpenseActionType.DELETE -> {
                    DeleteDialog(
                        item = state.selectedItem,
                        onDismiss = onDialogClosed,
                        onSubmit = onDelete,
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(
    item: ExpenseEntity,
    i: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val limitColor = Color.Red
    val defaultColor = if (i % 2 == 0)
        Color.DarkGray.copy(alpha = 0.2f) else
        Color.Gray.copy(alpha = 0.2f)

    Card(
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (item.isReachedLimit) limitColor else defaultColor
        ),
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column() {
                Text(
                    text = item.name.capitalize(Locale.ROOT),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (item.amountType == AmountType.FREQ.displayName)
                        "${item.amount}x" else
                        item.amount.formatCurrency(),
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "actions",
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExpenseListScreenPreview() {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {
        Text("ini text baru")
        Button(
            onClick = {
            }
        ) {
            Text("ini button")
        }
    }
}