package com.debanshu.xcalendar.platform

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.debanshu.xcalendar.data.localDataSource.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@Single
actual class DatabaseComponent {
    @Single
    actual fun getDatabase(): AppDatabase {
        val dbFile = documentDirectory() + "/assetx.db"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile,
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.Default)
            .build()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}