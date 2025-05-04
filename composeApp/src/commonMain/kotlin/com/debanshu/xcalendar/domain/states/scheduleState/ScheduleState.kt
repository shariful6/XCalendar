package com.debanshu.xcalendar.domain.states.scheduleState

import com.debanshu.xcalendar.common.model.YearMonth

data class ScheduleState(private val startMonth: YearMonth) {
    private var minOffset = -25
    private var maxOffset = 25
    private var lastMinOffset = minOffset
    private var lastMaxOffset = maxOffset

    fun getMonths(): List<YearMonth> {
        return (minOffset..maxOffset).map { offset ->
            startMonth.plusMonths(offset)
        }
    }

    fun expandBackward(amount: Int = 10) {
        lastMinOffset = minOffset
        minOffset -= amount
    }

    fun expandForward(amount: Int = 10) {
        lastMaxOffset = maxOffset
        maxOffset += amount
    }

    fun getLastAddedMonthsBackward(): List<YearMonth> {
        return (minOffset until lastMinOffset).map { offset ->
            startMonth.plusMonths(offset)
        }
    }

    fun getLastAddedMonthsForward(): List<YearMonth> {
        return ((lastMaxOffset + 1)..maxOffset).map { offset ->
            startMonth.plusMonths(offset)
        }
    }
}
