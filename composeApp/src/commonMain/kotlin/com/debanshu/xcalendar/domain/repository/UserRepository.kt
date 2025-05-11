package com.debanshu.xcalendar.domain.repository

import com.debanshu.xcalendar.common.model.asUserEntity
import com.debanshu.xcalendar.common.model.asUser
import com.debanshu.xcalendar.data.localDataSource.UserDao
import com.debanshu.xcalendar.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class UserRepository(private val userDao: UserDao) {
    fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { entities -> entities.map { it.asUser() } }

    suspend fun addUser(user: User) {
        userDao.insertUser(user.asUserEntity())
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.asUserEntity())
    }
}
