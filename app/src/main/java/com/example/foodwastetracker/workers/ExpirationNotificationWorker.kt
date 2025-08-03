package com.example.foodwastetracker.workers


// We'll use a default icon instead of R.drawable
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.foodwastetracker.MainActivity

// Database imports removed for simplicity - this demonstrates WorkManager functionality

class ExpirationNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Simulate checking for expiring items
            // In a real app, this would check the database
            // This demonstrates WorkManager background task functionality

            // Simulate finding expiring items (for demo purposes)
            val hasExpiringItems = true

            if (hasExpiringItems) {
                sendNotification(2, "Milk")
            }

            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    @Suppress("SameParameterValue")
    private fun sendNotification(count: Int, firstItemName: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Food Expiration Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for food items that are about to expire"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent to open the app
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("⚠️ Food Expiring Soon!")
            .setContentText(
                if (count == 1) {
                    "$firstItemName expires today!"
                } else {
                    "$count items including $firstItemName expire soon!"
                }
            )
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    if (count == 1) {
                        "Your $firstItemName is expiring today. Consider using it to avoid waste!"
                    } else {
                        "$count food items are expiring soon, including $firstItemName. Check your Food Waste Tracker to see all items!"
                    }
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "food_expiration_channel"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "food_expiration_check"
    }
}