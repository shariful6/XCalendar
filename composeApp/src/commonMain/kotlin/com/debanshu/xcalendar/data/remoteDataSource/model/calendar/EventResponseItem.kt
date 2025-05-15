package com.debanshu.xcalendar.data.remoteDataSource.model.calendar

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventResponseItem(
    @SerialName("calendarId")
    val calendarId: String,
    @SerialName("calenderName")
    val calenderName: String,
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
    val location: String? = null,
    @SerialName("recurringRule")
    val recurringRule: String? = null,
    @SerialName("reminderMinutes")
    val reminderMinutes: List<Int>,
    @SerialName("startTime")
    val startTime: Long,
    @SerialName("title")
    val title: String
)
