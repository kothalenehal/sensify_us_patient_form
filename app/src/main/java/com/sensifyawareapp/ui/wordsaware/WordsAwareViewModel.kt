package com.sensifyawareapp.ui.wordsaware

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sensifyawareapp.room.AppDatabase
import com.sensifyawareapp.room.WordsAware
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
class WordsAwareViewModel @Inject() constructor(application: Application) :
    BaseViewModel(application) {

    val timerLivedata: MutableLiveData<String> = MutableLiveData()

    fun insertRoomRecord() {
        viewModelScope.launch {
            try {
                /*val newList = ArrayList<String>()
                for (element in AppConstant.wordsList) {
                    if (!newList.contains(element)) {
                        newList.add(element)
                    }
                }*/
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val c = Calendar.getInstance()
                val wordsAware = WordsAware(
                    null,
                    prefUtils.getLongData(
                        getApplication(),
                        AppConstant.SharedPreferences.START_TIME
                    ),
                    prefUtils.getLongData(getApplication(), AppConstant.SharedPreferences.END_TIME),
                    AppConstant.totalWord,
                    AppConstant.wordsList.size
                )
                dbHelper.awareDao().insertWordsAware(wordsAware)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun uploadData(referenceLetters: String) {
        /* val newList = ArrayList<String>()
         for (element in AppConstant.wordsList) {
             if (!newList.contains(element)) {
                 newList.add(element)
             }
         }
         Log.e("Remove " , newList.toString())*/
// userId, words, referenceLetters, SessionId, IsSpokenWordCorrect
        val grammarAwares = JSONObject()
        grammarAwares.put("SessionId", UUID.randomUUID().toString())
        grammarAwares.put("words", AppConstant.wordsList.joinToString())
        grammarAwares.put("referenceLetters", referenceLetters)
        grammarAwares.put(
            "UserId",
            prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
        )

        val query = JSONObject()
        query.put("WordAwares", grammarAwares)
        query.put("Url", "/set-word-aware")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveWordsAware(body).subscribe({}, {})
    }
}