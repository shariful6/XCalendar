package com.debanshu.xcalendar.ui.screen.scheduleScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.dateState.DateStateHolder
import com.debanshu.xcalendar.domain.states.scheduleState.ScheduleStateHolder
import com.debanshu.xcalendar.common.model.YearMonth
import com.debanshu.xcalendar.ui.screen.scheduleScreen.components.DayWithEvents
import com.debanshu.xcalendar.ui.screen.scheduleScreen.components.MonthHeader
import com.debanshu.xcalendar.ui.screen.scheduleScreen.components.WeekHeader
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val currentDate = dateState.currentDate
    val currentYearMonth = YearMonth.from(currentDate)

    // Create a ScheduleState to manage schedule-related state
    val scheduleStateHolder = remember(currentYearMonth, events, holidays) {
        ScheduleStateHolder(
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
    LaunchedEffect(scheduleStateHolder.initialScrollIndex) {
        if (scheduleStateHolder.initialScrollIndex > 0) {
            listState.scrollToItem(scheduleStateHolder.initialScrollIndex)
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
                        idx < scheduleStateHolder.items.size &&
                                scheduleStateHolder.items[idx] is ScheduleItem.MonthHeader
                    }
                    ?.let { scheduleStateHolder.items[it] as? ScheduleItem.MonthHeader }
            }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { header ->
                    dateStateHolder.updateSelectedInViewMonthState(header.yearMonth)
                }
        }

        // Handle backward pagination
        launch {
            snapshotFlow { listState.firstVisibleItemIndex < ScheduleStateHolder.THRESHOLD }
                .distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore) {
                        val firstVisibleIndex = listState.firstVisibleItemIndex
                        val newItemsCount = scheduleStateHolder.loadMoreBackward()

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
                lastVisibleIndex >= scheduleStateHolder.items.size - ScheduleStateHolder.THRESHOLD
            }
                .distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore) {
                        scheduleStateHolder.loadMoreForward()
                    }
                }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(XCalendarTheme.colorScheme.surfaceContainerLow)
    ) {
        itemsIndexed(
            items = scheduleStateHolder.items,
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
