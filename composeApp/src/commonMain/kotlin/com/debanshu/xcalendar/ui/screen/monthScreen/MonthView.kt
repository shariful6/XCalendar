package com.debanshu.xcalendar.ui.screen.monthScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.common.customBorder
import com.debanshu.xcalendar.common.getBottomSystemBarHeight
import com.debanshu.xcalendar.common.getScreenHeight
import com.debanshu.xcalendar.common.getScreenWidth
import com.debanshu.xcalendar.common.getTopSystemBarHeight
import com.debanshu.xcalendar.common.lengthOfMonth
import com.debanshu.xcalendar.common.noRippleClickable
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.ui.YearMonth
import com.debanshu.xcalendar.ui.isLeap
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

@Composable
fun MonthView(
    modifier: Modifier,
    month: YearMonth,
    events: List<Event>,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
) {
    val firstDayOfMonth = LocalDate(month.year, month.month, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal + 1
    val daysInMonth = month.month.lengthOfMonth(month.year.isLeap())

    val skipPreviousPadding = firstDayOfWeek >= 7
    val totalDaysDisplayed = if (skipPreviousPadding) daysInMonth else firstDayOfWeek + daysInMonth
    val remainingCells = 42 - totalDaysDisplayed

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(7),
        userScrollEnabled = false
    ) {
        item(span = { GridItemSpan(7) }) {
            WeekdayHeader()
        }
        if (firstDayOfWeek > 0 && !skipPreviousPadding) {
            items(firstDayOfWeek) { index ->
                val prevMonth =
                    if (month.month.number == 1) Month(12) else Month(month.month.number - 1)
                val prevYear = if (month.month.number == 1) month.year - 1 else month.year
                val daysInPrevMonth = prevMonth.lengthOfMonth(prevYear.isLeap())
                val day = daysInPrevMonth - (firstDayOfWeek - index - 1)
                val date = LocalDate(prevYear, prevMonth, day)

                DayCell(
                    modifier = Modifier,
                    date = date,
                    events = events.filter { event ->
                        event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                    },
                    holidays = holidays.filter { holiday ->
                        holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                    },
                    isCurrentMonth = false,
                    onDayClick = onDayClick
                )
            }
        }

        items(daysInMonth) { day ->
            val date = LocalDate(month.year, month.month, day + 1)
            DayCell(
                modifier = Modifier,
                date = date,
                events = events.filter { event ->
                    event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                holidays = holidays.filter { holiday ->
                    holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                isCurrentMonth = true,
                onDayClick = onDayClick
            )
        }

        items(remainingCells) { day ->
            val nextMonth =
                if (month.month.number == 12) Month(1) else Month(month.month.number + 1)
            val nextYear = if (month.month.number == 12) month.year + 1 else month.year
            val date = LocalDate(nextYear, nextMonth, day + 1)

            DayCell(
                modifier = Modifier,
                date = date,
                events = events.filter { event ->
                    event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                holidays = holidays.filter { holiday ->
                    holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                isCurrentMonth = false,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
fun WeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val ordinalToday = if(today.dayOfWeek.ordinal == 6) 0 else today.dayOfWeek.ordinal + 1
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

        daysOfWeek.forEachIndexed {dayIndex, day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(XCalendarTheme.colorScheme.surfaceContainerLow)
                    .customBorder(
                        end = true,
                        bottom = true,
                        start = true,
                        startFraction = 0.70f,
                        startLengthFraction = 1f,
                        endFraction = 0.70f,
                        endLengthFraction = 1f,
                        bottomFraction = 0f,
                        bottomLengthFraction = 1f,
                        color = XCalendarTheme.colorScheme.outlineVariant,
                        width = 1.dp
                    )
                    .padding(vertical = XCalendarTheme.dimensions.spacing_8),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = XCalendarTheme.typography.bodySmall,
                    color = if(dayIndex == ordinalToday)
                        XCalendarTheme.colorScheme.primary
                    else
                        XCalendarTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun DayCell(
    modifier: Modifier,
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    isCurrentMonth: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today
    val screenWidth = getScreenWidth()
    val screenHeight =
        getScreenHeight().plus(30.dp) - getTopSystemBarHeight() - getBottomSystemBarHeight()

    Column(
        modifier = modifier
            .background(XCalendarTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 0.2.dp,
                color = XCalendarTheme.colorScheme.outlineVariant
            )
            .aspectRatio(screenWidth / screenHeight)
            .noRippleClickable { onDayClick(date) }
            .padding(top = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .background(
                    when {
                        isToday -> XCalendarTheme.colorScheme.primary
                        else -> Color.Transparent
                    },
                    CircleShape
                )
                .padding(4.dp),
            text = date.dayOfMonth.toString(),
            style = XCalendarTheme.typography.labelSmall,
            color = when {
                isToday -> XCalendarTheme.colorScheme.inverseOnSurface
                isCurrentMonth -> XCalendarTheme.colorScheme.onSurface
                else -> XCalendarTheme.colorScheme.onSurfaceVariant
            },
            textAlign = TextAlign.Center
        )

        val maxEventsToShow = 3
        val displayedEvents = events.take(maxEventsToShow)

        holidays.firstOrNull()?.let { holiday ->
            EventTag(
                text = holiday.name,
                color = Color(0xFF4285F4).copy(alpha = 0.8f),
                textColor = Color.White
            )
        }

        displayedEvents.forEach { event ->
            EventTag(
                text = event.title,
                color = Color(event.color ?: 0xFFE91E63.toInt()).copy(alpha = 0.8f),
                textColor = Color.White
            )
        }

        if (events.size > maxEventsToShow) {
            Text(
                text = "+${events.size - maxEventsToShow} more",
                style = XCalendarTheme.typography.labelSmall,
                fontSize = 8.sp,
                color = XCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 2.dp, top = 1.dp)
            )
        }
    }
}

@Composable
fun EventTag(
    text: String,
    color: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 1.dp)
            .height(16.dp)
            .background(color, RoundedCornerShape(2.dp))
            .padding(horizontal = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}
