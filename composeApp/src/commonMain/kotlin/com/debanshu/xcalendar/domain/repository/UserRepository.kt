package com.debanshu.xcalendar.domain.repository

import com.debanshu.xcalendar.data.localDataSource.UserDao
import com.debanshu.xcalendar.data.model.UserEntity
import com.debanshu.xcalendar.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class UserRepository(private val userDao: UserDao) {
    fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { entities -> entities.map { it.toUser() } }

    suspend fun addUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }

    private fun UserEntity.toUser(): User =
        User(id, name, email, photoUrl)

    private fun User.toEntity(): UserEntity =
        UserEntity(id, name, email, photoUrl)
}