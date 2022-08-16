package com.example.multithreading

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class ShowNotificationWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        repeat(15) {
            val oneNotificationWorker = OneTimeWorkRequestBuilder<OneNotificationWorker>()
                    .setInitialDelay( it.toLong(), TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueue(oneNotificationWorker)
        }
        Log.d("Worker", "doWork: 15 workers")
        return Result.success()
    }

}

class OneNotificationWorker(private val context: Context, params: WorkerParameters) :
    Worker(context, params) {
    override fun doWork(): Result {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "Notificaation!!!", Toast.LENGTH_SHORT).show()

        }
        Log.d("Worker", "doWork: After Toast")
        return Result.success()
    }

}