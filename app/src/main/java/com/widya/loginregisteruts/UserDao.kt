package com.widya.loginregisteruts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getUserByEmailAndPassword(email: String, password: String): User?

    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): kotlinx.coroutines.flow.Flow<List<User>>

    @Query("SELECT * FROM user_table WHERE id = :id")
    fun getUser(id: Int): kotlinx.coroutines.flow.Flow<User>

    @androidx.room.Delete
    suspend fun delete(user: User)

}