package com.sensifyawareapp.ui.calendar

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityAllEventBinding
import com.sensifyawareapp.databinding.DialogCancelTestBinding
import com.sensifyawareapp.databinding.LayoutBottomViewBinding
import com.sensifyawareapp.room.Reminder
import com.sensifyawareapp.room.ReminderList
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.ui.audioaware.AudioAwareActivity
import com.sensifyawareapp.ui.glanceaware.GlanceAwareTutorialActivity
import com.sensifyawareapp.ui.notification.NotificationUtils
import com.sensifyawareapp.ui.scentaware.HealthsQuestionsActivity
import com.sensifyawareapp.ui.scentaware.PatientActivity
import com.sensifyawareapp.ui.traceaware.TraceAwareTutorialActivity
import com.sensifyawareapp.ui.trackprogress.TrackProgressViewModel
import com.sensifyawareapp.utils.common.AppConstant
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AllEventActivity : BaseActivity() {
    lateinit var binding: ActivityAllEventBinding

    private var reminders: ArrayList<Reminder> = ArrayList()
    private lateinit var viewModel: TrackProgressViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAllEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProviders.of(this)[TrackProgressViewModel::class.java]

        binding.txEvent.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        getReminderDate()

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun getReminderDate() {
        viewModel.remindersList.observe(this) {
            if (it.isNotEmpty()) {

                binding.txEvent.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                reminders = it as ArrayList<Reminder>
                val reminderList = ArrayList<ReminderList>() // Pair of date and time
                for (i in 0 until it.size) {
                    reminderList.add(
                        ReminderList(
                            reminders[i].id!!.toInt(),
                            reminders[i].testName.toString(),
                            reminders[i].startDate!!,
                            reminders[i].repeatTime.toString(),
                            reminders[i].selectedInterval.toString(),
                            reminders[i].intervalNumber!!
                        )
                    )
                    /*val selectedTime = Calendar.getInstance()
                    val selectedEndTime = Calendar.getInstance()
                    val dateFormat = SimpleDateFormat(AppConstant.SharedPreferences.DateFormat4, Locale.getDefault())


                    val date = dateFormat.parse(reminders[i].startDateTime.toString())
                    if (date != null) {
                        selectedTime.time = date
                    }

                    val date2 = dateFormat.parse(reminders[i].endDateTime.toString())
                    if (date2 != null) {
                        selectedEndTime.time = date2
                    }
                    when (reminders[i].repeatSelection) {
                        0 -> {
                        }

                        1 -> {
                            while (selectedTime.timeInMillis <= selectedEndTime.timeInMillis) {
                                val dateAndTime = dateFormat.format(Date(selectedTime.timeInMillis))
                                reminderList.add(
                                    ReminderList(
                                        reminders[i].id!!.toInt(),
                                        reminders[i].testName.toString(),
                                        dateAndTime
                                    )
                                )

                                Log.e(
                                    "TAG",
                                    "setData: ${reminders[i].testName.toString()}  // $dateAndTime"
                                )
                                // Increment the calendar by one day for the next date
                                selectedTime.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        2 -> {
                            val nextWeek = Calendar.getInstance()
                            nextWeek.timeInMillis = selectedTime.timeInMillis

                            val selectedDayOfWeek = selectedTime.get(Calendar.DAY_OF_WEEK)

                            val daysUntilNextOccurrence =
                                (selectedDayOfWeek - nextWeek.get(Calendar.DAY_OF_WEEK) + 7) % 7

                            nextWeek.add(Calendar.DAY_OF_YEAR, daysUntilNextOccurrence)

                            nextWeek.set(
                                Calendar.HOUR_OF_DAY,
                                selectedTime.get(Calendar.HOUR_OF_DAY)
                            )
                            nextWeek.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))

                            while (nextWeek.timeInMillis <= selectedEndTime.timeInMillis) {
                                if (nextWeek.get(Calendar.DAY_OF_WEEK) == selectedDayOfWeek) {
                                    val dateAndTime = dateFormat.format(Date(nextWeek.timeInMillis))
                                    reminderList.add(
                                        ReminderList(
                                            reminders[i].id!!.toInt(),
                                            reminders[i].testName.toString(),
                                            dateAndTime
                                        )
                                    )
                                }

                                // Increment the calendar by one day for the next date
                                nextWeek.add(Calendar.DAY_OF_MONTH, 1)
                            }
                        }

                        3 -> {

                            val nextMonth = Calendar.getInstance()
                            nextMonth.timeInMillis = selectedTime.timeInMillis
                            val selectedDayOfMonth = selectedTime.get(Calendar.DAY_OF_MONTH)

                            // Set the day and time to match the original date and time
                            nextMonth.set(
                                Calendar.DAY_OF_MONTH,
                                selectedTime.get(Calendar.DAY_OF_MONTH)
                            )
                            nextMonth.set(
                                Calendar.HOUR_OF_DAY,
                                selectedTime.get(Calendar.HOUR_OF_DAY)
                            )
                            nextMonth.set(Calendar.MINUTE, selectedTime.get(Calendar.MINUTE))


                            while (nextMonth.timeInMillis <= selectedEndTime.timeInMillis) {
                                if (nextMonth.get(Calendar.DAY_OF_MONTH) == selectedDayOfMonth) {
                                    val dateAndTime =
                                        dateFormat.format(Date(nextMonth.timeInMillis))
                                    reminderList.add(
                                        ReminderList(
                                            reminders[i].id!!.toInt(),
                                            reminders[i].testName.toString(),
                                            dateAndTime
                                        )
                                    )
                                }

                                // Increment the calendar by one month for the next date
                                nextMonth.add(Calendar.MONTH, 1)
                            }
                        }

                        4 -> {

                            when (reminders[i].customSelection) {

                                getString(R.string.day) -> {

                                    while (selectedTime.timeInMillis <= selectedEndTime.timeInMillis) {

                                        val dateAndTime =
                                            dateFormat.format(Date(selectedTime.timeInMillis))
                                        reminderList.add(
                                            ReminderList(
                                                reminders[i].id!!.toInt(),
                                                reminders[i].testName.toString(),
                                                dateAndTime
                                            )
                                        )

                                        // Increment the calendar by the specified interval in days
                                        selectedTime.add(
                                            Calendar.DAY_OF_MONTH,
                                            reminders[i].intervalNumber!!
                                        )
                                    }

                                }

                                getString(R.string.week) -> {

                                    while (selectedTime.timeInMillis <= selectedEndTime.timeInMillis) {
                                        Log.e(
                                            "TAG",
                                            "toArrayList: ${toArrayList(reminders[i].customDaysOfWeek)!!}",
                                        )

                                        for (dayOfWeek in toArrayList(reminders[i].customDaysOfWeek)!!) {
                                            Log.e("TAG", "dayOfWeek: $dayOfWeek")
                                            selectedTime.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                                            val dateAndTime =
                                                dateFormat.format(Date(selectedTime.timeInMillis))
                                            reminderList.add(
                                                ReminderList(
                                                    reminders[i].id!!.toInt(),
                                                    reminders[i].testName.toString(),
                                                    dateAndTime
                                                )
                                            )
                                            // Increment the calendar by the specified interval in weeks

                                        }

                                        selectedTime.add(
                                            Calendar.WEEK_OF_YEAR,
                                            reminders[i].intervalNumber!!
                                        )

//
                                    }

                                }

                                getString(R.string.month) -> {

                                    while (selectedTime.timeInMillis <= selectedEndTime.timeInMillis) {

                                        val dateAndTime =
                                            dateFormat.format(Date(selectedTime.timeInMillis))
                                        reminderList.add(
                                            ReminderList(
                                                reminders[i].id!!.toInt(),
                                                reminders[i].testName.toString(),
                                                dateAndTime
                                            )
                                        )
                                        // Increment the calendar by the specified interval in weeks
                                        selectedTime.add(
                                            Calendar.MONTH,
                                            reminders[i].intervalNumber!!
                                        )
                                    }
                                }
                            }
                        }
                    }*/
                }
                Log.e("TAG", "setData: ${reminderList.size}")

                val adapter = AllEventAdapter(reminderList, this)
                binding.recyclerView.adapter = adapter
                eventCheck(reminderList, adapter)

            } else {
                Log.e("TAG", "getReminderDate: Call")
                binding.txEvent.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
        viewModel.getReminderDate()

    }
    private fun localizeText(text: String?): String? {

        if (text != null) {
            val resourceId =
               resources.getIdentifier("${text}", "string", packageName)
            return if (resourceId != 0) {
               getString(resourceId)
            } else {
                text // Return original text if no localized version is found
            }
        }
        return null
    }

    private fun eventCheck(
        reminderList: ArrayList<ReminderList>,
        allEventAdapter: AllEventAdapter
    ) {

        allEventAdapter.setOnClickListener(object : AllEventAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                var notificationId = 0
                for (i in 0 until reminders.size) {
                    if (reminderList[position].id == reminders[i].id!!.toInt()) {
                        Log.e("TAG", "onItemClick NotificationID: ${reminders[i].id!!.toInt()}", )
                         notificationId = reminders[i].id!!.toInt()
                        val bottomSheetDialog = BottomSheetDialog(this@AllEventActivity)
                        val bottomBinding: LayoutBottomViewBinding =
                            DataBindingUtil.inflate(
                                LayoutInflater.from(this@AllEventActivity),
                                R.layout.layout_bottom_view,
                                null,
                                false
                            )
                        bottomSheetDialog.setContentView(bottomBinding.root)

                        bottomSheetDialog.show()
                        bottomBinding.time = reminderList[position].dateTime
                        bottomBinding.title = localizeText(reminderList[position].testName)

                        if (reminders[position].repeatTime == "every") {

                            bottomBinding.repeat =
                                getString(R.string.every, reminders[position].intervalNumber.toString(), localizeText(reminders[position].selectedInterval))

                        }else{
                            bottomBinding.repeat = localizeText(reminders[position].repeatTime)

                        }
                        when (reminders[i].iconNumber) {
                            1 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@AllEventActivity,
                                        R.drawable.scent_aware
                                    )
                                )

                            2 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@AllEventActivity,
                                        R.drawable.training_icon
                                    )
                                )

                            3 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@AllEventActivity,
                                        R.drawable.trace_aware
                                    )
                                )

                            4 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@AllEventActivity,
                                        R.drawable.audio_aware
                                    )
                                )

                            5 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@AllEventActivity,
                                        R.drawable.ic_face
                                    )
                                )

                        }


                        bottomBinding.ivBack.setOnClickListener {
                            bottomSheetDialog.dismiss()
                        }

                        bottomBinding.ivEdit.setOnClickListener {
                            startActivity(
                                Intent(
                                    this@AllEventActivity,
                                    ReminderActivity::class.java
                                ).putExtra("isEdit", true)
                                    .putExtra("position", i)
                            )
                            bottomSheetDialog.dismiss()
                        }

                        bottomBinding.ivDelete.setOnClickListener {
                            val dialog = Dialog(this@AllEventActivity, R.style.Theme_Dialog)
                            val cancelTestBinding = DialogCancelTestBinding.inflate(
                                LayoutInflater.from(this@AllEventActivity),
                                ConstraintLayout(this@AllEventActivity),
                                false
                            )
                            dialog.setContentView(cancelTestBinding.root)
                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            cancelTestBinding.tvMsg.text = "DO You Want To Delete This Reminder?"
                            cancelTestBinding.btnNo.setOnClickListener {
                                dialog.dismiss()
                            }
                            cancelTestBinding.cardYes.setOnClickListener {

                                val item = reminders[i]
                                viewModel.deleteReminderDate(item)
                                reminders.removeAt(i)
                                allEventAdapter.notifyItemRemoved(i)

                                val formattedDate =
                                    SimpleDateFormat(
                                        "dd-LLL-yyyy",
                                        Locale.getDefault()
                                    ).format(Calendar.getInstance().time)
                                val simpleDateFormat =
                                    SimpleDateFormat("hh:mm aaa", Locale.getDefault())
                                var formattedTime = simpleDateFormat.format(Date())
                                formattedTime =
                                    formattedTime.replace("am", "AM").replace("pm", "PM")
                                val endDateTimeString = "$formattedDate $formattedTime"
                                val notificationUtils = NotificationUtils(this@AllEventActivity)
                                notificationUtils.scheduleStopNotification(
                                    endDateTimeString,
                                    notificationId
                                )
                                Log.e("TAG", "onItemClick: ${reminders.size}")
                                if (reminders.size == 0) {
                                    binding.txEvent.visibility = View.VISIBLE
                                    binding.recyclerView.visibility = View.GONE
                                }
                                bottomSheetDialog.dismiss()
                                dialog.dismiss()

                            }
                            dialog.show()

                        }

                        bottomBinding.btnStart.setOnClickListener {
                            when (reminders[i].testName) {
                                getString(R.string.scentaware_16_scent_test) -> {

                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        1
                                    )
                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                                        16
                                    )
                                    if (prefUtils.getBooleanData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.IS_MODERATOR
                                        )
                                    ) {
                                        startActivity(
                                            Intent(
                                                this@AllEventActivity, PatientActivity::class.java

                                            )
                                        )
                                    } else {

                                        prefUtils.saveData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.SITE_ID,
                                            0
                                        )

                                        prefUtils.saveData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.PATIENT_ID,
                                            null
                                        )

                                        prefUtils.saveData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.SITE_NAME,
                                            null
                                        )
                                        prefUtils.saveData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.STUDY_NUMBER,
                                            null
                                        )

                                        startActivity(
                                            Intent(
                                                this@AllEventActivity,
                                                HealthsQuestionsActivity::class.java
                                            )
                                        )

                                    }
                                }

                                getString(R.string.scentaware_8_scent_test) -> {

                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        1
                                    )
                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                                        8
                                    )
                                    if (prefUtils.getBooleanData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.IS_MODERATOR
                                        )
                                    ) {
                                        startActivity(
                                            Intent(
                                                this@AllEventActivity, PatientActivity::class.java

                                            )
                                        )
                                    } else {

                                        prefUtils.saveData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.SITE_ID,
                                            0
                                        )

                                        prefUtils.saveData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.PATIENT_ID,
                                            null
                                        )

                                        prefUtils.saveData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.SITE_NAME,
                                            null
                                        )
                                        prefUtils.saveData(
                                            this@AllEventActivity,
                                            AppConstant.SharedPreferences.STUDY_NUMBER,
                                            null
                                        )

                                        startActivity(
                                            Intent(
                                                this@AllEventActivity,
                                                HealthsQuestionsActivity::class.java
                                            )
                                        )

                                    }


                                }

                                getString(R.string.scentaware_16_smell_training) -> {
                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        2
                                    )
                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                                        16
                                    )
                                    SensifyAwareApplication.initTestData()
                                    startActivity(
                                        Intent(
                                            this@AllEventActivity,
                                            TutorialActivity::class.java
                                        )
                                    )

                                }

                                getString(R.string.scentaware_8_smell_training) -> {

                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        2
                                    )
                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                                        8
                                    )
                                    SensifyAwareApplication.initTestData()
                                    startActivity(
                                        Intent(
                                            this@AllEventActivity,
                                            TutorialActivity::class.java
                                        )
                                    )

                                }

                                getString(R.string.trace_aware) -> {
                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        3
                                    )
                                    startActivity(
                                        Intent(
                                            this@AllEventActivity,
                                            TraceAwareTutorialActivity::class.java
                                        )
                                    )

                                }

                                getString(R.string.audio_aware) -> {
                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        4
                                    )
                                    Intent(
                                        this@AllEventActivity,
                                        AudioAwareActivity::class.java
                                    ).putExtra(
                                        "level",
                                        1
                                    )


                                }

                                getString(R.string.glance_aware) -> {

                                    prefUtils.saveData(
                                        this@AllEventActivity,
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        5
                                    )
                                    startActivity(
                                        Intent(
                                            this@AllEventActivity,
                                            GlanceAwareTutorialActivity::class.java
                                        )
                                    )

                                }
                            }
                            bottomSheetDialog.dismiss()
                        }
                    }
                }

            }

        })
    }

}