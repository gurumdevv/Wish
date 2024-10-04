package com.gurumlab.wish.ui.util

import android.content.Context
import com.google.firebase.Timestamp
import com.gurumlab.wish.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault
import java.time.format.DateTimeFormatter


object DateTimeConverter {

    fun getCurrentDate(): Int {
        val currentDate = LocalDate.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        return formattedDate.toInt()
    }

    fun getDateMinusDays(monthsToSubtract: Int): Int {
        val currentDate = LocalDate.now()
        val newDate = currentDate.minusMonths(monthsToSubtract.toLong())
        val formattedDate = newDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        return formattedDate.toInt()
    }

    fun getDateTime(timestamp: Timestamp, context: Context): String {
        val current = LocalDateTime.now()
        val target = timestamp.toDate().toInstant().atZone(systemDefault()).toLocalDateTime()

        if (current.dayOfMonth == target.dayOfMonth) {
            return context.getString(R.string.today)
        } else if (current.dayOfMonth - target.dayOfMonth == 1) {
            return context.getString(R.string.yesterday)
        } else {
            val month = context.getString(R.string.month)
            val day = context.getString(R.string.day)
            return target.format(DateTimeFormatter.ofPattern("M$month d$day"))
        }
    }
}