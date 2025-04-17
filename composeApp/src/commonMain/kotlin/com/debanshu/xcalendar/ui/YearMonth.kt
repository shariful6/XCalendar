package com.debanshu.xcalendar.ui

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number

/**
 * Represents a year-month combination without a day component
 */
data class YearMonth(val year: Int, val month: Month) {

    constructor(year: Int, monthNumber: Int) : this(
        year,
        Month(monthNumber)
    )

    /**
     * Add months to this YearMonth
     */
    fun plusMonths(months: Int): YearMonth {
        var newYear = year
        var newMonthNum = month.number + months

        while (newMonthNum > 12) {
            newMonthNum -= 12
            newYear++
        }

        while (newMonthNum < 1) {
            newMonthNum += 12
            newYear--
        }

        return YearMonth(newYear, Month(newMonthNum))
    }

    /**
     * Format as a string
     */
    override fun toString(): String {
        return "$year-${month.number.toString().padStart(2, '0')}"
    }

    companion object {
        /**
         * Create a YearMonth from a LocalDate
         */
        fun from(date: LocalDate): YearMonth {
            return YearMonth(date.year, date.month)
        }
    }
}

/**
 * Extension function to check if a year is a leap year
 */
fun Int.isLeap(): Boolean {
    return (this % 4 == 0 && this % 100 != 0) || (this % 400 == 0)
}