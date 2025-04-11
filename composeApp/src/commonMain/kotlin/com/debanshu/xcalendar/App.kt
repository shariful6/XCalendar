package com.debanshu.xcalendar

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.CalendarView
import com.debanshu.xcalendar.ui.CalendarViewModel
import com.debanshu.xcalendar.ui.components.AddEventDialog
import com.debanshu.xcalendar.ui.components.CalendarDrawer
import com.debanshu.xcalendar.ui.components.EventDetailsDialog
import com.debanshu.xcalendar.ui.components.TopAppBar
import com.debanshu.xcalendar.ui.screen.dayScreen.DayScreen
import com.debanshu.xcalendar.ui.screen.monthScreen.MonthScreen
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleScreen
import com.debanshu.xcalendar.ui.screen.threeDayScreen.ThreeDayScreen
import com.debanshu.xcalendar.ui.screen.weekScreen.WeekScreen
import kotlinx.coroutines.launch
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
                    onDayClick = { date -> dateStateHolder.updateSelectedDateState(date) },
                )
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
            },
        ) { paddingValues ->
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
                    DayScreen(
                        dateStateHolder = dateStateHolder,
                        events = calendarUiState.events,
                        holidays = calendarUiState.holidays,
                        onEventClick = { event -> viewModel.selectEvent(event) }
                    )
                }

                is CalendarView.Schedule -> {
                    ScheduleScreen(
                        dateStateHolder = dateStateHolder,
                        events = calendarUiState.events,
                        holidays = calendarUiState.holidays,
                        onEventClick = { event -> viewModel.selectEvent(event) }
                    )
                }

                is CalendarView.ThreeDay -> {
                    ThreeDayScreen(
                        dateStateHolder = dateStateHolder,
                        events = calendarUiState.events,
                        holidays = calendarUiState.holidays,
                        onEventClick = { event -> viewModel.selectEvent(event) }
                    )
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
                    onDismiss = { viewModel.hideAddEventDialog() },
                )
            }

            if (calendarUiState.selectedEvent != null) {
                EventDetailsDialog(
                    event = calendarUiState.selectedEvent!!,
                    onEdit = { viewModel.editEvent(it) },
                    onDelete = { viewModel.deleteEvent(it) },
                    onDismiss = { viewModel.clearSelectedEvent() },
                )
            }
        }
    }
}
