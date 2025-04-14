package com.debanshu.xcalendar.ui.screen.scheduleScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
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
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.h6,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colors.onSurface
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

private fun formatTimeRange(start: LocalDateTime, end: LocalDateTime): String {
    fun formatTime(time: LocalDateTime): String {
        val hour = when {
            time.hour == 0 -> 12
            time.hour > 12 -> time.hour - 12
            else -> time.hour
        }
        val minute = time.minute.toString().padStart(2, '0')
        val amPm = if (time.hour >= 12) "am" else "pm"
        return "$hour:$minute $amPm"
    }

    return "${formatTime(start)} – ${formatTime(end)}"
}