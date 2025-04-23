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
    val selectedDay: LocalDate = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()
    ).date,

    // View state
    val showMonthDropdown: TopBarCalendarView = TopBarCalendarView.NoView,

    // Data
    val accounts: List<User> = emptyList(),
    val calendars: List<Calendar> = emptyList(),
    val events: List<Event> = emptyList(),
    val holidays: List<Holiday> = emptyList(),

    // Derived data for different views
    val weekStartDate: LocalDate = getWeekStartDate(selectedDay),
    val threeDayStartDate: LocalDate = get3DayStartDate(selectedDay),
    val upcomingEvents: List<Event> = getUpcomingEvents(events, selectedDay),

    // UI state
    val showAddEventDialog: Boolean = false,
    val selectedEvent: Event? = null
) {
    companion object {
        internal fun getWeekStartDate(date: LocalDate): LocalDate {
            val dayOfWeek = date.dayOfWeek.ordinal % 7
            return date.minus(DatePeriod(days = dayOfWeek))
        }

        internal fun get3DayStartDate(date: LocalDate): LocalDate {
            val dayOfWeek = date.dayOfWeek.ordinal % 3
            return date.minus(DatePeriod(days = dayOfWeek))
        }

        internal fun getOneDayStartDate(date: LocalDate): LocalDate {
            return date
        }

        internal fun getUpcomingEvents(events: List<Event>, fromDate: LocalDate): List<Event> {
            val fromInstant = fromDate.atStartOfDayIn(TimeZone.currentSystemDefault())
            val toInstant = fromDate.plus(DatePeriod(days = 30)).atStartOfDayIn(TimeZone
                .currentSystemDefault())

            return events
                .filter {
                    it.startTime >= fromInstant.toEpochMilliseconds()
                            && it.startTime <= toInstant.toEpochMilliseconds()
                }
                .sortedBy { it.startTime }

        }
    }
}