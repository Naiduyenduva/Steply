package com.example.stepcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.stepcounter.ui.theme.StepCounterTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var initialSteps = -1f
    private var stepCount by mutableStateOf(0)


    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (initialSteps < 0) {
                initialSteps = event.values[0]
            }
            val currentSteps = (event.values[0] - initialSteps).toInt()
            stepCount = currentSteps
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepSensor == null) {
            Log.d("STEP", "Step Counter sensor NOT available")
        } else {
            Log.d("STEP", "Step Counter sensor AVAILABLE")
        }
        requestActivityPermission()

        setContent {
            StepCounterTheme {
                StepCounterScreen(steps = stepCount)
            }
        }
    }
    override fun onResume() {
        super.onResume()
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

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(stepListener)
    }
    private fun requestActivityPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted
            stepSensor?.let {
                sensorManager.registerListener(
                    stepListener,
                    it,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        } else {
            // Ask permission
            activityPermissionLauncher.launch(
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }
    }

    private val activityPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                stepSensor?.let {
                    sensorManager.registerListener(
                        stepListener,
                        it,
                        SensorManager.SENSOR_DELAY_UI
                    )
                }
            } else {
                Log.d("STEP", "ACTIVITY_RECOGNITION permission denied")
            }
        }

}

@Composable
fun StepCounterScreen(steps: Int) {
    Text(text = "Steps: $steps")
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StepCounterTheme {
        StepCounterScreen(steps=0)
    }
}