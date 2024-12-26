package com.sensifyawareapp.ui.grammaraware

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.room.AppDatabase
import com.sensifyawareapp.room.GrammarAware
import com.sensifyawareapp.ui.BaseViewModel
import com.sensifyawareapp.utils.common.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*
import javax.inject.Inject


@HiltViewModel
class GrammarAwareViewModel @Inject() constructor(application: Application) :

    BaseViewModel(application) {
    val timerLiveData: MutableLiveData<String> = MutableLiveData()

    fun insertRoomRecord(avgTime: String) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val c = Calendar.getInstance()
                val grammarAware = GrammarAware(
                    null,
                    prefUtils.getLongData(
                        getApplication(),
                        AppConstant.SharedPreferences.START_TIME
                    ),
                    prefUtils.getLongData(getApplication(), AppConstant.SharedPreferences.END_TIME),
                    (AppConstant.Accuracy.sum()*5)/300,
                    avgTime
                )
                dbHelper.awareDao().insertGrammarAware(grammarAware)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

// UserId, GrammerAwaresSentences, SessionId, LevelType, GrammaticalAccuracy (In GrammerAwareSentences request body)
    fun uploadData() {
        val grammarAwares = JSONObject()
        grammarAwares.put("SessionId", UUID.randomUUID().toString())
        grammarAwares.put("GrammerAwaresSentences", SensifyAwareApplication.jsonArrayGrammar)
        grammarAwares.put(
            "UserId",
            prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
        )

        val query = JSONObject()
        query.put("GrammerAwares", grammarAwares)
        query.put("Url", "/set-grammer-aware")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        apiManager.saveGrammarAware(body).subscribe({}, {})
    }
}