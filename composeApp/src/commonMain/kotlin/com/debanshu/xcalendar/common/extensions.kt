package com.debanshu.xcalendar.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun Month.lengthOfMonth(isLeap: Boolean): Int {
    return when (this) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31

        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeap) 29 else 28
        else -> 0
    }
}

fun String.toSentenceCase(): String {
    return this.lowercase().replaceFirstChar {
        if (it
                .isLowerCase()
        ) it.titlecase() else it.toString()
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
expect fun getScreenWidth(): Dp

@Composable
expect fun getScreenHeight(): Dp

@Composable
fun getTopSystemBarHeight(): Dp {
    val windowInsets = WindowInsets.systemBars
    val density = LocalDensity.current

    // Get height of the system bars (status bar + navigation bar)
    return with(density) {
        windowInsets.getTop(density).toDp()
    }
}

@Composable
fun getBottomSystemBarHeight(): Dp {
    val windowInsets = WindowInsets.systemBars
    val density = LocalDensity.current

    // Get height of the system bars (status bar + navigation bar)
    return with(density) {
        windowInsets.getBottom(density).toDp()
    }
}

fun formatHour(hour: Int): String {
    val displayHour = when {
        hour == 0 || hour == 12 -> "12"
        hour > 12 -> (hour - 12).toString()
        else -> hour.toString()
    }
    val amPm = if (hour >= 12) "pm" else "am"
    if(hour == 0) {
        return ""
    }
    return "$displayHour $amPm"
}

@Composable
fun Modifier.customBorder(thickness: Dp, color: Color): Modifier {
    val density = LocalDensity.current
    val strokeWidthPx = density.run { thickness.toPx() }
    return this then Modifier.drawBehind {
        val width = size.width
        val height = size.height

        drawLine(
            color = color,
            start = Offset(x = (width/1.2).toFloat(), y = height),
            end = Offset(x = width, y = height),
            strokeWidth = strokeWidthPx
        )
    }
}