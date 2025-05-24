package com.debanshu.xcalendar.ui.screen.monthScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.dateState.DateStateHolder
import com.debanshu.xcalendar.common.model.YearMonth
import com.debanshu.xcalendar.ui.screen.monthScreen.components.SwipeableMonthView
import kotlinx.datetime.LocalDate

@Composable
fun MonthScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: () -> List<Event>,
    holidays: () -> List<Holiday>,
    onDateClick: (LocalDate) -> Unit,
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
        onSpecificDayClicked = { date ->
            dateStateHolder.updateSelectedDateState(date)
            onDateClick(date)
        },
        onMonthChange = { yearMonth ->
            dateStateHolder.updateSelectedInViewMonthState(yearMonth)
        }
    )
}