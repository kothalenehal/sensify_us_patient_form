package com.sensifyawareapp.ui.scentaware

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityQuestionAnswerBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.scentaware.model.TubeTestsRecord
import com.sensifyawareapp.utils.common.AppConstant
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.sensifyawareapp.ml.barcode.QRCodeScanActivity

class QuestionAnswerActivity : BaseActivity() {
    private lateinit var timer: CountDownTimer
    private lateinit var qrCodeOptions: List<String>
    private lateinit var binding: ActivityQuestionAnswerBinding

    private lateinit var qrCodeResult: String
    private var questionStartTime: Long = 0

    var scanToScanTimeEnd = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_answer)

        questionStartTime = System.currentTimeMillis()
        qrCodeResult = intent.getStringExtra(AppConstant.BundleExtra.SCANNED_RESULT)!!

        Log.e("TAG", "hereee Dta 1: ${SensifyAwareApplication.scentAwareTestData.IsStuffyNose}", )
        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedId ->
            binding.checked = checkedId[0]
            Log.e("TAG", "hereee onCreate: ${binding.checked}")
        }
        val withTimer =
            prefUtils.getBooleanData(this, AppConstant.SharedPreferences.WITH_TIMER)

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.START_TIME_SCENT,
            Calendar.getInstance().timeInMillis
        )
        /*if (withTimer) {
            timer = object : CountDownTimer(30000, 1000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millisUntilFinished: Long) {
                    binding.toolbar.tvTimer.text = "00:${millisUntilFinished / 1000}"
                    binding.timerProgress = (millisUntilFinished / 1000).toInt()
                }

                override fun onFinish() {
                }
            }
            timer.start()
        }*/
        val prefScanResult =
            prefUtils.getStringData(
                this,
                if (withTimer) AppConstant.SharedPreferences.PREF_SCANNED_RESULT_WITH_TIMER else AppConstant.SharedPreferences.PREF_SCANNED_RESULT
            )

        val ob = JSONObject(prefScanResult!!)
        val scannedTubes = ob.getJSONArray("scannedTubes")

        val selectedKitSize = prefUtils.getIntData(
            this,
            AppConstant.SharedPreferences.SELECTED_KIT_SIZE
        )

        binding.checkpointProgress =
            if (withTimer) selectedKitSize + scannedTubes.length() else scannedTubes.length()
        Log.e("Size ", selectedKitSize.toString())
        binding.totalQuestions = selectedKitSize
        binding.questionId = scannedTubes.length()

        qrCodeOptions = qrCodeResult.split("|")

        val list = ArrayList<String>()
        /*list.add(qrCodeOptions[1])
        list.add(qrCodeOptions[2])
        list.add(qrCodeOptions[3])
        list.add(qrCodeOptions[4])*/
        findAlternateOption(qrCodeOptions[1])?.let { list.add(it) }
        findAlternateOption(qrCodeOptions[2])?.let { list.add(it) }
        findAlternateOption(qrCodeOptions[3])?.let { list.add(it) }
        findAlternateOption(qrCodeOptions[4])?.let { list.add(it) }
        list.shuffle()

        binding.option1 = list[0]
        binding.option2 = list[1]
        binding.option3 = list[2]
        binding.option4 = list[3]
        binding.option5 = findAlternateOption(qrCodeOptions[5])

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }

        binding.btnCancel.setOnClickListener {
            showCloseDialog()
        }


        binding.btnSubmit.setOnClickListener {
            var answerSelect = String()
            val getCorrectAnswer = findAlternateOption(qrCodeOptions[6])
            var correctAnswers =
                prefUtils.getIntData(this, AppConstant.SharedPreferences.CORRECT_ANSWER_COUNT)
            var selectedAnswer =
                prefUtils.getStringData(this, AppConstant.SharedPreferences.PREF_SELECTED_ANSWERS)
            when (binding.chipGroup.checkedChipId) {
                binding.chip1.id -> {
                    if (/*findAlternateOption(qrCodeOptions[6]) qrCodeOptions[6]*/ getCorrectAnswer == binding.chip1.text) correctAnswers++
                    addValuesInOdor(
                        scannedTubes.length() - 1,
                        qrCodeOptions.indexOf(binding.chip1.text), withTimer
                    )
                    selectedAnswer += ",${binding.chip1.text}"
                    answerSelect = binding.chip1.text.toString()
                }

                binding.chip2.id -> {
                    if (getCorrectAnswer== binding.chip2.text) correctAnswers++
                    addValuesInOdor(
                        scannedTubes.length() - 1,
                        qrCodeOptions.indexOf(binding.chip2.text), withTimer
                    )
                    selectedAnswer += ",${binding.chip2.text}"
                    answerSelect = binding.chip2.text.toString()
                }

                binding.chip3.id -> {
                    if (getCorrectAnswer == binding.chip3.text) correctAnswers++
                    addValuesInOdor(
                        scannedTubes.length() - 1,
                        qrCodeOptions.indexOf(binding.chip3.text), withTimer
                    )
                    selectedAnswer += ",${binding.chip3.text}"
                    answerSelect = binding.chip3.text.toString()
                }

                binding.chip4.id -> {
                    if (getCorrectAnswer == binding.chip4.text) correctAnswers++
                    addValuesInOdor(
                        scannedTubes.length() - 1,
                        qrCodeOptions.indexOf(binding.chip4.text), withTimer
                    )
                    selectedAnswer += ",${binding.chip4.text}"
                    answerSelect = binding.chip4.text.toString()
                }

                binding.chip5.id -> {
                    if (getCorrectAnswer.equals(binding.chip5.text.toString(),true) ) correctAnswers++
                    addValuesInOdor(
                        scannedTubes.length() - 1,
                        qrCodeOptions.indexOf(binding.chip5.text), withTimer
                    )
                    selectedAnswer += ",${binding.chip5.text}"
                    answerSelect = binding.chip5.text.toString()
                }
            }
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.PREF_SELECTED_ANSWERS,
                selectedAnswer
            )
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
            Log.e("TAG", "hereee Difference: $difference")
            val difference_In_Seconds = ((difference?.div(1000)))

            var isAnswerCorrect = false
            if (answerSelect.equals(getCorrectAnswer, true) ) {
                isAnswerCorrect = true
            }
            Log.e(
                "TAG",
                "hereee selectedAnswer :  $answerSelect // ${qrCodeOptions[0]} // $getCorrectAnswer // $difference_In_Seconds  // $isAnswerCorrect "
            )
            val jsonObject = JSONObject()
            jsonObject.put("TubeId", qrCodeOptions[0])
            jsonObject.put("SelectedAnswer", answerSelect)
            jsonObject.put("ScantoScanTime", difference_In_Seconds)
            jsonObject.put("CorrectAnswer", getCorrectAnswer)
            jsonObject.put("IsAnswerCorrect", isAnswerCorrect)

            SensifyAwareApplication.tubeTestsList.add(
                TubeTestsRecord(
                    answerSelect,
                    getCorrectAnswer.toString(),
                    isAnswerCorrect,

                    )
            )
            SensifyAwareApplication.addScentAwareObjectInJSON(jsonObject)


            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.CORRECT_ANSWER_COUNT,
                correctAnswers
            )
            finish()

            if (scannedTubes.length() == selectedKitSize) {
                if (!withTimer) {
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.WITH_TIMER,
                        true
                    )
                    startActivity(
                        Intent(
                            this,
                            CheckPointActivity::class.java
                        ).putExtra("progress", scannedTubes.length())
                            .putExtra("completedCheckpoints", 1)
                    )
                } else {
                    /*if (selectedKitSize == 16) {
                        startActivity(
                            Intent(
                                this,
                                CheckPointActivity::class.java
                            ).putExtra("progress", scannedTubes.length() * 2)
                                .putExtra("checkPointOption", true)
                                .putExtra("completedCheckpoints", 2)
                        )
                    } else*/
                    startActivity(Intent(this, ScentAwareTestResultActivity::class.java))
                }

            } else {
                startActivity(Intent(this, QRCodeScanActivity::class.java))
            }
        }
    }

    /* private fun findAlternateOption(qrCodeOption: String): String? =
         SensifyAwareApplication.alternateDataList.find { it.first.equals(qrCodeOption, true) }
             ?.let { if (!it.second.isNullOrEmpty()) it.second else it.first }*/

    private fun findAlternateOption(qrCodeOption: String): String? {

        for (pair in SensifyAwareApplication.alternateDataList) {
            val tubeValue = pair.first
            val tubeAlternateValue = pair.second
            if (tubeValue?.lowercase().equals(qrCodeOption.lowercase())) {
                return if (!tubeAlternateValue.isNullOrEmpty()) {
                    tubeAlternateValue
                } else {
                    tubeValue
                }
            }
        }
        return qrCodeOption
    }

    /* private fun changeOption(option: String):String{
         // chives becomes onion and rum becomes alcohol
         if(option.equals("Chives" ,true)){
             return "Onion"
         }else if(option.equals("Rum" ,true)){
             return "Alcohol"
         } else {
             return option
         }

     }*/


    private fun addValuesInOdor(
        questionIndex: Int,
        optionsIndex: Int,
        withTimer: Boolean
    ) {
        val totalQuestionTime = System.currentTimeMillis() - questionStartTime
        Log.e("TAG", "hereee addValuesInOdor: $withTimer , $questionIndex , $optionsIndex")

        if (withTimer) {
            //update the index on which this tube was added at first scan
            for (i in 0..15) {//need this because, users can scan tubes in random sequence
                if (qrCodeOptions[0] == SensifyAwareApplication.scentAwareTestData.odors[i].TubeId) {
                    //TubeId is matched, update data to this Odor index
                    SensifyAwareApplication.scentAwareTestData.odors[i].under_stress_response =
                        optionsIndex
                    SensifyAwareApplication.scentAwareTestData.odors[i].response_time_round_two =
                        totalQuestionTime.toFloat() / 1000.toFloat()

                    if (optionsIndex == SensifyAwareApplication.scentAwareTestData.odors[i].response_in_round_one) {
                        SensifyAwareApplication.scentAwareTestData.corelationCount++

                    }
                    break
                }
            }
        } else {
            SensifyAwareApplication.scentAwareTestData.odors[questionIndex].TubeId =
                qrCodeOptions[0]
            SensifyAwareApplication.scentAwareTestData.odors[questionIndex].Odor =
                qrCodeOptions.indexOf(findAlternateOption(qrCodeOptions[6])/*qrCodeOptions[6]*/)
            SensifyAwareApplication.scentAwareTestData.odors[questionIndex].response_in_round_one =
                optionsIndex
            SensifyAwareApplication.scentAwareTestData.odors[questionIndex].response_time_round_one =
                totalQuestionTime.toFloat() / 1000.toFloat()
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }

}