package com.debanshu.xcalendar.ui.screen.weekScreen.components

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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateStateHolder
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeableWeekCalendarView(
    currentStartDate: LocalDate,
    events: List<Event>,
    holidays: List<Holiday> = emptyList(),
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit,
    selectedDay: LocalDate,
    onWeekChange: (LocalDate) -> Unit,
    timeRange: IntRange = 0..23,
    hourHeightDp: Float = 60f,
    scrollState: ScrollState,
    dateStateHolder: DateStateHolder,
) {
    val today  = dateStateHolder.currentDateState.collectAsState().value.currentDate
    var size by remember { mutableStateOf(IntSize.Zero) }
    val screenWidth by derivedStateOf { size.width.toFloat() }
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var targetOffsetX by remember { mutableStateOf(0f) }
    val prevWeekStartDate = remember(currentStartDate) {
        currentStartDate.minus(DatePeriod(days = 7))
    }
    val nextWeekStartDate = remember(currentStartDate) {
        currentStartDate.plus(DatePeriod(days = 7))
    }
    val animatedOffset by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (isAnimating) {
                if (targetOffsetX > 0) {
                    onWeekChange(prevWeekStartDate)
                } else if (targetOffsetX < 0) {
                    onWeekChange(nextWeekStartDate)
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
        WeekContent(
            startDate = currentStartDate,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            selectedDay = selectedDay,
            today = today,
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(effectiveOffset.roundToInt(), 0) }
        )

        WeekContent(
            startDate = prevWeekStartDate,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            selectedDay = selectedDay,
            today = today,
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(-screenWidth.roundToInt() + effectiveOffset.roundToInt(), 0) }
        )

        WeekContent(
            startDate = nextWeekStartDate,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            selectedDay = selectedDay,
            today = today,
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(screenWidth.roundToInt() + effectiveOffset.roundToInt(), 0) }
        )
    }
}

@Composable
private fun WeekContent(
    startDate: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    timeRange: IntRange,
    hourHeightDp: Float,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit,
    selectedDay: LocalDate,
    today: LocalDate,
    scrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        WeekHeaderRow(
            startDate = startDate,
            selectedDay = selectedDay,
            today = today,
            holidays = holidays,
            onDayClick = onDayClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        )

        WeekEventsGrid(
            startDate = startDate,
            events = events,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onEventClick = onEventClick,
            scrollState = scrollState
        )
    }
}

@Composable
private fun WeekHeaderRow(
    startDate: LocalDate,
    selectedDay: LocalDate,
    today: LocalDate,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colors.surface)
    ) {
        val weekDates = List(7) { index ->
            startDate.plus(DatePeriod(days = index))
        }

        weekDates.forEach { date ->
            val isSelected = date == selectedDay
            val isToday = date == today
            val holidaysForDate = holidays.filter { holiday ->
                holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onDayClick(date) }
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfWeek.name.take(1),
                        style = MaterialTheme.typography.caption.copy(
                            fontSize = 10.sp
                        )
                    )

                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .size(28.dp)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colors.primary
                                    isToday -> MaterialTheme.colors.primary.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.body1,
                            color = when {
                                isSelected -> Color.White
                                else -> MaterialTheme.colors.onSurface
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

@Composable
private fun WeekEventsGrid(
    startDate: LocalDate,
    events: List<Event>,
    timeRange: IntRange,
    hourHeightDp: Float,
    onEventClick: (Event) -> Unit,
    scrollState: ScrollState
) {
    val weekDates = List(7) { index ->
        startDate.plus(DatePeriod(days = index))
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        val dayColumnWidth = maxWidth / 7
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinute = now.hour * 60 + now.minute
        val currentDay = now.date

        Column() {
            timeRange.forEach { index ->
                val hour = timeRange.first + index

                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(hourHeightDp.dp)
                ) {
                    repeat(7) { dayIndex ->
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(0.5.dp, MaterialTheme.colors.onSurface.copy(0.1f))
                        )
                    }
                }
            }
        }

        if (weekDates.any { it == currentDay }) {
            val dayIndex = weekDates.indexOfFirst { it == currentDay }
            if (dayIndex >= 0) {
                val offsetX = dayColumnWidth * dayIndex
                val offsetY = (currentMinute / 60f * hourHeightDp).dp

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .width(dayColumnWidth)
                        .height(2.dp)
                        .background(MaterialTheme.colors.primary)
                )
            }
        }

        weekDates.forEachIndexed { dayIndex, date ->
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
            .background(Color(event.color ?: 0xFF4285F4.toInt()).copy(alpha = 0.8f))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.caption,
            color = Color.White,
            fontSize = 10.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
