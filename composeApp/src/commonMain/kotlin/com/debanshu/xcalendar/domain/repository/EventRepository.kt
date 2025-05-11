package com.debanshu.xcalendar.domain.repository

import com.debanshu.xcalendar.common.model.asEntity
import com.debanshu.xcalendar.common.model.asEvent
import com.debanshu.xcalendar.data.localDataSource.EventDao
import com.debanshu.xcalendar.data.localDataSource.model.EventReminderEntity
import com.debanshu.xcalendar.domain.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class EventRepository(private val eventDao: EventDao) {
    fun getEventsForCalendarsInRange(calendarIds: List<String>, start: Long, end: Long): Flow<List<Event>> =
        eventDao.getEventsBetweenDates(calendarIds, start, end).map { entities -> entities.map { it.asEvent() } }

    suspend fun addEvent(event: Event) {
        val eventEntity = event.asEntity()
        val reminderEntities = event.reminderMinutes.map { minutes -> EventReminderEntity(event.id, minutes) }
        eventDao.insertEventWithReminders(eventEntity, reminderEntities)
    }

    suspend fun updateEvent(event: Event) {
        val eventEntity = event.asEntity()
        eventDao.upsertEvent(eventEntity)

        eventDao.deleteEventReminders(event.id)
        val reminderEntities = event.reminderMinutes.map { minutes ->
            EventReminderEntity(event.id, minutes)
        }
        reminderEntities.forEach { reminder ->
            eventDao.insertEventReminder(reminder)
        }
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event.asEntity())
    }
}
