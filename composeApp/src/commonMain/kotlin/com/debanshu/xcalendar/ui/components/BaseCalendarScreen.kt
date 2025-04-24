package com.debanshu.xcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.common.customBorder
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.theme.XCalendarTheme

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
    onDateClickCallback: () -> Unit,
    numDays: Int,
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val verticalScrollState = rememberScrollState()
    val timeColumnWidth = 60.dp
    val timeRange = 0..23
    val hourHeightDp = 60f

    val startDate = dateState.selectedDate
    val isToday = startDate == dateState.currentDate
    Row(
        modifier = modifier
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(hourHeightDp.dp)
                    .width(timeColumnWidth)
                    .background(color = XCalendarTheme.colorScheme.surfaceContainerHigh)
            ) {
                if (numDays == 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .customBorder(
                                end = true,
                                endFraction = 0f,
                                endLengthFraction = 1f,
                                color = XCalendarTheme.colorScheme.surfaceVariant,
                                width = 1.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = startDate.dayOfWeek.name.take(3),
                            style = XCalendarTheme.typography.labelSmall,
                            color = when {
                                isToday -> XCalendarTheme.colorScheme.onPrimaryContainer
                                else -> XCalendarTheme.colorScheme.onSurface
                            }
                        )
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .size(28.dp)
                                .background(
                                    when {
                                        isToday -> XCalendarTheme.colorScheme.primary
                                        else -> Color.Transparent
                                    },
                                    if (isToday)
                                        CircleShape
                                    else
                                        RectangleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = startDate.dayOfMonth.toString(),
                                style = XCalendarTheme.typography.bodyMedium,
                                color = when {
                                    isToday -> XCalendarTheme.colorScheme.inverseOnSurface
                                    else -> XCalendarTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                }
            }
            TimeColumn(
                modifier = Modifier
                    .background(XCalendarTheme.colorScheme.surfaceContainerLow)
                    .width(timeColumnWidth),
                timeRange = timeRange,
                hourHeightDp = hourHeightDp,
                scrollState = verticalScrollState
            )
        }
        SwipeableCalendarView(
            startDate = startDate,
            events = events,
            holidays = holidays,
            onDayClick = { date ->
                dateStateHolder.updateSelectedDateState(date)
                onDateClickCallback()
            },
            onEventClick = onEventClick,
            onDateRangeChange = { newStartDate ->
                dateStateHolder.updateSelectedDateState(newStartDate)
            },
            numDays = numDays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState,
            currentDate = dateState.currentDate
        )
    }
}