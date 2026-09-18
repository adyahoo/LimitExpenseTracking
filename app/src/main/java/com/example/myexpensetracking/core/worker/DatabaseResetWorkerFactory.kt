package com.example.myexpensetracking.core.worker

import android.content.Context
import androidx.work.DelegatingWorkerFactory
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.myexpensetracking.core.preference.AppPreference
import com.example.myexpensetracking.data.local.daos.ExpenseDao
import com.example.myexpensetracking.data.local.daos.ResetHistoryDao
import javax.inject.Inject
import javax.inject.Singleton

class DatabaseResetWorkerFactory @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val resetDao: ResetHistoryDao,
    private val prefs: AppPreference,
) : WorkerFactory() {
    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return DatabaseResetWorker(
            appContext,
            workerParameters,
            expenseDao,
            resetDao,
            prefs
        )
    }
}

@Singleton
class DelegateWorkerFactory @Inject constructor(
    private val dbWorkerFactory: DatabaseResetWorkerFactory
) : DelegatingWorkerFactory() {
    init {
        addFactory(dbWorkerFactory)
    }
}