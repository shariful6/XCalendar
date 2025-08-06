package com.debanshu.xcalendar.domain.states.scheduleState

import com.debanshu.xcalendar.common.model.YearMonth

data class ScheduleState(
    private val startMonth: YearMonth,
    private val initialRange: Int = 25 // Default to 25 for backward compatibility
) {
    private var minOffset = -initialRange
    private var maxOffset = initialRange
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
