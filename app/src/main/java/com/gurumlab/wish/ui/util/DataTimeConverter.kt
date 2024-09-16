package com.gurumlab.wish.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter


object DataTimeConverter {

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
}