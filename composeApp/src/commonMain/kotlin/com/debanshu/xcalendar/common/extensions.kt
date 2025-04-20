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
import androidx.compose.ui.unit.dp
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

    return with(density) {
        windowInsets.getTop(density).toDp()
    }
}

@Composable
fun getBottomSystemBarHeight(): Dp {
    val windowInsets = WindowInsets.systemBars
    val density = LocalDensity.current

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

/**
 * Enhanced custom border modifier that allows enabling specific sides with
 * additional control over the start position and length of each border.
 *
 * @param start Enable/disable start (left) border
 * @param top Enable/disable top border
 * @param end Enable/disable end (right) border
 * @param bottom Enable/disable bottom border
 * @param startFraction Start position of start border (0f = top, 1f = bottom)
 * @param topFraction Start position of top border (0f = left, 1f = right)
 * @param endFraction Start position of end border (0f = top, 1f = bottom)
 * @param bottomFraction Start position of bottom border (0f = left, 1f = right)
 * @param startLengthFraction Length of start border (0f = none, 1f = full height)
 * @param topLengthFraction Length of top border (0f = none, 1f = full width)
 * @param endLengthFraction Length of end border (0f = none, 1f = full height)
 * @param bottomLengthFraction Length of bottom border (0f = none, 1f = full width)
 * @param color Border color
 * @param width Border width
 */
fun Modifier.customBorder(
    // Enable/disable borders on each side
    start: Boolean = false,
    top: Boolean = false,
    end: Boolean = false,
    bottom: Boolean = false,

    // Fractional start position for each border
    startFraction: Float = 0f,
    topFraction: Float = 0f,
    endFraction: Float = 0f,
    bottomFraction: Float = 0f,

    // Fractional length for each border
    startLengthFraction: Float = 1f,
    topLengthFraction: Float = 1f,
    endLengthFraction: Float = 1f,
    bottomLengthFraction: Float = 1f,

    // Styling
    color: Color = Color.Red,
    width: Dp = 2.dp
) = composed {
    drawBehind {
        val strokeWidth = width.toPx()

        if (start) {
            val startX = 0f
            val startY = size.height * startFraction.coerceIn(0f, 1f)
            val endX = 0f
            val endY = startY + (size.height * startLengthFraction.coerceIn(0f, 1f))
                .coerceAtMost(size.height - startY)

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }

        if (top) {
            val startX = size.width * topFraction.coerceIn(0f, 1f)
            val startY = 0f
            val endX = startX + (size.width * topLengthFraction.coerceIn(0f, 1f))
                .coerceAtMost(size.width - startX)
            val endY = 0f

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }

        if (end) {
            val startX = size.width
            val startY = size.height * endFraction.coerceIn(0f, 1f)
            val endX = size.width
            val endY = startY + (size.height * endLengthFraction.coerceIn(0f, 1f))
                .coerceAtMost(size.height - startY)

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }

        if (bottom) {
            val startX = size.width * bottomFraction.coerceIn(0f, 1f)
            val startY = size.height
            val endX = startX + (size.width * bottomLengthFraction.coerceIn(0f, 1f))
                .coerceAtMost(size.width - startX)
            val endY = size.height

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }
    }
}