package com.sensifyawareapp.ui.traceaware

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.room.AppDatabase
import com.sensifyawareapp.room.TraceAware
import com.sensifyawareapp.ui.BaseViewModel
import com.sensifyawareapp.utils.common.AppConstant
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


open class TraceAwareViewModel(application: Application) : BaseViewModel(application) {

    val showResultScreen: MutableLiveData<Boolean> = MutableLiveData()

    //    var similarity_score = 0.0f
    var pathwithFilename = String()
    var fingerTouchCount = String()
    var gridVisibility = String()
    var drawingTimeStart: Long = 0
    var drawingTimeEnd: Long = 0
    fun uploadImage(
        file: File,
        fileName: UUID,
        referenceImageId: Int,
        drawingTimeStart: Long,
        drawingTimeEnd: Long,
        fingerTouchCount: Int,
        gridVisibility: String,
        isRecalling: Boolean
    ) {
        val query = JSONObject()
        query.put("type", "dsst")
        query.put("Url", "/upload-presigned-url")
        query.put("filename", "${fileName}.png")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        apiManager.uploadPresignedImage(
            body,
        ).subscribe({
            if (it.isSuccess) {
//                getAccuraryData(referenceImageId, fileName, isRecalling)
                pathwithFilename = it.data!!.pathwithFilename.toString()
                this.drawingTimeStart = drawingTimeStart
                this.drawingTimeEnd = drawingTimeEnd
                this.fingerTouchCount = fingerTouchCount.toString()
                this.gridVisibility = gridVisibility

                /* val dateFormat =
                     SimpleDateFormat("MM-dd-yyyy'T'hh:mm:ss", Locale.getDefault())
                 val jsonObject = JSONObject()
                 jsonObject.put("ReferenceImage", referenceImageId.toString())
                 jsonObject.put("ReferenceImageId", referenceImageId)
                 jsonObject.put("DrawnImage", it.data!!.pathwithFilename)
                 jsonObject.put("DrawingTimeStart", dateFormat.format(Date(drawingTimeStart)))
                 jsonObject.put("DrawingTimeEnd", dateFormat.format(Date(drawingTimeEnd)))
                 jsonObject.put("FingerTouchCount", fingerTouchCount.toString())
                 jsonObject.put("GridVisibility", gridVisibility)
                 Log.e("TAG ", "uploadImage: Call $similarity_score")
                 SensifyAwareApplication.addTraceAwareObjectInJSON(jsonObject)*/

                //call api to upload image on presigned url
                Thread {
                    try {
                        if (file.exists()) {
                            val client = OkHttpClient().newBuilder()
                                .connectTimeout(20, TimeUnit.SECONDS)
                                .writeTimeout(20, TimeUnit.SECONDS)
                                .readTimeout(40, TimeUnit.SECONDS)
                                .build()
                            val request: Request = Request.Builder()
                                .url(it.data!!.presignedurl!!)
                                .method("PUT", file.asRequestBody(null))
                                .build()
                            val response: Response = client.newCall(request).execute()
                            getAccuraryData(referenceImageId, fileName, isRecalling)
                            Log.e("---file upload message", "apiCall: ${response.message}")
                            Log.e("---file upload", "apiCall: ${response.isSuccessful}")
                        } else showResultScreen.value = true
                    } catch (e: Exception) {
                        showResultScreen.value = true
                        e.printStackTrace()
                    }
                }.start()
            } else showResultScreen.value = true
        }, {
            it.printStackTrace()
            showResultScreen.value = true
        })
    }

    @SuppressLint("CheckResult")
    private fun getAccuraryData(
        referenceImageId: Int,
        fileName: UUID,
        isRecalling: Boolean
    ) {
        //dsst
        val queryDSST = JSONObject()
        queryDSST.put("org_img_path", "$referenceImageId.jpg")
        queryDSST.put("handwritten_img_path", "$fileName.png")

        val bodyDSST: RequestBody = queryDSST.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.dsst(bodyDSST).subscribe({
            Log.e("TAG ", " 11 getAccuraryData: ${it.similarity_score}'")

            val similarity_score_per = (it.similarity_score)!! * 100
            Log.e("T OLD Scale ::", " $similarity_score_per")
            val scale_tracing_accuracy_per = ((similarity_score_per - 30) / 3) * 5
            Log.e("T New Scale ::", " $scale_tracing_accuracy_per")
            val tracing_per = if (scale_tracing_accuracy_per < 0) {
                0
            } else if (scale_tracing_accuracy_per > 100) {
                100
            } else {
                scale_tracing_accuracy_per
            }
            Log.e("Tracing Result ::", " $tracing_per")
            val similarity_score: Float = tracing_per.toFloat() / 100
            Log.e("similarity_score ::", " $similarity_score")
// ReferenceImageId, DrawnImage, RefernceImage, DrawingTimeStart, DrawingTimeEnd, FingerTouchCount, GridVisibility, IsRecallImage, Accuracy, RecallAccuracy
            try {
                if (isRecalling) SensifyAwareApplication.recallDsstScore += similarity_score
                else SensifyAwareApplication.dsstScore += similarity_score
                showResultScreen.value = true
                Log.e("TAG ", "recallDsstScore Total : ${SensifyAwareApplication.recallDsstScore}")
//                similarity_score = it.similarity_score!!

                Log.e("TAG ", "getAccuraryData: $referenceImageId")

                val dateFormat =
                    SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())
//                dateFormat.timeZone = TimeZone.getTimeZone("UTC")

                val jsonObject = JSONObject()
                jsonObject.put("RefernceImage", referenceImageId.toString())
                jsonObject.put("ReferenceImageId", referenceImageId)
                jsonObject.put("DrawnImage", pathwithFilename)
                jsonObject.put("DrawingTimeStart", dateFormat.format(Date(drawingTimeStart)))
                jsonObject.put("DrawingTimeEnd", dateFormat.format(Date(drawingTimeEnd)))
                jsonObject.put("FingerTouchCount", fingerTouchCount)
                jsonObject.put("GridVisibility", gridVisibility)
//                jsonObject.put("Accuracy", "$similarity_score")
                if (isRecalling) {
                    jsonObject.put("IsRecallImage", true)
                    jsonObject.put("RecallAccuracy", similarity_score)
                    jsonObject.put("Accuracy", 0.0)
                } else {
                    jsonObject.put("IsRecallImage", false)
                    jsonObject.put("RecallAccuracy", 0.0)
                    jsonObject.put("Accuracy", similarity_score)
                }
//                Log.e("TAG ", "uploadImage: Call $similarity_score")
                SensifyAwareApplication.addTraceAwareObjectInJSON(jsonObject)
                Log.d("TAG", "getAccuraryData: ${SensifyAwareApplication.jsonArray}")


            } catch (e: Exception) {
                showResultScreen.value = true
                e.printStackTrace()
            }
        }, {
            showResultScreen.value = true
            it.printStackTrace()
        })
    }

    fun uploadData(level: String) {
        val startTime = prefUtils.getLongData(
            getApplication(),
            AppConstant.SharedPreferences.START_TIME
        )
        val endTime = prefUtils.getLongData(
            getApplication(),
            AppConstant.SharedPreferences.END_TIME
        )

        val dateFormat =
            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())
//        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val dsstRequest = JSONObject()
        dsstRequest.put(
            "UserId",
            prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
        )
        //UserId, SessionId, SessionStartTime, SessionEndTime, dsstResponsesList, IsThereLossOfMemory, LevelType
        dsstRequest.put("SessionId", UUID.randomUUID().toString())
        dsstRequest.put("SessionStartTime", dateFormat.format(Date(startTime)))
        dsstRequest.put("SessionEndTime", dateFormat.format(Date(endTime)))
        dsstRequest.put("dsstResponsesList", SensifyAwareApplication.jsonArray)
        dsstRequest.put("LevelType", level)
        dsstRequest.put(
            "IsThereLossOfMemory",
            false
        )

        val query = JSONObject()
        query.put("DsstRequest", dsstRequest)
        query.put("Url", "/SaveDSST")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveTraceAwareData(body).subscribe({}, {
            showResultScreen.value = true
        })
    }

    fun insertRoomRecord(time: Float, selectedTraces: Int, level: String) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val traceAware = TraceAware(
                    null,
                    prefUtils.getLongData(
                        getApplication(),
                        AppConstant.SharedPreferences.START_TIME
                    ),
                    prefUtils.getLongData(getApplication(), AppConstant.SharedPreferences.END_TIME),
                    time,
                    ((SensifyAwareApplication.dsstScore) * 100 / (selectedTraces * 3)).roundToInt(),
                    ((SensifyAwareApplication.recallDsstScore) * 100 / selectedTraces).roundToInt(),
                    selectedTraces * 4, level
                )
                dbHelper.awareDao().insertTraceAware(traceAware)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

