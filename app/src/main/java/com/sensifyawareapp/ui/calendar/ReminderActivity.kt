package com.sensifyawareapp.ui.calendar

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityReminderBinding
import com.sensifyawareapp.databinding.CustomPopupMenuItemBinding
import com.sensifyawareapp.room.AppDatabase
import com.sensifyawareapp.room.Reminder
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.audioaware.AudioAwareViewModel
import com.sensifyawareapp.ui.notification.NotificationUtils
import com.sensifyawareapp.ui.trackprogress.TrackProgressViewModel
import com.sensifyawareapp.utils.common.AppConstant
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ReminderActivity : BaseActivity() {
    lateinit var binding: ActivityReminderBinding
    private var DATE_FORMAT = "dd-LLL-yyyy"
    private var TIME_FORMAT = "hh:mm aaa"
    private var repeatSelection = 0
    private var iconNumber = 1
    private var isCustom = false
    private var isEdit = false
    private lateinit var progressViewModel: TrackProgressViewModel
    private var testName: String = String()
    private var repeatText: String = String()
    private var selectedInterval = ""
    private var number = 0

    var intervalNumber = 0
    var customSelection = String()
    var customDaysOfWeek = String()

    lateinit var reminderValue: Reminder
    var notificationId: Int = 0

    private val viewModel: AudioAwareViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)

        setContentView(binding.root)
        progressViewModel = ViewModelProviders.of(this)[TrackProgressViewModel::class.java]

        isCustom = intent.getBooleanExtra("isCustom", false)
        isEdit = intent.getBooleanExtra("isEdit", false)
        selectedInterval = intent.getStringExtra("selectedInterval").toString()
        number = intent.getIntExtra("Number", 0)
        binding.reminder = if (isEdit) "Edit Reminder"
        else getString(R.string.add_reminder)


        binding.cardView.setOnClickListener {
            showCustomOptionDialog()

        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        setCustomData()
        setTimeAndDate()

        binding.repeatCard.setOnClickListener {
            if (repeatSelection == 0) {
                binding.tvRepeat.text = getString(R.string.does_not_repeat)
            }
            showAlertDialog()
        }
        testName = localizeText("scentaware_8_scent_test")

        binding.saveButton.setOnClickListener {
            setupNotification()
        }
        if (isEdit) getReminderDate()
    }

    private fun setTimeAndDate() {
        val c: Calendar = Calendar.getInstance()
        val formattedDate = SimpleDateFormat(DATE_FORMAT, Locale.US).format(c.time)
        if (!isCustom)
            binding.tvStartDate.text = formattedDate
        Log.e("TAG", "setTimeAndDate: ${binding.tvStartDate.text}")

        binding.tvStartDate.setOnClickListener {
            datePicker(1)
        }

        binding.tvEndDate.setOnClickListener {
            datePicker(2)
        }

        val simpleDateFormat = SimpleDateFormat(TIME_FORMAT, Locale.US)
        var formattedTime = simpleDateFormat.format(Date())
        formattedTime = formattedTime.replace("am", "AM").replace("pm", "PM")
        if (!isCustom) {
            binding.tvSetTime.text = formattedTime
        }

        binding.cardTime.setOnClickListener {
            timePicker()
        }


    }

    private fun setupNotification() {

        if (binding.tvTest.text == getString(R.string.select_test)) {
            showError("Please Select Test")
            return
        }

        if (binding.tvRepeat.text == getString(R.string.repeat)) {
            showError("Please Select when Do you Want To Repeat Notification")
            return
        }

        val customDaysOfWeek =
            if (intent.getStringExtra("selectedItem") == getString(R.string.week))
                fromArrayList(intent.getIntegerArrayListExtra("customDaysOfWeek")) else null

        val selectedItem = if (intent.getStringExtra("selectedItem") == null) {
            null
        } else {
            intent.getStringExtra("selectedItem")
        }

        val selectedTime = Calendar.getInstance()
        val dateFormat =
            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat4, Locale.US)


        val startDateTimeString = "${binding.tvStartDate.text} ${binding.tvSetTime.text}"

        var endDateTimeString = "${binding.tvEndDate.text} ${binding.tvSetTime.text}"
        val reminderTimeString = "${binding.tvSetTime.text}"


        try {
            val date = dateFormat.parse(startDateTimeString)
            if (date != null) {
                selectedTime.time = date
            } else {
                // Handle invalid date format
                Toast.makeText(this, "invalid date format", Toast.LENGTH_SHORT).show()
                return
            }
        } catch (e: ParseException) {
            // Handle parsing exception
            Log.e("NotificationUtils", "scheduleRepeatingNotification: ${e.printStackTrace()}")
            e.printStackTrace()
            return
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedTime.timeInMillis
        calendar.add(Calendar.YEAR, 1)
        val nextYearTimestamp = calendar.timeInMillis

        if (binding.tvEndDate.text.equals("-------")) {
            endDateTimeString = dateFormat.format(Date(nextYearTimestamp))
        }

        if (isEdit) {
            progressViewModel.deleteReminderDate(reminderValue)
            val formattedDate =
                SimpleDateFormat(
                    DATE_FORMAT,
                    Locale.US
                ).format(Calendar.getInstance().time)
            val simpleDateFormat =
                SimpleDateFormat(TIME_FORMAT, Locale.US)
            var formattedTime = simpleDateFormat.format(Date())
            formattedTime = formattedTime.replace("am", "AM").replace("pm", "PM")
            val endDateTimeString1 = "$formattedDate $formattedTime"
            val notificationUtils = NotificationUtils(this)
            notificationUtils.scheduleStopNotification(
                endDateTimeString1,
                notificationId
            )
        }
        Log.e("TAG", "setupNotification: $testName")
        insertReminderRoomRecord(
            testName,
            startDateTimeString,
            endDateTimeString,
            reminderTimeString,
            repeatSelection,
            selectedItem.toString(),
            number,
            customDaysOfWeek.toString(),
            prefUtils.getStringData(this, AppConstant.SharedPreferences.REPEAT),
            iconNumber, selectedInterval
        )

        Toast.makeText(this, "Reminder Added", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun fromArrayList(list: ArrayList<Int>?): String? {
        return list?.joinToString(",")
    }

    private fun insertReminderRoomRecord(
        testName: String,
        startDateTimeString: String,
        endDateTimeString: String,
        reminderTimeString: String,
        repeatSelection: Int,
        selectedItem: String,
        number: Int,
        customDaysOfWeek: String?,
        repeatTime: String?,
        iconNumber: Int,
        selectedInterval: String
    ) {
        GlobalScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(application)
                val reminder = Reminder(
                    null,
                    testName,
                    startDateTimeString,
                    endDateTimeString,
                    reminderTimeString,
                    repeatSelection,
                    selectedItem,
                    number,
                    customDaysOfWeek,
                    repeatTime,
                    iconNumber, selectedInterval
                )
                val notificationId = dbHelper.awareDao().insertReminderId(reminder)
                Log.e("TAG", "insertReminderRoomRecord: $notificationId")
                setNotification(
                    startDateTimeString,
                    notificationId.toInt(),
                    repeatSelection,
                    endDateTimeString
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun localizeText(text: String): String {
        val resourceId = this.resources.getIdentifier(text, "string", this.packageName)
        return if (resourceId != 0) {
            this.getString(resourceId)
        } else {
            text // Return original text if no localized version is found
        }

    }

    private fun setNotification(
        startDateTimeString: String,
        notificationId: Int,
        repeatSelection: Int,
        endDateTimeString: String
    ) {
        val notificationUtils = NotificationUtils(this)
        notificationUtils.createNotificationChannel()
        if (repeatSelection == 4) {
            notificationUtils.scheduleRepeatingNotification(
                localizeText(testName),
                startDateTimeString,
                notificationId,
                repeatSelection,
                if (intent.getStringExtra("selectedItem") == getString(R.string.week))
                    intent.getIntegerArrayListExtra("customDaysOfWeek")!! else ArrayList(),
                intent.getStringExtra("selectedItem")!!,
                intent.getIntExtra("Number", 0)
            )
        } else {
            notificationUtils.scheduleRepeatingNotification(
                localizeText(testName),
                startDateTimeString,
                notificationId,
                repeatSelection,
                ArrayList(),
                String(),
                0,
            )
        }
        notificationUtils.scheduleStopNotification(endDateTimeString, notificationId)

    }

    private fun getReminderDate() {
        val position = intent.getIntExtra("position", 0)
        Log.e("TAG", "getReminderDate: $position")
        progressViewModel.remindersList.observe(this) {
            if (it.isNotEmpty()) {
                binding.tvTest.text = localizeText(it[position].testName.toString())
                testName = it[position].testName.toString()
                reminderValue = it[position]
                repeatSelection = it[position].repeatSelection!!
                notificationId = it[position].id!!.toInt()

                setImageIcon(it[position].iconNumber)
                iconNumber = it[position].iconNumber!!
                intervalNumber = it[position].intervalNumber!!
                customSelection = it[position].customSelection!!
                customDaysOfWeek = it[position].customDaysOfWeek!!
                if (!isCustom) {

                    if (it[position].repeatTime == "every") {

                        binding.tvRepeat.text =
                            getString(
                                R.string.every,
                                it[position].intervalNumber.toString(),
                                localizeText(it[position].selectedInterval.toString())
                            )

                    } else {
                        binding.tvRepeat.text = localizeText(it[position].repeatTime.toString())

                    }

                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.REPEAT,
                        it[position].repeatTime.toString()
                    )
                }

                // Define the date and time format
                val dateFormat =
                    SimpleDateFormat(AppConstant.SharedPreferences.DateFormat4, Locale.US)
                try {
                    // Parse the date and time string
                    val dateTime = dateFormat.parse(it[position].startDate.toString())
                    val dateTime1 = dateFormat.parse(it[position].endDate.toString())

                    // Separate date and time
                    val dateOnlyFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
                    val timeOnlyFormat = SimpleDateFormat("hh:mm a", Locale.US)
                    binding.tvStartDate.text = dateOnlyFormat.format(dateTime!!)
                    binding.tvSetTime.text = timeOnlyFormat.format(dateTime)
                    binding.tvEndDate.text = dateOnlyFormat.format(dateTime1!!)

                    setCustomData()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }
        progressViewModel.getReminderDate()
    }

    private fun setImageIcon(iconNumber: Int?) {
        when (iconNumber) {
            1 -> binding.ivImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.scent_aware
                )
            )

            2 -> binding.ivImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.training_icon
                )
            )

            3 -> binding.ivImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.trace_aware
                )
            )

            4 -> binding.ivImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.audio_aware
                )
            )

            5 -> binding.ivImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_face
                )
            )
        }

        /* binding.ivImage.setImageDrawable(
             ContextCompat.getDrawable(
                 this,
                 R.drawable.ic_face
             )
         )*/
    }


    private fun showCustomOptionDialog() {
        val builder: AlertDialog.Builder =
            AlertDialog.Builder(this@ReminderActivity, R.style.CustomAlertDialog)

        val popupMenuItemBinding = CustomPopupMenuItemBinding.inflate(layoutInflater)
        builder.setView(popupMenuItemBinding.root)
        val alertDialog = builder.create()
        alertDialog.show()

//        var test = String()

        popupMenuItemBinding.tvScent8.setOnClickListener {
            iconNumber = 1
            setTextData("scentaware_8_scent_test", R.drawable.scent_aware)
            alertDialog.dismiss()
        }
        popupMenuItemBinding.tvScent16.setOnClickListener {
            iconNumber = 1
            setTextData("scentaware_16_scent_test", R.drawable.scent_aware)
            alertDialog.dismiss()
        }
        popupMenuItemBinding.tvTraining8.setOnClickListener {
            iconNumber = 2
            setTextData("scentaware_8_smell_training", R.drawable.training_icon)
            alertDialog.dismiss()
        }
        popupMenuItemBinding.tvTraining16.setOnClickListener {
            iconNumber = 2
            setTextData("scentaware_16_smell_training", R.drawable.training_icon)
            alertDialog.dismiss()
        }
        popupMenuItemBinding.tvTraceAware.setOnClickListener {
            iconNumber = 3
            setTextData("trace_aware", R.drawable.trace_aware)
            alertDialog.dismiss()
        }
        popupMenuItemBinding.tvAudioAware.setOnClickListener {
            iconNumber = 4
            setTextData("audio_aware", R.drawable.audio_aware)
            alertDialog.dismiss()
        }
        popupMenuItemBinding.tvGlanceAware.setOnClickListener {
            iconNumber = 5
            setTextData("glance_aware", R.drawable.ic_face)
            alertDialog.dismiss()

        }

    }

    private fun setTextData(text: String, icIcon: Int) {
        testName = text
        binding.tvTest.text = localizeText(text)
        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.SELECTED_TEXT,
            localizeText(text)
        )
        binding.ivImage.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                icIcon
            )
        )
    }

    private fun timePicker() {

        val currentTime = Calendar.getInstance()
        val currentHour =
            currentTime.get(Calendar.HOUR_OF_DAY) // Get the current hour (24-hour format)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(currentHour)
                .setInputMode(INPUT_MODE_KEYBOARD)
                .setTheme(R.style.ThemeOverlay_App_TimePicker)
                .setMinute(currentMinute)
                .setTitleText("Enter time")
                .build()
        picker.show(supportFragmentManager, "tag")

        picker.addOnPositiveButtonClickListener {
            var newHour = picker.hour
            val newMinute = picker.minute

            val isAM = (newHour < 12)
            if (!isAM && newHour > 12) {
                newHour -= 12;
            }
            val timeString =
                String.format("%02d:%02d %s", newHour, newMinute, if (isAM) "AM" else "PM")
            binding.tvSetTime.text = timeString
        }
    }

    private fun datePicker(i: Int): String {

        val calendar = Calendar.getInstance()
        val selectedDate = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        val datePickerDialog = DatePickerDialog(
            this@ReminderActivity,
            { view, year, monthOfYear, dayOfMonth ->

                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val formattedDate =
                    SimpleDateFormat(DATE_FORMAT, Locale.US).format(selectedDate.time)
                if (i == 1) {
                    if (!binding.tvEndDate.text.equals("-------")){
                        binding.tvEndDate.text = "-------"
                    }
                    binding.tvStartDate.text = formattedDate

                } else {
                    binding.tvEndDate.text = formattedDate

                }

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        if (i == 2) {

            try {
                val date = simpleDateFormat.parse(binding.tvStartDate.text.toString())
                datePickerDialog.datePicker.minDate =  date?.time ?: 0L
            } catch (e: Exception) {
                Log.e("TAG", "datePicker: ${e.message}")
            }

        }


        datePickerDialog.show()
        return SimpleDateFormat(DATE_FORMAT, Locale.US).format(selectedDate.time)
    }

    private fun showAlertDialog() {

        val items = arrayOf(
            getString(R.string.does_not_repeat),
            getString(R.string.every_day),
            getString(R.string.every_week),
            getString(R.string.every_month),
            getString(R.string.custom)
        )

//        val repeatSelection = prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_TIME)
        MaterialAlertDialogBuilder(this, R.style.MaterialThemeDialog)
            .setTitle("Select Repeating")
            .setSingleChoiceItems(items, repeatSelection) { dialog_, which ->
                Log.e("TAG", "showAlertDialog: ${items[which]}")
                repeatSelection = which

                when (which) {
                    0 -> {
                        repeatText = "does_not_repeat"
                        binding.tvRepeat.text = getString(R.string.does_not_repeat)
                        prefUtils.saveData(this, AppConstant.SharedPreferences.SELECTED_TIME, 0)
                    }

                    1 -> {
                        repeatText = "every_day"
                        binding.tvRepeat.text = getString(R.string.every_day)
                        prefUtils.saveData(this, AppConstant.SharedPreferences.SELECTED_TIME, 1)
                    }

                    2 -> {
                        repeatText = "every_week"
                        binding.tvRepeat.text = getString(R.string.every_week)
                        prefUtils.saveData(this, AppConstant.SharedPreferences.SELECTED_TIME, 2)
                    }

                    3 -> {
                        repeatText = "every_month"
                        binding.tvRepeat.text = getString(R.string.every_month)
                        prefUtils.saveData(this, AppConstant.SharedPreferences.SELECTED_TIME, 3)
                    }

                    4 -> {

                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.START_DATE,
                            binding.tvStartDate.text.toString()
                        )
                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.END_DATE,
                            binding.tvEndDate.text.toString()
                        )
                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.REMINDER_TIME,
                            binding.tvSetTime.text.toString()
                        )

                        prefUtils.saveData(this, AppConstant.SharedPreferences.SELECTED_TIME, 5)
                        startActivity(
                            Intent(
                                this,
                                CustomReminderActivity::class.java
                            ).putExtra("Date", binding.tvStartDate.text.toString())
                                .putExtra("isEdit", isEdit)
                                .putExtra("position", intent.getIntExtra("position", 0))

                        )
                        Log.e("TAG", "showAlertDialog: ${intent.getIntExtra("position", 0)}")
                    }

                }

                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.REPEAT,
                    repeatText
                )
                dialog_.dismiss()

            }
            .setBackground(getDrawable(R.drawable.dialog_background))
            .show()
    }

    private fun setCustomData() {
        if (isCustom) {
            val selectedText =
                prefUtils.getStringData(this, AppConstant.SharedPreferences.SELECTED_TEXT)
            val startDate = prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.START_DATE
            )
            val endDate = prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.END_DATE
            )
            val reminderTime =
                prefUtils.getStringData(this, AppConstant.SharedPreferences.REMINDER_TIME)
            val repeat = prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.REPEAT
            )

            setIcon(selectedText)

            binding.tvStartDate.text = startDate
            binding.tvEndDate.text = endDate
            binding.tvSetTime.text = reminderTime
            if (repeat == "every") {
                binding.tvRepeat.text =
                    getString(R.string.every, number.toString(), localizeText(selectedInterval))
            } else {
                binding.tvRepeat.text = localizeText(repeat.toString())
            }


            repeatSelection = 4

            Log.e(
                "TAG",
                "isCustom: $selectedText // $startDate // $endDate // $reminderTime //  $repeat",
            )

        }
    }

    private fun setIcon(selectedText: String?) {
        if (selectedText == getString(R.string.scentaware_16_scent_test)) {
            setTextData(selectedText, R.drawable.scent_aware)
            iconNumber = 1
        }
        if (selectedText == getString(R.string.scentaware_8_scent_test)) {
            iconNumber = 1
            setTextData(selectedText, R.drawable.scent_aware)
        }
        if (selectedText == getString(R.string.scentaware_8_smell_training)) {
            setTextData(selectedText, R.drawable.training_icon)
            iconNumber = 2
        }
        if (selectedText == getString(R.string.scentaware_16_smell_training)) {
            setTextData(selectedText, R.drawable.training_icon)
            iconNumber = 2
        }
        if (selectedText == getString(R.string.trace_aware)) {
            setTextData(selectedText, R.drawable.trace_aware)
            iconNumber = 3
        }
        if (selectedText == getString(R.string.audio_aware)) {
            setTextData(selectedText, R.drawable.audio_aware)
            iconNumber = 4
        }
        if (selectedText == getString(R.string.glance_aware)) {
            setTextData(selectedText, R.drawable.ic_face)
            iconNumber = 5
        }
    }

}