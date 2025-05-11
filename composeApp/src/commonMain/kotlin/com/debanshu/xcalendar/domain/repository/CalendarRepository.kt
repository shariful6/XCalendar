package com.debanshu.xcalendar.domain.repository

import com.debanshu.xcalendar.common.model.asCalendar
import com.debanshu.xcalendar.common.model.asCalendarEntity
import com.debanshu.xcalendar.data.localDataSource.CalendarDao
import com.debanshu.xcalendar.domain.model.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class CalendarRepository(private val calendarDao: CalendarDao) {
    fun getCalendarsForUser(userId: String): Flow<List<Calendar>> =
        calendarDao.getCalendarsByUserId(userId).map { entities -> entities.map { it.asCalendar() } }

    suspend fun upsertCalendar(calendar: Calendar) {
        calendarDao.upsertCalendar(calendar.asCalendarEntity())
    }

    suspend fun deleteCalendar(calendar: Calendar) {
        calendarDao.deleteCalendar(calendar.asCalendarEntity())
    }
}
