package com.sensifyawareapp.ui.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.sensifyawareapp.R
import com.sensifyawareapp.utils.common.AppConstant
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationUtils(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


    private val channelId = "sensify_id"

    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AppConstant.SharedPreferences.channelId,
                "SensifyAware Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Description of my notification channel"
            channel.enableLights(true)
            channel.lightColor = Color.RED

            val notificationManager =
                context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleRepeatingNotification(
        message: String,
        startDateTimeString: String,
        notificationId: Int,
        repeat_selection: Int,
        selectedDaysList: ArrayList<Int>,
        selectedItem: String,
        number: Int
    ) {
        val selectedTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())

        try {
            val date = dateFormat.parse(startDateTimeString)
            if (date != null) {
                selectedTime.time = date
            } else {
                Toast.makeText(context, "invalid date format", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: ParseException) {
            // Handle parsing exception
            Log.e("NotificationUtils", "scheduleRepeatingNotification: ${e.printStackTrace()}")
            e.printStackTrace()
            return
        }


        val currentTime = Calendar.getInstance()
        val timeDifferenceMillis = selectedTime.timeInMillis - currentTime.timeInMillis

        val notificationIntent = Intent(context, NotificationReceiver::class.java)
            .putExtra("message", message)
            .putExtra("notificationId", notificationId)

        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                notificationIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
            )

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + timeDifferenceMillis,
            pendingIntent
        )

        when (repeat_selection) {
            0 -> {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    selectedTime.timeInMillis,
                    pendingIntent
                )
            }

            1 -> {

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    selectedTime.timeInMillis,
                    AlarmManager.INTERVAL_DAY, // Repeat every day
                    pendingIntent
                )
            }

            2 -> {

                val nextWeek = Calendar.getInstance()
                nextWeek.timeInMillis = selectedTime.timeInMillis

                val selectedDayOfWeek = selectedTime.get(Calendar.DAY_OF_WEEK)

// Calculate the number of days until the next occurrence of the selected day of the week
                val daysUntilNextOccurrence =
                    (selectedDayOfWeek - nextWeek.get(Calendar.DAY_OF_WEEK) + 7) % 7

// Add the calculated number of days to move to the next occurrence of the selected day
                nextWeek.add(Calendar.DAY_OF_YEAR, daysUntilNextOccurrence)

// Set the hour and minute to match the original time
                nextWeek.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
                nextWeek.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    nextWeek.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 7, // Repeat every week
                    pendingIntent
                )

            }

            3 -> {

                val nextMonth = Calendar.getInstance()
                nextMonth.timeInMillis = selectedTime.timeInMillis
                nextMonth.add(Calendar.MONTH, 1)

                // Set the day and time to match the original date and time
                nextMonth.set(Calendar.DAY_OF_MONTH, selectedTime.get(Calendar.DAY_OF_MONTH))
                nextMonth.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
                nextMonth.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))

                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    nextMonth.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 30, // Repeat every month (approximate)
                    pendingIntent
                )

            }

            4 -> {
                Log.e("TAG", "repeat_selection: $repeat_selection")
                Log.e("TAG", "selectedItem: $selectedItem")
                Log.e("TAG", "selectedDaysList: $selectedDaysList")
                Log.e("TAG", "INTERVAL_DAY: ${AlarmManager.INTERVAL_DAY * number.toLong()}")
                Log.e("TAG", "Week: ${AlarmManager.INTERVAL_DAY * 7 * number}")
                Log.e("TAG", "Month: ${AlarmManager.INTERVAL_DAY * 30 * number}")

                when (selectedItem) {

                    context.getString(R.string.day) -> {
                        if (number == 0) {

                            alarmManager.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                selectedTime.timeInMillis,
                                AlarmManager.INTERVAL_DAY, // Repeat every day
                                pendingIntent
                            )
                        } else {
                            alarmManager.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                selectedTime.timeInMillis,
                                AlarmManager.INTERVAL_DAY * number.toLong(),
                                pendingIntent
                            )
                        }


                    }

                    context.getString(R.string.week) -> {

                        for (dayOfWeek in selectedDaysList) {

                            selectedTime.set(Calendar.DAY_OF_WEEK, dayOfWeek)

                            if (number==0){
                                alarmManager.setRepeating(
                                    AlarmManager.RTC_WAKEUP,
                                    selectedTime.timeInMillis,
                                    AlarmManager.INTERVAL_DAY * 7, // Repeat every week
                                    pendingIntent
                                )
                            }else{

                                alarmManager.setRepeating(
                                    AlarmManager.RTC_WAKEUP,
                                    selectedTime.timeInMillis,
                                    AlarmManager.INTERVAL_DAY * 7 * number, // Repeat every week
                                    pendingIntent
                                )
                            }
                        }

                    }

                    context.getString(R.string.month) -> {
                        if (number==0){

                            val nextMonth = Calendar.getInstance()
                            nextMonth.timeInMillis = selectedTime.timeInMillis
                            nextMonth.add(Calendar.MONTH, 1)

                            // Set the day and time to match the original date and time
                            nextMonth.set(Calendar.DAY_OF_MONTH, selectedTime.get(Calendar.DAY_OF_MONTH))
                            nextMonth.set(Calendar.HOUR_OF_DAY, selectedTime.get(Calendar.HOUR_OF_DAY))
                            nextMonth.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))

                            alarmManager.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                nextMonth.timeInMillis,
                                AlarmManager.INTERVAL_DAY * 30, // Repeat every month (approximate)
                                pendingIntent
                            )

                        }else {

                            alarmManager.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                selectedTime.timeInMillis,
                                AlarmManager.INTERVAL_DAY * 30 * number, // Repeat every month (approximate)
                                pendingIntent
                            )
                        }

                    }

                }
            }

            else -> {

            }
        }

        // Calculate the time until the next week


        /*alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis,
            AlarmManager.INTERVAL_DAY, // Repeat every 3 hours
            pendingIntent
        )*/

    }

    fun scheduleStopNotification(stopDateTimeString: String, notificationId: Int) {

        val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())
        val stopTime = Calendar.getInstance()

        try {
            val date = dateFormat.parse(stopDateTimeString)
            if (date != null) {
                stopTime.time = date
            } else {
//                Toast.makeText(context, "invalid date format", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "scheduleStopNotification: invalid date format", )
                return
            }
        } catch (e: ParseException) {
            // Handle parsing exception
            Log.e("NotificationUtils", "scheduleStopNotification: ${e.printStackTrace()}")
            e.printStackTrace()
            return
        }

// Check the current date and time regularly (e.g., in your app's service or activity)
        val currentTime = Calendar.getInstance()
// Compare the current time to the stop time
        if (currentTime.timeInMillis >= stopTime.timeInMillis) {
            Log.e("TAG", "scheduleStopNotification notificationId : $notificationId", )
            // Current time has reached or passed the stop time, so cancel notifications here
            Intent(context, CancelNotificationsReceiver::class.java)
                .putExtra("notificationId", notificationId)

        }
    }

}