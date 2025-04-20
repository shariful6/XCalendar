package com.debanshu.xcalendar.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.domain.states.ViewType
import com.debanshu.xcalendar.ui.CalendarUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * Base calendar screen that provides common structure for day, three-day, and week views.
 *
 * @param dateStateHolder The date state holder
 * @param events The list of events to display
 * @param holidays The list of holidays to display
 * @param onEventClick Callback for when an event is clicked
 * @param numDays The number of days to display (1 for day view, 3 for three-day view, 7 for week view)
 */
@Composable
fun BaseCalendarScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit,
    onDateClick: (LocalDate) -> Unit,
    numDays: Int,
    viewType: ViewType,
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val verticalScrollState = rememberScrollState()
    val timeColumnWidth = 60.dp
    val timeRange = 0..23
    val hourHeightDp = 60f

    val startDate = dateState.viewStartDate
    Row(
        modifier = modifier
    ) {
        TimeColumn(
            modifier = Modifier
                .padding(top = hourHeightDp.dp)
                .width(timeColumnWidth),
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState
        )
        SwipeableCalendarView(
            startDate = startDate,
            events = events,
            holidays = holidays,
            onDayClick = { date ->
                dateStateHolder.updateSelectedDateState(date, viewType)
                onDateClick(
                    when (viewType) {
                        ViewType.MONTH_VIEW -> CalendarUiState.getWeekStartDate(date)
                        ViewType.WEEK_VIEW -> CalendarUiState.getWeekStartDate(date)
                        ViewType.THREE_DAY_VIEW -> CalendarUiState.get3DayStartDate(date)
                        ViewType.ONE_DAY_VIEW -> CalendarUiState.getOneDayStartDate(date)
                    }
                )
            },
            onEventClick = onEventClick,
            selectedDay = dateState.selectedDate,
            onDateRangeChange = { newStartDate ->
                dateStateHolder.updateViewStartDate(newStartDate)
            },
            numDays = numDays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState,
            currentDate = dateState.currentDate
        )
    }
}