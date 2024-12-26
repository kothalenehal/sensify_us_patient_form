package com.sensifyawareapp.ui.trackprogress

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sensifyawareapp.room.*
import com.sensifyawareapp.ui.BaseViewModel
import com.sensifyawareapp.ui.scentaware.model.LocationList
import com.sensifyawareapp.ui.scentaware.model.TubeIdAlternateDatas
import com.sensifyawareapp.utils.common.AppConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class TrackProgressViewModel(application: Application) : BaseViewModel(application) {

    val changePage: MutableLiveData<Boolean> = MutableLiveData()

    val glanceAwareList: MutableLiveData<ArrayList<GlanceAware>> = MutableLiveData()
    val wordsAwareList: MutableLiveData<ArrayList<WordsAware>> = MutableLiveData()
    val grammarAwareList: MutableLiveData<ArrayList<GrammarAware>> = MutableLiveData()
    val audioAwareEasyList: MutableLiveData<ArrayList<AudioAware>> = MutableLiveData()
    val audioAwareIntermediateList: MutableLiveData<ArrayList<AudioAware>> = MutableLiveData()
    val audioAwareHardList: MutableLiveData<ArrayList<AudioAware>> = MutableLiveData()
    val alternateDataList: MutableLiveData<ArrayList<AlternateData>> = MutableLiveData()
    val locationDataList: MutableLiveData<ArrayList<LocationData>> = MutableLiveData()

    val scentAware8List: MutableLiveData<ArrayList<ScentAware>> = MutableLiveData()
    val scentAware16List: MutableLiveData<ArrayList<ScentAware>> = MutableLiveData()
    val traceAwareList: MutableLiveData<ArrayList<TraceAware>> = MutableLiveData()
    val remindersList: MutableLiveData<ArrayList<Reminder>> = MutableLiveData()

    val insertAltDataFinish: MutableLiveData<Boolean> = MutableLiveData()

    fun nextPage() {
        changePage.value = true
    }

    fun previousPage() {
        changePage.value = false
    }

    fun insertAlternateData(alternateDataList: List<TubeIdAlternateDatas>) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                if (!prefUtils.getBooleanData(
                        getApplication(),
                        AppConstant.SharedPreferences.IS_FIRST_TIME
                    )
                ) {
                    prefUtils.saveData(
                        getApplication(),
                        AppConstant.SharedPreferences.IS_FIRST_TIME,
                        true
                    )
                    for (data in alternateDataList) {
                        val alternateData = AlternateData(
                            null,
                            data.id,
                            data.tubeValue,
                            data.tubeAlternateValue,
                            data.createdAt,
                            data.updatedAt,
                            data.locationId
                        )
                        dbHelper.awareDao().insertAlternateData(alternateData)
                    }
                    Log.e("TAG", "insertAlternateData insert:")

                } else {
                    for (data in alternateDataList) {
                        val alternateData = AlternateData(
                            null,
                            data.id,
                            data.tubeValue,
                            data.tubeAlternateValue,
                            data.createdAt,
                            data.updatedAt,
                            data.locationId
                        )
                        dbHelper.awareDao().updateAlternateData(alternateData)
                    }
                    Log.e("TAG", "insertAlternateData update:")
                }
                insertAltDataFinish.value = true

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun insertLocationData(locationList: List<LocationList>) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                if (!prefUtils.getBooleanData(
                        getApplication(),
                        AppConstant.SharedPreferences.IS_FIRST_TIME2
                    )
                ) {
                    prefUtils.saveData(
                        getApplication(),
                        AppConstant.SharedPreferences.IS_FIRST_TIME2,
                        true
                    )
                    for (data in locationList) {
                        val locations = LocationData(
                            null,
                            data.id,
                            data.name,

                            )
                        dbHelper.awareDao().insertLocationData(locations)
                    }
                    Log.e("TAG", "insertLocationData insert")

                } else {
                    for (data in locationList) {
                        val locations = LocationData(
                            null,
                            data.id,
                            data.name,
                        )
                        dbHelper.awareDao().updateLocationData(locations)
                    }
                    Log.e("TAG", "insertLocationData update")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAlternateData(languageCode: Int) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                dbHelper.awareDao().getAlternateData(languageCode).collect {
                    alternateDataList.value = it as ArrayList<AlternateData>
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getLocationData(language: String) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                dbHelper.awareDao().getLocationData(language).collect {
                    locationDataList.value = it as ArrayList<LocationData>
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getGlanceAwareData() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allAudioAware = dbHelper.awareDao().getGlanceAware()
                allAudioAware.collect {
                    glanceAwareList.value = it as ArrayList<GlanceAware>
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getWordsAwareData() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allAudioAware = dbHelper.awareDao().getWordsAware()
                allAudioAware.collect {
                    wordsAwareList.value = it as ArrayList<WordsAware>
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getGrammarAwareData() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allAudioAware = dbHelper.awareDao().getGrammarAware()
                allAudioAware.collect {
                    grammarAwareList.value = it as ArrayList<GrammarAware>
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getReminderDate() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase =
                    AppDatabase.getInstance(getApplication())
                val reminderData = dbHelper.awareDao().getReminder()
                reminderData.collect {
                    if (it.isNotEmpty()) {
                        remindersList.value = it as ArrayList<Reminder>
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TAG", "getReminderDate Exception : ${e.message}")
            }
        }
    }

    fun deleteReminderDate(item: Reminder) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    Log.e("TAG", "deleteReminderDate : $item")
                    val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                    dbHelper.awareDao().deleteReminder(item)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.e("TAG", "deleteReminderDate Exception: ${e.printStackTrace()}")
        }
    }

    fun getAudioAwareData(level: String) {

        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allAudioAware = dbHelper.awareDao().getAudioAwareByLevel()
                allAudioAware.collect {
//                    when (level.lowercase()) {
//                        "easy" -> audioAwareEasyList.value = it as ArrayList<AudioAware>
//                        "intermediate" -> audioAwareIntermediateList.value =
//                            it as ArrayList<AudioAware>
//                        "hard" -> audioAwareHardList.value = it as ArrayList<AudioAware>
//                    }
                    audioAwareEasyList.value = it as ArrayList<AudioAware>
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    fun getTraceAwareData() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allTraceAware = dbHelper.awareDao().getAllTraceAware()
                allTraceAware.collect {
                    traceAwareList.value = it as ArrayList<TraceAware>
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getScentAwareData(kit: Int) {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allScentAware = dbHelper.awareDao().getScentAwareByKit(kit)
                allScentAware.collect {
                    Log.e("TAG", "getScentAwareData 11: ${it.size}")
                    when (kit) {
                        8 -> scentAware8List.value = it as ArrayList<ScentAware>
                        16 -> scentAware16List.value = it as ArrayList<ScentAware>
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}

