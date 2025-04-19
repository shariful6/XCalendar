package com.debanshu.xcalendar.domain.states

import com.debanshu.xcalendar.ui.CalendarUiState
import com.debanshu.xcalendar.ui.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Single

data class DateState(
    val currentDate: LocalDate,
    val selectedDate: LocalDate,
    val selectedInViewMonth: YearMonth,
    val viewStartDate: LocalDate,
)

enum class ViewType {
    MONTH_VIEW,
    WEEK_VIEW,
    THREE_DAY_VIEW,
    ONE_DAY_VIEW
}

interface DateStateHolder {
    val currentDateState: StateFlow<DateState>
    fun updateSelectedInViewMonthState(selectedInViewMonth: YearMonth)
    fun updateSelectedDateState(selectedDate: LocalDate, viewType: ViewType)
    fun updateViewStartDate(viewStartDate: LocalDate)
}

@Single
class DateStateHolderImpl : DateStateHolder {
    val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val _currentDateState = MutableStateFlow(
        DateState(
            date,
            date,
            YearMonth(date.year, date.monthNumber),
            date
        )
    )
    override val currentDateState: StateFlow<DateState> = _currentDateState
    override fun updateSelectedInViewMonthState(selectedInViewMonth: YearMonth) {
        _currentDateState.tryEmit(
            _currentDateState.value.copy(
                selectedInViewMonth = selectedInViewMonth
            )
        )
    }

    override fun updateSelectedDateState(selectedDate: LocalDate, viewType: ViewType) {
        _currentDateState.tryEmit(
            _currentDateState.value.copy(
                selectedDate = selectedDate,
                selectedInViewMonth = YearMonth(selectedDate.year, selectedDate.month),
                viewStartDate = when(viewType) {
                    ViewType.MONTH_VIEW -> CalendarUiState.getWeekStartDate(selectedDate)
                    ViewType.WEEK_VIEW ->  CalendarUiState.getWeekStartDate(selectedDate)
                    ViewType.THREE_DAY_VIEW -> CalendarUiState.get3DayStartDate(selectedDate)
                    ViewType.ONE_DAY_VIEW ->  CalendarUiState.getOneDayStartDate(selectedDate)
                }
            )
        )
    }

    override fun updateViewStartDate(viewStartDate: LocalDate) {
        _currentDateState.tryEmit(
            _currentDateState.value.copy(
                viewStartDate = viewStartDate
            )
        )
    }
}