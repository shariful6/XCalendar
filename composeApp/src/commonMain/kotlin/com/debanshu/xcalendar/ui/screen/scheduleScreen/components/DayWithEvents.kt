package com.debanshu.xcalendar.ui.screen.scheduleScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.common.formatTimeRange
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun DayWithEvents(
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Day column (day of week and number)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Text(
                text = date.dayOfWeek.name.take(3).uppercase(),
                style = XCalendarTheme.typography.labelSmall,
                color = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = date.dayOfMonth.toString(),
                style = XCalendarTheme.typography.headlineSmall,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = XCalendarTheme.colorScheme.onSurface
            )
        }

        // Events column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            // Holidays first
            holidays.forEach { holiday ->
                EventItem(
                    title = holiday.name,
                    color = Color(0xFF4CAF50), // Green for holidays
                    onClick = { /* No action for holidays */ }
                )
            }

            // All events with consistent styling
            events.forEach { event ->
                val timeText = if (!event.isAllDay) {
                    val startDateTime = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                    val endDateTime = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())
                    formatTimeRange(startDateTime, endDateTime)
                } else null

                EventItem(
                    title = event.title,
                    color = Color(event.color ?: 0xFFE91E63.toInt()),
                    onClick = { onEventClick(event) },
                    timeText = timeText
                )
            }
        }
    }
}
