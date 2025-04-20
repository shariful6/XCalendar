package com.debanshu.xcalendar.ui.screen.threeDayScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.domain.states.ViewType
import com.debanshu.xcalendar.ui.components.BaseCalendarScreen
import kotlinx.datetime.LocalDate

/**
 * Three-day view screen that displays a 3-day calendar view.
 */
@Composable
fun ThreeDayScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit,
    onDateClick: (LocalDate) -> Unit,
) {
    BaseCalendarScreen(
        modifier = modifier,
        dateStateHolder = dateStateHolder,
        events = events,
        holidays = holidays,
        onEventClick = onEventClick,
        numDays = 3,
        viewType = ViewType.THREE_DAY_VIEW,
        onDateClick = onDateClick
    )
}