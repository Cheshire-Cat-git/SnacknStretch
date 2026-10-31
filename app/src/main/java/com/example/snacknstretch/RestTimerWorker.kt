package com.example.snacknstretch

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

class RestTimerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val intent = Intent(applicationContext, RestTimerService::class.java)
        applicationContext.startService(intent)
        return Result.success()
    }
}
