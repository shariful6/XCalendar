package com.debanshu.xcalendar.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
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
