package com.debanshu.xcalendar.data.remoteDataSource

import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday

interface CalendarApiService {
    suspend fun fetchCalendarsForUser(userId: String): List<Calendar>
    suspend fun fetchEventsForCalendar(calendarId: String, startTime: Long, endTime: Long): List<Event>
    suspend fun fetchHolidays(countryCode: String, year: Int): List<Holiday>
}