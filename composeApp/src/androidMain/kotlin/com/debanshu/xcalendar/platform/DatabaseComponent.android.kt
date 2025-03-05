package com.debanshu.xcalendar.platform

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.debanshu.xcalendar.data.localDataSource.AppDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single

@Single
actual class DatabaseComponent(val context: Context) {
    actual fun getDatabase(): AppDatabase = Room.databaseBuilder<AppDatabase>(
        context,
        context.getDatabasePath("calendar.db").absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}