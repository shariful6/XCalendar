package com.debanshu.xcalendar.domain.repository

import com.debanshu.xcalendar.data.localDataSource.EventDao
import com.debanshu.xcalendar.data.model.EventEntity
import com.debanshu.xcalendar.data.model.EventReminderEntity
import com.debanshu.xcalendar.domain.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository(private val eventDao: EventDao) {
    fun getEventsForCalendarsInRange(calendarIds: List<String>, start: Long, end: Long): Flow<List<Event>> =
        eventDao.getEventsBetweenDates(calendarIds, start, end).map { entities -> entities.map { it.toEvent() } }

    suspend fun addEvent(event: Event) {
        val eventEntity = event.toEntity()
        val reminderEntities = event.reminderMinutes.map { minutes -> EventReminderEntity(event.id, minutes) }
        eventDao.insertEventWithReminders(eventEntity, reminderEntities)
    }

    suspend fun updateEvent(event: Event) {
        val eventEntity = event.toEntity()
        eventDao.upsertEvent(eventEntity)

        // Update reminders
        eventDao.deleteEventReminders(event.id)
        val reminderEntities = event.reminderMinutes.map { minutes ->
            EventReminderEntity(event.id, minutes)
        }
        reminderEntities.forEach { reminder ->
            eventDao.insertEventReminder(reminder)
        }
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event.toEntity())
    }

    private fun EventEntity.toEvent(): Event {
        // Note: In a real implementation, you would fetch reminders for this event
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
            reminderMinutes = emptyList(), // Would be populated from DB
            color = color
        )
    }

    private fun Event.toEntity(): EventEntity =
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
            recurringRule = recurringRule,
            color = color
        )
}