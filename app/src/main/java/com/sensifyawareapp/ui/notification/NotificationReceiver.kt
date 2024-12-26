package com.sensifyawareapp.ui.notification

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.ui.audioaware.AudioAwareActivity
import com.sensifyawareapp.ui.glanceaware.GlanceAwareTutorialActivity
import com.sensifyawareapp.ui.scentaware.HealthsQuestionsActivity
import com.sensifyawareapp.ui.scentaware.PatientActivity
import com.sensifyawareapp.ui.traceaware.TraceAwareTutorialActivity
import com.sensifyawareapp.utils.PrefUtils
import com.sensifyawareapp.utils.common.AppConstant


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val message = intent?.getStringExtra("message")
            val notificationId = intent?.getIntExtra("notificationId", 0)
            Log.e("TAG", "onReceive: $message")
            Log.e("TAG", "onReceive: $notificationId")
            val notificationManager = NotificationManagerCompat.from(context)

            val intent1 = startTest(context, message)
            intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent =
                PendingIntent.getActivity(
                    context, 0, intent1,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
                )

            val builder =
                NotificationCompat.Builder(context, AppConstant.SharedPreferences.channelId)
                    .setSmallIcon(R.drawable.menu_logo)
                    .setContentTitle(message)
                    .setContentText("Start Test")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()

            builder.flags.and(Notification.FLAG_AUTO_CANCEL)

            if (notificationId != null) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                notificationManager.notify(notificationId, builder)
            }
        }
    }

    fun startTest(context: Context, message: String?): Intent {
        var intent1 = Intent()
        val prefUtils: PrefUtils = SensifyAwareApplication.getAppComponent().providePrefUtil()
        when (message) {
            context.getString(R.string.scentaware_8_scent_test) -> {
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    1
                )
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                    8
                )

                if (prefUtils.getBooleanData(
                        context,
                        AppConstant.SharedPreferences.IS_MODERATOR
                    )
                ) {

                    intent1 = Intent(context, PatientActivity::class.java)
                } else {

                    prefUtils.saveData(
                        context,
                        AppConstant.SharedPreferences.SITE_ID,
                        0
                    )

                    prefUtils.saveData(
                        context,
                        AppConstant.SharedPreferences.PATIENT_ID,
                        null
                    )

                    prefUtils.saveData(
                        context,
                        AppConstant.SharedPreferences.SITE_NAME,
                        null
                    )
                    prefUtils.saveData(
                        context,
                        AppConstant.SharedPreferences.STUDY_NUMBER,
                        null
                    )

                    intent1 = Intent(context, HealthsQuestionsActivity::class.java)

                }


            }

            context.getString(R.string.scentaware_16_scent_test) -> {
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    1
                )
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                    16
                )

                if (prefUtils.getBooleanData(
                        context,
                        AppConstant.SharedPreferences.IS_MODERATOR
                    )
                ) {
                    intent1 = Intent(context, PatientActivity::class.java)
                } else {

                    prefUtils.saveData(
                        context,
                        AppConstant.SharedPreferences.SITE_ID,
                        0
                    )

                    prefUtils.saveData(
                        context,
                        AppConstant.SharedPreferences.PATIENT_ID,
                        null
                    )

                    prefUtils.saveData(
                        context,
                        AppConstant.SharedPreferences.SITE_NAME,
                        null
                    )
                    prefUtils.saveData(
                        context,
                        AppConstant.SharedPreferences.STUDY_NUMBER,
                        null
                    )

                    intent1 = Intent(context, HealthsQuestionsActivity::class.java)

                }

            }

            context.getString(R.string.scentaware_8_smell_training) -> {
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    2
                )
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                    8
                )
                SensifyAwareApplication.initTestData()
                intent1 = Intent(context, TutorialActivity::class.java)
            }

            context.getString(R.string.scentaware_16_smell_training) -> {
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    2
                )
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                    16
                )
                SensifyAwareApplication.initTestData()
                intent1 = Intent(context, TutorialActivity::class.java)
            }

            context.getString(R.string.trace_aware) -> {
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    3
                )

                intent1 = Intent(context, TraceAwareTutorialActivity::class.java)
            }

            context.getString(R.string.audio_aware) -> {
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    4
                )

                intent1 = Intent(context, AudioAwareActivity::class.java)
            }

            context.getString(R.string.glance_aware) -> {
                prefUtils.saveData(
                    context,
                    AppConstant.SharedPreferences.SELECTED_MENU,
                    5
                )

                intent1 = Intent(context, GlanceAwareTutorialActivity::class.java)
            }
        }
        return intent1
    }

    fun setAlarm(context: Context, notificationId: Int, time: Long) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, NotificationReceiver::class.java)
        alarmIntent.putExtra(KEY_EXTRA_NOTIFICATION_ID, notificationId)
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context, notificationId, alarmIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, alarmPendingIntent)
    }

    fun cancelAlarm(context: Context, notificationId: Int) {
        val alarmIntent = Intent(context, NotificationReceiver::class.java)
        alarmIntent.putExtra(KEY_EXTRA_NOTIFICATION_ID, notificationId)
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context, notificationId, alarmIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(alarmPendingIntent)
    }

    companion object {
        private const val KEY_EXTRA_NOTIFICATION_ID = "notification_id"
    }

}


