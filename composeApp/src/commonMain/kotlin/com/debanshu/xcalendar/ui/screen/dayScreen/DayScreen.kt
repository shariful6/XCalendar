package com.debanshu.xcalendar.ui.screen.dayScreen

import androidx.compose.runtime.Composable
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.components.BaseCalendarScreen
import kotlinx.datetime.LocalDate

/**
 * Day view screen that displays a single day calendar view.
 */
@Composable
fun DayScreen(
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
        numDays = 1,
        getStartDate = { selectedDate ->
            // Use the selected date as the only day in the view
            selectedDate
        }
    )
}