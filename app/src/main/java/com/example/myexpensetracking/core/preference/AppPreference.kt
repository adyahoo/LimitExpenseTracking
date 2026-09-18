package com.example.myexpensetracking.core.preference

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

object PREF_KEY {
    val dailyReset = "DAILY_RESET"
    val weeklyReset = "WEEKLY_RESET"
    val monthlyReset = "MONTHLY_RESET"
}

class AppPreference @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    fun getLastDailyResetDate(): String = sharedPreferences.getString(PREF_KEY.dailyReset, "") ?: ""
    fun saveLastDailyResetDate(date: String) {
        sharedPreferences.edit {
            putString(PREF_KEY.dailyReset, date)
        }
    }

    fun getLastWeeklyResetDate(): String = sharedPreferences.getString(PREF_KEY.weeklyReset, "") ?: ""
    fun saveLastWeeklyResetDate(date: String) {
        sharedPreferences.edit {
            putString(PREF_KEY.weeklyReset, date)
        }
    }

    fun getLastMonthlyResetDate(): String = sharedPreferences.getString(PREF_KEY.monthlyReset, "") ?: ""
    fun saveLastMonthlyResetDate(date: String) {
        sharedPreferences.edit {
            putString(PREF_KEY.monthlyReset, date)
        }
    }
}