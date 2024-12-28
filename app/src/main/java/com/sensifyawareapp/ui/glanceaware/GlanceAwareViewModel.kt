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
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GlanceAwareViewModel @Inject() constructor(application: Application) :
    BaseViewModel(application) {

    var level = 0
    val timerLivedata: MutableLiveData<String> = MutableLiveData()
    val IndexLivedata: MutableLiveData<String> = MutableLiveData()

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
//  UserId, TrueNames, SelectedNames, TrueProfession, SelectedProfession, SessionId, IsThereLossOfMemory



//    fun uploadData(
//        level: String
//    ) {
//        /*val dateFormat =
//            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())*/
////        UUID.randomUUID().toString(
//        val glanceAware = JSONObject()
//        glanceAware.put("SessionId", "${UUID.randomUUID()}")
//        glanceAware.put("LevelType", level)
//        glanceAware.put("FaceAwareInfo", SensifyAwareApplication.jsonArrayGlance)
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
//        Log.e("TAG", " 11 uploadData: $glanceAware")
//        val query = JSONObject()
//        query.put("GlanceAwares", glanceAware)
//        query.put("Url", "/set-face-aware")
//
//        val body: RequestBody =
//            query.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//        apiManager.saveGlanceAware(body).subscribe({}, {})
//
//    }

    fun uploadData(level: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val glanceAware = JSONObject()
        glanceAware.put("SessionId", "${UUID.randomUUID()}")
        glanceAware.put("LevelType", level)
        glanceAware.put("FaceAwareInfo", SensifyAwareApplication.jsonArrayGlance)
        glanceAware.put("Date", dateFormat.format(Date()))
        glanceAware.put("UserId", prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID))
        glanceAware.put("IsThereLossOfMemory", false)
        glanceAware.put("PatientId", prefUtils.getStringData(getApplication(), AppConstant.SharedPreferences.PATIENT_ID))
        glanceAware.put("StudyNumber", prefUtils.getStringData(getApplication(), AppConstant.SharedPreferences.STUDY_ID))

        Log.e("TAG", "hereee 11 uploadData: $glanceAware")
        val query = JSONObject()
        query.put("GlanceAwares", glanceAware)
        query.put("Url", "/set-face-aware")

        val body: RequestBody = query.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        apiManager.saveGlanceAware(body).subscribe({}, {})
    }

}

