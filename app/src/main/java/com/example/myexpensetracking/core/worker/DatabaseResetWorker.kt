package com.example.myexpensetracking.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.example.myexpensetracking.core.preference.AppPreference
import com.example.myexpensetracking.data.local.daos.ExpenseDao
import com.example.myexpensetracking.data.local.daos.ResetHistoryDao
import com.example.myexpensetracking.data.local.entities.ResetHistoryEntity
import com.example.myexpensetracking.ui.screen.add_item.AmountType
import com.example.myexpensetracking.ui.screen.add_item.ExpenseType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@HiltWorker
class DatabaseResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val expenseDao: ExpenseDao,
    private val resetDao: ResetHistoryDao,
    private val prefs: AppPreference,
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val today = LocalDate.now()
        val todayString = today.toString() // Format: "2026-06-25"

        val currentDateTime = LocalDateTime.now() //
        // Output: 2026-07-08T15:08:23.456 (example)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") //
        val formatted = currentDateTime.format(formatter) //
        // Output: 2026-07-08 15:08:23
        val curTime = formatted.split(" ")[1]
        val histData = ResetHistoryEntity(
            date = todayString,
            time = curTime
        )

        return try {
            val isMonday = today.dayOfWeek == DayOfWeek.MONDAY
            val isFirstMonday = isMonday && today.dayOfMonth <= 7

            // daily reset
            if (prefs.getLastDailyResetDate() != todayString) {
                expenseDao.resetBudget(ExpenseType.HARIAN.displayName)
                prefs.saveLastDailyResetDate(todayString)
                resetDao.addHistory(
                    histData.copy(type = ExpenseType.HARIAN.displayName)
                )
            }

            // weekly reset
            if (isMonday && prefs.getLastWeeklyResetDate() != todayString) {
                expenseDao.resetBudget(ExpenseType.MINGGUAN.displayName)
                prefs.saveLastWeeklyResetDate(todayString)
                resetDao.addHistory(
                    histData.copy(type = ExpenseType.MINGGUAN.displayName)
                )
            }

            // monthly reset
            if (isFirstMonday && prefs.getLastMonthlyResetDate() != todayString) {
                expenseDao.resetBudget(ExpenseType.BULANAN.displayName)
                prefs.saveLastMonthlyResetDate(todayString)
                resetDao.addHistory(
                    histData.copy(type = ExpenseType.BULANAN.displayName)
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            resetDao.addHistory(
                histData.copy(
                    type = "Failed to reset",
                    errMsg = e.message,
                )
            )
            Result.failure()
        }
    }

    companion object {
        private fun getDelayToMidnightInSeconds(): Duration {
            val now = LocalDateTime.now()

            // Tentukan target: Jam 00:00 di hari berikutnya
            val nextMidnight = LocalDateTime.now()
                .with(LocalTime.MIDNIGHT)
                .plusDays(1)

            // Hitung selisih waktu dalam satuan detik
            return Duration.between(now, nextMidnight)
        }

        fun setupPeriodicWork(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            // Jadwalkan berjalan kurang lebih 1 kali sehari (24 Jam)
            val dailyWorkRequest = PeriodicWorkRequestBuilder<DatabaseResetWorker>(
                1,
                TimeUnit.DAYS
            )
                .setInitialDelay(getDelayToMidnightInSeconds())
                .setConstraints(constraints)
                .addTag("periodic_work_tag")
                .build()

            return dailyWorkRequest
        }
    }
}