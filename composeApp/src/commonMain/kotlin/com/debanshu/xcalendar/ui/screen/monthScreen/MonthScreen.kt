package com.debanshu.xcalendar.ui.screen.monthScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
    onDateClick: () -> Unit,
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    
    // Create stable callbacks to prevent unnecessary recompositions
    val onSpecificDayClicked = remember(dateStateHolder, onDateClick) {
        { date: LocalDate ->
            dateStateHolder.updateSelectedDateState(date)
            onDateClick()
        }
    }
    
    val onMonthChange = remember(dateStateHolder) {
        { yearMonth: YearMonth ->
            dateStateHolder.updateSelectedInViewMonthState(yearMonth)
        }
    }
    
    SwipeableMonthView(
        modifier = modifier.testTag("SwipeableMonthView"),
        currentMonth = YearMonth(
            dateState.selectedInViewMonth.year,
            dateState.selectedInViewMonth.month
        ),
        events = events,
        holidays = holidays,
        onSpecificDayClicked = onSpecificDayClicked,
        onMonthChange = onMonthChange
    )
}