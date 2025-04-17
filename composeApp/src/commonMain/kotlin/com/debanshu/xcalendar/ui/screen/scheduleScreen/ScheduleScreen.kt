package com.debanshu.xcalendar.ui.screen.scheduleScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import com.debanshu.xcalendar.ui.YearMonth
import com.debanshu.xcalendar.ui.screen.scheduleScreen.components.DayWithEvents
import com.debanshu.xcalendar.ui.screen.scheduleScreen.components.MonthHeader
import com.debanshu.xcalendar.ui.screen.scheduleScreen.components.WeekHeader
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val currentDate = dateState.currentDate
    val currentYearMonth = YearMonth.from(currentDate)

    // Create a ScheduleState to manage schedule-related state
    val scheduleState = remember(currentYearMonth, events, holidays) {
        ScheduleState(
            initialMonth = currentYearMonth,
            events = events,
            holidays = holidays
        )
    }

    // Initialize month in the TopAppBar immediately
    LaunchedEffect(currentYearMonth) {
        dateStateHolder.updateSelectedInViewMonthState(currentYearMonth)
    }

    // Create list state with initial position
    val listState = rememberLazyListState()

    // Apply initial scroll position after composition
    LaunchedEffect(scheduleState.initialScrollIndex) {
        if (scheduleState.initialScrollIndex > 0) {
            listState.scrollToItem(scheduleState.initialScrollIndex)
        }
    }

    // Handle pagination and TopAppBar updates
    LaunchedEffect(listState) {
        // Monitor visible month headers for TopAppBar updates
        launch {
            snapshotFlow {
                val firstVisible = listState.firstVisibleItemIndex
                val visibleCount = listState.layoutInfo.visibleItemsInfo.size

                // Find the first visible month header
                (firstVisible until firstVisible + visibleCount)
                    .firstOrNull { idx ->
                        idx < scheduleState.items.size &&
                                scheduleState.items[idx] is ScheduleItem.MonthHeader
                    }
                    ?.let { scheduleState.items[it] as? ScheduleItem.MonthHeader }
            }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { header ->
                    dateStateHolder.updateSelectedInViewMonthState(header.yearMonth)
                }
        }

        // Handle backward pagination
        launch {
            snapshotFlow { listState.firstVisibleItemIndex < ScheduleState.THRESHOLD }
                .distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore) {
                        val firstVisibleIndex = listState.firstVisibleItemIndex
                        val newItemsCount = scheduleState.loadMoreBackward()

                        if (newItemsCount > 0) {
                            // Adjust scroll position to maintain visual position
                            listState.scrollToItem(firstVisibleIndex + newItemsCount)
                        }
                    }
                }
        }

        // Handle forward pagination
        launch {
            snapshotFlow {
                val visibleInfo = listState.layoutInfo.visibleItemsInfo
                val lastVisibleIndex = visibleInfo.lastOrNull()?.index ?: 0
                lastVisibleIndex >= scheduleState.items.size - ScheduleState.THRESHOLD
            }
                .distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore) {
                        scheduleState.loadMoreForward()
                    }
                }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = scheduleState.items,
            key = { _, item -> item.uniqueId }
        ) { _, item ->
            when (item) {
                is ScheduleItem.MonthHeader -> MonthHeader(item.yearMonth)
                is ScheduleItem.WeekHeader -> WeekHeader(item.startDate, item.endDate)
                is ScheduleItem.DayEvents -> DayWithEvents(
                    date = item.date,
                    events = item.events,
                    holidays = item.holidays,
                    onEventClick = onEventClick
                )
            }
        }
    }
}

/**
 * Manages the state for the schedule screen
 */
class ScheduleState(
    initialMonth: YearMonth,
    val events: List<Event>,
    val holidays: List<Holiday>
) {
    private val _items = mutableStateListOf<ScheduleItem>()
    val items: List<ScheduleItem> = _items

    private val monthRange = MonthRange(initialMonth)
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

    companion object {
        const val THRESHOLD = 10
    }
}