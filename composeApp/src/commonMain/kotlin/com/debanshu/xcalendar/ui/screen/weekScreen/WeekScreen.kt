package com.debanshu.xcalendar.ui.screen.weekScreen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.common.customBorder
import com.debanshu.xcalendar.common.formatHour
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.CalendarUiState
import com.debanshu.xcalendar.ui.screen.weekScreen.components.SwipeableWeekCalendarView
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
    val verticalScrollState = rememberScrollState()
    val timeColumnWidth = 60.dp
    val timeRange = 0..23
    val hourHeightDp = 60f

    Row {
        TimeColumn(
            modifier = Modifier
                .padding(top = hourHeightDp.dp)
                .width(timeColumnWidth),
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState
        )
        SwipeableWeekCalendarView(
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
            },
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState,
            dateStateHolder = dateStateHolder
        )
    }
}

@Composable
fun TimeColumn(
    modifier: Modifier,
    timeRange: IntRange,
    hourHeightDp: Float,
    scrollState: ScrollState
) {
    Column(
        modifier
            .verticalScroll(scrollState)
    ) {
        // Time cells
        timeRange.forEach { hour ->
            TimeCell(
                hour = hour,
                hourHeightDp = hourHeightDp
            )
        }
    }
}

@Composable
fun TimeCell(
    hour: Int,
    hourHeightDp: Float
) {
    Box(
        modifier = Modifier
            .height(hourHeightDp.dp)
            .fillMaxWidth()
            .customBorder(
                2.dp,
                MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
            )
            .padding(end = 16.dp)
    ) {
        Text(
            text = formatHour(hour),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-8).dp)
        )
    }
}
