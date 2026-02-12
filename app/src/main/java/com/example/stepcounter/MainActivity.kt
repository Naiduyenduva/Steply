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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.stepcounter.ui.HealthDashboardScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.lifecycleScope // Required for lifecycleScope.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.room.Room
import com.example.stepcounter.data.db.StepsDatabase
import com.example.stepcounter.data.dao.UserStepsDao
import com.example.stepcounter.data.entity.UserStepsEntity
import kotlinx.coroutines.withContext
import com.example.stepcounter.viewmodel.StepsViewModel
import java.time.LocalDate


class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private var initialSteps = -1f
    private var stepCount by mutableStateOf(0)

    private lateinit var userStepsDao: UserStepsDao
    private val username = "naidu" // for now (we’ll improve later)
    private var databaseStepsAtStart = 0

    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (initialSteps < 0) {
                initialSteps = event.values[0]
            }

            // Calculate steps taken SINCE the app opened
            val sessionSteps = (event.values[0] - initialSteps).toInt()
            
            // UI = (Database Value) + (New steps taken right now)
            stepCount = databaseStepsAtStart + sessionSteps
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentDate = LocalDate.now().toString()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            Log.d("STEP", "Step Counter sensor NOT available")
        }

        requestActivityPermission()

        val database = Room.databaseBuilder(
            applicationContext,
            StepsDatabase::class.java,
            "steps_db"
        ).build()

        userStepsDao = database.userStepsDao()

        // 1. Initialize the ViewModel (Note: In a real app, use a ViewModelFactory)
        val viewModel = StepsViewModel(userStepsDao)

        // 2. Fetch the user data
        viewModel.loadUser(username)
        viewModel.loadHistory(username)

        // 3. Log the value to see if Room actually has data
        // We use a Coroutine because the DB fetch is 'suspend' (async)
        lifecycleScope.launch {
            val todayData = userStepsDao.getUserStepsForDate(username, currentDate)
            val history = userStepsDao.getAllStepsForUser(username)
            Log.d ("STEP_DB", "Success full: ${history}")
            if (todayData != null) {
                Log.d("STEP_DB", "Successfully fetched: ${todayData.stepCount}")
                
                databaseStepsAtStart = todayData?.stepCount ?:0
                stepCount = databaseStepsAtStart
                
            } else {
                Log.d("STEP_DB", "No data found in Room for user: $username")
            }
        }

        setContent {
            MaterialTheme {
                val historyList by viewModel.history
                HealthDashboardScreen(steps = stepCount, history = historyList)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerSensorIfPermitted()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(stepListener)
    }

    override fun onStop() {
        super.onStop()
        val currentDate = LocalDate.now().toString()

        CoroutineScope(Dispatchers.IO).launch {
            userStepsDao.insertOrUpdate(
                UserStepsEntity(
                    username = username,
                    stepCount = stepCount,
                    date = currentDate
                )
            )
        }
    }

    private fun registerSensorIfPermitted() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            stepSensor?.let {
                sensorManager.registerListener(
                    stepListener,
                    it,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }
    }

    private fun requestActivityPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityPermissionLauncher.launch(
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }

    private val activityPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                registerSensorIfPermitted()
            } else {
                Log.d("STEP", "ACTIVITY_RECOGNITION permission denied")
            }
        }
}
