package com.example.stepcounter.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_steps", primaryKeys=["username", "date"])
data class UserStepsEntity(
    val username: String,
    val date:     String, //Format YYYY-DD-MM
    val stepCount: Int
)