package com.example.stepcounter.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stepcounter.data.dao.UserStepsDao
import com.example.stepcounter.data.entity.UserStepsEntity
import kotlinx.coroutines.launch
import java.time.LocalDate

class StepsViewModel(
    private val dao: UserStepsDao
) : ViewModel() {

    val steps = mutableStateOf(0)
    val history = mutableStateOf<List<UserStepsEntity>>(emptyList())

    fun loadUser(username: String) {
        val currentDate = LocalDate.now().toString()
        viewModelScope.launch {
            val user = dao.getUserStepsForDate(username, currentDate)
            steps.value = user?.stepCount ?: 0
        }
    }

    fun updateSteps(username: String, newSteps: Int) {
        val currentDate = LocalDate.now().toString() // Get today's date
        steps.value = newSteps
        viewModelScope.launch {
            dao.insertOrUpdate(
                UserStepsEntity(
                    username,
                    date = currentDate,
                    newSteps
                )
            )
        }
    }

    fun loadHistory(username: String) {
        viewModelScope.launch {
            val allSteps = dao.getAllStepsForUser(username)
            history.value = allSteps
        }
    }
}
