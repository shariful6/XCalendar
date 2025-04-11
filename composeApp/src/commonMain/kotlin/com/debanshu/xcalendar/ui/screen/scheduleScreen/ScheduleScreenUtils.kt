package com.debanshu.xcalendar.ui.screen.scheduleScreen

import com.debanshu.xcalendar.common.lengthOfMonth
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.ui.YearMonth
import com.debanshu.xcalendar.ui.isLeap
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleItem.DayEvents
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleItem.MonthHeader
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleItem.WeekHeader
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlin.collections.chunked
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.last

class MonthRange(private val startMonth: YearMonth) {
    private var minOffset = -25
    private var maxOffset = 25
    private var lastMinOffset = minOffset
    private var lastMaxOffset = maxOffset

    fun getMonths(): List<YearMonth> {
        return (minOffset..maxOffset).map { offset ->
            startMonth.plusMonths(offset)
        }
    }

    fun expandBackward(amount: Int = 10) {
        lastMinOffset = minOffset
        minOffset -= amount
    }

    fun expandForward(amount: Int = 10) {
        lastMaxOffset = maxOffset
        maxOffset += amount
    }

    fun getLastAddedMonthsBackward(): List<YearMonth> {
        return (minOffset until lastMinOffset).map { offset ->
            startMonth.plusMonths(offset)
        }
    }

    fun getLastAddedMonthsForward(): List<YearMonth> {
        return ((lastMaxOffset + 1)..maxOffset).map { offset ->
            startMonth.plusMonths(offset)
        }
    }
}


fun createScheduleItemsForMonthRange(
    months: List<YearMonth>,
    allEvents: List<Event>,
    allHolidays: List<Holiday>
): List<ScheduleItem> {
    val items = mutableListOf<ScheduleItem>()

    months.forEach { yearMonth ->
        // Add month header
        items.add(MonthHeader(yearMonth))

        // Get days in month
        val daysInMonth = calculateDaysInMonth(yearMonth)

        // Group by weeks
        val weeks = daysInMonth.chunked(7)

        weeks.forEach { week ->
            if (week.isNotEmpty()) {
                val firstDay = week.first()
                val lastDay = week.last()

                // Add week header
                items.add(WeekHeader(firstDay, lastDay))

                // Add days with events
                week.forEach { date ->
                    val dayEvents = allEvents.filter { event ->
                        event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                    }

                    val dayHolidays = allHolidays.filter { holiday ->
                        holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                    }

                    if (dayEvents.isNotEmpty() || dayHolidays.isNotEmpty()) {
                        items.add(DayEvents(date, dayEvents, dayHolidays))
                    }
                }
            }
        }
    }

    return items
}

fun calculateDaysInMonth(yearMonth: YearMonth): List<LocalDate> {
    val daysInMonth = yearMonth.month.lengthOfMonth(yearMonth.year.isLeap())
    return (1..daysInMonth).map { day ->
        LocalDate(yearMonth.year, yearMonth.month, day)
    }
}

sealed class ScheduleItem {
    abstract val uniqueId: String

    data class MonthHeader(val yearMonth: YearMonth) : ScheduleItem() {
        override val uniqueId: String = "month_${yearMonth.year}_${yearMonth.month.number}"
    }

    data class WeekHeader(val startDate: LocalDate, val endDate: LocalDate) : ScheduleItem() {
        override val uniqueId: String = "week_${startDate.year}_${startDate.monthNumber}_${startDate.dayOfMonth}"
    }

    data class DayEvents(
        val date: LocalDate,
        val events: List<Event>,
        val holidays: List<Holiday>
    ) : ScheduleItem() {
        override val uniqueId: String = "day_${date.year}_${date.monthNumber}_${date.dayOfMonth}"
    }
}
