package com.sensifyawareapp.ui.scentawaretraining

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityRetrainingBinding
import com.sensifyawareapp.ml.barcode.QRCodeScanActivity
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class RetrainingActivity : BaseActivity() {
    private lateinit var binding: ActivityRetrainingBinding
    private lateinit var qrCodeOptions: List<String>
    private lateinit var qrCodeResult: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_retraining)

        qrCodeResult = intent.getStringExtra(AppConstant.BundleExtra.SCANNED_RESULT)!!

        val prefScanResult =
            prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.PREF_SCANNED_RESULT
            )

        val ob = JSONObject(prefScanResult!!)
        val scannedTubes = ob.getJSONArray("scannedTubes")

        val selectedKitSize = prefUtils.getIntData(
            this,
            AppConstant.SharedPreferences.SELECTED_KIT_SIZE
        )

        binding.totalQuestions = selectedKitSize
        binding.questionId = scannedTubes.length()

        val options = qrCodeResult.split("|")
        binding.answer = options[6]
        Log.e("TAG", "onCreate: 123 ${binding.answer}")


        var sliderValue = binding.slider.value
        if (sliderValue.toInt() == 0) {
            binding.smellIntensity = getString(R.string.no_smell_detected)
            for (i in 0 until binding.radioGrp.childCount) {
                val radioButton = binding.radioGrp.getChildAt(i) as RadioButton
                radioButton.isEnabled = false

                radioButton.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.radio_button_text_color_disabled
                    )
                )
                radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.radio_button_text_color_disabled))
            }

        }

        binding.slider.addOnChangeListener { slider, value, fromUser ->
            sliderValue = value.toString().toFloat()
            var radioButton = RadioButton(this)

            Log.e("TAG", "sliderValue: $sliderValue")
            for (i in 0 until binding.radioGrp.childCount) {
                radioButton = binding.radioGrp.getChildAt(i) as RadioButton
                radioButton.isEnabled = value.toInt() != 0
                Log.e("TAG", "onCreate: ${radioButton.isEnabled}")

                if (radioButton.isEnabled) {
                    radioButton.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.them_color
                        )
                    )
                    radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_light))
                } else {
                    radioButton.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.radio_button_text_color_disabled
                        )
                    )
                    radioButton.buttonTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.radio_button_text_color_disabled))
                }
            }
            when (value.toInt()) {
                0 -> {
                    binding.smellIntensity = getString(R.string.no_smell_detected)
                    binding.radioGrp.clearCheck()
                }

                1 -> {
                    binding.smellIntensity = getString(R.string.very_weak)
                }

                2 -> {
                    binding.smellIntensity = getString(R.string.weak)
                }

                3 -> {
                    binding.smellIntensity = getString(R.string.normal)
                }

                4 -> {
                    binding.smellIntensity = getString(R.string.strong)
                }

                5 -> {
                    binding.smellIntensity = getString(R.string.very_strong)
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            showCloseDialog()
        }


        binding.btnNext.setOnClickListener {

            if (sliderValue.toInt() != 0 && binding.radioGrp.checkedRadioButtonId == -1) {
                Toast.makeText(
                    this,
                    "Please select any option from the list before submitting.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            setData(sliderValue.toInt(), options)

            finish()
            if (scannedTubes.length() == selectedKitSize) {
                startActivity(
                    Intent(this, RetrainingCompletedActivity::class.java)
                        .putExtra("selectedKitSize", selectedKitSize)
                )
            } else {
                startActivity(Intent(this, QRCodeScanActivity::class.java))
            }
        }


    }

    private fun setData(sliderValue: Int, options: List<String>) {

        val temp_scan_to_scan_date = AppConstant.scanToScanDate

        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        AppConstant.scanToScanDate = Date()

        val startDate = temp_scan_to_scan_date?.let { it1 -> dateFormat.format(it1) }
        val endDate = AppConstant.scanToScanDate?.let { it1 -> dateFormat.format(it1) }

        val startTime = startDate?.let { it1 -> dateFormat.parse(it1) }
        val endTime = endDate?.let { it1 -> dateFormat.parse(it1) }

        var difference: Long? = 0
        if (startDate != null) {
            difference = (endTime?.time)?.minus(startTime?.time!!)
        }
        Log.e("TAG", "Difference: $difference")
        val difference_In_Seconds = ((difference?.div(1000)))

        val selectedOption: Int = binding.radioGrp.checkedRadioButtonId

        val radioButton = findViewById<RadioButton>(selectedOption)
        var selectedOpt = ""

        if (sliderValue == 0 && binding.radioGrp.checkedRadioButtonId == -1) {
            selectedOpt = null.toString()
        } else {
            selectedOpt = radioButton.text.toString()
        }

        val jsonObject = JSONObject()
        jsonObject.put("TubeId", options[0])
        jsonObject.put("TubeAnswer", options[6])
        jsonObject.put("ScantoScanTime", difference_In_Seconds)
//        jsonObject.put("AverageResponse", "")
        jsonObject.put("SmellIntensity", sliderValue)
        jsonObject.put("SmellPerception", selectedOpt)

        SensifyAwareApplication.addScentAwareTrainingObjectInJSON(jsonObject)
        Log.e(
            "TAG",
            "jsonObject: ${options[0]} // ${options[6]} // ${difference_In_Seconds}  // ${sliderValue} // ${selectedOpt}",
        )
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}