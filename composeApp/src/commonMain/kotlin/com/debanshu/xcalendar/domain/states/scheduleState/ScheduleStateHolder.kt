package com.debanshu.xcalendar.domain.states.scheduleState

import androidx.compose.runtime.mutableStateListOf
import com.debanshu.xcalendar.common.isLeap
import com.debanshu.xcalendar.common.lengthOfMonth
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.common.model.YearMonth
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleItem
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleItem.DayEvents
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleItem.MonthHeader
import com.debanshu.xcalendar.ui.screen.scheduleScreen.ScheduleItem.WeekHeader
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

/**
 * Manages the state for the schedule screen
 */
class ScheduleStateHolder(
    initialMonth: YearMonth,
    val events: List<Event>,
    val holidays: List<Holiday>
) {
    private val _items = mutableStateListOf<ScheduleItem>()
    val items: List<ScheduleItem> = _items

    private val monthRange = ScheduleState(initialMonth)
    val initialScrollIndex: Int

    init {
        // Load initial data synchronously in init block
        val initialItems = createScheduleItemsForMonthRange(
            monthRange.getMonths(),
            events,
            holidays
        )
        _items.addAll(initialItems)

        // Find position of current month
        initialScrollIndex = _items.indexOfFirst {
            it is ScheduleItem.MonthHeader &&
                    it.yearMonth.year == initialMonth.year &&
                    it.yearMonth.month == initialMonth.month
        }.coerceAtLeast(0)
    }

    /**
     * Loads more items at the beginning of the list
     * @return Number of new items added
     */
    fun loadMoreBackward(): Int {
        monthRange.expandBackward()
        val newMonths = monthRange.getLastAddedMonthsBackward()
        val newItems = createScheduleItemsForMonthRange(newMonths, events, holidays)

        if (newItems.isNotEmpty()) {
            _items.addAll(0, newItems)
            return newItems.size
        }
        return 0
    }

    /**
     * Loads more items at the end of the list
     * @return Number of new items added
     */
    fun loadMoreForward(): Int {
        monthRange.expandForward()
        val newMonths = monthRange.getLastAddedMonthsForward()
        val newItems = createScheduleItemsForMonthRange(newMonths, events, holidays)

        if (newItems.isNotEmpty()) {
            _items.addAll(newItems)
            return newItems.size
        }
        return 0
    }

    private fun createScheduleItemsForMonthRange(
        months: List<YearMonth>,
        allEvents: List<Event>,
        allHolidays: List<Holiday>
    ): List<ScheduleItem> {
        val items = mutableListOf<ScheduleItem>()
        fun calculateDaysInMonth(yearMonth: YearMonth): List<LocalDate> {
            val daysInMonth = yearMonth.month.lengthOfMonth(yearMonth.year.isLeap())
            return (1..daysInMonth).map { day ->
                LocalDate(yearMonth.year, yearMonth.month, day)
            }
        }
        months.forEach { yearMonth ->
            // Add month header
            items.add(MonthHeader(yearMonth))

            // Get days in month
            val daysInMonth = calculateDaysInMonth(yearMonth)

            // Group by weeks
            val weeks = daysInMonth.chunked(7)

            weeks.forEach { week ->
                if (week.isNotEmpty()) {
                    val firstDay = week.first()
                    val lastDay = week.last()

                    // Add week header
                    items.add(WeekHeader(firstDay, lastDay))

                    // Add days with events
                    week.forEach { date ->
                        val dayEvents = allEvents.filter { event ->
                            event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                        }

                        val dayHolidays = allHolidays.filter { holiday ->
                            holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                        }

                        if (dayEvents.isNotEmpty() || dayHolidays.isNotEmpty()) {
                            items.add(DayEvents(date, dayEvents, dayHolidays))
                        }
                    }
                }
            }
        }

        return items
    }

    companion object {
        const val THRESHOLD = 10
    }
}