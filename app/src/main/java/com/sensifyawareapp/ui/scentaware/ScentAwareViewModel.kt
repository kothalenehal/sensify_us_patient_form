package com.sensifyawareapp.ui.scentaware

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.room.AppDatabase
import com.sensifyawareapp.room.ScentAware
import com.sensifyawareapp.ui.BaseViewModel
import com.sensifyawareapp.utils.common.ApiConstants
import com.sensifyawareapp.utils.common.AppConstant
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.util.concurrent.TimeUnit


open class ScentAwareViewModel(application: Application) : BaseViewModel(application) {

    var scentAwareResponse: MutableLiveData<Pair<Boolean, String?>> = MutableLiveData()

    @OptIn(DelicateCoroutinesApi::class)
    fun uploadCSVData(totalQuestions: Int) {

        viewModelScope.launch {
            insertRoomRecord("pred", totalQuestions)
        }

        prefUtils.getStringData(getApplication(), AppConstant.SharedPreferences.AGE)
            ?.let {
                if (it.isNotEmpty()) {
                    SensifyAwareApplication.scentAwareTestData.age = it.toDouble()
                }
                Log.e("Age ", it)
            }

        /* SensifyAwareApplication.scentAwareTestData.age =
             prefUtils.getStringData(getApplication(), AppConstant.SharedPreferences.AGE)?.toDouble()
                 ?: 0.0*/
        SensifyAwareApplication.scentAwareTestData.Gender = if (prefUtils.getStringData(
                getApplication(),
                AppConstant.SharedPreferences.GENDER
            )?.lowercase() == "male"
        ) 0 else 1

        val jsonObject = JSONObject()
        jsonObject.put("Age", SensifyAwareApplication.scentAwareTestData.age)
        jsonObject.put("Gender", SensifyAwareApplication.scentAwareTestData.Gender)
        if (prefUtils.getBooleanData(getApplication(), AppConstant.SharedPreferences.IsStuffyNose)){
            jsonObject.put(
                "StuffyNose",
               1
            )
        } else{
            jsonObject.put(
                "StuffyNose",
                0
            )
        }

        jsonObject.put(
            "LossOfSmell",
            SensifyAwareApplication.scentAwareTestData.LossOfSmell
        )
        jsonObject.put(
            "LossOfMemory",
            SensifyAwareApplication.scentAwareTestData.LossOfMemory
        )
        jsonObject.put(
            "Medication",
           0
        )
        jsonObject.put(
            "TotalTime_mins",
            SensifyAwareApplication.scentAwareTestData.TotalTime_mins.toDouble()
        )
        jsonObject.put(
            "Smell_Intensity_Score",
            SensifyAwareApplication.scentAwareTestData.Smell_Intensity_Score.toDouble()
        )
        jsonObject.put(
            "time_for_question_one",
            SensifyAwareApplication.scentAwareTestData.time_for_question_one.toDouble()
        )
        jsonObject.put(
            "time_for_question_two",
            SensifyAwareApplication.scentAwareTestData.time_for_question_two.toDouble()
        )
        jsonObject.put(
            "smell_discrimination_score",
            SensifyAwareApplication.scentAwareTestData.smell_discrimination_score.toDouble()
        )
        jsonObject.put(
            "time_for_question_one.1",
            SensifyAwareApplication.scentAwareTestData.time_for_question_one1.toDouble()
        )
        jsonObject.put(
            "time_for_question_two.1",
            SensifyAwareApplication.scentAwareTestData.time_for_question_two1.toDouble()
        )
        jsonObject.put(
            "Odor_Identification(%)",
            SensifyAwareApplication.scentAwareTestData.Odor_Identification.toDouble()
        )
        jsonObject.put(
            "test-retest_corelation_score(%)",
            (SensifyAwareApplication.scentAwareTestData.corelationCount / prefUtils.getIntData(
                getApplication(),
                AppConstant.SharedPreferences.SELECTED_KIT_SIZE
            )) * 100
        )
        jsonObject.put(
            "Odor_1",
            SensifyAwareApplication.scentAwareTestData.odors[0].Odor
        )
        jsonObject.put(
            "response_in_round_one?",
            SensifyAwareApplication.scentAwareTestData.odors[0].response_in_round_one
        )
        jsonObject.put(
            "under_stress_response",
            SensifyAwareApplication.scentAwareTestData.odors[0].under_stress_response
        )
        jsonObject.put(
            "response_time_round_one",
            SensifyAwareApplication.scentAwareTestData.odors[0].response_time_round_one.toDouble()
        )
        jsonObject.put(
            "response_time_round_two(Under_stress)",
            SensifyAwareApplication.scentAwareTestData.odors[0].response_time_round_two.toDouble()
        )

        for (i in 1..15) {
            jsonObject.put(
                "Odor_${i + 1}",
                SensifyAwareApplication.scentAwareTestData.odors[i].Odor
            )
            jsonObject.put(
                "response_in_round_one?.$i",
                SensifyAwareApplication.scentAwareTestData.odors[i].response_in_round_one
            )
            jsonObject.put(
                "under_stress_response.$i",
                SensifyAwareApplication.scentAwareTestData.odors[i].under_stress_response
            )
            jsonObject.put(
                "response_time_round_one.$i",
                SensifyAwareApplication.scentAwareTestData.odors[i].response_time_round_one.toDouble()
            )
            jsonObject.put(
                "response_time_round_two(Under_stress).$i",
                SensifyAwareApplication.scentAwareTestData.odors[i].response_time_round_two.toDouble()
            )
        }

        /* viewModelScope.launch {
            insertRoomRecord("", totalQuestions)
        }*/

        loading.value = true
        Thread {
            try {
                Log.e("TAG", "uploadCSVData: $jsonObject")
                val client = OkHttpClient().newBuilder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build()
//                Log.e("TAG", "uploadCSVData: Thread Call ${client.cache}", )
                val request: Request = Request.Builder()
                    //.url("${ApiConstants.BASE_URL_OLD}${ApiConstants.PROD_OLD}/infer-condition")
                    .url("${ApiConstants.BASE_URL}${ApiConstants.PROD}/infer-condition")
                    .method("POST", jsonObject.toString().toRequestBody())
                    .build()
                val response: Response = client.newCall(request).execute()
                val msg = response.message
                val isSuccess = response.isSuccessful
                Log.e("---message", "apiCall: $msg")
                Log.e("---isSuccessful", "apiCall: $isSuccess")
                val res = response.body?.string() ?: msg
                Log.e("---body", "apiCall: $res")
//
                viewModelScope.launch {
                    delay(1000)     // 1 sec delay
                    withContext(Dispatchers.Main) {
                        scentAwareResponse.value = Pair(isSuccess, res)
                        Log.e("TAG", "scentAwareResponse.: ${scentAwareResponse.value}")
                    }

//                    insertRoomRecord(res, totalQuestions)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                GlobalScope.launch {
                    delay(1000)     // 1 sec delay
                    withContext(Dispatchers.Main) {
                        scentAwareResponse.value = Pair(false, "Timeout")
                        Log.e("TAG", "scentAwareResponse.value: ${scentAwareResponse.value}")
                    }
                }
                Log.e("TAG 123", "Exception: ${e.message}")
            }
        }.start()
    }

    private suspend fun insertRoomRecord(
        res: String, totalQuestions: Int
    ) {
        try {
            val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
//            val jb = JSONObject(res)
            val scentAware = ScentAware(
                null,
                prefUtils.getLongData(getApplication(), AppConstant.SharedPreferences.START_TIME),
                prefUtils.getLongData(getApplication(), AppConstant.SharedPreferences.END_TIME),
                prefUtils.getIntData(
                    getApplication(),
                    AppConstant.SharedPreferences.CORRECT_ANSWER_COUNT
                ),
                /*if (jb.has("pred")) jb.getString("pred") else*/ "pred",
                prefUtils.getIntData(
                    getApplication(),
                    AppConstant.SharedPreferences.SELECTED_KIT_SIZE
                ), totalQuestions
            )
            Log.e("TAG", "insertRoomRecord scentAware: $scentAware")
            dbHelper.awareDao().insertScentAware(scentAware)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*fun uploadData(date: String, isSkipRetest: Boolean) {
        val scannedResult = prefUtils.getStringData(
            getApplication(),
            AppConstant.SharedPreferences.PREF_SCANNED_RESULT
        )
        val scannedResultWithTimer = prefUtils.getStringData(
            getApplication(),
            AppConstant.SharedPreferences.PREF_SCANNED_RESULT_WITH_TIMER
        )
        var selectedAnswers = prefUtils.getStringData(
            getApplication(),
            AppConstant.SharedPreferences.PREF_SELECTED_ANSWERS
        )
        val selectedKitSize = prefUtils.getIntData(
            getApplication(),
            AppConstant.SharedPreferences.SELECTED_KIT_SIZE
        )

        val difference = (prefUtils.getLongData(
            getApplication(),
            AppConstant.SharedPreferences.END_TIME
        ) - prefUtils.getLongData(
            getApplication(),
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
//{
//  "userId": 0,
//  "testType": "string",
//  "sessionId": "string",
//  "date": "string",
//  "averageResponse": 0,
//  "isStuffyNose": true,
//  "isMedication": true,
//  "tubeInfo": [
//    {
//      "tubeId": "string",
//      "selectedAnswer": "string",
//      "scanToScanTime": 0,
//      "correctAnswer": "string",
//      "isAnswerCorrect": true
//    }
//  ]
//}


        val dateFormat =
            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())
//        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        Log.e("TAG", "uploadData: $selectedAnswers")

        val scentAwareTestResult = JSONObject()
//        scentAwareTestResult.put("TubeId", idsString)

        scentAwareTestResult.put(
            "IsMedication",
            SensifyAwareApplication.scentAwareTestData.Medication
        )
        scentAwareTestResult.put("TubeInfo", SensifyAwareApplication.jsonArrayScent)
        scentAwareTestResult.put("TestType", if (selectedKitSize == 8) "eight" else "sixteen")
        scentAwareTestResult.put("SessionId", UUID.randomUUID().toString())
        Log.e("TAG", "IS_MODERATOR: ${prefUtils.getBooleanData(
            getApplication(),
            AppConstant.SharedPreferences.IS_MODERATOR
        )}", )

        if (prefUtils.getBooleanData(getApplication(), AppConstant.SharedPreferences.IS_MODERATOR)){
            scentAwareTestResult.put("SiteId", prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.SITE_ID))
            scentAwareTestResult.put("PatientId", prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.PATIENT_ID))
            scentAwareTestResult.put("StudyNumber", prefUtils.getStringData(getApplication(), AppConstant.SharedPreferences.STUDY_NUMBER))
        }

//        scentAwareTestResult.put("SelectedAnswer", selectedAnswers)

        scentAwareTestResult.put(
            "AverageResponse",
            ((min * 60) + sec).toFloat() / (selectedKitSize * 2).toFloat()
        )
        scentAwareTestResult.put("Date", dateFormat.format(Date()))
        scentAwareTestResult.put(
            "UserId",
            prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
        )
        scentAwareTestResult.put(
            "IsStuffyNose",
            SensifyAwareApplication.scentAwareTestData.StuffyNose == 1
        )
        Log.e("TAG", " 123 uploadData :  $scentAwareTestResult")
        val query = JSONObject()
        query.put("ScentAwareTestResult", scentAwareTestResult)
        query.put("Url", "/set-tube-information")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveTubeInformation(body).subscribe({
            Log.e("TAG", "information: $it", )
        }, {})
    }*/
}

