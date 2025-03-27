package com.debanshu.xcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.CalendarView
import com.debanshu.xcalendar.ui.CalendarViewModel
import com.debanshu.xcalendar.ui.components.CalendarDrawer
import com.debanshu.xcalendar.ui.screen.monthScreen.components.SwipeableMonthView
import com.debanshu.xcalendar.ui.components.TopAppBar
import com.debanshu.xcalendar.ui.screen.monthScreen.MonthScreen
import com.debanshu.xcalendar.ui.screen.weekScreen.WeekScreen
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    val viewModel = koinViewModel<CalendarViewModel>()
    val dateStateHolder = koinInject<DateStateHolder>()
    CalendarApp(viewModel, dateStateHolder)
}

@Composable
fun CalendarApp(
    viewModel: CalendarViewModel,
    dateStateHolder: DateStateHolder
) {
    val calendarUiState by viewModel.uiState.collectAsState()
    val dataState by dateStateHolder.currentDateState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    MaterialTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    dateState = dataState,
                    monthDropdownState = calendarUiState.showMonthDropdown,
                    onMenuClick = { scope.launch { scaffoldState.drawerState.open() } },
                    onSelectToday = { dateStateHolder.updateSelectedDateState(dataState.currentDate) },
                    onToggleMonthDropdown = { viewModel.setTopAppBarMonthDropdown(it) },
                    onDayClick = { date -> dateStateHolder.updateSelectedDateState(date) })
            },
            drawerContent = {
                CalendarDrawer(
                    selectedView = calendarUiState.currentView,
                    onViewSelect = { view ->
                        viewModel.selectView(view)
                        scope.launch { scaffoldState.drawerState.close() }
                    },
                    accounts = calendarUiState.accounts,
                    calendars = calendarUiState.calendars,
                    onCalendarToggle = { calendar ->
                        viewModel.toggleCalendarVisibility(
                            calendar
                        )
                    })
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddEventDialog() },
                    contentColor = MaterialTheme.colors.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Event",
                        tint = Color.White
                    )
                }
            }) { paddingValues ->
            when (calendarUiState.currentView) {
                is CalendarView.Month -> {
                    MonthScreen(
                        dateStateHolder,
                        calendarUiState.events,
                        calendarUiState.holidays
                    )
                }

                is CalendarView.Week -> {
                    WeekScreen(
                        dateStateHolder = dateStateHolder,
                        events = calendarUiState.events,
                        holidays = calendarUiState.holidays,
                        onEventClick = { event -> viewModel.selectEvent(event) }
                    )
                }

                is CalendarView.Day -> {
                    DayView(
                        date = calendarUiState.selectedDay,
                        events = calendarUiState.events.filter {
                            it.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == calendarUiState.selectedDay
                        },
                        onEventClick = { event -> viewModel.selectEvent(event) })
                }

                is CalendarView.Schedule -> {
                    ScheduleView(
                        events = calendarUiState.upcomingEvents,
                        onEventClick = { event -> viewModel.selectEvent(event) })
                }

                is CalendarView.ThreeDay -> {
                    ThreeDayView(
                        startDate = calendarUiState.threeDayStartDate,
                        events = calendarUiState.events,
                        onEventClick = { event -> viewModel.selectEvent(event) })
                }
            }

            if (calendarUiState.showAddEventDialog) {
                AddEventDialog(
                    calendars = calendarUiState.calendars.filter { it.isVisible },
                    selectedDate = calendarUiState.selectedDay,
                    onSave = { event ->
                        viewModel.addEvent(event)
                        viewModel.hideAddEventDialog()
                    },
                    onDismiss = { viewModel.hideAddEventDialog() })
            }

            if (calendarUiState.selectedEvent != null) {
                EventDetailsDialog(
                    event = calendarUiState.selectedEvent!!,
                    onEdit = { viewModel.editEvent(it) },
                    onDelete = { viewModel.deleteEvent(it) },
                    onDismiss = { viewModel.clearSelectedEvent() })
            }
        }
    }
}


/*
 * DAY VIEW
 */

@Composable fun DayView(
    date: LocalDate, events: List<Event>, onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Date header
        Text(
            text = "${date.dayOfWeek.name}, ${date.month.name} ${date.dayOfMonth}",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(16.dp)
        )

        // All-day events
        val allDayEvents = events.filter { it.isAllDay }
        if (allDayEvents.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "All-day",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                allDayEvents.forEach { event ->
                    EventItem(
                        event = event, onClick = { onEventClick(event) })
                }
            }

            Divider()
        }

        // Timeline with events
        val timeEvents = events.filter { !it.isAllDay }
        Box(modifier = Modifier.fillMaxSize()) {
            // Time markers (hours)
            Column(modifier = Modifier.fillMaxSize()) {
                for (hour in 0..23) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth().height(60.dp) // 1 hour = 60dp
                    ) {
                        // Time label
                        Text(
                            text = "${if (hour == 0) 12 else if (hour > 12) hour - 12 else hour} ${if (hour >= 12) "PM" else "AM"}",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.width(60.dp).padding(end = 8.dp)
                                .padding(top = 4.dp),
                            textAlign = TextAlign.End
                        )

                        // Hour divider
                        Divider(
                            modifier = Modifier.weight(1f).padding(top = 12.dp),
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            // Events
            LazyColumn(
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 8.dp)
            ) {
                // Group events by start hour for better layout
                val eventsByHour = timeEvents.groupBy {
                    it.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).hour
                }

                for (hour in 0..23) {
                    val hourEvents = eventsByHour[hour] ?: emptyList()

                    if (hourEvents.isNotEmpty()) {
                        // Position is based on hour (60dp per hour) and minute (1dp per minute)
                        hourEvents.forEach { event ->
                            val eventDateTime =
                                event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                            val startMinute = eventDateTime.hour * 60 + eventDateTime.minute
                            val endDateTime =
                                event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())
                            val endMinute = endDateTime.hour * 60 + endDateTime.minute
                            val durationMinutes = endMinute - startMinute

                            item {
                                Box(
                                    modifier = Modifier.offset(y = (startMinute).dp)
                                        .padding(start = 68.dp, end = 8.dp).fillMaxWidth()
                                        .height(durationMinutes.dp)
                                ) {
                                    TimeEventItem(
                                        event = event, onClick = { onEventClick(event) })
                                }
                            }
                        }
                    }
                }
            }

            // Current time indicator
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val currentDay = now.date

            if (date == currentDay) {
                val currentMinute = now.hour * 60 + now.minute

                Box(
                    modifier = Modifier.offset(y = currentMinute.dp).padding(start = 60.dp)
                        .fillMaxWidth().height(2.dp).background(MaterialTheme.colors.primary)
                )
            }
        }
    }
}

@Composable fun EventItem(
    event: Event, onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.1f))
            .clickable(onClick = onClick).padding(8.dp)
    ) {
        // Color indicator
        Box(
            modifier = Modifier.size(12.dp)
                .background(MaterialTheme.colors.onSurface, CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Event title
        Text(
            text = event.title,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Medium
        )

        // If it has a location
        event.location?.let { location ->
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = location,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable fun TimeEventItem(
    event: Event, onClick: () -> Unit
) {
    val startTime = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTime = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

    val formattedStartTime = "${startTime.hour % 12}:${
        startTime.minute.toString().padStart(2, '0')
    } ${if (startTime.hour >= 12) "PM" else "AM"}"
    val formattedEndTime = "${endTime.hour % 12}:${
        endTime.minute.toString().padStart(2, '0')
    } ${if (endTime.hour >= 12) "PM" else "AM"}"

    Box(
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.2f)).border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
                shape = RoundedCornerShape(4.dp)
            ).clickable(onClick = onClick).padding(8.dp)
    ) {
        Column {
            Text(
                text = event.title,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.8f)
            )

            Text(
                text = "$formattedStartTime - $formattedEndTime",
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )

            event.location?.let { location ->
                Text(
                    text = location,
                    style = MaterialTheme.typography.body1,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/*
 * DIALOGS - EVENT MANAGEMENT
 */

@Composable fun AddEventDialog(
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
                style = MaterialTheme.typography.body1,
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
                                color = if (selectedCalendarId == calendar.id) MaterialTheme.colors.primary else Color.Transparent,
                                shape = CircleShape
                            ).clickable { selectedCalendarId = calendar.id })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // All day toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "All day", style = MaterialTheme.typography.body1
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
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Simplified time picker - in a real app you'd use a proper time picker
                Text(
                    text = "${startDateTime.hour}:${
                        startDateTime.minute.toString().padStart(2, '0')
                    }",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.surface).padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "End time",
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${endDateTime.hour}:${
                        endDateTime.minute.toString().padStart(2, '0')
                    }",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.surface).padding(8.dp)
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

@Composable fun EventDetailsDialog(
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
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date and time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = formattedDate, style = MaterialTheme.typography.body2
                    )

                    if (!event.isAllDay) {
                        Text(
                            text = "$formattedStartTime - $formattedEndTime",
                            style = MaterialTheme.typography.body2
                        )
                    } else {
                        Text(
                            text = "All day", style = MaterialTheme.typography.body2
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
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = location, style = MaterialTheme.typography.body1
                    )
                }
            }

            // Description if available
            event.description?.let { description ->
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description, style = MaterialTheme.typography.body1
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

/*
 * SIMPLIFIED IMPLEMENTATIONS FOR OTHER VIEWS
 * These would be fully implemented in a complete application
 */

@Composable fun WeekView(
    startDate: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Weekday header
        WeekHeader(startDate = startDate)

        // Simplified implementation - in a real app this would be more complex
        Text(
            text = "Week View - Starting ${startDate.month.name} ${startDate.dayOfMonth}",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )

        // Events for the week
        val weekEvents = events.sortedBy { it.startTime }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(weekEvents) { event ->
                EventItem(
                    event = event, onClick = { onEventClick(event) })
            }
        }
    }
}

@Composable fun WeekHeader(startDate: LocalDate) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        for (i in 0..6) {
            val date = startDate.plus(DatePeriod(days = i))
            val dayOfWeek = date.dayOfWeek.name.substring(0, 1)
            val dayOfMonth = date.dayOfMonth

            Column(
                horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.body2,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = dayOfMonth.toString(), style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Composable fun ThreeDayView(
    startDate: LocalDate, events: List<Event>, onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Simplified implementation
        Text(
            text = "3-Day View - Starting ${startDate.month.name} ${startDate.dayOfMonth}",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )

        // Events for the three days
        val threeDayEvents = events.filter { event ->
            val eventDate =
                event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
            eventDate >= startDate && eventDate < startDate.plus(DatePeriod(days = 3))
        }.sortedBy { it.startTime }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(threeDayEvents) { event ->
                EventItem(
                    event = event, onClick = { onEventClick(event) })
            }
        }
    }
}

@Composable fun ScheduleView(
    events: List<Event>, onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Simplified implementation
        Text(
            text = "Schedule View",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(16.dp)
        )

        // Group events by date
        val eventsByDate = events.sortedBy { it.startTime }
            .groupBy { it.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            eventsByDate.forEach { (date, dateEvents) ->
                item {
                    Text(
                        text = "${date.dayOfWeek.name}, ${date.month.name} ${date.dayOfMonth}",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(dateEvents) { event ->
                    EventItem(
                        event = event, onClick = { onEventClick(event) })
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
