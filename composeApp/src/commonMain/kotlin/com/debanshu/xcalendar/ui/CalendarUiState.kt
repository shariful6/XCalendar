package com.debanshu.xcalendar.ui

import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

data class CalendarUiState(
    val currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val selectedDay: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val selectedMonth: YearMonth = YearMonth(
        selectedDay.year,
        selectedDay.month
    ),

    // View state
    val currentView: CalendarView = CalendarView.Month,
    val showMonthDropdown: Boolean = false,

    // Data
    val accounts: List<User> = emptyList(),
    val calendars: List<Calendar> = emptyList(),
    val events: List<Event> = emptyList(),
    val holidays: List<Holiday> = emptyList(),

    // Derived data for different views
    val weekStartDate: LocalDate = getWeekStartDate(selectedDay),
    val threeDayStartDate: LocalDate = selectedDay,
    val upcomingEvents: List<Event> = getUpcomingEvents(events, selectedDay),

    // UI state
    val showAddEventDialog: Boolean = false,
    val selectedEvent: Event? = null
) {
    companion object {
        internal fun getWeekStartDate(date: LocalDate): LocalDate {
            // Get the previous Sunday (or the date itself if it's Sunday)
            val dayOfWeek = date.dayOfWeek.ordinal % 7 // 0 for Sunday, 1-6 for Monday-Saturday
            return date.minus(DatePeriod(days = dayOfWeek))
        }

        internal fun getUpcomingEvents(events: List<Event>, fromDate: LocalDate): List<Event> {
            val fromInstant = fromDate.atStartOfDayIn(TimeZone.currentSystemDefault())
            val toInstant = fromDate.plus(DatePeriod(days = 30)).atStartOfDayIn(TimeZone
                .currentSystemDefault())

            return events
                .filter { it.startTime >= fromInstant.toEpochMilliseconds() && it.startTime <= toInstant.toEpochMilliseconds() }
                .sortedBy { it.startTime }

        }
    }
}