package com.example.myexpensetracking

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.example.myexpensetracking.core.worker.DatabaseResetWorker
import com.example.myexpensetracking.core.worker.DelegateWorkerFactory
import com.example.myexpensetracking.utils.convertMillisToDateTime
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltAndroidApp
class ExpenseApp : Application() {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WorkerFactoryEntryPoint {
        fun workerFactory(): DelegateWorkerFactory
    }

    override fun onCreate() {
        val workManagerConfiguration: Configuration = Configuration.Builder()
            .setWorkerFactory(EntryPoints.get(this, WorkerFactoryEntryPoint::class.java).workerFactory())
            .setMinimumLoggingLevel(Log.VERBOSE)
            .build()
        WorkManager.initialize(this, workManagerConfiguration)

        val workManager = WorkManager.getInstance(this)
        val request = DatabaseResetWorker.setupPeriodicWork()

        workManager.enqueueUniquePeriodicWork(
            "reset_budget_limit",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        super.onCreate()
    }
}
