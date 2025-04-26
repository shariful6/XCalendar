package com.debanshu.xcalendar.data.localDataSource

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.debanshu.xcalendar.data.localDataSource.model.CalendarEntity
import com.debanshu.xcalendar.data.localDataSource.model.EventEntity
import com.debanshu.xcalendar.data.localDataSource.model.EventReminderEntity
import com.debanshu.xcalendar.data.localDataSource.model.HolidayEntity
import com.debanshu.xcalendar.data.localDataSource.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CalendarEntity::class,
        EventEntity::class,
        EventReminderEntity::class,
        HolidayEntity::class],
    version = 1
)
@ConstructedBy(LocalDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserEntityDao(): UserDao
    abstract fun getCalendarEntityDao(): CalendarDao
    abstract fun getEventEntityDao(): EventDao
    abstract fun getHolidayEntityDao(): HolidayDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LocalDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}