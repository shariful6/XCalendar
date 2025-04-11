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
import androidx.compose.runtime.rememberCoroutineScope
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

    val scheduleItems = remember { mutableStateListOf<ScheduleItem>() }

    val monthRange = remember { MonthRange(currentYearMonth) }

    LaunchedEffect(Unit) {
        val initialItems = createScheduleItemsForMonthRange(
            monthRange.getMonths(),
            events,
            holidays
        )
        scheduleItems.addAll(initialItems)
    }

    val initialScrollIndex = scheduleItems.indexOfFirst {
        it is ScheduleItem.MonthHeader &&
                it.yearMonth.year == currentDate.year &&
                it.yearMonth.month == currentDate.month
    }.coerceAtLeast(0)

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialScrollIndex)
    val coroutineScope = rememberCoroutineScope()

    val bufferThreshold = 10

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { firstVisible ->
                if (firstVisible < bufferThreshold) {
                    monthRange.expandBackward()
                    val newMonths = monthRange.getLastAddedMonthsBackward()

                    val newItems = createScheduleItemsForMonthRange(
                        newMonths,
                        events,
                        holidays
                    )

                    if (newItems.isNotEmpty()) {
                        scheduleItems.addAll(0, newItems)
                        coroutineScope.launch {
                            listState.scrollToItem(newItems.size)
                        }
                    }
                }
            }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= scheduleItems.size - bufferThreshold
        }
            .distinctUntilChanged()
            .collect { isNearBottom ->
                if (isNearBottom) {
                    monthRange.expandForward()
                    val newMonths = monthRange.getLastAddedMonthsForward()

                    val newItems = createScheduleItemsForMonthRange(
                        newMonths,
                        events,
                        holidays
                    )

                    scheduleItems.addAll(newItems)
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = scheduleItems,
            key = { _, item -> item.uniqueId }
        ) { _, item ->
            when (item) {
                is ScheduleItem.MonthHeader -> {
                    MonthHeader(item.yearMonth)
                }
                is ScheduleItem.WeekHeader -> {
                    WeekHeader(item.startDate, item.endDate)
                }
                is ScheduleItem.DayEvents -> {
                    DayWithEvents(
                        date = item.date,
                        events = item.events,
                        holidays = item.holidays,
                        onEventClick = onEventClick
                    )
                }
            }
        }
    }
}
