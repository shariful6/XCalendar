package com.debanshu.xcalendar.ui.screen.scheduleScreen

import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.common.model.YearMonth
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

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