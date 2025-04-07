package com.debanshu.xcalendar.ui.components

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
 * @param getStartDate Function to determine the start date based on the selected date
 */
@Composable
fun BaseCalendarScreen(
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit,
    numDays: Int,
    getStartDate: (selectedDate: LocalDate) -> LocalDate = { it }
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val verticalScrollState = rememberScrollState()
    val timeColumnWidth = 60.dp
    val timeRange = 0..23
    val hourHeightDp = 60f

    val startDate = getStartDate(dateState.selectedDate)

    Row {
        // Time column
        TimeColumn(
            modifier = Modifier
                .padding(top = hourHeightDp.dp)
                .width(timeColumnWidth),
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState
        )

        // Calendar view
        SwipeableCalendarView(
            startDate = startDate,
            events = events,
            holidays = holidays,
            onDayClick = { date -> dateStateHolder.updateSelectedDateState(date) },
            onEventClick = onEventClick,
            selectedDay = dateState.selectedDate,
            onDateRangeChange = { newStartDate ->
                // Preserve the selected day's position in the new range if possible
                val currentPosition = if (dateState.selectedDate >= startDate &&
                    dateState.selectedDate < startDate.plus(kotlinx.datetime.DatePeriod(days =
                        numDays))) {
                    (dateState.selectedDate.toEpochDays() - startDate.toEpochDays()).toInt()
                } else {
                    0
                }

                val newSelectedDate = if (currentPosition in 0 until numDays) {
                    newStartDate.plus(kotlinx.datetime.DatePeriod(days = currentPosition))
                } else {
                    newStartDate
                }

                dateStateHolder.updateSelectedDateState(newSelectedDate)
            },
            numDays = numDays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState,
            currentDate = dateState.currentDate
        )
    }
}