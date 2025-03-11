package com.debanshu.xcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.User
import com.debanshu.xcalendar.ui.CalendarView

@Composable
fun CalendarDrawer(
    selectedView: CalendarView,
    onViewSelect: (CalendarView) -> Unit,
    accounts: List<User>,
    calendars: List<Calendar>,
    onCalendarToggle: (Calendar) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colors.surface)
            .padding(vertical = 8.dp)
    ) {
        // App title
        Text(
            text = "Google Calendar",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.onSurface,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 16.dp)
        )
        Divider()
        // View options
        CalendarViewOption(
            name = "Schedule",
            selected = selectedView is CalendarView.Schedule,
            onClick = { onViewSelect(CalendarView.Schedule) }
        )

        CalendarViewOption(
            name = "Day",
            selected = selectedView is CalendarView.Day,
            onClick = { onViewSelect(CalendarView.Day) }
        )

        CalendarViewOption(
            name = "3 Day",
            selected = selectedView is CalendarView.ThreeDay,
            onClick = { onViewSelect(CalendarView.ThreeDay) }
        )

        CalendarViewOption(
            name = "Week",
            selected = selectedView is CalendarView.Week,
            onClick = { onViewSelect(CalendarView.Week) }
        )

        CalendarViewOption(
            name = "Month",
            selected = selectedView is CalendarView.Month,
            onClick = { onViewSelect(CalendarView.Month) }
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Accounts and their calendars
        accounts.forEach { user ->
            AccountSection(
                user = user,
                calendars = calendars.filter { it.userId == user.id },
                onCalendarToggle = onCalendarToggle
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Other calendar sections like Birthdays
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .clickable { /* Toggle birthdays visibility */ }
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color(0xFF8E24AA), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp).align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Birthdays",
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
fun CalendarViewOption(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick)
    ) {
        // Simple icon for the view (can be replaced with actual icons)
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (selected) MaterialTheme.colors.primary.copy(alpha = 0.1f)
                    else Color.Transparent,
                    shape = CircleShape
                )
        ) {
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors
                .onSurface
        )
    }
}

@Composable
fun AccountSection(
    user: User,
    calendars: List<Calendar>,
    onCalendarToggle: (Calendar) -> Unit
) {
    Column {
        // User info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // User profile image
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.primary)
            ) {
                // If we had an actual image, we'd load it here
            }
            Spacer(modifier = Modifier.width(16.dp))
            // User email
            Text(
                text = user.email,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
            )
        }

        // User's calendars
        calendars.forEach { calendar ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clickable { onCalendarToggle(calendar) }
            ) {
                // Calendar color indicator and checkbox
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(calendar.color), shape = CircleShape)
                ) {
                    if (calendar.isVisible) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp).align(Alignment.Center)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Calendar name
                Text(
                    text = calendar.name,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}
