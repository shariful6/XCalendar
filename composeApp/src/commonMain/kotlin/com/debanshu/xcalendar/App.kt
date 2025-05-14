package com.debanshu.xcalendar

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
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

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalendarApp(
    viewModel: CalendarViewModel, dateStateHolder: DateStateHolder
) {
    val calendarUiState by viewModel.uiState.collectAsState()
    val dataState by dateStateHolder.currentDateState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showBottomSheet by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RectangleShape,
                drawerContainerColor = XCalendarTheme.colorScheme.surfaceContainerHigh
            ) {
                currentRoute?.destination?.route?.let {
                    CalendarDrawer(
                        selectedView = it,
                        onViewSelect = { view ->
                            navController.navigate(view.toString())
                            scope.launch { drawerState.close() }
                        },
                        accounts = calendarUiState.accounts,
                        calendars = calendarUiState.calendars,
                        onCalendarToggle = { calendar ->
                            viewModel.toggleCalendarVisibility(
                                calendar
                            )
                        }
                    )
                }
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
                            dataState.currentDate,
                        )
                    },
                    onToggleMonthDropdown = { viewModel.setTopAppBarMonthDropdown(it) },
                    onDayClick = { date ->
                        dateStateHolder.updateSelectedDateState(
                            date
                        )
                    },
                    calendarUiState.events,
                    calendarUiState.holidays
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = FontAwesomeIcons.Solid.Plus,
                        contentDescription = "Add Event",
                    )
                }
            },
        ) { paddingValues ->
            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this,
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = CalendarView.Month.toString()
                    ) {
                        composable(
                            route = CalendarView.Month.toString(),
                        ) {
                            MonthScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder,
                                calendarUiState.events,
                                calendarUiState.holidays,
                                onDateClick = {
                                    navController.navigate(CalendarView.Day.toString())
                                }
                            )
                        }
                        composable(
                            route = CalendarView.Week.toString(),
                        ) {
                            WeekScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClickCallback = {
                                    navController.navigate(CalendarView.Day.toString())
                                }
                            )
                        }
                        composable(
                            route = CalendarView.Day.toString(),
                        ) {
                            DayScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClickCallback = {}
                            )
                        }
                        composable(
                            route = CalendarView.ThreeDay.toString(),
                        ) {
                            ThreeDayScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClickCallback = {
                                    navController.navigate(CalendarView.Day.toString())
                                }
                            )
                        }
                        composable(
                            route = CalendarView.Schedule.toString(),
                        ) {
                            ScheduleScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) }
                            )
                        }
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                    properties = ModalBottomSheetProperties(
                        shouldDismissOnBackPress = true
                    )
                ) {
                    AddEventDialog(
                        calendars = calendarUiState.calendars.filter { it.isVisible },
                        selectedDate = dataState.currentDate,
                    )
                }
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
