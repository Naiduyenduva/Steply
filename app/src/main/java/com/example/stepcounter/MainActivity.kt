package com.example.stepcounter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.stepcounter.data.dao.UserStepsDao
import com.example.stepcounter.data.db.StepsDatabase
import com.example.stepcounter.service.StepTrackService
import com.example.stepcounter.ui.MainScreen
import com.example.stepcounter.ui.theme.StepCounterTheme
import com.example.stepcounter.viewmodel.StepsViewModel

class MainActivity : ComponentActivity() {

    private lateinit var userStepsDao: UserStepsDao
    private val username = "naidu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d("STEPCOUNTR", "MainActivity onCreate called")

        val database = Room.databaseBuilder(
            applicationContext,
            StepsDatabase::class.java,
            "steps_db"
        ).build()

        userStepsDao = database.userStepsDao()

        checkPermissionAndStartService()

        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDark by remember { mutableStateOf(systemDark) }

            val vm: StepsViewModel = viewModel(factory = StepsViewModel.Factory(userStepsDao))

            LaunchedEffect(Unit) {
                vm.loadUser(username)
                vm.loadHistory(username)
            }

            val todaySteps by vm.steps
            val historyList by vm.history

            StepCounterTheme(darkTheme = isDark) {
                MainScreen(
                    steps = todaySteps,
                    history = historyList,
                    isDark = isDark,
                    onThemeToggle = { isDark = !isDark }
                )
            }
        }
    }

    private fun checkPermissionAndStartService() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startStepService()
        } else {
            activityPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    private fun startStepService() {
        val intent = Intent(this, StepTrackService::class.java)
        startForegroundService(intent)
    }

    private val activityPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startStepService()
            } else {
                Log.d("STEPCOUNT", "ACTIVITY_RECOGNITION permission denied")
            }
        }
}
