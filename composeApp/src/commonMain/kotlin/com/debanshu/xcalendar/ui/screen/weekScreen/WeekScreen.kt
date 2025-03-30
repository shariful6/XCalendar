package com.debanshu.xcalendar.ui.screen.weekScreen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.CalendarUiState
import com.debanshu.xcalendar.ui.CalendarViewModel
import com.debanshu.xcalendar.ui.screen.weekScreen.components.SwipeableWeekCalendarView
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun WeekScreen(
    dateStateHolder: DateStateHolder,
    viewModel: CalendarViewModel,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val verticalScrollState = rememberScrollState()
    val timeColumnWidth = 60.dp
    val timeRange = 0..23
    val hourHeightDp: Float = 60f
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    Row() {
        TimeColumn(
            modifier = Modifier.width(timeColumnWidth),
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
            scrollState = verticalScrollState
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
        // Header space
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(MaterialTheme.colors.surface)
        )

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
            .fillMaxSize()
            .border(0.5.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
            .padding(end = 8.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Text(
            text = formatHour(hour),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 2.dp, end = 4.dp)
        )
    }
}

private fun formatHour(hour: Int): String {
    val displayHour = when {
        hour == 0 || hour == 12 -> "12"
        hour > 12 -> (hour - 12).toString()
        else -> hour.toString()
    }
    val amPm = if (hour >= 12) "pm" else "am"
    return "$displayHour $amPm"
}