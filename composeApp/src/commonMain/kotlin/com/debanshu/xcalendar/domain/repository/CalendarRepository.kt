package com.debanshu.xcalendar.domain.repository

import com.debanshu.xcalendar.data.localDataSource.CalendarDao
import com.debanshu.xcalendar.data.localDataSource.model.CalendarEntity
import com.debanshu.xcalendar.domain.model.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class CalendarRepository(private val calendarDao: CalendarDao) {
    fun getCalendarsForUser(userId: String): Flow<List<Calendar>> =
        calendarDao.getCalendarsByUserId(userId).map { entities -> entities.map { it.toCalendar() } }

    suspend fun upsertCalendar(calendar: Calendar) {
        calendarDao.upsertCalendar(calendar.toEntity())
    }

    suspend fun deleteCalendar(calendar: Calendar) {
        calendarDao.deleteCalendar(calendar.toEntity())
    }

    private fun CalendarEntity.toCalendar(): Calendar =
        Calendar(id, name, color, userId, isVisible, isPrimary)

    private fun Calendar.toEntity(): CalendarEntity =
        CalendarEntity(id, name, color, userId, isVisible, isPrimary)
}