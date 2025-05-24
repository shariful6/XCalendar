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
    val showMonthDropdown: TopBarCalendarView = TopBarCalendarView.NoView,
    val accounts: List<User> = emptyList(),
    val calendars: List<Calendar> = emptyList(),
    val events: List<Event> = emptyList(),
    val holidays: List<Holiday> = emptyList(),
    val selectedEvent: Event? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val hasError: Boolean get() = errorMessage != null
    val isEmpty: Boolean get() = !isLoading && accounts.isEmpty() && calendars.isEmpty() && events.isEmpty()
}