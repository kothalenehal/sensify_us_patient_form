package com.sensifyawareapp.ui.grammaraware

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentGrammarAwareSpeakBinding
import com.sensifyawareapp.databinding.LayoutAlertDialogBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class GrammarAwareSpeakActivity : BaseActivity() {
    private lateinit var viewModel: GrammarAwareViewModel
    private lateinit var binding: FragmentGrammarAwareSpeakBinding
    var timeMilliSec: Long = 70000
    private var isTimeRunning = false
    private lateinit var timer: CountDownTimer
    private lateinit var speechRecognizer: SpeechRecognizer
    var level = 0
    private var levelRemain = 3
    var totalTime = 0
    var words: List<String> = ArrayList()
    var isFirstClick = true
    var isSecondClick = false
    private var formatter = SimpleDateFormat()
    var date = Date()
    private var speakStartTime = String()
    private var noun = String()
    private var verb = String()
    private var adjective = String()
    var listTemp: ArrayList<String> = ArrayList()
    var spokenText = StringBuilder()
    var isEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_grammar_aware_speak)
        viewModel = ViewModelProviders.of(this)[GrammarAwareViewModel::class.java]
        updateTime(timeMilliSec)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO), 1
            )
        }
        level = intent.getIntExtra("level", 0)
        if (level != 0) {
            levelRemain = intent.getIntExtra("levelRemain", 0)
        }
        adjective = intent.getStringExtra("adjective").toString()
        noun = intent.getStringExtra("noun").toString()
        verb = intent.getStringExtra("verb").toString()
        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }
        date = Calendar.getInstance().time
        formatter = SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        if (level == 0) {
            val nounList = noun.split("\n")
            val adjectiveList = adjective.split("\n")
            val verbList = verb.split("\n")
            for (i in 0 until 3) {
                val string = StringBuilder()
                string.append(nounList[((Math.random() * nounList.size).toInt())]).append(" ")
                string.append(verbList[((Math.random() * verbList.size).toInt())])
                if (intent.getIntExtra("levelValue", 0) == 2) {
                    string.append(" ")
                        .append(adjectiveList[((Math.random() * adjectiveList.size).toInt())])
                }
                AppConstant.wordsString.add(string.toString())
                Log.e("random", string.toString())
            }
            Log.e("Array ", AppConstant.wordsString.toString())
            binding.words = AppConstant.wordsString[0].replace(" ", "\n")
        } else if (level == 1) {
            binding.words = AppConstant.wordsString[1].replace(" ", "\n")
        } else {
            binding.words = AppConstant.wordsString[2].replace(" ", "\n")
        }

        binding.level = level.toString()
        binding.levelRemain = levelRemain.toString()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        startTime(false)
        binding.txSpeech.movementMethod = ScrollingMovementMethod()
        binding.ivSpeaker.setOnClickListener {
            if (isFirstClick) {
                startRecognition()
                binding.ivSpeaker.let {
                    it.setCardBackgroundColor(
                        ContextCompat.getColor(
                            this, R.color.green
                        )
                    )
                    it.strokeColor = ContextCompat.getColor(this, R.color.green)
                }
                binding.speakerOff.visibility = View.GONE
                binding.speakerOn.visibility = View.VISIBLE
                binding.mic.setImageResource(R.drawable.mic_on)
                binding.ivMic.strokeWidth = 2
                binding.ivMic.strokeColor = ContextCompat.getColor(this, R.color.border_color)
                binding.tvActive.let {
                    it.text = getString(R.string.active)
                    it.setTextColor(
                        ContextCompat.getColor(
                            this, R.color.border_color
                        )
                    )

                    it.typeface = Typeface.DEFAULT_BOLD
                }
                startSpeech()
                speakStartTime = formatter.format(Calendar.getInstance().time)
                isSecondClick = true
                isFirstClick = false
            } else if (isSecondClick) {
                Log.e(
                    "VAlies ",
                    spokenText.isEmpty().toString() + "  " + spokenText
                )
                if (spokenText.isEmpty()) {
                    val dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.show()
                    val alertBinding: LayoutAlertDialogBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(this), R.layout.layout_alert_dialog, null, false
                    )
                    dialog.setContentView(alertBinding.root)
                    alertBinding.txWord.text = getString(R.string.alert_2)
                    alertBinding.btnSubmit.visibility = View.GONE
                    alertBinding.btnProceed.visibility = View.VISIBLE
                    alertBinding.txCancel.visibility = View.VISIBLE
                    alertBinding.btnProceed.setOnClickListener {
                        dialog.dismiss()
                        startActivity(
                            Intent(
                                this, MainActivity::class.java
                            )
                        )
                        speechRecognizer.stopListening()
                        stopTime()
                    }
                    alertBinding.txCancel.setOnClickListener {
                        stopRecording()
                        dialog.dismiss()
                    }

                } else {
                    stopRecording()
                }
            } else {
                speechRecognizer.stopListening()
                changeScreen()
            }
        }

        binding.restartSpeech.setOnClickListener {
            if (isEnabled) {
                startRecognition()
                startTime(true)
                startSpeech()
                spokenText.clear()
                binding.ivSpeaker.let {
                    it.setCardBackgroundColor(ContextCompat.getColor(this, R.color.green))
                    it.strokeColor = ContextCompat.getColor(this, R.color.green)
                }
                binding.restartSpeech.setImageResource(R.drawable.restart_gray)
                binding.speakerOn.visibility = View.VISIBLE
                binding.submit.visibility = View.GONE
                binding.ivMic.strokeWidth = 2
                binding.ivMic.strokeColor = ContextCompat.getColor(this, R.color.border_color)
                binding.mic.setImageResource(R.drawable.mic_on)
                binding.tvActive.let {
                    it.text = getString(R.string.active)
                    it.setTextColor(ContextCompat.getColor(this, R.color.border_color))
                    it.typeface = Typeface.DEFAULT_BOLD
                }

                isSecondClick = true
                isEnabled = false
            }
        }
    }

    private fun stopRecording() {
        binding.txSpeech.text = spokenText
        stopTime()
        speechRecognizer.stopListening()
        binding.ivSpeaker.let {
            it.setCardBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.white
                )
            )
            it.strokeColor = ContextCompat.getColor(this, R.color.blue)
        }
        binding.restartSpeech.setImageResource(R.drawable.restart_blue)
        isEnabled = true
        binding.speakerOn.visibility = View.GONE
        binding.submit.visibility = View.VISIBLE
        binding.mic.setImageResource(R.drawable.vector)
        binding.ivMic.strokeWidth = 0
        binding.tvActive.let {
            it.text = getString(R.string.stand_by)
            it.setTextColor(
                ContextCompat.getColor(
                    this, R.color.divider_grey
                )
            )
            it.typeface = Typeface.DEFAULT
        }
        isSecondClick = false
    }


    private fun startSpeech() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
                listTemp.clear()
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
                spokenText.append(binding.txSpeech.text)
                Log.d("SpokenText", "onEndOfSpeech: ${binding.txSpeech.text}")

            }

            override fun onError(p0: Int) {
            }

            override fun onResults(p0: Bundle) {
            }

            override fun onPartialResults(p0: Bundle?) {
                if (p0 != null) {
                    val data1 = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (data1 != null) {
                        if (data1.isNotEmpty()) {
                            if (data1[0] != "") {
                                listTemp.add(data1[0])

                                binding.txSpeech.text =  /*tempText.toString() + ""+*/data1[0]
                            }
                        }
                    }
                }
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
                Log.e("onEvent", "onEvent: $p1")
            }

        })
    }

    private fun startRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_CALLING_PACKAGE, this.packageName
        )

        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 50000
        )

        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 70000);
        speechRecognizer.startListening(intent)
    }

    private fun changeScreen() {
        val bundle = Bundle()
        if (level == 0) {
            bundle.putInt("level", 1)
            bundle.putInt("levelRemain", 2)
            val textWords = spokenText.split(" ")
            AppConstant.Time1 = totalTime

            AppConstant.arrayS.add(textWords.size)
            Log.e("Length", textWords.size.toString() + " / /" + words.size)
            upLoadData(
                spokenText.toString(),
                AppConstant.wordsString[0],
                speakStartTime,
                formatter.format(Calendar.getInstance().time)
            )
            startActivity(
                Intent(
                    this, GrammarAwareCheckPointActivity::class.java
                ).putExtra("level", 1).putExtra("levelRemain", 2)
            )
        } else if (level == 1) {
            bundle.putInt("level", 2)
            bundle.putInt("levelRemain", 1)
            AppConstant.Time2 = totalTime
            val textWords = spokenText.split(" ")
            AppConstant.arrayS.add(textWords.size)
            Log.e("Length", textWords.size.toString() + " / /" + words.size)
            upLoadData(
                spokenText.toString(),
                AppConstant.wordsString[1],
                speakStartTime,
                formatter.format(Calendar.getInstance().time)
            )
            startActivity(
                Intent(
                    this, GrammarAwareCheckPointActivity::class.java
                ).putExtra("level", 2).putExtra("levelRemain", 1)
            )

        } else {
            AppConstant.Time3 = totalTime
            val textWords = spokenText.split(" ")
            AppConstant.arrayS.add(textWords.size)
            Log.e("Length", textWords.size.toString() + " / /" + words.size)
            upLoadData(
                spokenText.toString(),
                AppConstant.wordsString[2],
                speakStartTime,
                formatter.format(Calendar.getInstance().time)
            )
        }
    }

    private fun startTime(isReset: Boolean) {
        if (isReset) {
            timeMilliSec = 70000
        }

        timer = object : CountDownTimer(timeMilliSec, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeMilliSec = millisUntilFinished
                updateTime(millisUntilFinished)
                binding.timerProgress = (millisUntilFinished / 1000).toInt()
                val time = (millisUntilFinished / 1000).toInt()
                val timer = (70000 / 1000)
                totalTime = timer - time
//                Log.e("Time ", "$time // $timer  // $total")
            }

            override fun onFinish() {
                changeScreen()
                stopTime()
                speechRecognizer.stopListening()
                totalTime = 70
            }
        }
        timer.start()
        isTimeRunning = true
    }

    private fun stopTime() {
        timer.cancel()
        isTimeRunning = false
    }

    fun updateTime(l: Long) {
        val minutes: Int = l.toInt() / 60000
        val seconds: Int = l.toInt() % 60000 / 1000

        var timeLeft: String = "0$minutes:"
        if (seconds < 10) timeLeft += "0"
        timeLeft += seconds
        binding.toolbar.tvTimer.text = timeLeft
        viewModel.timerLiveData.value = timeLeft
        binding.timerProgress = l.toInt()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        stopTime()
        speechRecognizer.destroy()
    }

    fun upLoadData(
        sentences: String, referenceWords: String, speakStartTime: String, speakEndTime: String
    ) {
        calculateAccuracy(referenceWords, sentences, speakStartTime, speakEndTime)

    }

    private fun jsonData(
        sentences: String,
        referenceWords: String,
        speakStartTime: String,
        speakEndTime: String,
        accuracy: Int
    ) {
        val jsonObject = JSONObject()
        jsonObject.put("Sentences", sentences)
        jsonObject.put("ReferenceWords", referenceWords)
        jsonObject.put("SpeakStartTime", speakStartTime)
        jsonObject.put("SpeakEndTime", speakEndTime)
        jsonObject.put("GrammaticalAccuracy", accuracy)
        SensifyAwareApplication.addGrammarAwareObjectInJSON(jsonObject)
    }

    @SuppressLint("CheckResult")
    private fun calculateAccuracy(
        referenceWords: String,
        sentences: String,
        speakStartTime: String,
        speakEndTime: String
    ) {
        val query = JSONObject()
        query.put(
            "UserId", prefUtils.getIntData(this, AppConstant.SharedPreferences.USER_ID)
        )
        query.put("Sentence", sentences)
        query.put("ReferenceWord", referenceWords)
        query.put("Url", "/grammer-aware-sentence-accuracy")

        val body: RequestBody =
            query.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        apiManager.calculateAccuracy(body).subscribe({
            if (it.isSuccess) {
                jsonData(
                    sentences,
                    referenceWords,
                    speakStartTime,
                    speakEndTime,
                    it.dataList.accuracy
                )
                AppConstant.Accuracy.add(it.dataList.accuracy)
                if (level == 2) {
                    val bundle = Bundle()
                    bundle.putInt("level", 3)
                    startActivity(
                        Intent(
                            this, GrammarAwareCheckPointActivity::class.java
                        ).putExtra("level", 3).putExtra("levelRemain", 0)
                    )
                }
            }
        }, {})
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}