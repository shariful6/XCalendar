package com.debanshu.xcalendar.ui

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

/**
 * Represents a year-month combination without a day component
 */
data class YearMonth(val year: Int, val month: Month) {

    constructor(year: Int, monthNumber: Int) : this(
        year,
        Month(monthNumber)
    )

    /**
     * Get the length of this month, considering leap years
     */
    private fun lengthOfMonth(): Int {
        return when (month) {
            Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
            Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            Month.FEBRUARY -> if (year.isLeap()) 29 else 28
            else -> 0
        }
    }

    /**
     * Get the first date of this month
     */
    fun atDay(day: Int): LocalDate {
        require(day in 1..lengthOfMonth()) { "Day must be valid for month" }
        return LocalDate(year, month, day)
    }

    /**
     * Get the first date of this month
     */
    fun atStartOfMonth(): LocalDate {
        return LocalDate(year, month, 1)
    }

    /**
     * Get the last date of this month
     */
    fun atEndOfMonth(): LocalDate {
        return LocalDate(year, month, lengthOfMonth())
    }

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
         * Create a YearMonth from the current date
         */
        fun now(timeZone: TimeZone = TimeZone.currentSystemDefault()): YearMonth {
            val now = Clock.System.now().toLocalDateTime(timeZone)
            return YearMonth(now.year, now.month)
        }

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