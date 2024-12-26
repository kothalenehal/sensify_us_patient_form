package com.sensifyawareapp.ui.scentaware

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityScentAwareTestResultBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.ui.abouttests.AboutSmellLossActivity
import com.sensifyawareapp.ui.calendar.ReminderActivity
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil


class ScentAwareTestResultActivity : BaseActivity() {
    private lateinit var binding: ActivityScentAwareTestResultBinding
    private var isSkipRetest = false
    private lateinit var viewModel: ScentAwareViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scent_aware_test_result)
        viewModel = ViewModelProviders.of(this)[ScentAwareViewModel::class.java]

        isSkipRetest = intent.getBooleanExtra("isSkipRetest", false)

        binding.tvLearn.paintFlags = binding.tvLearn.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.END_TIME,
            Calendar.getInstance().timeInMillis
        )
        val correctAnswers =
            prefUtils.getIntData(this, AppConstant.SharedPreferences.CORRECT_ANSWER_COUNT)

        val totalQuestions =
            if (prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_KIT_SIZE) == 8) {
                if (isSkipRetest)
                    8 else 16
            } else {
                if (isSkipRetest)
                    16 else 32
            }
        val selectedKit =
            prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_KIT_SIZE)

        //(total result * 4) /total question
        val ansStars = ((correctAnswers * 4.0) / totalQuestions)
        var myInteger = ceil(ansStars)
        if (myInteger.toInt() == 0) myInteger = 1.0
//        binding.correctAnswers = myInteger.toInt()
        binding.correctAnswers = correctAnswers
        binding.totalQuestions = totalQuestions
        Log.e("Ans ", "$myInteger // $ansStars // $totalQuestions // $correctAnswers")
        setTestData(correctAnswers, totalQuestions, myInteger.toInt(), selectedKit)


        binding.btnClose.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        binding.btnTryRetraining.setOnClickListener {
            Log.e("TAG", "onCreate: Call Button")
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        Log.e("TAG", "jsonArrayScent: ${SensifyAwareApplication.jsonArrayScent.length()}")

        uploadData(
            isSkipRetest, myInteger.toInt()
        )
        binding.btnReminder.setOnClickListener {
            setData()

            startActivity(
                Intent(
                    this,
                    ReminderActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        binding.tvLearn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AboutSmellLossActivity::class.java
                )
            )
        }

    }

    private fun setTestData(
        correctAnswers: Int,
        totalQuestions: Int,
        ansStars: Int,
        selectedKit: Int
    ) {
        val difference = (prefUtils.getLongData(
            this,
            AppConstant.SharedPreferences.END_TIME
        ) - prefUtils.getLongData(
            this,
            AppConstant.SharedPreferences.START_TIME
        ))

        val days = (difference / (1000 * 60 * 60 * 24))
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)
        val sec =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours - 1000 * 60 * min) / 1000

        SensifyAwareApplication.scentAwareTestData.TotalTime_mins =
            if (sec >= 30) (min + 1).toFloat() else min.toFloat()

        SensifyAwareApplication.scentAwareTestData.Odor_Identification =
            (correctAnswers.toFloat() / totalQuestions.toFloat()) * 100.toFloat()

        Log.d("---", "setTestData: ${SensifyAwareApplication.scentAwareTestData}")

        showLoader()
        viewModel.uploadCSVData(totalQuestions)
    }

    fun uploadData(isSkipRetest: Boolean, ansTest: Int) {
        val scannedResult = prefUtils.getStringData(
            application,
            AppConstant.SharedPreferences.PREF_SCANNED_RESULT
        )
        val scannedResultWithTimer = prefUtils.getStringData(
            application,
            AppConstant.SharedPreferences.PREF_SCANNED_RESULT_WITH_TIMER
        )
        var selectedAnswers = prefUtils.getStringData(
            application,
            AppConstant.SharedPreferences.PREF_SELECTED_ANSWERS
        )
        val selectedKitSize = prefUtils.getIntData(
            application,
            AppConstant.SharedPreferences.SELECTED_KIT_SIZE
        )

        val difference = (prefUtils.getLongData(
            application,
            AppConstant.SharedPreferences.END_TIME
        ) - prefUtils.getLongData(
            application,
            AppConstant.SharedPreferences.START_TIME
        ))

        val days = (difference / (1000 * 60 * 60 * 24))
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)
        val sec =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours - 1000 * 60 * min) / 1000


        //get ids of scanned tubes
        var idsString = ""
        val scannedTubes = JSONObject(scannedResult!!).getJSONArray("scannedTubes")
        for (i in 0 until scannedTubes.length()) {
            val item = scannedTubes.getString(i)
            idsString += ",${item.split("|")[0]}"
        }
        if (!isSkipRetest) {
            val scannedTubes2 = JSONObject(scannedResultWithTimer!!).getJSONArray("scannedTubes")
            for (i in 0 until scannedTubes2.length()) {
                val item = scannedTubes2.getString(i)
                idsString += ",${item.split("|")[0]}"
            }
        }

        idsString = idsString.substring(1, idsString.length)

        selectedAnswers = selectedAnswers!!.substring(1, selectedAnswers.length)//remove first comma

        val dateFormat =
            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())

        Log.e("TAG", "uploadData: $selectedAnswers")
        Log.e("TAG", "Medication: ${SensifyAwareApplication.scentAwareTestData.Medication}")


        val scentAwareTestResult = JSONObject()
        scentAwareTestResult.put(
            "IsMedication",
            SensifyAwareApplication.scentAwareTestData.Medication
        )
        scentAwareTestResult.put("TubeInfo", SensifyAwareApplication.jsonArrayScent)
        scentAwareTestResult.put("TestType", if (selectedKitSize == 8) "eight" else "sixteen")
        scentAwareTestResult.put("SessionId", UUID.randomUUID().toString())


        if (prefUtils.getBooleanData(this, AppConstant.SharedPreferences.IS_MODERATOR)) {
//            scentAwareTestResult.put("SiteId", prefUtils.getIntData(this, AppConstant.SharedPreferences.SITE_ID))
            scentAwareTestResult.put(
                "PatientId",
                prefUtils.getStringData(this, AppConstant.SharedPreferences.PATIENT_ID)

            )
            scentAwareTestResult.put(
                "StudyNumber",
                prefUtils.getStringData(this, AppConstant.SharedPreferences.STUDY_ID)
            )
        }

        Log.e("TAG", "difference: $isSkipRetest  // $difference")
        val kitSizeValue: Double = if (selectedKitSize == 8) {
            if (isSkipRetest) {
                ((min * 60) + sec).toDouble() / (selectedKitSize).toDouble()
            } else {
                ((min * 60) + sec).toDouble() / (selectedKitSize * 2).toDouble()
            }
        } else {
            if (isSkipRetest) {
                ((min * 60) + sec).toDouble() / (selectedKitSize).toDouble()
            } else {
                ((min * 60) + sec).toDouble() / (selectedKitSize * 2).toDouble()
            }
        }

        scentAwareTestResult.put(
            "AverageResponse",
            kitSizeValue
        )
        scentAwareTestResult.put("Date", dateFormat.format(Date()))
        scentAwareTestResult.put(
            "UserId",
            prefUtils.getIntData(this, AppConstant.SharedPreferences.USER_ID)
        )

        Log.e(
            "TAG",
            "Dta 1: ${prefUtils.getBooleanData(this, AppConstant.SharedPreferences.IsStuffyNose)}",
        )

        /* if (SensifyAwareApplication.scentAwareTestData.IsStuffyNose){
             scentAwareTestResult.put(
                 "IsStuffyNose",
                true
             )
         } else{
             scentAwareTestResult.put(
                 "IsStuffyNose",
                 false
             )
         }*/
        scentAwareTestResult.put(
            "IsStuffyNose",
            prefUtils.getBooleanData(this, AppConstant.SharedPreferences.IsStuffyNose)
        )


        Log.e("TAG", " 123 uploadData :  $scentAwareTestResult")
        val query = JSONObject()
        query.put("ScentAwareTestResult", scentAwareTestResult)
        query.put("Url", "/set-tube-information")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveTubeInformation(body).subscribe({
            Log.e("TAG", "information: $it")

            binding.testResult = when (ansTest) {
                4 ->
                    getString(R.string.smell_normal)

                3 ->
                    getString(R.string.smell_mild)

                2 ->
                    getString(R.string.smell_moderate)

                else ->
                    getString(R.string.smell_anosmia)
            }
            hideLoader()
            val adapter = ScentAnswerAdapter(it.data.tubeTest, isSkipRetest)
            binding.recyclerView.adapter = adapter
        }, {
            handleError(it)
        })
    }

    private fun setData() {
        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.SELECTED_TEXT,
            null
        )

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.START_DATE,
            null
        )
        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.END_DATE,
            null
        )
        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.REMINDER_TIME,
            null
        )

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.REPEAT,
            null
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }
}