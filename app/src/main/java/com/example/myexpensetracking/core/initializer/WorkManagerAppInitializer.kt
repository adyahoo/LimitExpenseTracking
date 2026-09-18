package com.example.myexpensetracking.core.initializer

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.example.myexpensetracking.core.worker.DatabaseResetWorker
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkManagerInitializerEntryPoint {
    fun hiltWorkerFactory(): HiltWorkerFactory
}

class WorkManagerAppInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WorkManagerInitializerEntryPoint::class.java
        )
        val workerFactory = entryPoint.hiltWorkerFactory()

        val config = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

        WorkManager.initialize(context, config)
        val workManager = WorkManager.getInstance(context)
        val request = DatabaseResetWorker.setupPeriodicWork()

        workManager.enqueueUniquePeriodicWork(
            "reset_budget_limit",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        return workManager
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return emptyList()
    }
}