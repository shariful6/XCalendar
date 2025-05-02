package com.debanshu.xcalendar.ui.screen.monthScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.common.getBottomSystemBarHeight
import com.debanshu.xcalendar.common.getScreenHeight
import com.debanshu.xcalendar.common.getScreenWidth
import com.debanshu.xcalendar.common.getTopSystemBarHeight
import com.debanshu.xcalendar.common.noRippleClickable
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.ui.components.EventTag
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


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
    val maxEventsToShow = 3
    val displayedEvents = events.take(maxEventsToShow)
    val screenWidth = getScreenWidth()
    val screenHeight =
        getScreenHeight().plus(30.dp) - getTopSystemBarHeight() - getBottomSystemBarHeight()

    LazyColumn(
        modifier = modifier
            .background(XCalendarTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 0.2.dp,
                color = XCalendarTheme.colorScheme.outlineVariant
            )
            .aspectRatio(screenWidth / screenHeight)
            .noRippleClickable { onDayClick(date) }
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
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
        }

        item {
            Spacer(modifier = Modifier.height(2.dp))
            holidays.forEach { holiday ->
                EventTag(
                    modifier = Modifier.padding(bottom = 2.dp),
                    text = holiday.name,
                    color = Color(0xFF007F73),
                    textColor = XCalendarTheme.colorScheme.inverseOnSurface
                )
            }
        }

        items(displayedEvents){ event ->
            Spacer(modifier = Modifier.height(2.dp))
            EventTag(
                text = event.title,
                color = Color(event.color ?: 0xFFE91E63.toInt()),
                textColor = XCalendarTheme.colorScheme.inverseOnSurface
            )
        }

        if (events.size > maxEventsToShow) {
            item {
                Text(
                    text = "+${events.size - maxEventsToShow} more",
                    style = XCalendarTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 2.dp, top = 1.dp)
                )
            }
        }
    }
}