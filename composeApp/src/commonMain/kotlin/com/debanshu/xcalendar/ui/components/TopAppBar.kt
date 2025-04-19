package com.debanshu.xcalendar.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.common.lengthOfMonth
import com.debanshu.xcalendar.common.noRippleClickable
import com.debanshu.xcalendar.common.toLocalDateTime
import com.debanshu.xcalendar.common.toSentenceCase
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.domain.states.DateState
import com.debanshu.xcalendar.ui.TopBarCalendarView
import com.debanshu.xcalendar.ui.YearMonth
import com.debanshu.xcalendar.ui.isLeap
import com.debanshu.xcalendar.ui.theme.XCalendarTheme
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopAppBar(
    dateState: DateState,
    monthDropdownState: TopBarCalendarView,
    onMenuClick: () -> Unit,
    onSelectToday: () -> Unit,
    onToggleMonthDropdown: (TopBarCalendarView) -> Unit,
    onDayClick: (LocalDate) -> Unit,
    events: List<Event>,
    holidays: List<Holiday>
) {
    Column(
        modifier = Modifier.background(
            color = XCalendarTheme.colorScheme.onPrimary
        ).animateContentSize()
    ) {
        val currentYear = dateState.currentDate.year

        val showYear = dateState.selectedInViewMonth.year != currentYear

        val rotationDegree by animateFloatAsState(
            targetValue = if (monthDropdownState != TopBarCalendarView.NoView)
                180f else 0f,
            animationSpec = tween(
                durationMillis = 300,
                easing = EaseInCubic
            ),
            label = "rotation"
        )

        val monthTitle = if (showYear) {
            "${dateState.selectedInViewMonth.month.name.toSentenceCase()} ${dateState.selectedInViewMonth.year}"
        } else {
            dateState.selectedInViewMonth.month.name.toSentenceCase()
        }

        TopAppBar(
            colors = TopAppBarColors(
                containerColor = XCalendarTheme.colorScheme.onPrimary,
                scrolledContainerColor = XCalendarTheme.colorScheme.onPrimary,
                navigationIconContentColor = XCalendarTheme.colorScheme.onPrimaryContainer,
                titleContentColor = XCalendarTheme.colorScheme.onPrimaryContainer,
                actionIconContentColor = XCalendarTheme.colorScheme.onPrimaryContainer
            ),
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
            },
            title = {
                Row(
                    modifier = Modifier.noRippleClickable {
                        val toggleView =
                            if (monthDropdownState != TopBarCalendarView.NoView)
                                TopBarCalendarView.NoView
                            else
                                TopBarCalendarView.Month
                        onToggleMonthDropdown(toggleView)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthTitle,
                        style = XCalendarTheme.typography.bodyLarge,
                        color = XCalendarTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        modifier = Modifier.rotate(rotationDegree),
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Toggle Month Dropdown",
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* Handle search */ }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = { onSelectToday() }) {
                    Text(
                        text = dateState.currentDate.dayOfMonth.toString(),
                        style = XCalendarTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                CoilImage(
                    imageModel = {
                        "https://t4.ftcdn.net/jpg/00/04/09/63/360_F_4096398_nMeewldssGd7guDmvmEDXqPJUmkDWyqA.jpg"
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            },
        )

        when (monthDropdownState) {
            TopBarCalendarView.Month -> {
                TopBarMonthView(
                    month = YearMonth(
                        dateState.selectedInViewMonth.year, dateState
                            .selectedInViewMonth.month
                    ),
                    events = events,
                    holidays = holidays,
                    onDayClick = onDayClick,
                    selectedDay = dateState.selectedDate
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun TopBarMonthView(
    month: YearMonth,
    events: List<Event>,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    selectedDay: LocalDate
) {
    val firstDayOfMonth = LocalDate(month.year, month.month, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal + 1
    val daysInMonth = month.month.lengthOfMonth(month.year.isLeap())
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
    ) {

        item(span = { GridItemSpan(7) }) {
            TopAppBarWeekdayHeader()
        }

        items(firstDayOfWeek) {
            TopAppBarEmptyPagingDayCell()
        }

        items(daysInMonth) { day ->
            val date = LocalDate(month.year, month.month, day + 1)
            TopAppBarDayCell(
                date = date,
                events = events.filter { event ->
                    event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                holidays = holidays.filter { holiday ->
                    holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                isSelected = date == selectedDay,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
private fun TopAppBarWeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

        daysOfWeek.forEach { day ->
            Text(
                text = day,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = XCalendarTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TopAppBarDayCell(
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    isSelected: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                when {
                    isSelected -> XCalendarTheme.colorScheme.tertiaryContainer
                    isToday -> XCalendarTheme.colorScheme.secondaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable { onDayClick(date) }
            .padding(4.dp)
    ) {
        Column {
            Text(
                text = date.dayOfMonth.toString(),
                fontSize = 12.sp,
                style = XCalendarTheme.typography.bodyMedium,
                color = when {
                    isToday -> XCalendarTheme.colorScheme.onSecondaryContainer
                    else -> XCalendarTheme.colorScheme.onPrimaryContainer
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Holidays
            holidays.firstOrNull()?.let { holiday ->
                Text(
                    text = holiday.name,
                    style = XCalendarTheme.typography.bodyMedium,
                    color = Color(0xFF2196F3),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF2196F3).copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                        .padding(2.dp)
                )
            }

            // Events (show up to 3 dots for events)
            val maxEventsToDisplay = 3
            val displayedEvents = events.take(maxEventsToDisplay)

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            ) {
                displayedEvents.forEach { event ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                Color((event.color ?: 0xFFE91E63) as Int),
                                CircleShape
                            )
                            .padding(1.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }

                // If there are more events than we can display
                if (events.size > maxEventsToDisplay) {
                    Text(
                        text = "+${events.size - maxEventsToDisplay}",
                        fontSize = 10.sp,
                        color = XCalendarTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppBarEmptyPagingDayCell() {
    Box(
        modifier = Modifier.padding(4.dp)
    )
}