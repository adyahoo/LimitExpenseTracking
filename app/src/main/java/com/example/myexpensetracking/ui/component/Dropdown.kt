package com.example.myexpensetracking.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> Dropdown(
    label: String?,
    items: List<T>,
    selectedValue: T?,
    onValueSelected: (T) -> Unit,
    itemToString: (T) -> String,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column {
        if (label != null) {
            Text(
                label,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 8.dp)
            )
        }
        Box(
            modifier = modifier.fillMaxWidth()
        ) {
            TextField(
                label = null,
                value = selectedValue?.let { itemToString(it) } ?: "",
                onValueChange = {},
                isReadOnly = true,
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = { isExpanded = true })
            )
            DropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(text = itemToString(item))
                        },
                        onClick = {
                            onValueSelected(item)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}