package com.debanshu.xcalendar.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "events",
    foreignKeys = [
        ForeignKey(
            entity = CalendarEntity::class,
            parentColumns = ["id"],
            childColumns = ["calendarId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id", unique = true),
        Index("calendarId")
    ]
)
data class EventEntity(
    @PrimaryKey val id: String,
    val calendarId: String,
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean,
    val isRecurring: Boolean,
    val recurringRule: String?,
    val color: Int?
)