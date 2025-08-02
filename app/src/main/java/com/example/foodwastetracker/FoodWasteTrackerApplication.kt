package com.example.foodwastetracker

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.example.foodwastetracker.workers.ExpirationNotificationWorker
import java.util.concurrent.TimeUnit

class FoodWasteTrackerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Schedule periodic expiration checks
        scheduleExpirationNotifications()
    }

    private fun scheduleExpirationNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<ExpirationNotificationWorker>(
            // Check every 15 minutes (minimum for testing - normally would be 12 hours)
            15, TimeUnit.SECONDS
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            ExpirationNotificationWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}