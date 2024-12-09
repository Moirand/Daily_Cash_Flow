package com.my.dailycashflow.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.my.dailycashflow.R
import com.my.dailycashflow.ui.listexpenses.ListExpensesByCategoryActivity
import com.my.dailycashflow.util.CATEGORY_ID
import com.my.dailycashflow.util.CATEGORY_LIMIT
import com.my.dailycashflow.util.CATEGORY_NAME
import com.my.dailycashflow.util.CATEGORY_TOTAL
import com.my.dailycashflow.util.NOTIFICATION_CHANNEL_ID

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    private val categoryId = inputData.getInt(CATEGORY_ID, 0)
    private val categoryName = inputData.getString(CATEGORY_NAME)
    private val categoryTotal = inputData.getInt(CATEGORY_TOTAL, 0)
    private val categoryLimit = inputData.getInt(CATEGORY_LIMIT, 0)

    private fun getPendingIntent(): PendingIntent? {
        val intent = Intent(applicationContext, ListExpensesByCategoryActivity::class.java).apply {
            putExtra(CATEGORY_ID, categoryId)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val prefManager = androidx.preference.PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify = prefManager.getBoolean(applicationContext.getString(R.string.pref_key_notify), false)

        if (shouldNotify) {
            val pendingIntent = getPendingIntent()
            val notificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)
            val notificationBuilder =
                NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(
                        String.format(
                            applicationContext.getString(R.string.notify_title),
                            categoryName
                        )
                    )
                    .setContentText(
                        String.format(
                            applicationContext.getString(R.string.notify_content),
                            categoryTotal,
                            categoryLimit
                        )
                    )
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    applicationContext.getString(R.string.notify_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(categoryId, notificationBuilder.build())
        }
        return Result.success()
    }
}