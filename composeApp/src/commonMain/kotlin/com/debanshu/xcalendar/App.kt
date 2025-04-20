package com.debanshu.xcalendar

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.domain.states.ViewType
import com.debanshu.xcalendar.ui.CalendarView
import com.debanshu.xcalendar.ui.CalendarViewModel
import com.debanshu.xcalendar.ui.components.AddEventDialog
import com.debanshu.xcalendar.ui.components.CalendarDrawer
import com.debanshu.xcalendar.ui.components.EventDetailsDialog
import com.debanshu.xcalendar.ui.components.CalendarTopAppBar
import com.debanshu.xcalendar.ui.screen.dayScreen.DayScreen
import com.debanshu.xcalendar.ui.screen.monthScreen.MonthScreen
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleScreen
import com.debanshu.xcalendar.ui.screen.threeDayScreen.ThreeDayScreen
import com.debanshu.xcalendar.ui.screen.weekScreen.WeekScreen
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope =
    compositionLocalOf<SharedTransitionScope> { error("Error Occurred during creation of SharedTransitionScope ") }

@Composable
@Preview
fun App() {
    val viewModel = koinViewModel<CalendarViewModel>()
    val dateStateHolder = koinInject<DateStateHolder>()
    XCalendarTheme {
        CalendarApp(viewModel, dateStateHolder)
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CalendarApp(
    viewModel: CalendarViewModel, dateStateHolder: DateStateHolder
) {
    val calendarUiState by viewModel.uiState.collectAsState()
    val dataState by dateStateHolder.currentDateState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentViewType by remember {
        derivedStateOf {
            when (calendarUiState.currentView) {
                is CalendarView.Month -> ViewType.MONTH_VIEW
                is CalendarView.Week -> ViewType.WEEK_VIEW
                is CalendarView.Day -> ViewType.ONE_DAY_VIEW
                is CalendarView.ThreeDay -> ViewType.THREE_DAY_VIEW
                else -> ViewType.MONTH_VIEW
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                CalendarDrawer(
                    selectedView = calendarUiState.currentView,
                    onViewSelect = { view ->
                        viewModel.selectView(view)
                        scope.launch { drawerState.close() }
                    },
                    accounts = calendarUiState.accounts,
                    calendars = calendarUiState.calendars,
                    onCalendarToggle = { calendar ->
                        viewModel.toggleCalendarVisibility(
                            calendar
                        )
                    })
            }
        },
    ) {
        Scaffold(
            topBar = {
                CalendarTopAppBar(
                    dateState = dataState,
                    monthDropdownState = calendarUiState.showMonthDropdown,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSelectToday = {
                        dateStateHolder.updateSelectedDateState(
                            dataState
                                .currentDate, currentViewType
                        )
                    },
                    onToggleMonthDropdown = { viewModel.setTopAppBarMonthDropdown(it) },
                    onDayClick = { date ->
                        dateStateHolder.updateSelectedDateState(
                            date,
                            currentViewType
                        )
                    },
                    calendarUiState.events,
                    calendarUiState.holidays
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddEventDialog() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Event",
                    )
                }
            },
        ) { paddingValues ->
            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this,
                ) {
                    when (calendarUiState.currentView) {
                        is CalendarView.Month -> {
                            MonthScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder,
                                calendarUiState.events,
                                calendarUiState.holidays,
                                onDateClick = {
                                    viewModel.selectView(CalendarView.Day)
                                }
                            )
                        }

                        is CalendarView.Week -> {
                            WeekScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClick = {
                                    viewModel.selectView(CalendarView.Day)
                                }
                            )
                        }

                        is CalendarView.Day -> {
                            DayScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClick = {
                                    viewModel.selectView(CalendarView.Day)
                                }
                            )
                        }

                        is CalendarView.Schedule -> {
                            ScheduleScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) })
                        }

                        is CalendarView.ThreeDay -> {
                            ThreeDayScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClick = {
                                    viewModel.selectView(CalendarView.Day)
                                }
                            )
                        }
                    }
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
