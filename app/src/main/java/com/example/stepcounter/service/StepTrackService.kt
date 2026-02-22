package com.example.stepcounter.service

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.app.Service
import android.content.Context
import android.app.Notification
import android.os.Build
import android.os.IBinder
import com.example.stepcounter.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.room.Room
import com.example.stepcounter.data.db.StepsDatabase
import com.example.stepcounter.data.dao.UserStepsDao
import com.example.stepcounter.data.entity.UserStepsEntity
import java.time.LocalDate
import android.content.Intent
import android.content.pm.ServiceInfo
import android.content.SharedPreferences
import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class StepTrackService: Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private var initialSteps = -1f
    private lateinit var prefs: SharedPreferences

    private lateinit var userStepsDao: UserStepsDao
    private val username = "naidu"

    companion object {
        private const val PREFS_NAME = "step_prefs"
        private const val KEY_INITIAL_STEPS = "initial_steps"
        private const val KEY_SAVED_DATE = "saved_date"
    }

     override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("STEP_SERVICE", "Service created")
        createNotificationChannel()

        val notification = builfdNotification()
        Log.d("STEPCOUNTR", "Starting foreground service with notification")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH)
        } else {
            startForeground(1, notification)
        }
        Log.d("STEPCOUNTR", "Foreground service started")

         // ✅ SharedPreferences init — restores initialSteps across service restarts
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDate = prefs.getString(KEY_SAVED_DATE, null)
        val today = LocalDate.now().toString()

        // Reset initialSteps at the start of a new day
        if (savedDate == today) {
            initialSteps = prefs.getFloat(KEY_INITIAL_STEPS, -1f)
        } else {
            prefs.edit()
                .remove(KEY_INITIAL_STEPS)
                .putString(KEY_SAVED_DATE, today)
                .apply()
            initialSteps = -1f
        }

        // ✅ DB init
        val database = Room.databaseBuilder(
            applicationContext,
            StepsDatabase::class.java,
            "steps_db"
        ).build()

        userStepsDao = database.userStepsDao()

        // ✅ Sensor init
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

       override fun onSensorChanged(event: SensorEvent) {
        Log.d("STEP_SENSOR", "Raw sensor: ${event.values[0]}")

        val currentDate = LocalDate.now().toString()

        if (initialSteps < 0) {
            initialSteps = event.values[0]
            // Persist so service restarts don't reset the baseline
            prefs.edit()
                .putFloat(KEY_INITIAL_STEPS, initialSteps)
                .putString(KEY_SAVED_DATE, LocalDate.now().toString())
                .apply()
            return
        }

        val todaySteps = (event.values[0] - initialSteps).toInt()

        CoroutineScope(Dispatchers.IO).launch {
            Log.d("STEP_DB", "Saving steps: $todaySteps")
            userStepsDao.insertOrUpdate(
                UserStepsEntity(
                    username = username,
                    stepCount = todaySteps,
                    date = currentDate
                )
            )
        }
    }

     override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

     override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }

    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Foreground Service"
            val CHANNEL_ID = "STEP_TRACKING_CHANNEL_ID"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, channelName, importance)
            channel.description = "Background steps tracking"     
            val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    } 

    fun builfdNotification(): Notification {
        val CHANNEL_ID = "STEP_TRACKING_CHANNEL_ID"
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Step Counter")
            .setContentText("Tracking your steps in the background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return notificationBuilder.build()
    }


}