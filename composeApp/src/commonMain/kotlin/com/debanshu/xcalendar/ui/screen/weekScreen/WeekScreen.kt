package com.debanshu.xcalendar.ui.screen.weekScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.CalendarUiState
import com.debanshu.xcalendar.ui.screen.weekScreen.components.SwipeableWeekView
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus

@Composable
fun WeekScreen(
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()

    SwipeableWeekView(
        currentStartDate = CalendarUiState.getWeekStartDate(dateState.selectedDate),
        events = events,
        holidays = holidays,
        onDayClick = { date -> dateStateHolder.updateSelectedDateState(date) },
        onEventClick = onEventClick,
        selectedDay = dateState.selectedDate,
        onWeekChange = { newStartDate ->
            val currentDayOfWeek = dateState.selectedDate.dayOfWeek.ordinal
            val targetDate = newStartDate.plus(DatePeriod(days = currentDayOfWeek))
            dateStateHolder.updateSelectedDateState(targetDate)
        }
    )
}