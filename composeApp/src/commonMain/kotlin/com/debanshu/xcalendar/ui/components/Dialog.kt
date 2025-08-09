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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.common.convertStringToColor
import com.debanshu.xcalendar.common.noRippleClickable
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.User
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import com.skydoves.landscapist.coil3.CoilImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bars
import compose.icons.fontawesomeicons.solid.Bell
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.Edit
import compose.icons.fontawesomeicons.solid.LocationArrow
import compose.icons.fontawesomeicons.solid.Trash
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddEventDialog(
    user: User,
    calendars: List<Calendar>,
    selectedDate: LocalDate,
    onSave: (Event) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCalendarId by remember { mutableStateOf(calendars.firstOrNull()?.id ?: "") }
    var isAllDay by remember { mutableStateOf(false) }
    var selectedEventType by remember { mutableStateOf(EventType.EVENT) }
    var startDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year,
                selectedDate.month,
                selectedDate.day,
                12,
                0,
            ),
        )
    }
    var endDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year,
                selectedDate.month,
                selectedDate.day,
                12,
                30,
            ),
        )
    }
    var showMoreOptions by remember { mutableStateOf(false) }
    var reminderMinutes by remember { mutableStateOf(10) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Cancel",
                modifier =
                    Modifier.clickable {
                        onDismiss
                    },
                color = XCalendarTheme.colorScheme.primary,
            )
            Text(
                "Save",
                style = XCalendarTheme.typography.bodyLargeEmphasized,
                modifier =
                    Modifier.clickable {
                        if (title.isNotBlank()) {
                            val selectedCalendar = calendars.find { it.id == selectedCalendarId }
                            val event =
                                Event(
                                    id = "",
                                    calendarId = selectedCalendarId,
                                    calendarName = selectedCalendar?.name ?: "",
                                    title = title,
                                    description = description.takeIf { it.isNotBlank() },
                                    location = location.takeIf { it.isNotBlank() },
                                    startTime =
                                        if (isAllDay) {
                                            LocalDateTime(
                                                selectedDate.year,
                                                selectedDate.month,
                                                selectedDate.day,
                                                0,
                                                0,
                                            ).toInstant(TimeZone.currentSystemDefault())
                                                .toEpochMilliseconds()
                                        } else {
                                            startDateTime
                                                .toInstant(TimeZone.currentSystemDefault())
                                                .toEpochMilliseconds()
                                        },
                                    endTime =
                                        if (isAllDay) {
                                            LocalDateTime(
                                                selectedDate.year,
                                                selectedDate.month,
                                                selectedDate.day,
                                                23,
                                                59,
                                            ).toInstant(TimeZone.currentSystemDefault())
                                                .toEpochMilliseconds()
                                        } else {
                                            endDateTime
                                                .toInstant(TimeZone.currentSystemDefault())
                                                .toEpochMilliseconds()
                                        },
                                    isAllDay = isAllDay,
                                    reminderMinutes = if (reminderMinutes > 0) listOf(reminderMinutes) else emptyList(),
                                    color =
                                        selectedCalendar?.color
                                            ?: convertStringToColor("defaultColor", 255),
                                )
                            onSave(event)
                        }
                    },
                color = XCalendarTheme.colorScheme.primary,
            )
        }

        TextField(
            modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
            value = title,
            onValueChange = { title = it },
            textStyle = MaterialTheme.typography.headlineSmall,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                ),
            placeholder = {
                Text(
                    text = "Add title",
                    color = XCalendarTheme.colorScheme.onSurface,
                    style = XCalendarTheme.typography.headlineSmall,
                )
            },
        )

        EventTypeSelector(
            selectedType = selectedEventType,
            onTypeSelected = { selectedEventType = it },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)

        CaladerTimeSection(
            isAllDayBase = isAllDay,
            selectedDate = selectedDate,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            onSwitchAllDayChange = {
                isAllDay = it
            },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)

        CalenderSelectionSection(
            user,
            calendars,
            selectedCalendarId,
            onCalendarSelected =
                { selectedCalendarId = it },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)

        AddEventOption(
            icon = FontAwesomeIcons.Solid.LocationArrow,
            text = "Add location",
            onClick = {
                showMoreOptions = !showMoreOptions
            },
        )

        if (showMoreOptions) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("Enter location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)
        NotificationRow(
            reminderMinutes = reminderMinutes,
            onReminderChange = { reminderMinutes = it },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)
        AddEventOption(
            icon = FontAwesomeIcons.Solid.Bars,
            text = "Add description",
            onClick = { /* Handle add description */ },
        )
    }
}

@Composable
private fun CalenderSelectionSection(
    user: User,
    calendars: List<Calendar>,
    incomingSelectedCalendarId: String,
    onCalendarSelected: (String) -> Unit,
) {
    var selectedCalendarId by remember { mutableStateOf(incomingSelectedCalendarId) }
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 8.dp,
                        start = 16.dp,
                    ),
        ) {
            CoilImage(
                imageModel = { user.photoUrl },
                modifier =
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = user.email,
                style = XCalendarTheme.typography.bodySmall,
                color = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
        onCalenderList(
            calendars,
            selectedCalendarId,
            onCalendarSelected = {
                selectedCalendarId = it
                onCalendarSelected(it)
            },
        )
    }
}

@Composable
private fun CaladerTimeSection(
    isAllDayBase: Boolean,
    selectedDate: LocalDate,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime,
    onSwitchAllDayChange: (Boolean) -> Unit,
) {
    var isAllDay by remember { mutableStateOf(isAllDayBase) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.fillMaxWidth().padding(
                horizontal = 16.dp,
            ),
    ) {
        Icon(
            imageVector = FontAwesomeIcons.Solid.Clock,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "All day",
            style = XCalendarTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(checked = isAllDay, onCheckedChange = {
            isAllDay = !isAllDay
            onSwitchAllDayChange(isAllDay)
        })
    }

    // Date and time selection
    if (isAllDay) {
        TimeDisplayRow(
            label = "${
                selectedDate.month.name.lowercase().replaceFirstChar {
                    it.titlecase()
                }
            } ${selectedDate.day}, ${selectedDate.year}",
            onClick = { /* Handle date picker */ },
        )
    } else {
        TimeDisplayRow(
            label = "${
                selectedDate.month.name.lowercase().replaceFirstChar {
                    it.titlecase()
                }
            } ${selectedDate.day}, ${selectedDate.year}",
            startTime = formatTime(startDateTime),
            endTime = formatTime(endDateTime),
            onClick = { /* Handle time picker */ },
        )
    }
}

@Composable
private fun onCalenderList(
    calendars: List<Calendar>,
    selectedCalendarId: String,
    onCalendarSelected: (String) -> Unit = {},
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(calendars) { index, calendar ->
            Row(
                modifier =
                    Modifier
                        .padding(
                            start = if (index == 0) 50.dp else 0.dp,
                            end = if (index == calendars.size - 1) 16.dp else 0.dp,
                        ).border(
                            0.5.dp,
                            XCalendarTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp),
                        ).background(
                            color =
                                if (selectedCalendarId != calendar.id) {
                                    XCalendarTheme.colorScheme.surfaceContainerLow
                                } else {
                                    XCalendarTheme.colorScheme.primary
                                },
                            RoundedCornerShape(8.dp),
                        ).padding(8.dp)
                        .noRippleClickable {
                            onCalendarSelected(calendar.id)
                        },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(calendar.color)),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = calendar.name,
                    style = XCalendarTheme.typography.bodySmall,
                    color =
                        if (selectedCalendarId != calendar.id) {
                            XCalendarTheme.colorScheme.onSurfaceVariant
                        } else {
                            XCalendarTheme.colorScheme.onPrimary
                        },
                )
            }
        }
    }
}

enum class EventType(
    val displayName: String,
) {
    EVENT("Event"),
    TASK("Task"),
    WORKING_LOCATION("Working location"),
    OUT_OF_OFFICE("Out of office"),
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EventTypeSelector(
    selectedType: EventType,
    onTypeSelected: (EventType) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        itemsIndexed(EventType.entries.toTypedArray()) { index, type ->
            val isSelected = selectedType == type
            Box(
                modifier =
                    Modifier
                        .padding(
                            start = if (index == 0) 54.dp else 0.dp,
                            end =
                                if (index ==
                                    EventType.entries.size - 1
                                ) {
                                    16.dp
                                } else {
                                    0.dp
                                },
                        ).clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) {
                                XCalendarTheme.colorScheme.primary
                            } else {
                                XCalendarTheme.colorScheme.surfaceVariant
                            },
                        ).clickable { onTypeSelected(type) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = type.displayName,
                    color =
                        if (isSelected) {
                            XCalendarTheme.colorScheme.onPrimary
                        } else {
                            XCalendarTheme.colorScheme.onSurfaceVariant
                        },
                    style = XCalendarTheme.typography.bodyMediumEmphasized,
                )
            }
        }
    }
}

@Composable
private fun TimeDisplayRow(
    label: String,
    startTime: String? = null,
    endTime: String? = null,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(top = 8.dp, bottom = 8.dp, start = 52.dp, end = 16.dp),
    ) {
        Text(
            text = label,
            style = XCalendarTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!startTime.isNullOrEmpty()) {
            Text(
                text = startTime,
                style = XCalendarTheme.typography.bodyMedium,
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(top = 8.dp, bottom = 8.dp, start = 52.dp, end = 16.dp),
    ) {
        Text(
            text = label,
            style = XCalendarTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!endTime.isNullOrEmpty()) {
            Text(
                text = endTime,
                style = XCalendarTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun AddEventOption(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = XCalendarTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun NotificationRow(
    reminderMinutes: Int,
    onReminderChange: (Int) -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Icon(
            imageVector = FontAwesomeIcons.Solid.Bell,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "$reminderMinutes minutes before",
            style = XCalendarTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = FontAwesomeIcons.Solid.Bars,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
    }
}

private fun formatTime(dateTime: LocalDateTime): String {
    val hour =
        if (dateTime.hour == 0) {
            12
        } else if (dateTime.hour > 12) {
            dateTime.hour - 12
        } else {
            dateTime.hour
        }
    val minute = dateTime.minute.toString().padStart(2, '0')
    val amPm = if (dateTime.hour >= 12) "PM" else "AM"
    return "$hour:$minute $amPm"
}

@Composable
fun EventDetailsDialog(
    event: Event,
    onEdit: (Event) -> Unit,
    onDelete: (Event) -> Unit,
    onDismiss: () -> Unit,
) {
    val startDateTime = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endDateTime = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

    val formattedDate =
        "${startDateTime.date.month.name} ${startDateTime.date.day}, ${startDateTime.date.year}"
    val formattedStartTime = "${startDateTime.hour % 12}:${
        startDateTime.minute.toString().padStart(2, '0')
    } ${if (startDateTime.hour >= 12) "PM" else "AM"}"
    val formattedEndTime = "${endDateTime.hour % 12}:${
        endDateTime.minute.toString().padStart(2, '0')
    } ${if (endDateTime.hour >= 12) "PM" else "AM"}"

    AlertDialog(onDismissRequest = onDismiss, title = null, text = {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            // Event color bar
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(event.color)),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Event title
            Text(
                text = event.title,
                style = XCalendarTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date and time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Bars,
                    contentDescription = null,
                    tint = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = formattedDate,
                        style = XCalendarTheme.typography.bodyMedium,
                    )

                    if (!event.isAllDay) {
                        Text(
                            text = "$formattedStartTime - $formattedEndTime",
                            style = XCalendarTheme.typography.bodyMedium,
                        )
                    } else {
                        Text(
                            text = "All day",
                            style = XCalendarTheme.typography.bodyMedium,
                        )
                    }
                }
            }

            // Location if available
            event.location?.let { location ->
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.LocationArrow,
                        contentDescription = null,
                        tint = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = location,
                        style = XCalendarTheme.typography.bodySmall,
                    )
                }
            }

            // Description if available
            event.description?.let { description ->
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description,
                        style = XCalendarTheme.typography.bodySmall,
                    )
                }
            }
        }
    }, confirmButton = {
        Row {
            TextButton(onClick = { onDelete(event) }) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Trash,
                    contentDescription = "Delete",
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete")
            }

            TextButton(onClick = { onEdit(event) }) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Edit,
                    contentDescription = "Edit",
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
