package com.debanshu.xcalendar.common.model

import com.debanshu.xcalendar.data.localDataSource.model.CalendarEntity
import com.debanshu.xcalendar.data.remoteDataSource.model.calendar.CalendarResponseItem
import com.debanshu.xcalendar.domain.model.Calendar

fun CalendarResponseItem.asCalendar(): Calendar {
    return Calendar(
        id = id,
        name = name,
        color = color,
        isVisible = isVisible,
        isPrimary = isPrimary,
        userId = userId
    )
}

fun CalendarEntity.asCalendar(): Calendar =
    Calendar(id, name, color, userId, isVisible, isPrimary)

fun Calendar.asCalendarEntity(): CalendarEntity =
    CalendarEntity(id, name, color, userId, isVisible, isPrimary)

fun Calendar.CalendarResponseItem(): CalendarResponseItem =
    CalendarResponseItem(
        id = id,
        name = name,
        color = color,
        userId = userId,
        isVisible = isVisible,
        isPrimary = isPrimary
    )