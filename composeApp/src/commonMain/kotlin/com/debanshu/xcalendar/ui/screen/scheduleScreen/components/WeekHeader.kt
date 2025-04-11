package com.debanshu.xcalendar.ui.screen.scheduleScreen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@Composable
fun WeekHeader(startDate: LocalDate, endDate: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${startDate.dayOfMonth} – ${endDate.dayOfMonth} ${endDate.month.name.take(3)}",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Medium
        )
    }
}