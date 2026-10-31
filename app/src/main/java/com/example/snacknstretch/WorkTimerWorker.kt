package com.example.snacknstretch

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

class WorkTimerWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val intent = Intent(applicationContext, WorkTimerService::class.java)
        applicationContext.startService(intent)
        return Result.success()
    }
}
