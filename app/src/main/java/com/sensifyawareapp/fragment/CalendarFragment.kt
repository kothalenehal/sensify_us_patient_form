package com.sensifyawareapp.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.DialogCancelTestBinding
import com.sensifyawareapp.databinding.FragmentCalendarBinding
import com.sensifyawareapp.databinding.LayoutBottomViewBinding
import com.sensifyawareapp.room.Reminder
import com.sensifyawareapp.room.ReminderList
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.ui.audioaware.AudioAwareActivity
import com.sensifyawareapp.ui.calendar.AllEventActivity
import com.sensifyawareapp.ui.calendar.AllEventAdapter
import com.sensifyawareapp.ui.calendar.ReminderActivity
import com.sensifyawareapp.ui.glanceaware.GlanceAwareTutorialActivity
import com.sensifyawareapp.ui.notification.NotificationUtils
import com.sensifyawareapp.ui.scentaware.HealthsQuestionsActivity
import com.sensifyawareapp.ui.traceaware.TraceAwareTutorialActivity
import com.sensifyawareapp.ui.trackprogress.TrackProgressViewModel
import com.sensifyawareapp.utils.common.AppConstant
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarFragment : BaseFragment() {
    lateinit var binding: FragmentCalendarBinding
    private lateinit var allEventAdapter: AllEventAdapter
    val reminderList = ArrayList<ReminderList>()

//    lateinit var requestLauncher:ActivityResultLauncher<String>

    private var reminders: ArrayList<Reminder> = ArrayList()
    private lateinit var viewModel: TrackProgressViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this)[TrackProgressViewModel::class.java]

        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_MONTH, -1) // Subtract 1 day

        val calendarForMinimumDate = Calendar.getInstance()
        calendarForMinimumDate.time = calendar.time
        binding.calendarView.setMinimumDate(calendarForMinimumDate)

        binding.calendarView.setHeaderColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.them_color
            )
        )

        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar

                val dateFormat1 =
                    SimpleDateFormat(AppConstant.SharedPreferences.DateFormat3, Locale.US)
                val formattedDate = dateFormat1.format(clickedDayCalendar.time)
                if (reminderList.isNotEmpty()) {
                    val reminderFullList = ArrayList<ReminderList>()
                    binding.tvEvent.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    for (i in 0 until reminderList.size) {
                        Log.e(
                            "onDayClick",
                            "onDayClick: ${reminderList[i].id} // ${reminderList[i].testName}",
                        )
                        val dateFormat =
                            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat4, Locale.US)
                        val setTime = dateFormat1.format(Date(reminderList[i].dateTime))
                        val timeDAte = dateFormat.format(Date(reminderList[i].dateTime))
                        val timeDAte2 = dateFormat.format(Date(System.currentTimeMillis()))

                        try {
                            val myDate = dateFormat.parse(timeDAte)
                            val currentDate = dateFormat.parse(timeDAte2)
                            val calendar1 = Calendar.getInstance()
                            calendar1.time = myDate!!
                            val myTimeMillis = myDate.time
                            val currentTimeMillis = currentDate!!.time

                            val myTimeSeconds = myTimeMillis / 1000
                            val currentTimeSeconds = currentTimeMillis / 1000
                            if (myTimeSeconds > currentTimeSeconds) {
                                if (formattedDate == setTime) {

                                    reminderFullList.add(
                                        ReminderList(
                                            reminderList[i].id,
                                            reminderList[i].testName,
                                            reminderList[i].dateTime,
                                            reminderList[i].repeatTime,
                                            reminderList[i].selectedInterval,
                                            reminderList[i].number
                                        )
                                    )
                                }
                            }
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }

                        /*if (formattedDate == setTime) {
                            reminderFullList.add(
                                ReminderList(
                                    reminderList[i].id,
                                    reminderList[i].testName,
                                    reminderList[i].dateTime,
                                    reminderList[i].repeatTime
                                )
                            )
                        }*/
                    }

                    Log.e("TAG", "onDayClick 11: ${reminderFullList.size}")
                    binding.tvEvent.visibility = if (reminderFullList.isEmpty()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }

                    allEventAdapter = AllEventAdapter(reminderFullList, requireContext())
                    binding.recyclerView.adapter = allEventAdapter
                    eventCheck(reminderFullList)
                } else {
                    Log.e("TAG", "onDayClick: ${reminders.isEmpty()}")
                }
            }
        })

        binding.fabButton.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ReminderActivity::class.java
                )
            )
        }

        binding.txViewAll.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    AllEventActivity::class.java
                )
            )
        }

        setData()
        getReminderDate()

        binding.tvEvent.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        getReminderDate()
    }

    private fun getReminderDate() {
        viewModel.remindersList.observe(requireActivity()) {

            if (it.isNotEmpty()) {
                binding.tvEvent.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                reminders = it as ArrayList<Reminder>
                getDataWithDate(it)

            } else {
                binding.tvEvent.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
        viewModel.getReminderDate()


    }

    fun isDatePassed(givenDate: Date): Boolean {
        val currentDate = Date()
        Log.e("{TAG}", "isDatePassed: $currentDate")
        return givenDate.before(currentDate)
    }

    private fun getDataWithDate(reminders: ArrayList<Reminder>) {
        reminderList.clear()
        var notificationId = 0
        Log.e("TAG", "getReminderDate 11: ${reminders.size}")
        for (i in 0 until reminders.size) {
            notificationId = reminders[i].id!!.toInt()
            val dateFormat = SimpleDateFormat(AppConstant.SharedPreferences.DateFormat4, Locale.US)
            val givenDate = dateFormat.parse(reminders[i].endDate.toString())
            if (givenDate != null) {
                val isPassed = isDatePassed(givenDate)
                if (isPassed) {
                    val item = reminders[i]
                    viewModel.deleteReminderDate(item)
                    reminders.removeAt(i)
//                    allEventAdapter.notifyItemRemoved(i)

                    Log.e("TAG", "onItemClick: Notification ID ${notificationId}")

                    val formattedDate =
                        SimpleDateFormat(
                            "dd-LLL-yyyy",
                            Locale.US
                        ).format(Calendar.getInstance().time)
                    val simpleDateFormat =
                        SimpleDateFormat("hh:mm aaa", Locale.US)
                    var formattedTime = simpleDateFormat.format(Date())
                    formattedTime =
                        formattedTime.replace("am", "AM").replace("pm", "PM")
                    val endDateTimeString = "$formattedDate $formattedTime"
                    val notificationUtils = NotificationUtils(requireContext())
                    notificationUtils.scheduleStopNotification(
                        endDateTimeString,
                        notificationId
                    )
                    Log.e("TAG", "onItemClick: ${reminders.size}")
                    if (reminders.size == 0) {
                        binding.tvEvent.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                } else {
                    Log.e("TAG", "The given date is in the future. ")
                }
            } else {
                println("Invalid date format.")
            }
        }

        Log.e("TAG", "getReminderDate 22: ${reminders.size}")

        for (i in 0 until reminders.size) {

            val selectedTime = Calendar.getInstance()
            val selectedEndTime = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(AppConstant.SharedPreferences.DateFormat4, Locale.US)

            val date = dateFormat.parse(reminders[i].startDate.toString())
            if (date != null) {
                selectedTime.time = date
            }

            val date2 = dateFormat.parse(reminders[i].endDate.toString())
            if (date2 != null) {
                selectedEndTime.time = date2
            }
            Log.e("r", "123: ${reminders[i].repeatSelection}")
            when (reminders[i].repeatSelection) {
                0 -> {
                }

                1 -> {
                    Log.e("TAG", "getDataWithDate: ${reminders[i].selectedInterval} ")
                    Log.e(
                        "TAG",
                        "getDataWithDate:11 ${localizeText(reminders[i].customSelection.toString())} "
                    )
                    Log.e("TAG", "getDataWithDate:22 ${getString(R.string.day)} ")

                    while (selectedTime.timeInMillis <= selectedEndTime.timeInMillis) {
                        val dateAndTime = dateFormat.format(Date(selectedTime.timeInMillis))
                        reminderList.add(
                            ReminderList(
                                reminders[i].id!!.toInt(),
                                reminders[i].testName.toString(),
                                dateAndTime,
                                reminders[i].repeatTime.toString(),
                                reminders[i].selectedInterval.toString(),
                                reminders[i].intervalNumber!!
                            )
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
                                    dateAndTime,
                                    reminders[i].repeatTime.toString(),
                                    reminders[i].selectedInterval.toString(),
                                    reminders[i].intervalNumber!!
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
                                    dateAndTime,
                                    reminders[i].repeatTime.toString(),
                                    reminders[i].selectedInterval.toString(),
                                    reminders[i].intervalNumber!!
                                )
                            )
                        }

                        // Increment the calendar by one month for the next date
                        nextMonth.add(Calendar.MONTH, 1)
                    }
                }

                4 -> {

                    Log.e("TAG", "getDataWithDate: ${reminders[i].selectedInterval} ")
                    Log.e(
                        "TAG",
                        "getDataWithDate:11 ${localizeText(reminders[i].selectedInterval.toString())} "
                    )
                    Log.e("TAG", "getDataWithDate:22 ${reminders[i].customSelection.toString()} ")
                    when (localizeText(reminders[i].selectedInterval.toString())) {
                        getString(R.string.day) -> {

                            while (selectedTime.timeInMillis <= selectedEndTime.timeInMillis) {

                                val dateAndTime =
                                    dateFormat.format(Date(selectedTime.timeInMillis))

                                reminderList.add(
                                    ReminderList(
                                        reminders[i].id!!.toInt(),
                                        reminders[i].testName.toString(),
                                        dateAndTime,
                                        reminders[i].repeatTime.toString(),
                                        reminders[i].selectedInterval.toString(),
                                        reminders[i].intervalNumber!!
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

                                for (dayOfWeek in toArrayList(reminders[i].customDaysOfWeek)!!) {
                                    selectedTime.set(Calendar.DAY_OF_WEEK, dayOfWeek)
                                    val dateAndTime =
                                        dateFormat.format(Date(selectedTime.timeInMillis))


                                    reminderList.add(
                                        ReminderList(
                                            reminders[i].id!!.toInt(),
                                            reminders[i].testName.toString(),
                                            dateAndTime,
                                            reminders[i].repeatTime.toString(),
                                            reminders[i].selectedInterval.toString(),
                                            reminders[i].intervalNumber!!
                                        )
                                    )
                                }
                                selectedTime.add(
                                    Calendar.WEEK_OF_YEAR,
                                    reminders[i].intervalNumber!!
                                )
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
                                        dateAndTime,
                                        reminders[i].repeatTime.toString(),
                                        reminders[i].selectedInterval.toString(),
                                        reminders[i].intervalNumber!!
                                    )
                                )
                                // Increment the calendar by the specified interval in weeks
                                selectedTime.add(Calendar.MONTH, reminders[i].intervalNumber!!)
                            }
                        }
                    }
                }
            }

        }


        val reminderDataList = ArrayList<ReminderList>()
        val events: MutableList<EventDay> = ArrayList()

        for (i in 0 until reminderList.size) {
            val dateFormat1 = SimpleDateFormat(AppConstant.SharedPreferences.DateFormat3, Locale.US)
            val currentTime = dateFormat1.format(Date(Calendar.getInstance().timeInMillis))
            val dateFormat = SimpleDateFormat(AppConstant.SharedPreferences.DateFormat4, Locale.US)
            Log.e("TAG", "getDataWithDate: ${Date(reminderList[i].dateTime)}")
            val setTime = dateFormat1.format(Date(reminderList[i].dateTime))
            val timeDAte = dateFormat.format(Date(reminderList[i].dateTime))
            val timeDAte2 = dateFormat.format(Date(System.currentTimeMillis()))

            try {
                val myDate = dateFormat.parse(timeDAte)
                val currentDate = dateFormat.parse(timeDAte2)
                val calendar = Calendar.getInstance()
                calendar.time = myDate!!
                val myTimeMillis = myDate.time
                val currentTimeMillis = currentDate!!.time

                val myTimeSeconds = myTimeMillis / 1000
                val currentTimeSeconds = currentTimeMillis / 1000

                if (myTimeSeconds > currentTimeSeconds) {
                    if (currentTime == setTime) {
                        binding.tvEvent.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        reminderDataList.add(
                            ReminderList(
                                reminderList[i].id,
                                reminderList[i].testName,
                                reminderList[i].dateTime,
                                reminderList[i].repeatTime,
                                reminderList[i].selectedInterval,
                                reminderList[i].number
                            )
                        )
                    } else {
                        binding.tvEvent.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                    events.add(
                        EventDay(
                            calendar,
                            R.drawable.baseline_circle_24,
                            Color.parseColor("#228B22")
                        )
                    )
                } else if (myTimeSeconds < currentTimeSeconds) {
                } else {
                    events.add(
                        EventDay(
                            calendar,
                            R.drawable.baseline_circle_24,
                            Color.parseColor("#228B22")
                        )
                    )
                }


            } catch (e: ParseException) {
                e.printStackTrace()
            }


        }
        binding.calendarView.setEvents(events)

        allEventAdapter = AllEventAdapter(reminderDataList, requireContext())
        binding.recyclerView.adapter = allEventAdapter
        allEventAdapter.notifyDataSetChanged()
        eventCheck(reminderDataList)
    }

    private fun localizeText(text: String): String {
        val resourceId =
            requireContext().resources.getIdentifier(text, "string", requireContext().packageName)
        return if (resourceId != 0) {
            this.getString(resourceId)
        } else {
            text // Return original text if no localized version is found
        }

    }

    private fun eventCheck(
        reminderDataList: ArrayList<ReminderList>
    ) {

        allEventAdapter.setOnClickListener(object : AllEventAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                var notificationId = 0
                for (i in 0 until reminders.size) {

                    if (reminderDataList[position].id == reminders[i].id!!.toInt()) {
                        notificationId = reminders[i].id!!.toInt()
                        Log.e("TAG", "onItemClick NotificationID: ${reminders[i].id!!.toInt()}")
                        val bottomSheetDialog = BottomSheetDialog(requireContext())
                        val bottomBinding: LayoutBottomViewBinding =
                            DataBindingUtil.inflate(
                                LayoutInflater.from(requireContext()),
                                R.layout.layout_bottom_view,
                                null,
                                false
                            )
                        bottomSheetDialog.setContentView(bottomBinding.root)

                        bottomSheetDialog.show()
                        bottomBinding.time = reminderDataList[position].dateTime
                        bottomBinding.title = localizeText(reminderDataList[position].testName)

                        if (reminders[position].repeatTime == "every") {

                            bottomBinding.repeat =
                                requireContext().getString(
                                    R.string.every,
                                    reminders[position].intervalNumber.toString(),
                                    localizeText(reminders[position].selectedInterval.toString())
                                )

                        } else {
                            bottomBinding.repeat =
                                localizeText(reminders[position].repeatTime.toString())

                        }
                        when (reminders[i].iconNumber) {
                            1 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.scent_aware
                                    )
                                )

                            2 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.training_icon
                                    )
                                )

                            3 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.trace_aware
                                    )
                                )

                            4 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
                                        R.drawable.audio_aware
                                    )
                                )

                            5 ->
                                bottomBinding.ivIcon.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        requireContext(),
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
                                    requireContext(),
                                    ReminderActivity::class.java
                                ).putExtra("isEdit", true)
                                    .putExtra("position", i)
                            )
                            bottomSheetDialog.dismiss()
                        }

                        bottomBinding.ivDelete.setOnClickListener {
                            val dialog = Dialog(requireContext(), R.style.Theme_Dialog)
                            val cancelTestBinding = DialogCancelTestBinding.inflate(
                                LayoutInflater.from(requireContext()),
                                ConstraintLayout(requireContext()),
                                false
                            )
                            dialog.setContentView(cancelTestBinding.root)
                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            cancelTestBinding.tvMsg.text = getString(R.string.delete_reminder)

                            cancelTestBinding.btnNo.setOnClickListener {
                                dialog.dismiss()
                            }
                            cancelTestBinding.cardYes.setOnClickListener {
                                val item = reminders[i]
                                viewModel.deleteReminderDate(item)
                                reminders.removeAt(i)
                                allEventAdapter.notifyItemRemoved(i)

                                Log.e("TAG", "onItemClick: Notification ID $notificationId")
                                val formattedDate =
                                    SimpleDateFormat(
                                        "dd-LLL-yyyy",
                                        Locale.US
                                    ).format(Calendar.getInstance().time)
                                val simpleDateFormat =
                                    SimpleDateFormat("hh:mm aaa", Locale.US)
                                var formattedTime = simpleDateFormat.format(Date())
                                formattedTime =
                                    formattedTime.replace("am", "AM").replace("pm", "PM")
                                val endDateTimeString = "$formattedDate $formattedTime"
                                val notificationUtils = NotificationUtils(requireContext())
                                notificationUtils.scheduleStopNotification(
                                    endDateTimeString,
                                    notificationId
                                )
                                Log.e("TAG", "onItemClick: ${reminders.size}")
                                if (reminders.size == 0) {
                                    binding.tvEvent.visibility = View.VISIBLE
                                    binding.recyclerView.visibility = View.GONE
                                }

                                bottomSheetDialog.dismiss()
                                dialog.dismiss()

                                fragmentManager!!.beginTransaction()
                                    .detach(CalendarFragment())
                                    .attach(CalendarFragment())
                                    .commit()
                            }

                            dialog.show()

                        }

                        bottomBinding.btnStart.setOnClickListener {
                            when (reminders[i].testName) {
                                getString(R.string.scentaware_16_scent_test) -> {

                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        1
                                    )
                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                                        16
                                    )
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            HealthsQuestionsActivity::class.java
                                        )
                                    )
                                }

                                getString(R.string.scentaware_8_scent_test) -> {

                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        1
                                    )
                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                                        8
                                    )
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            HealthsQuestionsActivity::class.java
                                        )
                                    )


                                }

                                getString(R.string.scentaware_16_smell_training) -> {
                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        2
                                    )
                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                                        16
                                    )
                                    SensifyAwareApplication.initTestData()
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            TutorialActivity::class.java
                                        )
                                    )

                                }

                                getString(R.string.scentaware_8_smell_training) -> {

                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        2
                                    )
                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                                        8
                                    )
                                    SensifyAwareApplication.initTestData()
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            TutorialActivity::class.java
                                        )
                                    )

                                }

                                getString(R.string.trace_aware) -> {
                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        3
                                    )
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            TraceAwareTutorialActivity::class.java
                                        )
                                    )

                                }

                                getString(R.string.audio_aware) -> {
                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        4
                                    )

                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            AudioAwareActivity::class.java
                                        ).putExtra(
                                            "level",
                                            1
                                        )
                                    )


                                }

                                getString(R.string.glance_aware) -> {

                                    prefUtils.saveData(
                                        requireContext(),
                                        AppConstant.SharedPreferences.SELECTED_MENU,
                                        5
                                    )
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            GlanceAwareTutorialActivity::class.java
                                        )/*.putExtra(
                                "level",
                                1
                            )*/
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

    private fun toArrayList(value: String?): ArrayList<Int>? {
        return value?.split(",")?.mapNotNull { it.toIntOrNull() }?.toCollection(ArrayList())
    }

    private fun setData() {
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.SELECTED_TEXT,
            null
        )

        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.START_DATE,
            null
        )
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.END_DATE,
            null
        )
        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.REMINDER_TIME,
            null
        )

        prefUtils.saveData(
            requireContext(),
            AppConstant.SharedPreferences.REPEAT,
            null
        )

    }
}