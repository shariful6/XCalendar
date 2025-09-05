package com.debanshu.xcalendar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.debanshu.xcalendar.domain.states.dateState.DateStateHolder
import com.debanshu.xcalendar.ui.CalendarView
import com.debanshu.xcalendar.ui.CalendarViewModel
import com.debanshu.xcalendar.ui.components.AddEventDialog
import com.debanshu.xcalendar.ui.components.CalendarDrawer
import com.debanshu.xcalendar.ui.components.CalendarTopAppBar
import com.debanshu.xcalendar.ui.components.EventDetailsDialog
import com.debanshu.xcalendar.ui.screen.dayScreen.DayScreen
import com.debanshu.xcalendar.ui.screen.monthScreen.MonthScreen
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleScreen
import com.debanshu.xcalendar.ui.screen.threeDayScreen.ThreeDayScreen
import com.debanshu.xcalendar.ui.screen.weekScreen.WeekScreen
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Plus
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CalendarApp() {
    val viewModel = koinViewModel<CalendarViewModel>()
    val dateStateHolder = koinInject<DateStateHolder>()
    XCalendarTheme {
        val calendarUiState by viewModel.uiState.collectAsState()
        val dataState by dateStateHolder.currentDateState.collectAsState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val currentRoute by navController.currentBackStackEntryAsState()
        var showAddBottomSheet by remember { mutableStateOf(false) }
        var showDetailsBottomSheet by remember { mutableStateOf(false) }

        // Extract drawer-specific state to prevent unnecessary recompositions
        val drawerAccounts = remember(calendarUiState.accounts) { calendarUiState.accounts }
        val drawerCalendars = remember(calendarUiState.calendars) { calendarUiState.calendars }

        val visibleCalendars by remember(calendarUiState.calendars) {
            derivedStateOf { calendarUiState.calendars.filter { it.isVisible } }
        }

        ModalNavigationDrawer(
            modifier = Modifier.testTag("ModalNavigationDrawer"),
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = RectangleShape,
                    drawerContainerColor = XCalendarTheme.colorScheme.surfaceContainerHigh,
                ) {
                    // Use stable route to avoid recomposition from navigation state changes
                    val stableRoute =
                        remember(currentRoute?.destination?.route) {
                            currentRoute?.destination?.route
                        }

                    stableRoute?.let { route ->
                        CalendarDrawer(
                            selectedView = route,
                            onViewSelect =
                                remember {
                                    { view ->
                                        scope.launch {
                                            navController.navigate(view.toString())
                                            drawerState.close()
                                        }
                                    }
                                },
                            accounts = drawerAccounts,
                            calendars = drawerCalendars,
                            onCalendarToggle =
                                remember {
                                    { calendar ->
                                        viewModel.toggleCalendarVisibility(calendar)
                                    }
                                },
                        )
                    }
                }
            },
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    CalendarTopAppBar(
                        dateState = dataState,
                        monthDropdownState = calendarUiState.showMonthDropdown,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSelectToday = {
                            dateStateHolder.updateSelectedDateState(dataState.currentDate)
                        },
                        onToggleMonthDropdown = { show ->
                            viewModel.setTopAppBarMonthDropdown(show)
                        },
                        onDayClick = { date ->
                            dateStateHolder.updateSelectedDateState(date)
                        },
                        calendarUiState.events,
                        calendarUiState.holidays,
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { showAddBottomSheet = true }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = FontAwesomeIcons.Solid.Plus,
                            contentDescription = "Add Event",
                        )
                    }
                },
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = CalendarView.Month.toString(),
                ) {
                    composable(route = CalendarView.Month.toString()) {
                        // Extract only the state that MonthScreen actually needs
                        val events = calendarUiState.events
                        val holidays = calendarUiState.holidays

                        // Create stable lambda providers
                        val eventsProvider =
                            remember(events) {
                                { events }
                            }
                        val holidaysProvider =
                            remember(holidays) {
                                { holidays }
                            }
                        val onDateClickCallback =
                            remember(navController) {
                                { navController.navigate(CalendarView.Day.toString()) }
                            }

                        MonthScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = eventsProvider,
                            holidays = holidaysProvider,
                            onDateClick = onDateClickCallback,
                        )
                    }
                    composable(route = CalendarView.Week.toString()) {
                        WeekScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = calendarUiState.events,
                            holidays = calendarUiState.holidays,
                            onEventClick = { event ->
                                viewModel.selectEvent(event)
                                showDetailsBottomSheet = true
                            },
                            onDateClickCallback = {
                                navController.navigate(CalendarView.Day.toString())
                            },
                        )
                    }
                    composable(route = CalendarView.Day.toString()) {
                        DayScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = calendarUiState.events,
                            holidays = calendarUiState.holidays,
                            onEventClick = { event ->
                                viewModel.selectEvent(event)
                                showDetailsBottomSheet = true
                            },
                        )
                    }
                    composable(route = CalendarView.ThreeDay.toString()) {
                        ThreeDayScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = calendarUiState.events,
                            holidays = calendarUiState.holidays,
                            onEventClick = { event ->
                                viewModel.selectEvent(event)
                                showDetailsBottomSheet = true
                            },
                            onDateClickCallback = {
                                navController.navigate(CalendarView.Day.toString())
                            },
                        )
                    }
                    composable(route = CalendarView.Schedule.toString()) {
                        ScheduleScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = calendarUiState.events,
                            holidays = calendarUiState.holidays,
                            onEventClick = { event ->
                                viewModel.selectEvent(event)
                                showDetailsBottomSheet = true
                            },
                        )
                    }
                }
                if (showAddBottomSheet) {
                    AddEventDialog(
                        user = calendarUiState.accounts[0],
                        calendars = visibleCalendars,
                        selectedDate = dataState.currentDate,
                        onSave = { event ->
                            viewModel.addEvent(event)
                            showAddBottomSheet = false
                        },
                        onDismiss = {
                            showAddBottomSheet = false
                        },
                    )
                }

                if (showDetailsBottomSheet) {
                    calendarUiState.selectedEvent?.let { event ->
                        EventDetailsDialog(
                            event = event,
                            onEdit = {
                                viewModel.editEvent(it)
                                viewModel.clearSelectedEvent()
                                showDetailsBottomSheet = false
                            },
                            onDismiss = {
                                viewModel.clearSelectedEvent()
                                showDetailsBottomSheet = false
                            },
                        )
                    }
                }
            }
        }
    }
}
