package com.debanshu.xcalendar.data.remoteDataSource

import com.debanshu.xcalendar.data.remoteDataSource.error.DataError
import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import io.ktor.client.HttpClient
import org.koin.core.annotation.Singleton

@Singleton
class RemoteCalendarApiService(client: HttpClient) {
    private val clientWrapper = ClientWrapper(client)
    private val baseUrl = "https://www.googleapis.com/calendar/v3/"
    suspend fun fetchCalendarsForUser(userId: String): Result<List<Calendar>, DataError> {
        return clientWrapper.networkGetUsecase<List<Calendar>>(
            baseUrl+"users/me/calendarList",
            mapOf(
                "user_id" to userId
            )
        )
    }

    suspend fun fetchEventsForCalendar(
        calendarId: String,
        startTime: Long,
        endTime: Long
    ): Result<List<Event>, DataError> {
        return clientWrapper.networkGetUsecase<List<Event>>(
            baseUrl+"calendars/${calendarId}/events",
            mapOf(
                "calendar_id" to calendarId,
                "start_time" to startTime.toString(),
                "end_time" to endTime.toString()
            )
        )
    }
}