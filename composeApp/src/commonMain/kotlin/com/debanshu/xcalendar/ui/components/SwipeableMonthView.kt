package com.debanshu.xcalendar.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.ui.screen.MonthScreen.MonthView
import com.debanshu.xcalendar.ui.YearMonth
import kotlinx.datetime.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A swipeable month view component that allows infinite horizontal paging through months
 * while preserving the selected day between swipes.
 */
@Composable
fun SwipeableMonthView(
    initialMonth: YearMonth,
    events: List<Event>,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    selectedDay: LocalDate,
    onMonthChange: (YearMonth) -> Unit
) {
    // Use derived state to track the current month being displayed
    var centerMonth by remember(initialMonth) { mutableStateOf(initialMonth) }

    // Track the previous initialMonth to detect external navigation
    val previousInitialMonth = remember { mutableStateOf(initialMonth) }

    // Handle external navigation (like Today button)
    LaunchedEffect(initialMonth) {
        if (initialMonth != previousInitialMonth.value) {
            centerMonth = initialMonth
            previousInitialMonth.value = initialMonth
        }
    }

    // Size of the component for calculating offsets
    var size by remember { mutableStateOf(IntSize.Zero) }

    // Drag gesture and animation state
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var targetOffsetX by remember { mutableStateOf(0f) }

    // Calculate adjacent months
    val previousMonth = remember(centerMonth) { centerMonth.plusMonths(-1) }
    val nextMonth = remember(centerMonth) { centerMonth.plusMonths(1) }

    // Animate offset for smooth transitions
    val animatedOffset by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (isAnimating) {
                // Determine which month should become the new center
                if (targetOffsetX > 0) {
                    // Moving to previous month - preserve selected day
                    centerMonth = previousMonth
                    onMonthChange(centerMonth)
                } else if (targetOffsetX < 0) {
                    // Moving to next month - preserve selected day
                    centerMonth = nextMonth
                    onMonthChange(centerMonth)
                }

                // Reset state for next interaction
                offsetX = 0f
                targetOffsetX = 0f
                isAnimating = false
            }
        }
    )

    // The actual animated offset used in layout
    val effectiveOffset = if (isAnimating) animatedOffset else offsetX

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // Threshold for triggering month change (30% of screen width)
                        val screenWidth = size.width.toFloat()
                        val threshold = screenWidth * 0.3f

                        if (abs(offsetX) > threshold) {
                            // Animate to next/previous month
                            isAnimating = true
                            targetOffsetX = if (offsetX > 0) {
                                screenWidth  // Animate to full screen width (prev month)
                            } else {
                                -screenWidth // Animate to negative width (next month)
                            }
                        } else {
                            // Not enough drag, snap back to current month
                            isAnimating = true
                            targetOffsetX = 0f
                        }
                    },
                    onDragCancel = {
                        isAnimating = true
                        targetOffsetX = 0f
                    },
                    onHorizontalDrag = { change, amount ->
                        if (!isAnimating) {
                            offsetX += amount
                            change.consume()
                        }
                    }
                )
            }
    ) {
        val screenWidth = size.width

        // Only render if we have dimensions
        if (screenWidth > 0) {
            // Current month (centered)
            MonthView(
                month = centerMonth,
                events = events,
                holidays = holidays,
                onDayClick = onDayClick,
                selectedDay = selectedDay,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(effectiveOffset.roundToInt(), 0) }
            )

            // Previous month (to the left)
            MonthView(
                month = previousMonth,
                events = events,
                holidays = holidays,
                onDayClick = onDayClick,
                selectedDay = selectedDay,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(-screenWidth + effectiveOffset.roundToInt(), 0) }
            )

            // Next month (to the right)
            MonthView(
                month = nextMonth,
                events = events,
                holidays = holidays,
                onDayClick = onDayClick,
                selectedDay = selectedDay,
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(screenWidth + effectiveOffset.roundToInt(), 0) }
            )
        }
    }
}