package com.debanshu.xcalendar.data.localDataSource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.debanshu.xcalendar.data.localDataSource.model.EventEntity
import com.debanshu.xcalendar.data.localDataSource.model.EventReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE calendarId IN (:calendarIds) AND startTime >= :startTime AND endTime <= :endTime")
    fun getEventsBetweenDates(calendarIds: List<String>, startTime: Long, endTime: Long): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): EventEntity?

    @Upsert
    suspend fun upsertEvent(event: EventEntity): Long

    @Delete
    suspend fun deleteEvent(event: EventEntity)

    @Transaction
    suspend fun insertEventWithReminders(event: EventEntity, reminders: List<EventReminderEntity>) {
        upsertEvent(event)
        reminders.forEach { reminder ->
            insertEventReminder(reminder)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventReminder(reminder: EventReminderEntity)

    @Query("DELETE FROM event_reminders WHERE eventId = :eventId")
    suspend fun deleteEventReminders(eventId: String)
}