package com.sensifyawareapp.ui.notification


import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class CancelNotificationsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.e("TAG", "CancelNotificationsReceiver: ${intent?.action}")
        val notificationId = intent?.getIntExtra("notificationId",0)

        // Cancel the repeating alarms
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, NotificationReceiver::class.java)

        val pendingIntent = notificationId?.let {
            PendingIntent.getBroadcast(
                context,
                it,
                notificationIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationId != null) {
            notificationManager.cancel(notificationId)
        }

    }

    companion object {
        const val CANCEL_ACTION = "com.yourapp.CANCEL_NOTIFICATIONS"
    }
}