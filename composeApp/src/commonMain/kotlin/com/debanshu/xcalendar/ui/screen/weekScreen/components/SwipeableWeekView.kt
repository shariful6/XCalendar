package com.debanshu.xcalendar.ui.screen.weekScreen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.ui.screen.weekScreen.WeekView
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
fun SwipeableWeekView(
    currentStartDate: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit,
    selectedDay: LocalDate,
    onWeekChange: (LocalDate) -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val screenWidth by derivedStateOf { size.width.toFloat() }
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var targetOffsetX by remember { mutableStateOf(0f) }
    val scrollState = rememberLazyListState()
    val weekDates = List(7) { index ->
        currentStartDate.plus(DatePeriod(days = index))
    }
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

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
                    // Swiped right, show previous week
                    onWeekChange(prevWeekStartDate)
                } else if (targetOffsetX < 0) {
                    // Swiped left, show next week
                    onWeekChange(nextWeekStartDate)
                }
                offsetX = 0f
                targetOffsetX = 0f
                isAnimating = false
            }
        }
    )

    val effectiveOffset = if (isAnimating) animatedOffset else offsetX

    Row {
        TimeColumn(
            modifier = Modifier,
            scrollState = scrollState,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { size = it }
        ) {
            WeekHeader(
                weekDates = weekDates,
                onDayClick = onDayClick,
                selectedDay = selectedDay,
                today = today,
                holidays = holidays
            )

            Box(
                modifier = Modifier
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
                                if (!isAnimating) {
                                    offsetX += amount
                                    change.consume()
                                }
                            }
                        )
                    }
            ) {
                if (screenWidth > 0) {
                    WeekView(
                        scrollState = scrollState,
                        startDate = currentStartDate,
                        events = events,
                        onEventClick = onEventClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset { IntOffset(effectiveOffset.roundToInt(), 0) }
                    )

                    WeekView(
                        scrollState = scrollState,
                        startDate = prevWeekStartDate,
                        events = events,
                        onEventClick = onEventClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .offset {
                                IntOffset(
                                    -screenWidth.roundToInt() + effectiveOffset.roundToInt(),
                                    0
                                )
                            }
                    )

                    WeekView(
                        scrollState = scrollState,
                        startDate = nextWeekStartDate,
                        events = events,
                        onEventClick = onEventClick,
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
        }
    }
}

@Composable
fun TimeColumn(modifier: Modifier, scrollState: LazyListState) {
    LazyColumn(
        modifier = modifier,
//        state = scrollState
    ) {
        items(24) { hour ->
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = formatHour(hour),
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .padding(end = 8.dp, top = 4.dp)
                        .align(Alignment.TopEnd),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun WeekHeader(
    weekDates: List<LocalDate>,
    onDayClick: (LocalDate) -> Unit,
    selectedDay: LocalDate,
    today: LocalDate,
    holidays: List<Holiday>
) {
    LazyRow(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .padding(vertical = 8.dp)
            .padding(start = 10.dp)
    ) {
        items(weekDates) { date ->
            val isSelected = date == selectedDay
            val isToday = date == today
            val holidaysForDate = holidays.filter { holiday ->
                holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillParentMaxWidth(fraction = 1F / 7)
                    .clickable { onDayClick(date) }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = date.dayOfWeek.name.first().toString(),
                    style = MaterialTheme.typography.caption,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .size(28.dp)
                        .background(
                            when {
                                isSelected -> Color(0xFF673AB7) // Purple color as in the image
                                isToday -> MaterialTheme.colors.primary.copy(alpha = 0.2f)
                                else -> Color.Transparent
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.body2,
                        color = when {
                            isSelected -> Color.White
                            else -> MaterialTheme.colors.onSurface
                        },
                        fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }

                holidaysForDate.firstOrNull()?.let { holiday ->
                    Text(
                        text = holiday.name,
                        style = MaterialTheme.typography.caption,
                        color = Color(0xFF4CAF50),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 8.sp,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF4CAF50).copy(alpha = 0.1f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

private fun formatHour(hour: Int): String {
    val displayHour = when {
        hour == 0 -> "12"
        hour > 12 -> (hour - 12).toString()
        else -> hour.toString()
    }
    val amPm = if (hour >= 12) "am" else "am"
    return "$displayHour $amPm"
}