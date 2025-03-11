package com.debanshu.xcalendar.common

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun Month.lengthOfMonth(isLeap: Boolean): Int {
    return when (this) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31

        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeap) 29 else 28
        else -> 0
    }
}