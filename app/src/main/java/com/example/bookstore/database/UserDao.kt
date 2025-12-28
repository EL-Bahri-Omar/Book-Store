package com.example.bookstore.database

import androidx.room.*
import com.example.bookstore.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Update
    suspend fun update(user: User)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun delete(userId: Int)
}