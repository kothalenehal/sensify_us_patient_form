package com.sensifyawareapp.ui.grammaraware.sample

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentGrammarAwareSpeakBinding
import com.sensifyawareapp.databinding.LayoutAlertDialogBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.ui.grammaraware.GrammarAwareCheckPointActivity
import com.sensifyawareapp.ui.grammaraware.GrammarAwareViewModel
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class GrammarAwareSpeakFragment : BaseFragment() {
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
    var formatter = SimpleDateFormat()
    var date = Date()
    private var speakStartTime = String()
    var noun = String()
    var verb = String()
    var adjective = String()
    var listData: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            level = it.getInt("level", 0)
            if (level != 0) {
                levelRemain = it.getInt("levelRemain", 0)
            }
            adjective = it.getString("adjective").toString()
            noun = it.getString("noun").toString()
            verb = it.getString("verb").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGrammarAwareSpeakBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(requireActivity())[GrammarAwareViewModel::class.java]
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateTime(timeMilliSec)

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }

        date = Calendar.getInstance().time
        formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        if (level == 0) {
            val nounList = noun.split("\n")
            val adjectiveList = adjective.split("\n")
            val verbList = verb.split("\n")
            for (i in 0 until 3) {
                val string = StringBuilder()
                string.append(nounList.random()).append(" ")
                string.append(verbList.random())
                if (requireActivity().intent.getIntExtra("level", 0) == 2) {
                    string.append(" ").append(adjectiveList.random())
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
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity())

        startTime(false)
        binding.txSpeech.movementMethod = ScrollingMovementMethod()
        binding.ivSpeaker.setOnClickListener {
            if (isFirstClick) {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    requireActivity().packageName
                )

//                 intent.putExtra(
//                           RecognizerIntent.ACTION_RECOGNIZE_SPEECH,
//                           RecognizerIntent.EXTRA_PREFER_OFFLINE
//                       )
//                intent.putExtra("android.speech.extra.DICTATION_MODE", true)
                intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
//                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 50000)
//                intent.putExtra(
//                    RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
//                    20000
//                )
//                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 30)
//                intent.putExtra(RecognizerIntent.EXTRA_AUDIO_INJECT_SOURCE, true)
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 70000);
                speechRecognizer.startListening(intent)
                binding.ivSpeaker.let {
                    it.setCardBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                    it.strokeColor =
                        ContextCompat.getColor(requireContext(), R.color.green)
                }
                binding.speakerOff.visibility = View.GONE
                binding.speakerOn.visibility = View.VISIBLE
                binding.mic.setImageResource(R.drawable.mic_on)
                binding.ivMic.strokeWidth = 2
                binding.ivMic.strokeColor =
                    ContextCompat.getColor(requireContext(), R.color.border_color)
                binding.tvActive.let {
                    it.text = getString(R.string.active)
                    it.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.border_color
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
                    binding.txSpeech.text.isEmpty().toString() + "  " + binding.txSpeech.text
                )
                if (binding.txSpeech.text.isEmpty()) {
                    val dialog = Dialog(requireContext())
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.show()
                    val alertBinding: LayoutAlertDialogBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(requireContext()),
                            R.layout.layout_alert_dialog,
                            null,
                            false
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
                                requireActivity(),
                                MainActivity::class.java
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
//            speechRecognizer.startListening(intent)
            startTime(true)
            startSpeech()
            binding.ivSpeaker.let {
                it.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                it.strokeColor = ContextCompat.getColor(requireContext(), R.color.green)
            }
            binding.restartSpeech.setImageResource(R.drawable.restart_gray)
            binding.speakerOn.visibility = View.VISIBLE
            binding.submit.visibility = View.GONE
            binding.ivMic.strokeWidth = 2
            binding.ivMic.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.border_color)
            binding.mic.setImageResource(R.drawable.mic_on)
            binding.tvActive.let {
                it.text = getString(R.string.active)
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.border_color))
                it.typeface = Typeface.DEFAULT_BOLD
            }

            isSecondClick = true
        }
    }

    private fun stopRecording() {
        stopTime()
        speechRecognizer.stopListening()
        binding.ivSpeaker.let {
            it.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            it.strokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
        }
        binding.restartSpeech.setImageResource(R.drawable.restart_blue)
        binding.speakerOn.visibility = View.GONE
        binding.submit.visibility = View.VISIBLE
        binding.mic.setImageResource(R.drawable.vector)
        binding.ivMic.strokeWidth = 0
        binding.tvActive.let {
            it.text = getString(R.string.stand_by)
            it.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.divider_grey
                )
            )
            it.typeface = Typeface.DEFAULT
        }
        isSecondClick = false
    }

    fun startSpeech() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {

            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(p0: Int) {
            }

            override fun onResults(p0: Bundle) {
                val data = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (data != null) {
                    binding.txSpeech.text = data[0]
                    words = data[0].split(" ")
                    Log.e("Text 11", data[0])
                }
            }

            override fun onPartialResults(p0: Bundle?) {
                if (p0 != null) {
//                   val data =
//                    p0.getStringArray("com.google.android.voicesearch.UNSUPPORTED_PARTIAL_RESULTS")
                    var str: String? = String()
                    val data = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (data != null) {
                        if (data.isNotEmpty()) {
                            listData.add(data[0])
                            binding.txSpeech.text = data[0]
                            /*textList = data[0]
                            for (i in 0 until data.size) {
                                Log.d("TAG", "result " + data[i])
                                str += data[i]
                            }*/
                        }
                    }
                    Log.e("Text ", "$listData")

                }
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }

        })
    }

    private fun changeScreen() {
        val bundle = Bundle()
        if (level == 0) {
            bundle.putInt("level", 1)
            bundle.putInt("levelRemain", 2)
            val textWords = binding.txSpeech.text.split(" ")
            AppConstant.Time1 = totalTime

            AppConstant.arrayS.add(textWords.size)
            Log.e("Length", textWords.size.toString() + " / /" + words.size)
            upLoadData(
                binding.txSpeech.text.toString(),
                AppConstant.wordsString[0],
                speakStartTime,
                formatter.format(Calendar.getInstance().time)
            )
            startActivity(
                Intent(
                    requireActivity(),
                    GrammarAwareCheckPointActivity::class.java
                ).putExtra("level", 1).putExtra("levelRemain", 2)
            )
//            findNavController().navigate(R.id.action_grammarAwareSpeakFragment_to_grammarAwareCheckPointFragment, bundle)
//          findNavController().navigate(GrammarAwareSpeakFragmentDirections.actionGrammarAwareSpeakFragmentSelf())
        } else if (level == 1) {
            bundle.putInt("level", 2)
            bundle.putInt("levelRemain", 1)
            AppConstant.Time2 = totalTime
            val textWords = binding.txSpeech.text.split(" ")
            AppConstant.arrayS.add(textWords.size)
            Log.e("Length", textWords.size.toString() + " / /" + words.size)
                upLoadData(
                binding.txSpeech.text.toString(),
                AppConstant.wordsString[1],
                speakStartTime,
                formatter.format(Calendar.getInstance().time)
            )
            startActivity(
                Intent(
                    requireActivity(),
                    GrammarAwareCheckPointActivity::class.java
                ).putExtra("level", 2).putExtra("levelRemain", 1)
            )

//            findNavController().navigate(R.id.action_grammarAwareSpeakFragment_to_grammarAwareCheckPointFragment, bundle)
//            findNavController().navigate(GrammarAwareSpeakFragmentDirections.actionGrammarAwareSpeakFragmentSelf())
        } else {
            AppConstant.Time3 = totalTime
            val textWords = binding.txSpeech.text.split(" ")
            AppConstant.arrayS.add(textWords.size)
            Log.e("Length", textWords.size.toString() + " / /" + words.size)
            upLoadData(
                binding.txSpeech.text.toString(),
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
                if (isVisible) {
                    changeScreen()
                    speechRecognizer.stopListening()
                    totalTime = 70
                }
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

        var timeLeft: String = " $minutes:"
        if (seconds < 10) timeLeft += "0"
        timeLeft += seconds

        viewModel.timerLiveData.value = timeLeft
        binding.timerProgress = l.toInt()
    }

    override fun onPause() {
        super.onPause()
        speechRecognizer.destroy()
    }

    fun upLoadData(
        sentences: String,
        referenceWords: String,
        speakStartTime: String,
        speakEndTime: String
    ) {
        calculateAccuracy(referenceWords, sentences)
        val jsonObject = JSONObject()
        jsonObject.put("Sentences", sentences)
        jsonObject.put("ReferenceWords", referenceWords)
        jsonObject.put("SpeakStartTime", speakStartTime)
        jsonObject.put("SpeakEndTime", speakEndTime)
        SensifyAwareApplication.addGrammarAwareObjectInJSON(jsonObject)
    }

    private fun calculateAccuracy(referenceWords: String, sentences: String) {
        val query = JSONObject()
        query.put(
            "UserId",
            prefUtils.getIntData(requireContext(), AppConstant.SharedPreferences.USER_ID)
        )
        query.put("Sentence", sentences)
        query.put("ReferenceWord", referenceWords)
        query.put("Url", "/grammer-aware-sentence-accuracy")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        apiManager.calculateAccuracy(body).subscribe({
            if (it.isSuccess) {
                AppConstant.Accuracy.add(it.dataList.accuracy)
                if (level == 2) {
                    val bundle = Bundle()
                    bundle.putInt("level", 3)
                    startActivity(
                        Intent(
                            requireActivity(),
                            GrammarAwareCheckPointActivity::class.java
                        ).putExtra("level", 3).putExtra("levelRemain", 0)
                    )
//                    findNavController().navigate(R.id.action_grammarAwareSpeakFragment_to_grammarAwareCheckPointFragment, bundle)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please say something, I'm listening!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, {})
    }

    /* private lateinit var viewModel: GrammarAwareViewModel
     private lateinit var binding: FragmentGrammarAwareSpeakBinding
     var timeMilliSec: Long = 70000
     var isTimeRunning = false
     private lateinit var timer: CountDownTimer
     private lateinit var speechRecognizer: SpeechRecognizer
     var level: Int = 0
     var totalTime = 0
     var words: List<String> = ArrayList()
     var isFirstClick = true
     var isSecondClick = false
     var wordsString: ArrayList<String> = ArrayList()
     var formatter = SimpleDateFormat()
     var date = Date()
     var speakStartTime = String()
     var noun = String()
     var verb = String()
     var adjective = String()
     private var phrases = 0

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         arguments?.let {
             adjective = it.getString("adjective").toString()
             noun = it.getString("noun").toString()
             verb = it.getString("verb").toString()
         }
     }

     override fun onCreateView(
         inflater: LayoutInflater,
         container: ViewGroup?,
         savedInstanceState: Bundle?
     ): View {
         binding = FragmentGrammarAwareSpeakBinding.inflate(inflater)
         viewModel = ViewModelProviders.of(requireActivity())[GrammarAwareViewModel::class.java]
         return binding.root
     }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         updateTime(timeMilliSec)

         if (ContextCompat.checkSelfPermission(
                 requireActivity(),
                 Manifest.permission.RECORD_AUDIO
             ) != PackageManager.PERMISSION_GRANTED
         ) {
             ActivityCompat.requestPermissions(
                 requireActivity(),
                 arrayOf(Manifest.permission.RECORD_AUDIO),
                 1
             )
         }

         date = Calendar.getInstance().time
         formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

         if (level == 0) {
             startTime(false)
             val nounList = noun.split("\n")
             val adjectiveList = adjective.split("\n")
             val verbList = verb.split("\n")
             for (i in 0 until 3) {
                 val string = StringBuilder()
                 string.append(nounList.random()).append(" ")
                 string.append(verbList.random())
                 if (requireActivity().intent.getIntExtra("level", 0) == 2) {
                     string.append(" ").append(adjectiveList.random())
                 }
                 wordsString.add(string.toString())
                 Log.e("random", string.toString())
             }
             Log.e("Array ", wordsString.toString())
             speakStartTime = formatter.format(Calendar.getInstance().time)
             binding.words = wordsString[0].replace(" ", "\n")
             binding.level = "0"
         } else if (level == 1) {
             timeMilliSec= 70000
             startTime(false)
             binding.level = "1"
             binding.words = wordsString[1].replace(" ", "\n")
             speakStartTime = formatter.format(Calendar.getInstance().time)
         } else {
             timeMilliSec= 70000
             startTime(false)
             binding.level = "1"
             binding.words = wordsString[2].replace(" ", "\n")
             speakStartTime = formatter.format(Calendar.getInstance().time)
         }


         speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity())
         val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
         intent.putExtra(
             RecognizerIntent.EXTRA_LANGUAGE_MODEL,
             RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
         )
         intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
         intent.putExtra(
             RecognizerIntent.ACTION_RECOGNIZE_SPEECH,
             RecognizerIntent.EXTRA_PREFER_OFFLINE
         )
         intent.putExtra("android.speech.extra.DICTATION_MODE", true)
         intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
         intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000)
         intent.putExtra(
             RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
             5000
         )
         intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 70000);


         binding.txSpeech.movementMethod = ScrollingMovementMethod()
         binding.ivSpeaker.setOnClickListener {
             if (isFirstClick) {
                 speechRecognizer.startListening(intent)
                 binding.ivSpeaker.setCardBackgroundColor(
                     ContextCompat.getColor(
                         requireContext(),
                         R.color.green
                     )
                 )
                 binding.ivSpeaker.strokeColor =
                     ContextCompat.getColor(requireContext(), R.color.green)
                 binding.speakerOff.visibility = View.GONE
                 binding.speakerOn.visibility = View.VISIBLE
                 startSpeech()
                 isSecondClick = true
                 isFirstClick = false
             } else if (isSecondClick) {
                 Log.e(
                     "VAlies ",
                     binding.txSpeech.text.isEmpty().toString() + "  " + binding.txSpeech.text
                 )
                 if (binding.txSpeech.text.isEmpty()) {
                     Toast.makeText(
                         requireContext(),
                         "Please say something, I'm listening!",
                         Toast.LENGTH_SHORT
                     ).show()
                 } else {
                     stopTime()
                     speechRecognizer.stopListening()
                     binding.ivSpeaker.let {
                         it.setCardBackgroundColor(
                             ContextCompat.getColor(
                                 requireContext(),
                                 R.color.white
                             )
                         )
                         it.strokeColor = ContextCompat.getColor(requireContext(), R.color.blue)
                     }
                     binding.restartSpeech.setImageResource(R.drawable.restart_blue)
                     binding.speakerOn.visibility = View.GONE
                     binding.submit.visibility = View.VISIBLE
                     binding.mic.setImageResource(R.drawable.vector)
                     binding.ivMic.strokeWidth = 0
                     binding.tvActive.let {
                         it.text = getString(R.string.stand_by)
                         it.setTextColor(
                             ContextCompat.getColor(
                                 requireContext(),
                                 R.color.divider_grey
                             )
                         )
                         it.typeface = Typeface.DEFAULT
                     }
                     isFirstClick = false
                     isSecondClick = false
                 }
             } else {
                 binding.ivSpeaker.setCardBackgroundColor(
                     ContextCompat.getColor(
                         requireContext(),
                         R.color.blue
                     )
                 )
                 binding.ivSpeaker.strokeColor =
                     ContextCompat.getColor(requireContext(), R.color.blue)
                 binding.speakerOff.visibility = View.VISIBLE
                 binding.submit.visibility = View.GONE
                 speechRecognizer.stopListening()
                 changeScreen()
             }
         }

         binding.restartSpeech.setOnClickListener {
             speechRecognizer.startListening(intent)
             startTime(true)
             startSpeech()
             binding.ivSpeaker.let {
                 it.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                 it.strokeColor = ContextCompat.getColor(requireContext(), R.color.green)
             }
             binding.restartSpeech.setImageResource(R.drawable.restart_gray)
             binding.speakerOn.visibility = View.VISIBLE
             binding.submit.visibility = View.GONE
             binding.ivMic.strokeWidth = 2
             binding.mic.setImageResource(R.drawable.mic_on)
             binding.tvActive.let {
                 it.text = getString(R.string.active)
                 it.setTextColor(ContextCompat.getColor(requireContext(), R.color.border_color))
                 it.typeface = Typeface.DEFAULT_BOLD
             }
             isFirstClick = true
         }
     }

     fun startSpeech() {
         speechRecognizer.setRecognitionListener(object : RecognitionListener {
             override fun onReadyForSpeech(p0: Bundle?) {

             }

             override fun onBeginningOfSpeech() {
             }

             override fun onRmsChanged(p0: Float) {
             }

             override fun onBufferReceived(p0: ByteArray?) {
             }

             override fun onEndOfSpeech() {
             }

             override fun onError(p0: Int) {
             }

             override fun onResults(p0: Bundle) {
                 val data = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                 if (data != null) {
                     binding.txSpeech.text = data[0]
                     words = data[0].split(" ")
                 }
             }

             override fun onPartialResults(p0: Bundle?) {
                 if (p0 != null) {
                     val data = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                     if (data != null && data.size > 0) {
                         binding.txSpeech.text = data[0]
                     }

                 }
             }

             override fun onEvent(p0: Int, p1: Bundle?) {
             }

         })
     }

     private fun changeScreen() {
         val bundle = Bundle()
         bundle.putStringArrayList("words", wordsString)
         if (level == 0) {
             val textWords = binding.txSpeech.text.split(" ")
             level = 1
             AppConstant.Time1 = totalTime

             AppConstant.arrayS.add(textWords.size)
             Log.e("Length", textWords.size.toString() + " / /" + words.size)
             upLoadData(
                 binding.txSpeech.text.toString(),
                 wordsString[0],
                 speakStartTime,
                 formatter.format(Calendar.getInstance().time)
             )
             isFirstClick = true
             isSecondClick =false

         } else if (level == 1) {
             level = 2
             AppConstant.Time2 = totalTime
             val textWords = binding.txSpeech.text.split(" ")
             AppConstant.arrayS.add(textWords.size)
             Log.e("Length", textWords.size.toString() + " / /" + words.size)
             upLoadData(
                 binding.txSpeech.text.toString(),
                 wordsString[1],
                 speakStartTime,
                 formatter.format(Calendar.getInstance().time)
             )
             isFirstClick = true
             isSecondClick =false
         } else {
             val textWords = binding.txSpeech.text.split(" ")
             AppConstant.arrayS.add(textWords.size)
             Log.e("Length", textWords.size.toString() + " / /" + words.size)
             upLoadData(
                 binding.txSpeech.text.toString(),
                 wordsString[2],
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
                 if (isVisible) {
                     changeScreen()
                     speechRecognizer.stopListening()
                     totalTime = 70
                 }
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

         var timeLeft: String = " $minutes:"
         if (seconds < 10) timeLeft += "0"
         timeLeft += seconds

         viewModel.timerLiveData.value = timeLeft
         binding.timerProgress = l.toInt()
     }

     override fun onPause() {
         super.onPause()
         speechRecognizer.destroy()
     }

     fun upLoadData(
         sentences: String,
         referenceWords: String,
         speakStartTime: String,
         speakEndTime: String
     ) {
         calculateAccuracy(referenceWords, sentences)
         val jsonObject = JSONObject()
         jsonObject.put("Sentences", sentences)
         jsonObject.put("ReferenceWords", referenceWords)
         jsonObject.put("SpeakStartTime", speakStartTime)
         jsonObject.put("SpeakEndTime", speakEndTime)
         SensifyAwareApplication.addGrammarAwareObjectInJSON(jsonObject)
     }

     private fun calculateAccuracy(referenceWords: String, sentences: String) {
         val query = JSONObject()
         query.put(
             "UserId",
             prefUtils.getIntData(requireContext(), AppConstant.SharedPreferences.USER_ID)
         )
         query.put("Sentence", sentences)
         query.put("ReferenceWord", referenceWords)
         query.put("Url", "/grammer-aware-sentence-accuracy")

         val body: RequestBody = query.toString()
             .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
         apiManager.calculateAccuracy(body).subscribe({
             if (it.isSuccess) {
                 AppConstant.Accuracy.add(it.dataList.accuracy)
                 if (level == 2) {
                     val avgTime = (AppConstant.Time1 + AppConstant.Time2 + totalTime) / 3F

                     startActivity(
                         Intent(
                             requireActivity(),
                             GrammarAwareResultActivity::class.java
                         ).putExtra("avgTime", avgTime)
                     )
                 }
             } else {
                 Toast.makeText(
                     requireContext(),
                     "Please say something, I'm listening!",
                     Toast.LENGTH_SHORT
                 ).show()
             }
         }, {})
     }*/
}