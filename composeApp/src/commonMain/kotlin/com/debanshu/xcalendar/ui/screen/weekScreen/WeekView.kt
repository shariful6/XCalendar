package com.debanshu.xcalendar.ui.screen.weekScreen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Composable
fun WeekView(
    modifier: Modifier = Modifier,
    startDate: LocalDate,
    events: List<Event>,
    onEventClick: (Event) -> Unit,
    scrollState: LazyListState
) {
    val weekDates = List(7) { index ->
        startDate.plus(DatePeriod(days = index))
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = scrollState
    ) {
        items(24) { hour ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // 1 hour = 60dp
            ) {
                // Time label - each cell shows events per day
                for (dayIndex in 0..6) {
                    val date = weekDates[dayIndex]

                    // Find events for this day and hour
                    val eventsForThisTimeSlot = events.filter { event ->
                        val eventDateTime =
                            event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                        eventDateTime.date == date && eventDateTime.hour == hour
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                            .border(
                                width = 0.5.dp,
                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f)
                            )
                    ) {
                        eventsForThisTimeSlot.forEach { event ->
                            val eventStart =
                                event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                            val eventEnd =
                                event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

                            // Calculate position and height
                            val startMinute = eventStart.minute
                            val durationMinutes = if (eventStart.date == eventEnd.date) {
                                (eventEnd.hour - eventStart.hour) * 60 + (eventEnd.minute - eventStart.minute)
                            } else {
                                // If event spans multiple days, show until end of day
                                (23 - eventStart.hour) * 60 + (60 - eventStart.minute)
                            }

                            Box(
                                modifier = Modifier
                                    .offset(y = startMinute.dp)
                                    .fillMaxWidth()
                                    .height((durationMinutes).dp.coerceAtLeast(30.dp))
                                    .padding(1.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Color(event.color ?: 0xFF4285F4.toInt()).copy(
                                            alpha = 0.8f
                                        )
                                    )
                                    .clickable { onEventClick(event) }
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
                    }
                }
            }
        }
    }
}


