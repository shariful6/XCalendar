package com.debanshu.xcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

@Composable
fun AddEventDialog(
    calendars: List<Calendar>,
    selectedDate: LocalDate,
    onSave: (Event) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCalendarId by remember { mutableStateOf(calendars.firstOrNull()?.id ?: "") }
    var isAllDay by remember { mutableStateOf(false) }

    // Selected date with default times (9:00 AM to 10:00 AM)
    var startDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year, selectedDate.month, selectedDate.dayOfMonth, 9, 0
            )
        )
    }
    var endDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year, selectedDate.month, selectedDate.dayOfMonth, 10, 0
            )
        )
    }

    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Event") }, text = {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar selection
            Text(
                text = "Calendar",
                style = XCalendarTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(calendars) { calendar ->
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape)
                            .background(Color(calendar.color)).border(
                                width = 2.dp,
                                color = if (selectedCalendarId == calendar.id) XCalendarTheme.colorScheme
                                    .primary else Color.Transparent,
                                shape = CircleShape
                            ).clickable { selectedCalendarId = calendar.id })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // All day toggle
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "All day", style = XCalendarTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = isAllDay, onCheckedChange = { isAllDay = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date and time pickers (simplified for this example)
            if (!isAllDay) {
                Text(
                    text = "Start time",
                    style = XCalendarTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Simplified time picker - in a real app you'd use a proper time picker
                Text(
                    text = "${startDateTime.hour}:${
                        startDateTime.minute.toString().padStart(2, '0')
                    }",
                    style = XCalendarTheme.typography.bodySmall,
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(XCalendarTheme.colorScheme.surface).padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "End time",
                    style = XCalendarTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${endDateTime.hour}:${
                        endDateTime.minute.toString().padStart(2, '0')
                    }",
                    style = XCalendarTheme.typography.bodySmall,
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(XCalendarTheme.colorScheme.surface).padding(8.dp)
                )
            }
        }
    }, confirmButton = {
        Button(
            onClick = {
                val calendar = calendars.first { it.id == selectedCalendarId }
                val startInstant = startDateTime.toInstant(TimeZone.currentSystemDefault())
                val endInstant = endDateTime.toInstant(TimeZone.currentSystemDefault())

                val event = Event(
                    id = "efjbewkbfkjbewkjfb",
                    calendarId = selectedCalendarId,
                    title = title,
                    description = description,
                    location = location.takeIf { it.isNotEmpty() },
                    startTime = startInstant.toEpochMilliseconds(),
                    endTime = endInstant.toEpochMilliseconds(),
                    isAllDay = isAllDay,
                    color = calendar.color
                )

                onSave(event)
            }, enabled = title.isNotEmpty() && selectedCalendarId.isNotEmpty()
        ) {
            Text("Save")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    })
}

@Composable
fun EventDetailsDialog(
    event: Event, onEdit: (Event) -> Unit, onDelete: (Event) -> Unit, onDismiss: () -> Unit
) {
    val startDateTime = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endDateTime = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

    val formattedDate =
        "${startDateTime.date.month.name} ${startDateTime.date.dayOfMonth}, ${startDateTime.date.year}"
    val formattedStartTime = "${startDateTime.hour % 12}:${
        startDateTime.minute.toString().padStart(2, '0')
    } ${if (startDateTime.hour >= 12) "PM" else "AM"}"
    val formattedEndTime = "${endDateTime.hour % 12}:${
        endDateTime.minute.toString().padStart(2, '0')
    } ${if (endDateTime.hour >= 12) "PM" else "AM"}"

    AlertDialog(onDismissRequest = onDismiss, title = null, text = {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            // Event color bar
            Box(
                modifier = Modifier.fillMaxWidth().height(8.dp)
                    .background(Color((event.color ?: 0xFFE91E63) as Int))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Event title
            Text(
                text = event.title,
                style = XCalendarTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date and time
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = formattedDate, style = XCalendarTheme.typography.bodyMedium
                    )

                    if (!event.isAllDay) {
                        Text(
                            text = "$formattedStartTime - $formattedEndTime",
                            style = XCalendarTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "All day", style = XCalendarTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Location if available
            event.location?.let { location ->
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = location, style = XCalendarTheme.typography.bodySmall
                    )
                }
            }

            // Description if available
            event.description?.let { description ->
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description, style = XCalendarTheme.typography.bodySmall
                    )
                }
            }
        }
    }, confirmButton = {
        Row {
            TextButton(
                onClick = { onDelete(event) }) {
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = "Delete"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete")
            }

            TextButton(
                onClick = { onEdit(event) }) {
                Icon(
                    imageVector = Icons.Default.Edit, contentDescription = "Edit"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit")
            }
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Close")
        }
    })
}