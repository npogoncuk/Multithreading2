package com.example.multithreading

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.multithreading.databinding.ActivityMainBinding

class BootCompletedReceiver(private val callback: (() -> Unit)) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                    .getBoolean(IS_SHOW_NOTIFICATION_KEY, false)
            )
                callback.invoke()
        }
    }
}