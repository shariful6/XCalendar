package com.debanshu.xcalendar.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.repository.CalendarRepository
import com.debanshu.xcalendar.domain.repository.EventRepository
import com.debanshu.xcalendar.domain.repository.HolidayRepository
import com.debanshu.xcalendar.domain.repository.UserRepository
import com.debanshu.xcalendar.domain.states.CalendarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CalendarViewModel(
    private val userRepository: UserRepository,
    private val calendarRepository: CalendarRepository,
    private val eventRepository: EventRepository,
    private val holidayRepository: HolidayRepository,
) : ViewModel() {
    private val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val startTime = currentDate
        .minus(DatePeriod(months = 10))
        .atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    val endTime = currentDate
        .plus(DatePeriod(months = 10))
        .atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    private val visibleCalendarIds = mutableStateOf<Set<String>>(emptySet())


    private val users = userRepository.getAllUsers()
    private val holidays = holidayRepository.getHolidaysForYear("IN", currentDate.year)
    private val calendars = calendarRepository.getCalendarsForUser("user_id")
    private val events = eventRepository.getEventsForCalendarsInRange(
        listOf("cal_001", "cal_002", "cal_003", "cal_004", "cal_005"),
        startTime,
        endTime
    )

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState = combine(
        _uiState,
        holidays,
        calendars,
        events,
    ) { state, holidays, calendars, events ->
        state.copy(
            holidays = holidays,
            calendars = calendars,
            events = events
        )
    }.onStart {
        init()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalendarUiState()
    )

    private fun init() {
        viewModelScope.launch {
            launch { holidayRepository.updateHolidays("IN", currentDate.year) }
            launch {
                userRepository.getUserFromApi()
                calendarRepository.getCalendersForUser("user_id")
                eventRepository.getEventsForCalendar(emptyList(), startTime, endTime)
            }
        }
    }

    fun setTopAppBarMonthDropdown(viewType: TopBarCalendarView) {
        _uiState.update { it.copy(showMonthDropdown = viewType) }
    }

    fun toggleCalendarVisibility(calendar: Calendar) {
        val updatedCalendar = calendar.copy(isVisible = !calendar.isVisible)

        viewModelScope.launch {
            // calendarRepository.upsertCalendar(updatedCalendar)

            if (updatedCalendar.isVisible) {
                visibleCalendarIds.value += updatedCalendar.id
            } else {
                visibleCalendarIds.value -= updatedCalendar.id
            }

            _uiState.update {
                val updatedCalendars = it.calendars.map { cal ->
                    if (cal.id == calendar.id) updatedCalendar else cal
                }

                it.copy(calendars = updatedCalendars)
            }
        }
    }

    fun showAddEventDialog() {
        _uiState.update { it.copy(showAddEventDialog = true) }
    }

    fun hideAddEventDialog() {
        _uiState.update { it.copy(showAddEventDialog = false) }
    }

    fun selectEvent(event: Event) {
        _uiState.update { it.copy(selectedEvent = event) }
    }

    fun clearSelectedEvent() {
        _uiState.update { it.copy(selectedEvent = null) }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.addEvent(event)

            _uiState.update {
                val updatedEvents = it.events + event
                it.copy(
                    events = updatedEvents,
                    upcomingEvents = CalendarUiState.getUpcomingEvents(
                        updatedEvents,
                        it.selectedDay
                    )
                )
            }
        }
    }

    fun editEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.updateEvent(event)

            _uiState.update {
                val updatedEvents = it.events.map { e ->
                    if (e.id == event.id) event else e
                }
                it.copy(
                    events = updatedEvents,
                    upcomingEvents = CalendarUiState.getUpcomingEvents(
                        updatedEvents,
                        it.selectedDay
                    ),
                    selectedEvent = null
                )
            }
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.deleteEvent(event)

            _uiState.update {
                val updatedEvents = it.events.filter { e -> e.id != event.id }
                it.copy(
                    events = updatedEvents,
                    upcomingEvents = CalendarUiState.getUpcomingEvents(
                        updatedEvents,
                        it.selectedDay
                    ),
                    selectedEvent = null
                )
            }
        }
    }
}