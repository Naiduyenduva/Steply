package com.example.stepcounter.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.stepcounter.data.dao.UserStepsDao
import com.example.stepcounter.data.entity.UserStepsEntity

@Database(
    entities = [UserStepsEntity::class],
    version = 1
)
abstract class StepsDatabase : RoomDatabase() {
    abstract fun userStepsDao(): UserStepsDao
}
