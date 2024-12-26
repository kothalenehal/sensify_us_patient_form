package com.sensifyawareapp.ui.audioaware

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sensifyawareapp.room.AppDatabase
import com.sensifyawareapp.room.AudioAware
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
class AudioAwareViewModel @Inject() constructor(application: Application) :
    BaseViewModel(application) {

    val timerLivedata: MutableLiveData<String> = MutableLiveData()

    fun insertRoomRecord(score: Int, level: String, evaluation: String) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val c = Calendar.getInstance()
                val audioAware = AudioAware(
                    null,
                    prefUtils.getLongData(
                        getApplication(),
                        AppConstant.SharedPreferences.START_TIME
                    ),
                    prefUtils.getLongData(getApplication(), AppConstant.SharedPreferences.END_TIME),
                    score, 10, level, evaluation
                )
                dbHelper.awareDao().insertAudioAware(audioAware)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //  UserId, SelectedWords, PlayedWords, SessionId, IsThereLossOfMemory, DisplayedWords, DisplayedWordsStartTime, DisplayedWordsEndTime, Score, LevelType
    fun uploadData(
        selectedWords: String,
        wordsToSpeakList: ArrayList<String>,
        wordsToDisplayList: String,
        score: Int
    ) {
        val startTime = prefUtils.getLongData(
            getApplication(),
            AppConstant.SharedPreferences.AUDIO_START_TIME
        )
        val endTime = prefUtils.getLongData(
            getApplication(),
            AppConstant.SharedPreferences.AUDIO_END_TIME
        )
        val level = prefUtils.getStringData(
            getApplication(),
            AppConstant.SharedPreferences.LEVEL
        )
        /*MM-dd-yyyy'T'hh:mm:ss*/
        val dateFormat =
            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        var playedWords = ""
        for (w in wordsToSpeakList)
            playedWords += ",$w"

        Log.e("List Datda ", wordsToDisplayList)
        val audioAwares = JSONObject()
        audioAwares.put("SessionId", UUID.randomUUID().toString())
        audioAwares.put("SelectedWords", selectedWords)
        audioAwares.put("Score", score)
        audioAwares.put("LevelType", level)
        audioAwares.put("DisplayedWordsStartTime", dateFormat.format(startTime))
        audioAwares.put("DisplayedWordsEndTime", dateFormat.format(endTime))
        audioAwares.put("DisplayedWords", wordsToDisplayList)
        audioAwares.put("PlayedWords", playedWords.substring(1, playedWords.length))
        audioAwares.put("Date", dateFormat.format(Date()))
        audioAwares.put(
            "UserId",
            prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
        )
        audioAwares.put(
            "IsThereLossOfMemory",
            false
        )

        val query = JSONObject()
        query.put("AudioAwares", audioAwares)
        query.put("Url", "/set-audio-aware")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveAudioAware(body).subscribe({}, {})

    }

   /* fun insertReminderRoomRecord(
        testName: String,
        startDateTimeString: String,
        endDateTimeString: String,
        repeat_selection: Int,
        selectedItem: String,
        number: Int,
        customDaysOfWeek: String?,
        repeatTime: String?,
        isChecked: Boolean,
        iconNumber: Int,
        repeatText: String
    ): Long {
        var klsm :Long = 0
        GlobalScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val reminder = Reminder(
                    null,
                    testName,
                    startDateTimeString,
                    endDateTimeString,
                    repeat_selection,
                    selectedItem,
                    number,
                    customDaysOfWeek,
                    repeatTime,
                    isChecked,
                    iconNumber,
                    repeatText
                )
//                dbHelper.awareDao().insertReminder(reminder)
                klsm = dbHelper.awareDao().insertReminderId(reminder)

                Log.e("TAG", "insertReminderRoomRecord: $klsm")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return klsm
    }*/
}

