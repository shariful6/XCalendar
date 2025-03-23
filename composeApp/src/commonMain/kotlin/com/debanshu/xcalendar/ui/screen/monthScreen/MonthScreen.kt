package com.debanshu.xcalendar.ui.screen.monthScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.YearMonth
import com.debanshu.xcalendar.ui.screen.monthScreen.components.SwipeableMonthView

@Composable
fun MonthScreen(
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    SwipeableMonthView(
        initialMonth = YearMonth(dateState.selectedDate.year, dateState.selectedDate.monthNumber),
        events = events,
        holidays = holidays,
        onDayClick = { date -> dateStateHolder.updateSelectedDateState(date) },
        selectedDay = dateState.selectedDate,
        onMonthChange = { yearMonth ->
            dateStateHolder.updateSelectedInViewMonthState(yearMonth)
        }
    )
}