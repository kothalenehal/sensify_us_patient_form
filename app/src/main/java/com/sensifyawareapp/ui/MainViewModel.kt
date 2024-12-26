package com.sensifyawareapp.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.room.*
import com.sensifyawareapp.ui.scentaware.model.TubeIdAlternateDatas
import com.sensifyawareapp.utils.common.AppConstant
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


open class MainViewModel(application: Application) : BaseViewModel(application) {

    val scentAware8Date: MutableLiveData<String> = MutableLiveData()
    val scentAware16Date: MutableLiveData<String> = MutableLiveData()
    val traceAwareDate: MutableLiveData<String> = MutableLiveData()
    val audioAwareDate: MutableLiveData<String> = MutableLiveData()
    val glanceAwareDate: MutableLiveData<String> = MutableLiveData()
    val wordsAwareDate: MutableLiveData<String> = MutableLiveData()
    val grammarAwareDate: MutableLiveData<String> = MutableLiveData()

    val canTestScentAware: MutableLiveData<Boolean> = MutableLiveData(true)
    val canTestTraceAware: MutableLiveData<Boolean> = MutableLiveData(true)
    val canTestAudioAware: MutableLiveData<Boolean> = MutableLiveData(true)
    val canTestGlanceAware: MutableLiveData<Boolean> = MutableLiveData(true)
    val canTestWordsAware: MutableLiveData<Boolean> = MutableLiveData(true)
    val canTestGrammarAware: MutableLiveData<Boolean> = MutableLiveData(true)

    init {
        getScentAwareDate8()
        getScentAwareDate16()
        getTraceAwareDate()
        getAudioAwareDate()
        getGlanceAwareDate()
        getWordsAwareDate()
        getGrammarAwareDate()
    }

    private fun getScentAwareDate8() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allScentAware = dbHelper.awareDao().getScentAwareLastDate(8)
                allScentAware.collect {
                    if (it.isNotEmpty()) {
                        scentAware8Date.value =
                            getApplication<SensifyAwareApplication>().getString(
                                R.string.last_tested_on_,
                                formatDate(it[0].endDate!!, 1)
                            )
                    } else {
                        scentAware8Date.value =
                            getApplication<SensifyAwareApplication>().getString(R.string.not_tested_yet)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getScentAwareDate16() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allScentAware = dbHelper.awareDao().getScentAwareLastDate(16)
                allScentAware.collect {
                    if (it.isNotEmpty()) {
                        scentAware16Date.value =
                            getApplication<SensifyAwareApplication>().getString(
                                R.string.last_tested_on_,
                                formatDate(it[0].endDate!!, 1)
                            )
                    } else {
                        scentAware16Date.value =
                            getApplication<SensifyAwareApplication>().getString(R.string.not_tested_yet)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getTraceAwareDate() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allScentAware = dbHelper.awareDao().getTraceAwareLastDate()
                allScentAware.collect {
                    if (it.isNotEmpty())
                        traceAwareDate.value = getApplication<SensifyAwareApplication>().getString(
                            R.string.last_tested_on_,
                            formatDate((it as ArrayList<TraceAware>)[0].endDate!!, 2)
                        )
                    else traceAwareDate.value =
                        getApplication<SensifyAwareApplication>().getString(R.string.not_tested_yet)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAudioAwareDate() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allScentAware = dbHelper.awareDao().getAudioAwareLastDate()
                allScentAware.collect {
                    if (it.isNotEmpty())
                        audioAwareDate.value =
                            getApplication<SensifyAwareApplication>().getString(
                                R.string.last_tested_on_,
                                formatDate((it as ArrayList<AudioAware>)[0].endDate!!, 3)
                            )
                    else audioAwareDate.value =
                        getApplication<SensifyAwareApplication>().getString(R.string.not_tested_yet)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getGlanceAwareDate() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allScentAware = dbHelper.awareDao().getGlanceAwareLastDate()
                allScentAware.collect {
                    if (it.isNotEmpty())
                        glanceAwareDate.value = getApplication<SensifyAwareApplication>().getString(
                            R.string.last_tested_on_,
                            formatDate((it as ArrayList<GlanceAware>)[0].endDate!!, 4)
                        )
                    else glanceAwareDate.value =
                        getApplication<SensifyAwareApplication>().getString(R.string.not_tested_yet)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getWordsAwareDate() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allScentAware = dbHelper.awareDao().getWordsAwareLastDate()
                allScentAware.collect {
                    if (it.isNotEmpty())
                        wordsAwareDate.value = getApplication<SensifyAwareApplication>().getString(
                            R.string.last_tested_on_,
                            formatDate((it as ArrayList<WordsAware>)[0].endDate!!, 5)
                        )
                    else wordsAwareDate.value =
                        getApplication<SensifyAwareApplication>().getString(R.string.not_tested_yet)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getGrammarAwareDate() {
        viewModelScope.launch {
            try {
                val dbHelper: AppDatabase = AppDatabase.getInstance(getApplication())
                val allScentAware = dbHelper.awareDao().getGrammarAwareLastDate()
                allScentAware.collect {
                    if (it.isNotEmpty())
                        grammarAwareDate.value =
                            getApplication<SensifyAwareApplication>().getString(
                                R.string.last_tested_on_,
                                formatDate((it as ArrayList<GrammarAware>)[0].endDate!!, 6)
                            )
                    else grammarAwareDate.value =
                        getApplication<SensifyAwareApplication>().getString(R.string.not_tested_yet)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun formatDate(timeStamp: Long, type: Int): String {
        val c = Calendar.getInstance()
        c.timeInMillis = timeStamp
        val difference = Calendar.getInstance().timeInMillis - c.timeInMillis
        val days = (difference / (1000 * 60 * 60 * 24))
        when (type) {
            1 -> canTestScentAware.value = days > 14
            2 -> canTestTraceAware.value = days > 7
            3 -> canTestAudioAware.value = days > 7
            4 -> canTestGlanceAware.value = days > 7
            5 -> canTestWordsAware.value = days > 7
            6 -> canTestGrammarAware.value = days > 7
        }

        val formatter: DateFormat =
            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat1, Locale.US)
//        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val text: String = formatter.format(Date(timeStamp))
        Log.e("TIme", text)

//        Log.e("--", "formatDate:${dateFormat.format(timeStamp)}  //  ${c[Calendar.MONTH] + 1}/${c[Calendar.DAY_OF_MONTH]}/${c[Calendar.YEAR]} ${c[Calendar.HOUR_OF_DAY]}:${c[Calendar.MINUTE]}")
//        "${c[Calendar.DAY_OF_MONTH]}/${c[Calendar.MONTH] + 1}/${c[Calendar.YEAR]} ${c[Calendar.HOUR_OF_DAY]}:${c[Calendar.MINUTE]}"
        return text
    }

}

