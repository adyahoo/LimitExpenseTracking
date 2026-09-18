package com.example.myexpensetracking.ui.screen.reset_history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myexpensetracking.data.local.entities.ResetHistoryEntity
import com.example.myexpensetracking.ui.component.TopBar

@Composable
fun ResetHistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: ResetHistoryViewModel = hiltViewModel(),
) {
    val histories by vm.histories.collectAsStateWithLifecycle()
    val nextExecute by vm.nextExecute.collectAsStateWithLifecycle()

    ResetHistoryScreenContent(
        histories,
        nextExecute,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun ResetHistoryScreenContent(
    histories: List<ResetHistoryEntity>,
    nextExecute: String?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopBar(
                onBack = onBack,
                title = "Reset History",
                modifier = Modifier.statusBarsPadding()
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (nextExecute != null) {
                item {
                    Text(
                        "Next Execute: $nextExecute",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            if (histories.isEmpty()) {
                item {
                    Text(
                        "Data not found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                items(count = histories.size, key = { i -> histories[i].id }) { i ->
                    HistoryItem(histories[i])
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun HistoryItem(hist: ResetHistoryEntity, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("${hist.id + 1}")
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Reset: ${hist.type}",
                fontWeight = FontWeight.Bold
            )
            if (hist.errMsg != null) {
                Text(
                    hist.errMsg,
                    maxLines = 2,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                hist.date,
                color = Color.Gray
            )
            Text(
                hist.time,
                color = Color.Gray
            )
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun Prev(modifier: Modifier = Modifier) {
//    val hists = listOf(
//        ResetHistoryEntity(id = 0, type = "Mingguan", date = "10 February 2026", time = "10:00 AM"),
//        ResetHistoryEntity(id = 1, type = "Mingguan", date = "10 February 2026", time = "10:00 AM"),
//        ResetHistoryEntity(
//            id = 2,
//            type = "Mingguan",
//            date = "10 February 2026",
//            time = "10:00 AM",
//            errMsg = "error nih"
//        ),
//    )
//    ResetHistoryScreenContent(
//        histories = hists,
//        nextExecute = "adsf",
//        onBack = {}
//    )
//}