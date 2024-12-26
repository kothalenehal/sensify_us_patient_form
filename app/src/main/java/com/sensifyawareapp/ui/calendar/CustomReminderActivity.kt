package com.sensifyawareapp.ui.calendar

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.widget.AppCompatToggleButton
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityCustomReminderBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.Calendar


class CustomReminderActivity : BaseActivity() {
    lateinit var binding: ActivityCustomReminderBinding
    var repeat_selection = 0
    var number = ""
    var isEdit = false
    var position = 0

    var intervalNumber = 0
    var customSelection = String()
    var customDaysOfWeek = String()

    lateinit var toggleButtonIds: Array<Int>

    private val selectedDays = mutableSetOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isEdit = intent.getBooleanExtra("isEdit", false)
        position = intent.getIntExtra("position", 0)
        Log.e("TAG", "position: ${position}")
        val list = ArrayList<String>()
        list.add(getString(R.string.day))
        list.add(getString(R.string.week))
        list.add(getString(R.string.month))
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item, list)
        binding.autoCompleteTextView.setAdapter(arrayAdapter)

        binding.title = "1 ${getString(R.string.day)}"

        toggleButtonIds = arrayOf(
            R.id.tv_sunday,
            R.id.tv_monday,
            R.id.tv_tuesday,
            R.id.tv_wednesday,
            R.id.tv_thursday,
            R.id.tv_friday,
            R.id.tv_saturday
        )

        for (id in toggleButtonIds) {
            val toggleButton = findViewById<AppCompatToggleButton>(id)
            toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
                val day = buttonView.text.toString()
                if (isChecked) {
                    selectedDays.add(day)
                } else {
                    selectedDays.remove(day)
                }
                updateLiveDataText()
            }
        }
        if (isEdit) isEditData()

        binding.autoCompleteTextView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                binding.isWeek =
                    binding.autoCompleteTextView.text.toString() == getString(R.string.week)
                updateLiveDataText()
            }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnDone.setOnClickListener {
            if (binding.tvNumber.text?.isEmpty() == true) {
                Toast.makeText(this, "Please Enter Number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setUpNotification()
        }

        binding.tvNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateLiveDataText()
            }
        })
    }

    fun isEditData() {
//        intervalNumber = intent.getIntExtra("intervalNumber", 0)
//        customSelection = intent.getStringExtra("customSelection")!!
//        customDaysOfWeek = intent.getStringExtra("customDaysOfWeek")!!

//        binding.tvNumber.setText(intervalNumber.toString())
//        binding.autoCompleteTextView.setText(customSelection)
//        binding.isWeek = customSelection == getString(R.string.week)

//        Log.e("TAG", "isEditData: $intervalNumber // $customSelection // $customSelection")
//        binding.title = "$intervalNumber $customSelection"
    }

    private var repetition: String = "day"
    private fun updateLiveDataText() {
        val interval = binding.tvNumber.text.toString()
        val intervalValue = if (interval.isNotEmpty()) interval.toInt() else 0
        val selectedInterval = binding.autoCompleteTextView.text.toString()

        if (intervalValue > 0) {

            repetition =
                when (selectedInterval) {
                    getString(R.string.day) -> "day"

                    getString(R.string.week) -> {
                        val selectedDaysText = selectedDays.joinToString(", ")
                        // Update ToggleButton labels with full day names
                        val fullDayNames = mapOf(
                            "S" to "Sunday",
                            "M" to "Monday",
                            "T" to "Tuesday",
                            "W" to "Wednesday",
                            "R" to "Thursday",
                            "F" to "Friday",
                            "A" to "Saturday"
                        )

                        for (id in toggleButtonIds) {

                            val toggleButton = findViewById<AppCompatToggleButton>(id)

                            val day = toggleButton.text
                            val fullDayName = fullDayNames[day]

                            if (selectedDays.contains(day)) {
//                            toggleButton.text = fullDayNames[day]
                                Log.e("TAG", "updateLiveDataText: ${fullDayNames[day]}")
                            }
                        }
//                        "weeks on ${selectedDays.joinToString(", ")}"
                        "week"
                    }

                    getString(R.string.month) -> "month"
                    else -> ""
                }


            val liveDataText = "$intervalValue ${localizeText(repetition)}"
            binding.title = liveDataText
        } else {
            binding.title = ""
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

    private fun setUpNotification() {

        val intent =
            Intent(this, ReminderActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("isEdit", isEdit)
        intent.putExtra("Number", binding.tvNumber.text.toString().toInt())
        intent.putExtra("selectedInterval", repetition)
        intent.putExtra("isCustom", true)
        intent.putExtra("position", position)

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.REPEAT,
            "every"
        )

        when (binding.autoCompleteTextView.text.toString()) {
            getString(R.string.day) -> {
                repeat_selection = 1
                intent.putExtra("selectedItem", getString(R.string.day))
            }

            getString(R.string.week) -> {

                repeat_selection = 2
                val customDaysOfWeek = ArrayList<Int>()

                if (binding.tvSunday.isChecked) {
                    customDaysOfWeek.add(Calendar.SUNDAY)
                }


                if (binding.tvMonday.isChecked) {
                    customDaysOfWeek.add(Calendar.MONDAY)
                }


                if (binding.tvTuesday.isChecked) {
                    customDaysOfWeek.add(Calendar.TUESDAY)
                }


                if (binding.tvWednesday.isChecked) {
                    customDaysOfWeek.add(Calendar.WEDNESDAY)
                }


                if (binding.tvThursday.isChecked) {
                    customDaysOfWeek.add(Calendar.THURSDAY)
                }


                if (binding.tvFriday.isChecked) {
                    customDaysOfWeek.add(Calendar.FRIDAY)
                }


                if (binding.tvSaturday.isChecked) {
                    customDaysOfWeek.add(Calendar.SATURDAY)
                }


//                val object1 = intent?.getIntegerArrayListExtra("customDaysOfWeek")
                intent.putIntegerArrayListExtra("customDaysOfWeek", customDaysOfWeek)
                intent.putExtra("selectedItem", getString(R.string.week))

            }

            getString(R.string.month) -> {
                repeat_selection = 3
                intent.putExtra("selectedItem", getString(R.string.month))

            }
        }
        startActivity(intent)
        finish()

    }
}