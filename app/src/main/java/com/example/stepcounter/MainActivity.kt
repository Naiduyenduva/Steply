package com.example.stepcounter

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.stepcounter.ui.HealthDashboardScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.room.Room
import com.example.stepcounter.data.db.StepsDatabase
import com.example.stepcounter.data.dao.UserStepsDao
import com.example.stepcounter.data.entity.UserStepsEntity
import kotlinx.coroutines.withContext
import com.example.stepcounter.viewmodel.StepsViewModel
import java.time.LocalDate
import android.content.Intent
import com.example.stepcounter.service.StepTrackService



class MainActivity : ComponentActivity() {

    private lateinit var userStepsDao: UserStepsDao
    private val username = "naidu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("STEPCOUNTR", "MainActivity onCreate called")

         // ✅ DB init

        val database = Room.databaseBuilder(
            applicationContext,
            StepsDatabase::class.java,
            "steps_db"
        ).build()
        Log.d("STEPCOUNTR", "Database initialized")

        userStepsDao = database.userStepsDao()

        checkPermissionAndStartService()

        setContent {
            val viewModel: StepsViewModel = viewModel(
                factory = StepsViewModel.Factory(userStepsDao)
            )

            LaunchedEffect(Unit) {
                viewModel.loadUser(username)
                viewModel.loadHistory(username)
            }

            MaterialTheme {
                val historyList by viewModel.history
                val todaySteps by viewModel.steps
                HealthDashboardScreen(
                    steps = todaySteps,
                    history = historyList
                )
            }
        }
    }

    // 🔥 Permission gate
    private fun checkPermissionAndStartService() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startStepService()
            Log.d("STEPCOUNTR", "ACTIVITY_RECOGNITION permission already granted, starting service")
        } else {
            activityPermissionLauncher.launch(
                Manifest.permission.ACTIVITY_RECOGNITION
            )
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

