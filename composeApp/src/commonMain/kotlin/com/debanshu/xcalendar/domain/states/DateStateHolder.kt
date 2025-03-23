package com.debanshu.xcalendar.domain.states

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
)

interface DateStateHolder {
    val currentDateState: StateFlow<DateState>
    fun updateSelectedInViewMonthState(selectedInViewMonth: YearMonth)
    fun updateSelectedDateState(selectedDate: LocalDate)
}

@Single
class DateStateHolderImpl : DateStateHolder {
    val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val _currentDateState = MutableStateFlow(
        DateState(
            date,
            date,
            YearMonth(date.year, date.monthNumber)
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

    override fun updateSelectedDateState(selectedDate: LocalDate) {
        _currentDateState.tryEmit(_currentDateState.value.copy(selectedDate = selectedDate))
    }
}