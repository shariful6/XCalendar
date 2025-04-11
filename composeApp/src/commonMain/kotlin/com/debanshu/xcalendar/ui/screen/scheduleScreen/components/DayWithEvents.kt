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
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import kotlinx.datetime.LocalDate

@Composable
fun DayWithEvents(
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Day column (day of week and number)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Text(
                text = date.dayOfWeek.name.take(3),
                style = MaterialTheme.typography.caption
            )
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
        }

        // Events column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            // Holidays first
            holidays.forEach { holiday ->
                EventItem(
                    title = holiday.name,
                    color = Color(0xFF4CAF50), // Green for holidays
                    onClick = { /* No action for holidays */ }
                )
            }

            // Regular events
            events.forEach { event ->
                EventItem(
                    title = event.title,
                    color = Color(event.color ?: 0xFFE91E63.toInt()), // Use event color or default
                    // pink
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}