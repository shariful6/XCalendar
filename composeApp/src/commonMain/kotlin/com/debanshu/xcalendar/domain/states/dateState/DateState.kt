package com.debanshu.xcalendar.domain.states.dateState

import com.debanshu.xcalendar.common.model.YearMonth
import kotlinx.datetime.LocalDate

data class DateState(
    val currentDate: LocalDate,
    val selectedDate: LocalDate,
    val selectedInViewMonth: YearMonth,
)