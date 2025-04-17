package com.debanshu.xcalendar.ui.screen.monthScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.YearMonth
import com.debanshu.xcalendar.ui.screen.monthScreen.components.SwipeableMonthView

@Composable
fun MonthScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    SwipeableMonthView(
        modifier = modifier,
        currentMonth = YearMonth(
            dateState.selectedInViewMonth.year,
            dateState.selectedInViewMonth.month
        ),
        events = events,
        holidays = holidays,
        onSpecificDayClicked = { date -> dateStateHolder.updateSelectedDateState(date) },
        currentSelectedDay = dateState.selectedDate,
        onMonthChange = { yearMonth ->
            dateStateHolder.updateSelectedInViewMonthState(yearMonth)
        }
    )
}