package com.debanshu.xcalendar.data.remoteDataSource

import com.debanshu.xcalendar.domain.model.Calendar
import com.debanshu.xcalendar.domain.model.Event
import com.debanshu.xcalendar.domain.model.Holiday
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Singleton
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

@Singleton
class DummyCalendarApiService : CalendarApiService {
    override suspend fun fetchCalendarsForUser(userId: String): List<Calendar> {
        // Generate dummy calendars with different colors
        val colors = listOf(0xFF4285F4, 0xFFDB4437, 0xFF0F9D58, 0xFFF4B400, 0xFF8560A8, 0xFF03BCD4)
        return listOf(
            Calendar(
                id = "${userId}_primary",
                name = "My Calendar",
                color = colors[0].toInt(),
                userId = userId,
                isPrimary = true
            ),
            Calendar(
                id = "${userId}_work",
                name = "Work",
                color = colors[1].toInt(),
                userId = userId
            ),
            Calendar(
                id = "${userId}_family",
                name = "Family",
                color = colors[2].toInt(),
                userId = userId
            )
        )
    }

    override suspend fun fetchEventsForCalendar(calendarId: String, startTime: Long, endTime: Long): List<Event> {
        // Create random events within the date range
        val random = Random(calendarId.hashCode() + startTime)
        val events = mutableListOf<Event>()

        // Recurring events like "MF Standup"
        if (calendarId.contains("work")) {
            // Convert epoch milliseconds to Instant and LocalDateTime
            val startInstant = Instant.fromEpochMilliseconds(startTime)
            val endInstant = Instant.fromEpochMilliseconds(endTime)
            val timeZone = TimeZone.currentSystemDefault()

            var currentDate = startInstant.toLocalDateTime(timeZone).date
            val endDate = endInstant.toLocalDateTime(timeZone).date

            // Daily standup on weekdays
            while (currentDate <= endDate) {
                val dayOfWeek = currentDate.dayOfWeek
                // Monday to Friday (DayOfWeek.MONDAY is 1, DayOfWeek.FRIDAY is 5)
                if (dayOfWeek.ordinal in 1..5) {
                    // 11:30 AM
                    val eventStartDateTime = LocalDateTime(
                        currentDate.year,
                        currentDate.month,
                        currentDate.dayOfMonth,
                        11, 30, 0, 0
                    )
                    // Convert to epoch millis
                    val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                    // 30 minutes later
                    val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds

                    events.add(
                        Event(
                            id = "standup_${eventStartTimeMillis}",
                            calendarId = calendarId,
                            title = "MF Standup",
                            startTime = eventStartTimeMillis,
                            endTime = eventEndTimeMillis,
                            location = "Groww Vaishnavi Tech Park"
                        )
                    )
                }

                // Add a day
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }

            // Reset to start date for other events
            currentDate = startInstant.toLocalDateTime(timeZone).date

            // Add biweekly meetings
            var biweeklyCounter = 0
            while (currentDate <= endDate) {
                val dayOfWeek = currentDate.dayOfWeek
                // Thursday and every other week
                if (dayOfWeek == DayOfWeek.THURSDAY && biweeklyCounter % 2 == 0) {
                    // 2:30 PM
                    val eventStartDateTime = LocalDateTime(
                        currentDate.year,
                        currentDate.month,
                        currentDate.dayOfMonth,
                        14, 30, 0, 0
                    )
                    // Convert to epoch millis
                    val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                    // 30 minutes later
                    val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds

                    events.add(
                        Event(
                            id = "automation_${eventStartTimeMillis}",
                            calendarId = calendarId,
                            title = "App Automation biweekly",
                            startTime = eventStartTimeMillis,
                            endTime = eventEndTimeMillis,
                            location = "Groww Vaishnavi Tech Park 3rd Floor"
                        )
                    )
                }

                // Add a day and increment counter on Sundays
                if (dayOfWeek == DayOfWeek.SUNDAY) {
                    biweeklyCounter++
                }
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }

            // Reset to start date for other events
            currentDate = startInstant.toLocalDateTime(timeZone).date

            // Product pitch reviews
            while (currentDate <= endDate) {
                val dayOfWeek = currentDate.dayOfWeek
                if (dayOfWeek == DayOfWeek.WEDNESDAY) {
                    // 4:00 PM
                    val eventStartDateTime = LocalDateTime(
                        currentDate.year,
                        currentDate.month,
                        currentDate.dayOfMonth,
                        16, 0, 0, 0
                    )
                    // Convert to epoch millis
                    val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                    // 50 minutes later
                    val eventEndTimeMillis = eventStartTimeMillis + 50.minutes.inWholeMilliseconds

                    events.add(
                        Event(
                            id = "pitch_${eventStartTimeMillis}",
                            calendarId = calendarId,
                            title = "Product Pitch reviews [Candle 3rd floor]",
                            startTime = eventStartTimeMillis,
                            endTime = eventEndTimeMillis,
                            location = "Groww Vaishnavi Tech Park 3rd Floor-3rd Floor-Candle (20)"
                        )
                    )
                }

                // Add a day
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }

            // Reset to start date for other events
            currentDate = startInstant.toLocalDateTime(timeZone).date

            // Fun Fridays
            while (currentDate <= endDate) {
                val dayOfWeek = currentDate.dayOfWeek
                if (dayOfWeek == DayOfWeek.FRIDAY) {
                    // 4:30 PM
                    val eventStartDateTime = LocalDateTime(
                        currentDate.year,
                        currentDate.month,
                        currentDate.dayOfMonth,
                        16, 30, 0, 0
                    )
                    // Convert to epoch millis
                    val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                    // 30 minutes later
                    val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds

                    events.add(
                        Event(
                            id = "fun_${eventStartTimeMillis}",
                            calendarId = calendarId,
                            title = "Fun fridays",
                            startTime = eventStartTimeMillis,
                            endTime = eventEndTimeMillis,
                            location = "Groww Vaishnavi Tech Park"
                        )
                    )
                }

                // Add a day
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }
        }

        return events
    }

    override suspend fun fetchHolidays(countryCode: String, year: Int): List<Holiday> {
        // Generate some dummy holidays
        val holidays = mutableListOf<Holiday>()
        val timeZone = TimeZone.currentSystemDefault()

        // Add some common holidays
        if (countryCode == "IN") {
            // Republic Day (India)
            val republicDay = LocalDateTime(year, Month.JANUARY, 26, 0, 0)
            holidays.add(
                Holiday(
                    id = "republic_day_$year",
                    name = "Republic Day",
                    date = republicDay.toInstant(timeZone).toEpochMilliseconds(),
                    countryCode = "IN"
                )
            )

            // Maha Shivaratri (date varies by year, using a fixed date for example)
            val shivaratri = LocalDateTime(year, Month.FEBRUARY, 26, 0, 0)
            holidays.add(
                Holiday(
                    id = "shivaratri_$year",
                    name = "Maha Shivaratri/Shivaratri",
                    date = shivaratri.toInstant(timeZone).toEpochMilliseconds(),
                    countryCode = "IN"
                )
            )

            // Holi
            val holi = LocalDateTime(year, Month.MARCH, 9, 0, 0) // Example date for 2025
            holidays.add(
                Holiday(
                    id = "holi_$year",
                    name = "Holi",
                    date = holi.toInstant(timeZone).toEpochMilliseconds(),
                    countryCode = "IN"
                )
            )

            // Ramadan Start (tentative)
            val ramadan = LocalDateTime(year, Month.MARCH, 2, 0, 0) // Example date for 2025
            holidays.add(
                Holiday(
                    id = "ramadan_$year",
                    name = "Ramadan Start (tentative)",
                    date = ramadan.toInstant(timeZone).toEpochMilliseconds(),
                    countryCode = "IN"
                )
            )
        }

        return holidays
    }
}