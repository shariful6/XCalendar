package com.debanshu.xcalendar.domain.states

import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.model.User
import com.debanshu.xcalendar.ui.TopBarCalendarView
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
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
    val upcomingEvents: List<Event> = getUpcomingEvents(events, selectedDay),

    // UI state
    val showAddEventDialog: Boolean = false,
    val selectedEvent: Event? = null
) {
    companion object {
        internal fun getUpcomingEvents(events: List<Event>, fromDate: LocalDate): List<Event> {
            val fromInstant = fromDate.atStartOfDayIn(TimeZone.currentSystemDefault())
            val toInstant = fromDate.plus(DatePeriod(days = 30)).atStartOfDayIn(
                TimeZone
                    .currentSystemDefault()
            )

            return events
                .filter {
                    it.startTime >= fromInstant.toEpochMilliseconds() && it.startTime <= toInstant.toEpochMilliseconds()
                }
                .sortedBy { it.startTime }

        }
    }
}