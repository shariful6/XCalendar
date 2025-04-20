package com.debanshu.xcalendar.ui.screen.monthScreen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.debanshu.xcalendar.ui.YearMonth
import com.debanshu.xcalendar.ui.screen.monthScreen.MonthView
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
    events: List<Event>,
    holidays: List<Holiday>,
    onSpecificDayClicked: (LocalDate) -> Unit,
    currentSelectedDay: LocalDate,
    onMonthChange: (YearMonth) -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val screenWidth by derivedStateOf { size.width.toFloat() }
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var targetOffsetX by remember { mutableStateOf(0f) }


    val animatedOffset by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (isAnimating) {
                if (targetOffsetX > 0) {
                    onMonthChange(currentMonth.plusMonths(-1))
                } else if (targetOffsetX < 0) {
                    onMonthChange(currentMonth.plusMonths(1))
                }
                offsetX = 0f
                targetOffsetX = 0f
                isAnimating = false
            }
        }
    )

    val effectiveOffset = if (isAnimating) animatedOffset else offsetX

    Surface(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
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
                        targetOffsetX += amount
                        if (!isAnimating) {
                            offsetX += amount
                            change.consume()
                        }
                    }
                )
            }
    ) {
        MonthView(
            month = currentMonth,
            events = events,
            holidays = holidays,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(effectiveOffset.roundToInt(), 0) }
        )

        MonthView(
            month = currentMonth.plusMonths(-1),
            events = events,
            holidays = holidays,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(-screenWidth.roundToInt() + effectiveOffset.roundToInt(), 0) }
        )

        MonthView(
            month = currentMonth.plusMonths(1),
            events = events,
            holidays = holidays,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(screenWidth.roundToInt() + effectiveOffset.roundToInt(), 0) }
        )

    }
}