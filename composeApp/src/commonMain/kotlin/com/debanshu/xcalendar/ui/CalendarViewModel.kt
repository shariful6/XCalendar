package com.debanshu.xcalendar.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.debanshu.xcalendar.common.model.asCalendar
import com.debanshu.xcalendar.common.model.asEvent
import com.debanshu.xcalendar.common.model.asHoliday
import com.debanshu.xcalendar.data.remoteDataSource.HolidayApiService
import com.debanshu.xcalendar.data.remoteDataSource.RemoteCalendarApiService
import com.debanshu.xcalendar.data.remoteDataSource.Result
import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.User
import com.debanshu.xcalendar.domain.repository.CalendarRepository
import com.debanshu.xcalendar.domain.repository.EventRepository
import com.debanshu.xcalendar.domain.repository.HolidayRepository
import com.debanshu.xcalendar.domain.repository.UserRepository
import com.debanshu.xcalendar.domain.states.CalendarUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val apiService: RemoteCalendarApiService,
    private val holidayApiService: HolidayApiService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    private val now = Clock.System.now()
    private val currentDate = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val visibleCalendarIds = mutableStateOf<Set<String>>(emptySet())

    init {
        viewModelScope.launch {
            launch { loadUsers() }
            launch { loadHolidays("IN", currentDate.year) }
        }
    }

    private suspend fun loadUsers() {
        userRepository.getAllUsers().collectLatest { users ->
            if (users.isEmpty()) {
                val dummyUser = User(
                    id = "user_id",
                    name = "Demo User",
                    email = "user@example.com",
                    photoUrl = "https://t4.ftcdn.net/jpg/00/04/09/63/360_F_4096398_nMeewldssGd7guDmvmEDXqPJUmkDWyqA.jpg"
                )
                userRepository.addUser(dummyUser)
                _uiState.update { it.copy(accounts = listOf(dummyUser)) }
                loadCalendarsForUser(dummyUser.id)
            } else {
                _uiState.update { it.copy(accounts = users) }

                users.forEach { user ->
                    loadCalendarsForUser(user.id)
                }
            }
        }
    }

    private suspend fun loadCalendarsForUser(userId: String) {
        calendarRepository.getCalendarsForUser(userId).collectLatest { dbCalendars ->
           if (dbCalendars.isEmpty()) {
                when(val apiCalendars = apiService.fetchCalendarsForUser(userId)){
                    is Result.Error -> {
                        println("HEREEEEEEE" + apiCalendars.error.toString())
                    }
                    is Result.Success -> {
                        val calendars = apiCalendars.data.map { it.asCalendar() }
                        _uiState.update { it.copy(calendars = calendars) }
                        calendars.forEach { calendar ->
                            calendarRepository.upsertCalendar(calendar)
                        }
                        loadEventsForCalendars(calendars.map { it.id })
                        //updateVisibleCalendars(calendars)
                    }
                }
            } else {
                //updateVisibleCalendars(dbCalendars)

                _uiState.update { it.copy(calendars = dbCalendars) }

                loadEventsForCalendars(dbCalendars.map { it.id })
            }
        }
    }

//    private fun updateVisibleCalendars(calendars: List<Calendar>) {
//        val visibleIds = calendars.filter { it.isVisible }.map { it.id }.toSet()
//        visibleCalendarIds.value += visibleIds
//    }

    private suspend fun loadEventsForCalendars(calendarIds: List<String>) {
        val startDate = currentDate.minus(DatePeriod(months = 10))
        val endDate = currentDate.plus(DatePeriod(months = 10))

        val startTime = startDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val endTime = endDate.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()

        eventRepository.getEventsForCalendarsInRange(calendarIds, startTime, endTime).collectLatest { dbEvents ->
            if (dbEvents.isEmpty()) {
                val allEvents = mutableListOf<Event>()
                calendarIds.forEach { calendarId ->
                    when(val apiEvents = apiService.fetchEventsForCalendar(calendarId, startTime, endTime)){
                        is Result.Error -> {
                            println("HEREEEEEEE" + apiEvents.error.toString())
                        }
                        is Result.Success -> {
                            val events = apiEvents.data.map { it.asEvent() }
                            events.forEach { event ->
                                eventRepository.addEvent(event)
                            }
                            allEvents.addAll(events)
                            _uiState.update {
                                it.copy(
                                    events = allEvents,
                                    upcomingEvents = CalendarUiState.getUpcomingEvents(allEvents, currentDate)
                                )
                            }
                        }
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        events = dbEvents,
                        upcomingEvents = CalendarUiState.getUpcomingEvents(dbEvents, currentDate)
                    )
                }
            }
        }
    }

    private suspend fun loadHolidays(countryCode: String, year: Int) {
        val holidays = holidayRepository.getHolidaysForYear(countryCode, year)
        holidays.collectLatest { days ->
            if (days.isEmpty()) {
                when (val response = holidayApiService.getHolidays(countryCode, year)) {
                    is Result.Error -> {
                        println("HEREEEEEEE" + response.error.toString())
                    }

                    is Result.Success -> {
                        val remoteHolidays = response.data.response.holidays.map { it.asHoliday() }
                        holidayRepository.addHolidays(remoteHolidays)
                        _uiState.update { it.copy(holidays = remoteHolidays) }
                    }
                }
            } else {
                _uiState.update { it.copy(holidays = days) }
            }
        }
    }

    fun setTopAppBarMonthDropdown(viewType: TopBarCalendarView) {
        _uiState.update { it.copy(showMonthDropdown = viewType) }
    }

    fun toggleCalendarVisibility(calendar: Calendar) {
        val updatedCalendar = calendar.copy(isVisible = !calendar.isVisible)

        viewModelScope.launch {
            calendarRepository.upsertCalendar(updatedCalendar)

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
                    upcomingEvents = CalendarUiState.getUpcomingEvents(updatedEvents, it.selectedDay)
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
                    upcomingEvents = CalendarUiState.getUpcomingEvents(updatedEvents, it.selectedDay),
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
                    upcomingEvents = CalendarUiState.getUpcomingEvents(updatedEvents, it.selectedDay),
                    selectedEvent = null
                )
            }
        }
    }
}