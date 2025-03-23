package com.debanshu.xcalendar.ui.screen.monthScreen.components

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
import com.debanshu.xcalendar.ui.screen.monthScreen.MonthView
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
    var centerMonth by remember(initialMonth) { mutableStateOf(initialMonth) }
    val previousMonth = remember(centerMonth) { centerMonth.plusMonths(-1) }
    val nextMonth = remember(centerMonth) { centerMonth.plusMonths(1) }

    var size by remember { mutableStateOf(IntSize.Zero) }
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var targetOffsetX by remember { mutableStateOf(0f) }


    val animatedOffset by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (isAnimating) {
                if (targetOffsetX > 0) {
                    centerMonth = previousMonth
                    onMonthChange(centerMonth)
                } else if (targetOffsetX < 0) {
                    centerMonth = nextMonth
                    onMonthChange(centerMonth)
                }

                offsetX = 0f
                targetOffsetX = 0f
                isAnimating = false
            }
        }
    )

    val effectiveOffset = if (isAnimating) animatedOffset else offsetX

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val screenWidth = size.width.toFloat()
                        val threshold = screenWidth * 0.3f

                        if (abs(offsetX) > threshold) {
                            isAnimating = true
                            targetOffsetX = if (offsetX > 0) {
                                screenWidth
                            } else {
                                -screenWidth
                            }
                        } else {
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
        if (screenWidth > 0) {
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