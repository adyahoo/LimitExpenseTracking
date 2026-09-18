package com.example.myexpensetracking.ui.screen.expense_list.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.myexpensetracking.data.local.entities.ExpenseEntity
import com.example.myexpensetracking.ui.component.Spacer
import com.example.myexpensetracking.ui.component.TextField
import com.example.myexpensetracking.ui.screen.add_item.AmountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitDialog(
    item: ExpenseEntity,
    onDismiss: () -> Unit,
    onSubmit: (item: ExpenseEntity, amount: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var amount by remember { mutableStateOf(0) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            val annotatedString = buildAnnotatedString {
                append("The item ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(item.name)
                }
                append("\nhas reached limit?")
            }
            Text(annotatedString, maxLines = 2)
            Spacer(height = 16.dp)
            if (item.amountType == AmountType.BUDGET.displayName) {
                TextField(
                    value = amount.toString(),
                    onValueChange = { amount = it.toInt() },
                    keyboardType = KeyboardType.Number
                )
                Spacer(height = 16.dp)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Spacer(width = 16.dp)
                Button(
                    onClick = { onSubmit(item, amount) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Yes")
                }
            }
        }
    }
}