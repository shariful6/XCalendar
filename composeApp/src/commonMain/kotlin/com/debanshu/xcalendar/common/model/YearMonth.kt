package com.debanshu.xcalendar.common.model

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


    override fun toString(): String {
        return "$year-${month.number.toString().padStart(2, '0')}"
    }

    companion object {
        fun from(date: LocalDate): YearMonth {
            return YearMonth(date.year, date.month)
        }
    }
}