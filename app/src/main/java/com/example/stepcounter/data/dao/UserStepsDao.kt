package com.example.stepcounter.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.stepcounter.data.entity.UserStepsEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface UserStepsDao {
    @Query("SELECT * FROM user_steps WHERE username = :username AND date = :date")
    suspend fun getUserStepsForDate(username: String, date: String): UserStepsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserStepsEntity)

    @Query("SELECT * FROM user_steps WHERE username = :username ORDER BY date DESC")
    fun getAllStepsForUser(username: String): Flow<List<UserStepsEntity>>

    @Query("SELECT * FROM user_steps WHERE username = :username AND date = :date")
    fun getTodayStepsFlow(username: String,date: String): Flow<UserStepsEntity?>}