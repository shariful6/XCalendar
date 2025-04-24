package com.debanshu.xcalendar.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.common.customBorder
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A swipeable calendar view that can be used for day, three-day or week views.
 *
 * @param startDate The first date to display
 * @param events The list of events to display
 * @param holidays The list of holidays to display
 * @param onDayClick Callback for when a day is clicked
 * @param onEventClick Callback for when an event is clicked
 * @param onDateRangeChange Callback for when the date range changes due to swiping
 * @param numDays The number of days to display (1 for day view, 3 for three-day view, 7 for week view)
 * @param timeRange The range of hours to display
 * @param hourHeightDp The height of each hour cell
 * @param scrollState The scroll state to synchronize scrolling
 * @param currentDate The current date (today)
 * @param headerHeight The height of the header row
 */
@Composable
fun SwipeableCalendarView(
    startDate: LocalDate,
    events: List<Event>,
    holidays: List<Holiday> = emptyList(),
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit,
    onDateRangeChange: (LocalDate) -> Unit,
    numDays: Int = 7,
    timeRange: IntRange = 0..23,
    hourHeightDp: Float = 60f,
    scrollState: ScrollState,
    currentDate: LocalDate,
    headerHeight: Int = 60
) {
    require(numDays in 1..31) { "numDays must be between 1 and 31" }

    var size by remember { mutableStateOf(IntSize.Zero) }
    val screenWidth by derivedStateOf { size.width.toFloat() }
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var targetOffsetX by remember { mutableStateOf(0f) }
    val prevStartDate = remember(startDate) {
        startDate.minus(DatePeriod(days = numDays))
    }
    val nextStartDate = remember(startDate) {
        startDate.plus(DatePeriod(days = numDays))
    }

    val animatedOffset by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (isAnimating) {
                if (targetOffsetX > 0) {
                    onDateRangeChange(prevStartDate)
                } else if (targetOffsetX < 0) {
                    onDateRangeChange(nextStartDate)
                }
                offsetX = 0f
                targetOffsetX = 0f
                isAnimating = false
            }
        }
    )

    val effectiveOffset = if (isAnimating) animatedOffset else offsetX

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = screenWidth * 0.3f
                        if (abs(offsetX) > threshold) {
                            isAnimating = true
                            targetOffsetX = if (offsetX > 0) {
                                screenWidth
                            } else {
                                -screenWidth
                            }
                        } else {
                            isAnimating = true
                            targetOffsetX = 0f
                        }
                    },
                    onDragCancel = {
                        isAnimating = true
                        targetOffsetX = 0f
                    },
                    onHorizontalDrag = { change, amount ->
                        targetOffsetX += amount
                        if (!isAnimating) {
                            offsetX += amount
                            change.consume()
                        }
                    }
                )
            }
    ) {
        CalendarContent(
            startDate = startDate,
            numDays = numDays,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            currentDate = currentDate,
            scrollState = scrollState,
            headerHeight = headerHeight,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(effectiveOffset.roundToInt(), 0) }
        )

        CalendarContent(
            startDate = prevStartDate,
            numDays = numDays,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            currentDate = currentDate,
            scrollState = scrollState,
            headerHeight = headerHeight,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        -screenWidth.roundToInt() + effectiveOffset.roundToInt(),
                        0
                    )
                }
        )

        CalendarContent(
            startDate = nextStartDate,
            numDays = numDays,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            currentDate = currentDate,
            scrollState = scrollState,
            headerHeight = headerHeight,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        screenWidth.roundToInt() + effectiveOffset.roundToInt(),
                        0
                    )
                }
        )
    }
}

@Composable
private fun CalendarContent(
    startDate: LocalDate,
    numDays: Int,
    events: List<Event>,
    holidays: List<Holiday>,
    timeRange: IntRange,
    hourHeightDp: Float,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit,
    currentDate: LocalDate,
    scrollState: ScrollState,
    headerHeight: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        DaysHeaderRow(
            startDate = startDate,
            numDays = numDays,
            currentDate = currentDate,
            holidays = holidays,
            onDayClick = onDayClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight.dp)
        )

        CalendarEventsGrid(
            startDate = startDate,
            numDays = numDays,
            events = events,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onEventClick = onEventClick,
            currentDate = currentDate,
            scrollState = scrollState
        )
    }
}

@Composable
private fun DaysHeaderRow(
    startDate: LocalDate,
    numDays: Int,
    currentDate: LocalDate,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val dates = List(numDays) { index ->
        startDate.plus(DatePeriod(days = index))
    }
    Row(
        modifier = modifier
            .background(XCalendarTheme.colorScheme.onPrimary)
    ) {
        if(numDays >1) {
            dates.forEach { date ->
                val isToday = date == currentDate
                val holidaysForDate = holidays.filter { holiday ->
                    holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onDayClick(date) }
                        .customBorder(
                            end = true,
                            bottom = true,
                            start = true,
                            startFraction = 0.85f,
                            startLengthFraction = 1f,
                            endFraction = 0.85f,
                            endLengthFraction = 1f,
                            bottomFraction = 0f,
                            bottomLengthFraction = 1f,
                            color = XCalendarTheme.colorScheme.surfaceVariant,
                            width = 1.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val dayNameLength = when {
                            numDays <= 3 -> 3
                            else -> 1
                        }

                        Text(
                            text = date.dayOfWeek.name.take(dayNameLength),
                            style = XCalendarTheme.typography.labelSmall
                        )

                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .size(28.dp)
                                .background(
                                    when {
                                        isToday -> XCalendarTheme.colorScheme.primary
                                        else -> XCalendarTheme.colorScheme.onPrimary
                                    },
                                    if (isToday)
                                        CircleShape
                                    else
                                        RectangleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = XCalendarTheme.typography.bodyMedium,
                                color = when {
                                    isToday -> XCalendarTheme.colorScheme.inverseOnSurface
                                    else -> XCalendarTheme.colorScheme.onSurface
                                },
                            )
                        }

                        if (holidaysForDate.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF4CAF50), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarEventsGrid(
    startDate: LocalDate,
    numDays: Int,
    events: List<Event>,
    timeRange: IntRange,
    hourHeightDp: Float,
    onEventClick: (Event) -> Unit,
    currentDate: LocalDate,
    scrollState: ScrollState
) {
    val dates = List(numDays) { index ->
        startDate.plus(DatePeriod(days = index))
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        val dayColumnWidth = maxWidth / numDays
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinute = now.hour * 60 + now.minute

        Column {
            timeRange.forEach { _ ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(hourHeightDp.dp)
                ) {
                    repeat(numDays) {
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(0.5.dp, XCalendarTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }
        }

        if (dates.any { it == currentDate }) {
            val dayIndex = dates.indexOfFirst { it == currentDate }
            if (dayIndex >= 0) {
                val offsetX = dayColumnWidth * dayIndex
                val offsetY = (currentMinute / 60f * hourHeightDp).dp

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .width(dayColumnWidth)
                        .height(2.dp)
                        .background(XCalendarTheme.colorScheme.primary)
                )
            }
        }

        dates.forEachIndexed { dayIndex, date ->
            val dayEvents = events.filter { event ->
                event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
            }

            dayEvents.forEach { event ->
                val eventStart = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                val eventEnd = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

                val hour = eventStart.hour
                val minute = eventStart.minute

                if (hour in timeRange) {
                    val durationMinutes = if (eventStart.date == eventEnd.date) {
                        (eventEnd.hour - hour) * 60 + (eventEnd.minute - minute)
                    } else {
                        (24 - hour) * 60 - minute
                    }

                    val topOffset = (hour - timeRange.first) * hourHeightDp + (minute / 60f) * hourHeightDp
                    val eventHeight = (durationMinutes / 60f) * hourHeightDp

                    EventItem(
                        event = event,
                        onClick = { onEventClick(event) },
                        modifier = Modifier
                            .offset(
                                x = dayColumnWidth * dayIndex,
                                y = topOffset.dp
                            )
                            .width(dayColumnWidth)
                            .height(eventHeight.dp.coerceAtLeast(30.dp))
                            .padding(1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EventItem(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(event.color ?: 0xFF4285F4.toInt()))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Text(
            text = event.title,
            style = XCalendarTheme.typography.labelSmall,
            fontSize = 10.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}