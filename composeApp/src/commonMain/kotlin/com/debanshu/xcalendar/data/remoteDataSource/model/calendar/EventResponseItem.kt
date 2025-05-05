package com.debanshu.xcalendar.data.remoteDataSource.model.calendar


import com.debanshu.xcalendar.domain.model.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventResponseItem(
    @SerialName("calendarId")
    val calendarId: String,
    @SerialName("description")
    val description: String,
    @SerialName("endTime")
    val endTime: Long,
    @SerialName("id")
    val id: String,
    @SerialName("isAllDay")
    val isAllDay: Boolean,
    @SerialName("isRecurring")
    val isRecurring: Boolean,
    @SerialName("location")
    val location: String,
    @SerialName("recurringRule")
    val recurringRule: String,
    @SerialName("reminderMinutes")
    val reminderMinutes: List<Int>,
    @SerialName("startTime")
    val startTime: Long,
    @SerialName("title")
    val title: String
)

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
        calendarId = calendarId
    )
}