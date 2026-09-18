package com.example.myexpensetracking.utils

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.formatCurrency(): String {
    val idFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    return idFormat.format(this)
}

fun Long.convertMillisToDateTime(): String {
    // 1. Create an Instant from the milliseconds
    val instant = Instant.ofEpochMilli(this)

    // 2. Define a formatter with your preferred pattern
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault()) // Uses the device's local time zone

    // 3. Format the instant into a human-readable String
    return formatter.format(instant)
}