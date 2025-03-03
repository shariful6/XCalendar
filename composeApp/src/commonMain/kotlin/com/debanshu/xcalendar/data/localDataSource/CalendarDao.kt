package com.debanshu.xcalendar.data.localDataSource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.debanshu.xcalendar.data.model.CalendarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM calendars WHERE userId = :userId")
    fun getCalendarsByUserId(userId: String): Flow<List<CalendarEntity>>

    @Upsert
    suspend fun upsertCalendar(calendar: CalendarEntity)

    @Delete
    suspend fun deleteCalendar(calendar: CalendarEntity)
}