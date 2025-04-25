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

    // Calendar types with associated names and typical events
    private val calendarTypes = mapOf(
        "work" to "Work",
        "personal" to "Personal",
        "family" to "Family",
        "fitness" to "Fitness",
        "travel" to "Travel",
        "study" to "Study",
        "finance" to "Finance",
        "health" to "Health"
    )

    // Event titles organized by calendar type
    private val eventTitles = mapOf(
        "work" to listOf(
            "Team Meeting", "Sprint Planning", "Product Demo", "Client Call",
            "Design Review", "Performance Review", "1:1 with Manager", "Department Lunch",
            "Project Kickoff", "Code Review", "Release Planning", "Interview",
            "Training Session", "Conference Call", "Board Meeting"
        ),
        "personal" to listOf(
            "Dinner with Friends", "Movie Night", "Shopping", "Home Maintenance",
            "Call Parents", "Haircut", "Pay Bills", "Read Book", "Writing Session",
            "Gardening", "Cooking Class", "Visit Friends", "Clean Apartment"
        ),
        "family" to listOf(
            "Family Dinner", "School Meeting", "Soccer Practice", "Dance Recital",
            "Parent Teacher Conference", "Family Movie Night", "Weekend Trip",
            "Birthday Party", "Anniversary Celebration"
        ),
        "fitness" to listOf(
            "Morning Run", "Gym Workout", "Yoga Class", "Swimming", "Cycling",
            "Hiking Trip", "Sports Game", "Meditation", "Personal Trainer Session"
        ),
        "travel" to listOf(
            "Flight to Paris", "Hotel Check-in", "Sightseeing Tour", "Car Rental",
            "Museum Visit", "Restaurant Reservation", "Beach Day", "Hiking Trip"
        ),
        "study" to listOf(
            "Study Session", "Exam Prep", "Group Project", "Research", "Writing",
            "Assignment Due", "Online Course", "Reading Session", "Lab Work"
        ),
        "finance" to listOf(
            "Budget Review", "Tax Preparation", "Investment Check", "Mortgage Payment",
            "Financial Advisor Meeting", "Expense Report", "Account Reconciliation"
        ),
        "health" to listOf(
            "Doctor Appointment", "Dentist Appointment", "Therapy Session",
            "Health Checkup", "Medication Refill", "Eye Exam", "Specialist Consultation"
        )
    )

    // Locations organized by calendar type
    private val eventLocations = mapOf(
        "work" to listOf(
            "Conference Room A", "Meeting Room 3", "Office Building", "Head Office",
            "Client's Office", "Co-working Space", "Zoom Call", "Microsoft Teams",
            "Groww Vaishnavi Tech Park", "WeWork Downtown", "3rd Floor - Candle Room"
        ),
        "personal" to listOf(
            "Home", "Town Center", "Local Mall", "Downtown", "Friend's Place",
            "Coffee Shop", "Library", "Park", "Community Center"
        ),
        "family" to listOf(
            "Home", "School", "Community Center", "Park", "Restaurant",
            "Grandparents' House", "Movie Theater", "Zoo", "Museum"
        ),
        "fitness" to listOf(
            "Fitness Center", "City Gym", "Yoga Studio", "Swimming Pool",
            "Running Track", "Sports Complex", "Mountain Trail", "Beach"
        ),
        "travel" to listOf(
            "Airport", "Train Station", "Hotel Lobby", "City Center",
            "Tourist Information", "Resort", "Beach", "National Park"
        ),
        "study" to listOf(
            "Library", "Study Room", "University Campus", "Coffee Shop",
            "Home Office", "Classroom", "Conference Center", "Learning Lab"
        ),
        "finance" to listOf(
            "Bank", "Financial Advisor's Office", "Home Office", "Tax Office",
            "Investment Firm", "Accounting Department"
        ),
        "health" to listOf(
            "Doctor's Office", "Clinic", "Hospital", "Dental Clinic",
            "Therapy Center", "Medical Center", "Pharmacy", "Health Center"
        )
    )

    // Common reminder times in minutes
    private val reminderTimes = listOf(0, 5, 10, 15, 30, 60, 120, 1440)

    override suspend fun fetchCalendarsForUser(userId: String): List<Calendar> {
        // Create a deterministic random generator based on userId
        val random = Random(userId.hashCode())

        // Standard colors
        val colors = listOf(
            0xFF4285F4, // Blue
            0xFFDB4437, // Red
            0xFF0F9D58, // Green
            0xFFF4B400, // Yellow
            0xFF8560A8, // Purple
            0xFF03BCD4, // Cyan
            0xFFFF6D00, // Orange
            0xFF9C27B0, // Deep Purple
            0xFF795548, // Brown
            0xFF607D8B  // Blue Grey
        )

        // Base calendars (keeping your original ones for compatibility)
        val calendars = mutableListOf(
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

        // Add 1-3 additional random calendars
        val additionalCalendarTypes = calendarTypes.keys.toList() - listOf("work", "family")
        val numAdditional = random.nextInt(1, 4)
        val shuffledTypes = additionalCalendarTypes.shuffled(random)

        for (i in 0 until numAdditional) {
            if (i < shuffledTypes.size) {
                val calType = shuffledTypes[i]
                val colorIndex = random.nextInt(3, colors.size)

                calendars.add(
                    Calendar(
                        id = "${userId}_$calType",
                        name = calendarTypes[calType] ?: calType,
                        color = colors[colorIndex].toInt(),
                        userId = userId
                    )
                )
            }
        }

        return calendars
    }

    override suspend fun fetchEventsForCalendar(calendarId: String, startTime: Long, endTime: Long): List<Event> {
        val events = mutableListOf<Event>()
        val random = Random(calendarId.hashCode() + startTime)

        // Extract calendar type from ID (after userId_)
        val calendarType = calendarId.substringAfter('_', "personal")

        // Convert to Instant and LocalDateTime
        val startInstant = Instant.fromEpochMilliseconds(startTime)
        val endInstant = Instant.fromEpochMilliseconds(endTime)
        val timeZone = TimeZone.currentSystemDefault()

        val currentDate = startInstant.toLocalDateTime(timeZone).date
        val endDate = endInstant.toLocalDateTime(timeZone).date

        // Keep existing work calendar events for compatibility
        if (calendarId.contains("work")) {
            // Keep all the existing work events implementation
            events.addAll(generateWorkEvents(calendarId, startTime, endTime))
        }

        // Random events for the calendar based on its type
        val daysInRange = (endDate.toEpochDays() - currentDate.toEpochDays()).toInt() + 1
        val eventDensity = when (calendarType) {
            "work" -> 0.7    // Higher density for work calendars
            "family" -> 0.4  // Medium density for family calendars
            "finance" -> 0.2 // Lower density for finance calendars
            else -> 0.3      // Default density
        }

        // Calculate number of events based on date range and density
        val numberOfEvents = (daysInRange * eventDensity).toInt().coerceAtLeast(1)

        // Get available titles and locations for this calendar type
        val titles = eventTitles[calendarType] ?: eventTitles["personal"]!!
        val locations = eventLocations[calendarType] ?: eventLocations["personal"]!!

        // Generate random events
        repeat(numberOfEvents) {
            // Pick a random date within the range
            val dayOffset = random.nextInt(daysInRange)
            val eventDate = currentDate.plus(DatePeriod(days = dayOffset))

            // Skip weekends for work calendar (already handled by generateWorkEvents)
            if (calendarType == "work" && eventDate.dayOfWeek.ordinal > 5) {
                return@repeat
            }

            // Random starting time (8 AM to 7 PM)
            val hour = random.nextInt(8, 20)
            val minute = (random.nextInt(4) * 15) // 0, 15, 30, or 45

            // Duration between 30 mins and 2 hours
            val durationMinutes = random.nextInt(1, 5) * 30

            val eventStartDateTime = LocalDateTime(
                eventDate.year,
                eventDate.month,
                eventDate.dayOfMonth,
                hour,
                minute,
                0,
                0
            )

            val eventStartTimeMillis = eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
            val eventEndTimeMillis = eventStartTimeMillis + durationMinutes.minutes.inWholeMilliseconds

            // Random title and potentially location
            val title = titles[random.nextInt(titles.size)]
            val location = if (random.nextInt(10) < 7) {
                locations[random.nextInt(locations.size)]
            } else null

            // Decide if it's an all-day event (5-10% chance depending on calendar)
            val isAllDay = random.nextInt(100) < when(calendarType) {
                "travel" -> 30  // Travel events often all-day
                "finance" -> 5  // Finance events rarely all-day
                else -> 10      // Default probability
            }

            // Generate random reminders
            val reminderCount = random.nextInt(3)
            val remindersList = if (reminderCount > 0) {
                List(reminderCount) { reminderTimes[random.nextInt(reminderTimes.size)] }.distinct()
            } else emptyList()

            // Generate description (50% chance)
            val description = if (random.nextInt(2) == 0) {
                generateDescription(title, calendarType, random)
            } else null

            // Create the event with a unique ID
            val event = Event(
                id = "event_${calendarId}_${eventStartTimeMillis}_${random.nextInt(1000)}",
                calendarId = calendarId,
                title = title,
                description = description,
                location = location,
                startTime = if (isAllDay) {
                    LocalDateTime(eventDate.year, eventDate.month, eventDate.dayOfMonth, 0, 0)
                        .toInstant(timeZone).toEpochMilliseconds()
                } else {
                    eventStartTimeMillis
                },
                endTime = if (isAllDay) {
                    LocalDateTime(eventDate.year, eventDate.month, eventDate.dayOfMonth, 23, 59)
                        .toInstant(timeZone).toEpochMilliseconds()
                } else {
                    eventEndTimeMillis
                },
                isAllDay = isAllDay,
                isRecurring = random.nextInt(20) == 0, // 5% chance of recurring
                recurringRule = null,
                reminderMinutes = remindersList,
                color = null // Use calendar color
            )

            events.add(event)
        }

        return events
    }

    // Keep your original work events generation logic
    private fun generateWorkEvents(calendarId: String, startTime: Long, endTime: Long): List<Event> {
        val events = mutableListOf<Event>()
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

        return events
    }

    private fun generateDescription(title: String, calendarType: String, random: Random): String {
        // Specific descriptions for common events
        val specialDescriptions = mapOf(
            "Team Meeting" to listOf(
                "Weekly sync to discuss project status and blockers.",
                "Quarterly planning session for team objectives.",
                "Review of team metrics and performance indicators."
            ),
            "Sprint Planning" to listOf(
                "Plan tasks for the upcoming two-week sprint.",
                "Review backlog items and estimate story points.",
                "Prioritize features and plan delivery timeline."
            ),
            "Doctor Appointment" to listOf(
                "Regular checkup with Dr. Johnson.",
                "Annual physical examination.",
                "Follow-up appointment for test results."
            ),
            "Gym Workout" to listOf(
                "Focus on upper body strength training.",
                "Cardio session - 30 min run, 15 min HIIT.",
                "Full body workout with trainer."
            )
        )

        // If we have specific descriptions for this title, use one
        specialDescriptions[title]?.let {
            return it[random.nextInt(it.size)]
        }

        // Generic descriptions based on calendar type
        val genericDescriptions = when (calendarType) {
            "work" -> listOf(
                "Don't forget to prepare the agenda beforehand.",
                "Bring your laptop and project notes.",
                "Meeting to discuss ${listOf("quarterly goals", "project timelines", "team performance", "upcoming deadlines").random(random)}.",
                "Virtual meeting - check email for link."
            )
            "fitness" -> listOf(
                "Remember to bring water and a towel.",
                "Wear appropriate workout clothes.",
                "Duration: ${listOf("30 minutes", "45 minutes", "1 hour", "1.5 hours").random(random)}."
            )
            "travel" -> listOf(
                "Check-in opens 24 hours before departure.",
                "Confirmation #: ${random.nextInt(100000, 1000000)}",
                "Bring passport and booking confirmation.",
                "Pack light, weather forecast: ${listOf("sunny", "rainy", "cloudy", "warm", "cold").random(random)}."
            )
            "health" -> listOf(
                "Bring insurance card and ID.",
                "Fasting required beforehand.",
                "Remember to ask about ${listOf("test results", "prescription refill", "referral", "symptoms").random(random)}."
            )
            else -> listOf(
                "Don't forget to set a reminder.",
                "Important event - mark as priority.",
                "Check details before attending.",
                "Add notes for preparation."
            )
        }

        return genericDescriptions[random.nextInt(genericDescriptions.size)]
    }

    override suspend fun fetchHolidays(countryCode: String, year: Int): List<Holiday> {
        // Keep your existing holiday implementation
        val holidays = mutableListOf<Holiday>()
        val timeZone = TimeZone.currentSystemDefault()

        // Add country-specific holidays
        when (countryCode) {
            "IN" -> {
                // India holidays
                holidays.add(createHoliday("republic_day_$year", "Republic Day", year, Month.JANUARY, 26, "IN", timeZone))
                holidays.add(createHoliday("shivaratri_$year", "Maha Shivaratri/Shivaratri", year, Month.FEBRUARY, 26, "IN", timeZone))
                holidays.add(createHoliday("holi_$year", "Holi", year, Month.MARCH, 9, "IN", timeZone))
                holidays.add(createHoliday("ramadan_$year", "Ramadan Start (tentative)", year, Month.MARCH, 2, "IN", timeZone))
                // Additional Indian holidays
                holidays.add(createHoliday("independence_day_$year", "Independence Day", year, Month.AUGUST, 15, "IN", timeZone))
                holidays.add(createHoliday("gandhi_jayanti_$year", "Gandhi Jayanti", year, Month.OCTOBER, 2, "IN", timeZone))
                holidays.add(createHoliday("diwali_$year", "Diwali", year, Month.NOVEMBER, 4, "IN", timeZone))
            }
            "US" -> {
                // United States holidays
                holidays.add(createHoliday("new_year_$year", "New Year's Day", year, Month.JANUARY, 1, "US", timeZone))
                holidays.add(createHoliday("mlk_day_$year", "Martin Luther King Jr. Day", year, Month.JANUARY, 17, "US", timeZone))
                holidays.add(createHoliday("presidents_day_$year", "Presidents' Day", year, Month.FEBRUARY, 21, "US", timeZone))
                holidays.add(createHoliday("memorial_day_$year", "Memorial Day", year, Month.MAY, 30, "US", timeZone))
                holidays.add(createHoliday("independence_day_$year", "Independence Day", year, Month.JULY, 4, "US", timeZone))
                holidays.add(createHoliday("labor_day_$year", "Labor Day", year, Month.SEPTEMBER, 5, "US", timeZone))
                holidays.add(createHoliday("thanksgiving_$year", "Thanksgiving", year, Month.NOVEMBER, 24, "US", timeZone))
                holidays.add(createHoliday("christmas_$year", "Christmas", year, Month.DECEMBER, 25, "US", timeZone))
            }
            "UK" -> {
                // United Kingdom holidays
                holidays.add(createHoliday("new_year_$year", "New Year's Day", year, Month.JANUARY, 1, "UK", timeZone))
                holidays.add(createHoliday("good_friday_$year", "Good Friday", year, Month.APRIL, 7, "UK", timeZone))
                holidays.add(createHoliday("easter_monday_$year", "Easter Monday", year, Month.APRIL, 10, "UK", timeZone))
                holidays.add(createHoliday("early_may_$year", "Early May Bank Holiday", year, Month.MAY, 1, "UK", timeZone))
                holidays.add(createHoliday("spring_bank_$year", "Spring Bank Holiday", year, Month.MAY, 29, "UK", timeZone))
                holidays.add(createHoliday("summer_bank_$year", "Summer Bank Holiday", year, Month.AUGUST, 28, "UK", timeZone))
                holidays.add(createHoliday("christmas_$year", "Christmas Day", year, Month.DECEMBER, 25, "UK", timeZone))
                holidays.add(createHoliday("boxing_day_$year", "Boxing Day", year, Month.DECEMBER, 26, "UK", timeZone))
            }
            "AU" -> {
                // Australia holidays
                holidays.add(createHoliday("new_year_$year", "New Year's Day", year, Month.JANUARY, 1, "AU", timeZone))
                holidays.add(createHoliday("australia_day_$year", "Australia Day", year, Month.JANUARY, 26, "AU", timeZone))
                holidays.add(createHoliday("good_friday_$year", "Good Friday", year, Month.APRIL, 7, "AU", timeZone))
                holidays.add(createHoliday("easter_monday_$year", "Easter Monday", year, Month.APRIL, 10, "AU", timeZone))
                holidays.add(createHoliday("anzac_day_$year", "Anzac Day", year, Month.APRIL, 25, "AU", timeZone))
                holidays.add(createHoliday("christmas_$year", "Christmas Day", year, Month.DECEMBER, 25, "AU", timeZone))
                holidays.add(createHoliday("boxing_day_$year", "Boxing Day", year, Month.DECEMBER, 26, "AU", timeZone))
            }
        }

        return holidays
    }

    private fun createHoliday(
        id: String,
        name: String,
        year: Int,
        month: Month,
        day: Int,
        countryCode: String,
        timeZone: TimeZone
    ): Holiday {
        val date = LocalDateTime(year, month, day, 0, 0)
        return Holiday(
            id = id,
            name = name,
            date = date.toInstant(timeZone).toEpochMilliseconds(),
            countryCode = countryCode
        )
    }
}