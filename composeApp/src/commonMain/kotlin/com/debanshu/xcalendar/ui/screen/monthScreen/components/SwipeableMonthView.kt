package com.debanshu.xcalendar.ui.screen.monthScreen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import com.debanshu.xcalendar.common.model.YearMonth
import kotlinx.datetime.LocalDate
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A swipeable month view component that allows infinite horizontal paging through months
 * while preserving the selected day between swipes.
 */
@Composable
fun SwipeableMonthView(
    modifier: Modifier = Modifier,
    currentMonth: YearMonth,
    events: () -> List<Event>,
    holidays: () -> List<Holiday>,
    onSpecificDayClicked: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    // Consolidated swipe state to minimize recompositions
    var swipeState by remember { mutableStateOf(SwipeState()) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    // Derive screen width only when size changes
    val screenWidth by remember {
        derivedStateOf { screenSize.width.toFloat() }
    }

    // Pre-calculate month data with stable references
    val monthData = remember(currentMonth) {
        MonthData(
            current = MonthViewData(currentMonth, events, holidays),
            previous = MonthViewData(currentMonth.plusMonths(-1), events, holidays),
            next = MonthViewData(currentMonth.plusMonths(1), events, holidays)
        )
    }

    // Animate offset with proper state management
    val animatedOffset by animateFloatAsState(
        targetValue = swipeState.targetOffsetX,
        animationSpec = tween(durationMillis = 300),
        finishedListener = { finalValue ->
            if (swipeState.isAnimating) {
                when {
                    finalValue > 0 -> onMonthChange(currentMonth.plusMonths(-1))
                    finalValue < 0 -> onMonthChange(currentMonth.plusMonths(1))
                }
                swipeState = SwipeState() // Reset to initial state
            }
        },
        label = "month_swipe_animation"
    )

    val effectiveOffset = if (swipeState.isAnimating) animatedOffset else swipeState.offsetX


    Surface(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { newSize ->
                if (screenSize != newSize) {
                    screenSize = newSize
                }
            }
            .pointerInput(screenWidth) { // Key on screenWidth to reset when size changes
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = screenWidth * 0.3f
                        swipeState = if (abs(swipeState.offsetX) > threshold) {
                            swipeState.copy(
                                isAnimating = true,
                                targetOffsetX = if (swipeState.offsetX > 0) screenWidth else -screenWidth
                            )
                        } else {
                            swipeState.copy(isAnimating = true, targetOffsetX = 0f)
                        }
                    },
                    onDragCancel = {
                        swipeState = swipeState.copy(isAnimating = true, targetOffsetX = 0f)
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        swipeState = swipeState.copy(
                            targetOffsetX = swipeState.targetOffsetX + dragAmount
                        )
                        if (!swipeState.isAnimating) {
                            swipeState = swipeState.copy(
                                offsetX = swipeState.offsetX + dragAmount
                            )
                            change.consume()
                        }
                    }
                )
            }
    ) {
        // Current month view
        MonthViewContainer(
            monthViewData = monthData.current,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(effectiveOffset.roundToInt(), 0) }
        )

        // Previous month view
        MonthViewContainer(
            monthViewData = monthData.previous,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        -screenWidth.roundToInt() + effectiveOffset.roundToInt(),
                        0
                    )
                }
        )

        // Next month view
        MonthViewContainer(
            monthViewData = monthData.next,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        screenWidth.roundToInt() + effectiveOffset.roundToInt(),
                        0
                    )
                }
        )
    }
}

/**
 * Container for MonthView that handles lazy data evaluation
 */
@Composable
private fun MonthViewContainer(
    monthViewData: MonthViewData,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    // Lazy evaluate events and holidays only when needed
    val events = remember(monthViewData.month) { monthViewData.events() }
    val holidays = remember(monthViewData.month) { monthViewData.holidays() }

    MonthView(
        month = monthViewData.month,
        events = { events },
        holidays = { holidays },
        onDayClick = onDayClick,
        modifier = modifier
    )
}

/**
 * Consolidated swipe state to minimize recompositions
 */
@Stable
private data class SwipeState(
    val offsetX: Float = 0f,
    val isAnimating: Boolean = false,
    val targetOffsetX: Float = 0f
)

/**
 * Stable data holder for month view data
 */
@Stable
private data class MonthViewData(
    val month: YearMonth,
    val events: () -> List<Event>,
    val holidays: () -> List<Holiday>
)

/**
 * Container for all three month data instances
 */
@Stable
private data class MonthData(
    val current: MonthViewData,
    val previous: MonthViewData,
    val next: MonthViewData
)