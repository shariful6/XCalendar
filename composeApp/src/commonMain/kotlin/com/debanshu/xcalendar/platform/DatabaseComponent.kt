package com.debanshu.xcalendar.platform

import com.debanshu.xcalendar.data.localDataSource.AppDatabase

expect class DatabaseComponent {
    fun getDatabase() : AppDatabase
}