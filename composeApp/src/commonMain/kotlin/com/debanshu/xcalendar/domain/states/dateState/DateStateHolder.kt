package com.debanshu.xcalendar.domain.states.dateState

import com.debanshu.xcalendar.common.model.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Single
import kotlin.time.ExperimentalTime

@Single
class DateStateHolder {
    @OptIn(ExperimentalTime::class)
    val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val _currentDateState = MutableStateFlow(
        DateState(
            date,
            date,
            YearMonth(date.year, date.monthNumber),
        )
    )
    val currentDateState: StateFlow<DateState> = _currentDateState
    fun updateSelectedInViewMonthState(selectedInViewMonth: YearMonth) {
        _currentDateState.tryEmit(
            _currentDateState.value.copy(
                selectedInViewMonth = selectedInViewMonth
            )
        )
    }

    fun updateSelectedDateState(selectedDate: LocalDate) {
        _currentDateState.tryEmit(
            _currentDateState.value.copy(
                selectedDate = selectedDate,
                selectedInViewMonth = YearMonth(selectedDate.year, selectedDate.month),
            )
        )
    }
}