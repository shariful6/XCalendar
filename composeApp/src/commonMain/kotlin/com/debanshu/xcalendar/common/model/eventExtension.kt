package com.debanshu.xcalendar.common.model

import com.debanshu.xcalendar.common.stringToColor
import com.debanshu.xcalendar.data.localDataSource.model.EventEntity
import com.debanshu.xcalendar.data.remoteDataSource.model.calendar.EventResponseItem
import com.debanshu.xcalendar.domain.model.Event


fun EventResponseItem.asEvent(): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        location = location,
        startTime = startTime,
        endTime = endTime,
        isAllDay = isAllDay,
        isRecurring = isRecurring,
        recurringRule = recurringRule,
        reminderMinutes = reminderMinutes,
        calendarId = calendarId,
        calendarName = calenderName,
        color = stringToColor(calendarId)
    )
}

fun EventEntity.asEvent(): Event {
    return Event(
        id = id,
        calendarId = calendarId,
        title = title,
        description = description,
        location = location,
        startTime = startTime,
        endTime = endTime,
        isAllDay = isAllDay,
        isRecurring = isRecurring,
        recurringRule = recurringRule,
        reminderMinutes = emptyList(),
        calendarName = calendarName,
        color = stringToColor(calendarId)
    )
}

fun Event.asEntity(): EventEntity =
    EventEntity(
        id = id,
        calendarId = calendarId,
        title = title,
        description = description,
        location = location,
        startTime = startTime,
        endTime = endTime,
        isAllDay = isAllDay,
        isRecurring = isRecurring,
        calendarName = calendarName,
        recurringRule = recurringRule,
    )
