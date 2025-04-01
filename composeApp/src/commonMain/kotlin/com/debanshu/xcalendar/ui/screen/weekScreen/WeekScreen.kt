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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Row() {
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
            .bottomBorder(
                2.dp,
                MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
            )
            .padding(end = 16.dp)
    ) {
        // Position the text at the bottom, slightly offset to appear on the line
        Text(
            text = formatHour(hour),
            style = MaterialTheme.typography.caption.copy(
                fontSize = 10.sp
            ),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = (-8).dp) // Negative offset to position text on the line
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
    if(hour == 0) {
        return ""
    }
    return "$displayHour $amPm"
}

@Composable
fun Modifier.bottomBorder(thickness: Dp, color: Color): Modifier {
    val density = LocalDensity.current
    val strokeWidthPx = density.run { thickness.toPx() }
    return this then Modifier.drawBehind {
        val width = size.width
        val height = size.height

        drawLine(
            color = color,
            start = Offset(x = (width/1.2).toFloat(), y = height),
            end = Offset(x = width, y = height),
            strokeWidth = strokeWidthPx
        )
    }
}