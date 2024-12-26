//package com.sensifyawareapp.ui.glanceaware
//
//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.viewModelScope
//import com.sensifyawareapp.SensifyAwareApplication
//import com.sensifyawareapp.room.AppDatabase
//import com.sensifyawareapp.room.GlanceAware
//import com.sensifyawareapp.ui.BaseViewModel
//import com.sensifyawareapp.utils.common.AppConstant
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.launch
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.RequestBody
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONObject
//import java.util.*
//import javax.inject.Inject
//
//@HiltViewModel
//class GlanceAwareViewModel @Inject() constructor(application: Application) :
//    BaseViewModel(application) {
//
//    var level = 0
//    val timerLivedata: MutableLiveData<String> = MutableLiveData()
//    val IndexLivedata: MutableLiveData<String> = MutableLiveData()
//
//    fun insertRoomRecord(correctAns: Int, totalQ: Int, level: String) {
//        viewModelScope.launch {
//            try {
//                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
//                val glanceAware = GlanceAware(
//                    null,
//                    prefUtils.getLongData(
//                        getApplication(), AppConstant.SharedPreferences.START_TIME
//                    ),
//                    prefUtils.getLongData(getApplication(), AppConstant.SharedPreferences.END_TIME),
//                    correctAns,
//                    totalQ,
//                    level
//                )
//                dbHelper.awareDao().insertGlanceAware(glanceAware)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
////  UserId, TrueNames, SelectedNames, TrueProfession, SelectedProfession, SessionId, IsThereLossOfMemory
//
//
//
//    fun uploadData(
//        level: String
//    ) {
//        /*val dateFormat =
//            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())*/
////        UUID.randomUUID().toString(
//
//        val studyNumber = prefUtils.getStringData(
//            getApplication(),
//            AppConstant.SharedPreferences.STUDY_ID
//        )
//        val patientId = prefUtils.getStringData(
//            getApplication(),
//            AppConstant.SharedPreferences.PATIENT_ID
//        )
//
//        val glanceAware = JSONObject()
//        glanceAware.put("SessionId", "${UUID.randomUUID()}")
//        glanceAware.put("LevelType", level)
//        glanceAware.put("FaceAwareInfo", SensifyAwareApplication.jsonArrayGlance)
//        glanceAware.put("StudyNumber", studyNumber)
//        glanceAware.put("PatientId", patientId)
////        glanceAware.put("TrueNames", trueNames.substring(1, trueNames.length))
////        glanceAware.put("SelectedNames", selectedNames.substring(1, selectedNames.length))
////        glanceAware.put("TrueProfession", trueProfession.substring(1, trueProfession.length))
////        glanceAware.put("SelectedProfession", selectedProfession.substring(1, selectedProfession.length))
//
//        glanceAware.put(
//            "UserId", prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
//        )
//
//        glanceAware.put(
//            "IsThereLossOfMemory", false
//        )
//        Log.e("TAG", "hereee  11 uploadData: $glanceAware")
//        val query = JSONObject()
//        query.put("GlanceAwares", glanceAware)
//        query.put("Url", "/set-face-aware")
//        Log.e("TAG", "hereee Final API Payload: $query")
//
//
//        val body: RequestBody =
//            query.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//        apiManager.saveGlanceAware(body).subscribe({}, {})
//
//    }
//}
//

package com.sensifyawareapp.ui.glanceaware

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.room.AppDatabase
import com.sensifyawareapp.room.GlanceAware
import com.sensifyawareapp.ui.BaseViewModel
import com.sensifyawareapp.utils.common.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GlanceAwareViewModel @Inject constructor(application: Application) :
    BaseViewModel(application) {

    var level = 0
    val timerLivedata: MutableLiveData<String> = MutableLiveData()
    val IndexLivedata: MutableLiveData<String> = MutableLiveData()

    // Helper function to extract English text from bilingual string
    private fun extractEnglishText(bilingualText: String): String {
        // Handle the format "English / Hindi"
        return bilingualText.split("/").firstOrNull()?.trim() ?: bilingualText
    }

    // Helper function to process FaceAwareInfo array
    private fun processFaceAwareInfo(jsonArray: JSONArray): JSONArray {
        val processedArray = JSONArray()

        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONObject(i)
            val processedItem = JSONObject()

            // Copy all existing fields
            for (key in item.keys()) {
                when (key) {
                    "SelectedNames" -> processedItem.put(key, extractEnglishText(item.getString(key)))
                    "SelectedProfession" -> processedItem.put(key, extractEnglishText(item.getString(key)))
                    else -> processedItem.put(key, item.get(key))
                }
            }

            // Recalculate IsNameCorrect and IsProfessionCorrect
            processedItem.put("IsNameCorrect",
                processedItem.getString("TrueNames") == processedItem.getString("SelectedNames"))
            processedItem.put("IsProfessionCorrect",
                processedItem.getString("TrueProfession") == processedItem.getString("SelectedProfession"))

            processedArray.put(processedItem)
        }

        return processedArray
    }

    fun insertRoomRecord(correctAns: Int, totalQ: Int, level: String) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val glanceAware = GlanceAware(
                    null,
                    prefUtils.getLongData(
                        getApplication(), AppConstant.SharedPreferences.START_TIME
                    ),
                    prefUtils.getLongData(getApplication(), AppConstant.SharedPreferences.END_TIME),
                    correctAns,
                    totalQ,
                    level
                )
                dbHelper.awareDao().insertGlanceAware(glanceAware)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun uploadData(level: String) {
        val studyNumber = prefUtils.getStringData(
            getApplication(),
            AppConstant.SharedPreferences.STUDY_ID
        )
        val patientId = prefUtils.getStringData(
            getApplication(),
            AppConstant.SharedPreferences.PATIENT_ID
        )

        val glanceAware = JSONObject().apply {
            put("SessionId", "${UUID.randomUUID()}")
            put("LevelType", level)
            // Process the FaceAwareInfo array before adding it
            put("FaceAwareInfo", processFaceAwareInfo(SensifyAwareApplication.jsonArrayGlance))
            put("StudyNumber", studyNumber)
            put("PatientId", patientId)
            put(
                "UserId",
                prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
            )
            put("IsThereLossOfMemory", false)
        }

        Log.e("TAG", "hereee  11 uploadData: $glanceAware")

        val query = JSONObject().apply {
            put("GlanceAwares", glanceAware)
            put("Url", "/set-face-aware")
        }
        Log.e("TAG", "hereee Final API Payload: $query")

        val body: RequestBody =
            query.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveGlanceAware(body).subscribe({}, {})
    }
}