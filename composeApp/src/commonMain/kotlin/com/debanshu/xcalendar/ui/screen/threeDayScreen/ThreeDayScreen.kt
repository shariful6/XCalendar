package com.debanshu.xcalendar.ui.screen.threeDayScreen

import androidx.compose.runtime.Composable
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.components.BaseCalendarScreen
import kotlinx.datetime.LocalDate

/**
 * Three-day view screen that displays a 3-day calendar view.
 */
@Composable
fun ThreeDayScreen(
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    BaseCalendarScreen(
        dateStateHolder = dateStateHolder,
        events = events,
        holidays = holidays,
        onEventClick = onEventClick,
        numDays = 3,
        getStartDate = { selectedDate ->
            // Use the selected date as the first day in the 3-day view
            selectedDate
        }
    )
}