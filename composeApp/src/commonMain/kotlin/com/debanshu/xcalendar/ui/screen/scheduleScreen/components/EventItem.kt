package com.debanshu.xcalendar.ui.screen.scheduleScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.ui.theme.XCalendarTheme

@Composable
fun EventItem(
    title: String,
    color: Color,
    onClick: () -> Unit,
    timeText: String? = null
) {
    // Optimized: Pre-calculate colors to avoid repeated alpha calculations
    val backgroundColor = remember(color) { color.copy(alpha = 0.15f) }
    val timeTextColor = remember(color) { color.copy(alpha = 0.7f) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                style = XCalendarTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )

            timeText?.let {
                Text(
                    text = it,
                    style = XCalendarTheme.typography.labelSmall,
                    color = timeTextColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}