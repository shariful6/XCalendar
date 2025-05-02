package com.debanshu.xcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.debanshu.xcalendar.ui.theme.XCalendarTheme

@Composable
fun EventTag(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    textColor: Color,
) {
    Text(
        text = text,
        style = XCalendarTheme.typography.labelSmall.copy(fontSize = 8.sp),
        textAlign = TextAlign.Start,
        maxLines = 1,
        color = textColor,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.fillMaxWidth()
            .background(color, RoundedCornerShape(2.dp))
            .padding(2.dp)
    )
}
