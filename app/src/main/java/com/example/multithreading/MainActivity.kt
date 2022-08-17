package com.example.multithreading

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.multithreading.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


private const val CHANNEL_NAME = "channel_name"
const val CHANNEL_ID = "channel_id"
private const val CHANNEL_DESCRIPTION = "This channel sends the same notification every minute"
const val SHARED_PREFERENCE_NAME = "boolean_shared_preference"
const val IS_SHOW_NOTIFICATION_KEY = "isShowNotification"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var broadcastReceiver: BootCompletedReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()

        val sharedPreference = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        binding.startButton.setOnClickListener {
            start()
            editor.apply {
                putBoolean(IS_SHOW_NOTIFICATION_KEY, true)
                apply()
            }
        }
        binding.stopButton.setOnClickListener {
            WorkManager.getInstance(applicationContext).cancelAllWork()
            Toast.makeText(this, "Showing notification cancelled", Toast.LENGTH_SHORT).show()
            editor.apply {
                putBoolean(IS_SHOW_NOTIFICATION_KEY, false)
                apply()
            }
        }
    }

    private fun start() {
        val workRequest = PeriodicWorkRequestBuilder<ShowNotificationWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = CHANNEL_DESCRIPTION
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStart() {
        super.onStart()
        if (broadcastReceiver == null) broadcastReceiver = BootCompletedReceiver { binding.startButton.performClick() }
        registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_BOOT_COMPLETED))
    }

    override fun onStop() {
        unregisterReceiver(broadcastReceiver)
        super.onStop()
    }
}