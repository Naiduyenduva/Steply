package com.example.stepcounter.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
        val today = LocalDate.now().toString()

        viewModelScope.launch {
            dao.getTodayStepsFlow(username, today).collect {
            steps.value = it?.stepCount ?: 0
            }
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
            dao.getAllStepsForUser(username).collect {
                history.value = it
            }
        }
    }

    class Factory(private val dao: UserStepsDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return StepsViewModel(dao) as T
        }
    }
}
