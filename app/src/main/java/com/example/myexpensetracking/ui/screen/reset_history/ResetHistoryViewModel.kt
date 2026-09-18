package com.example.myexpensetracking.ui.screen.reset_history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.myexpensetracking.data.local.daos.ResetHistoryDao
import com.example.myexpensetracking.data.local.entities.ResetHistoryEntity
import com.example.myexpensetracking.utils.convertMillisToDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetHistoryViewModel @Inject constructor(
    private val resetHistoryDao: ResetHistoryDao,
    private val workManager: WorkManager,
) : ViewModel() {
    private val _nextExecute = MutableStateFlow<String?>(null)
    val nextExecute: StateFlow<String?> = _nextExecute.asStateFlow()
    val histories = resetHistoryDao.getAllHistories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(3000),
            initialValue = emptyList()
        )

    init {
        getNextExecute()
    }

    private fun getNextExecute() {
        viewModelScope.launch {
            workManager.getWorkInfosForUniqueWorkFlow("reset_budget_limit")
                .collect { workInfos ->
                    val info = workInfos.firstOrNull()

                    if (info != null && info.state == WorkInfo.State.ENQUEUED) {
                        _nextExecute.value = info.nextScheduleTimeMillis.convertMillisToDateTime()
                    }
                }
        }
    }
}