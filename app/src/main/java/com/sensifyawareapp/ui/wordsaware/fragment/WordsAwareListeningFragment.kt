package com.sensifyawareapp.ui.wordsaware.fragment

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentWordsAwareListeningBinding
import com.sensifyawareapp.databinding.LayoutAlertDialogBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.ui.wordsaware.WordsAwareViewModel
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*


class WordsAwareListeningFragment : BaseFragment() {
    private lateinit var binding: FragmentWordsAwareListeningBinding
    private lateinit var timer: CountDownTimer
    private lateinit var viewModel: WordsAwareViewModel
    var timeMilliSec: Long = 60000
    var data: ArrayList<String>? = ArrayList()
    private lateinit var speechRecognizer: SpeechRecognizer
    var words: List<String> = kotlin.collections.ArrayList()
    var spokenText = StringBuilder()
    var listData: ArrayList<String> = ArrayList()
    var isTimeFinish = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordsAwareListeningBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(requireActivity())[WordsAwareViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val strings = arrayOf(
            "ab",
            "ae",
            "ad",
            "af",
            "ag",
            "ah",
            "ap",
            "aq",
            "ar",
            "as",
            "at",
            "au",
            "al",
            "ba",
            "be",
            "bl",
            "bi",
            "bo",
            "bu",
            "ca",
            "ci",
            "cl",
            "cu",
            "co",
            "ce",
            "cr",
            "ch",
            "di",
            "do",
            "da",
            "dr",
            "de",
            "du",
            "pa",
            "pe",
            "pl",
            "ps",
            "ph",
            "po",
            "pi",
            "ra",
            "ro",
            "ru",
            "ri",
            "sh",
            "sa",
            "se",
            "st",
            "si",
            "th",
            "te",
            "ti",
            "ta",
            "to",
            "tr",
            "va",
            "ve",
            "vi",
            "vo",
            "wi",
            "we",
            "wh",
            "wo",
            "yo",
            "ye",
            "ya"
        )
        val random = strings[(Math.random() * strings.size).toInt()]
        binding.words = random

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


        /*if (mSpeechManager == null) {
            SetSpeechListener()
        } else if (!mSpeechManager!!.ismIsListening()) {
            mSpeechManager!!.destroy()
            SetSpeechListener()
        }*/


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
//        intent.putExtra("android.speech.extra.DICTATION_MODE", true)
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000)
        intent.putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
            5000
        )
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 60000)
        speechRecognizer.startListening(intent)
        updateTime(timeMilliSec)

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
                if (data?.isNotEmpty() == true /*&& listData.isNotEmpty()*/) {
                    Log.e(" Text Result 22 ", data!!.last())
                    spokenText.append(binding.nameText1?.text.toString())
                    Log.e("Speech 44", "" + binding.nameText1?.text.toString())
                }
            }

            override fun onError(p0: Int) {
                Log.e("Error", "" + p0)
            }

            override fun onResults(p0: Bundle) {
                val data = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (data != null) {
                    Log.e(" Text Result ", data.toString())
                }
            }

            override fun onPartialResults(p0: Bundle?) {
                if (p0 != null) {
                    data = p0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (data != null) {
                        if (data!!.isNotEmpty()) {
                            if (data!![0] != "") {
                                binding.nameText1?.text = data!![0]
                                listData.add(binding.nameText1?.text.toString())
                                Log.e("Speech 33", "" + data!!.last())
                            }
                        }
                    }
                }

            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }
        })

        timer = object : CountDownTimer(timeMilliSec, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeMilliSec = millisUntilFinished
                updateTime(millisUntilFinished)
                binding.timerProgress = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                speechRecognizer.stopListening()
                changeScreen()
                isTimeFinish = true
            }
        }
        timer.start()

        binding.btnSubmit.setOnClickListener {
            if (spokenText.isNotEmpty() || binding.nameText1?.text.toString()
                    .isNotEmpty() && binding.nameText1?.text != null
            ) {
                Log.e("Words ", spokenText.toString() + " // " + (binding.nameText1?.text))
                speechRecognizer.stopListening()
                changeScreen()
            } else {
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

                alertBinding.btnSubmit.setOnClickListener {
                    dialog.dismiss()
                    startActivity(
                        Intent(
                            requireActivity(),
                            MainActivity::class.java
                        )
                    )
                }
                speechRecognizer.stopListening()
                timer.cancel()
            }
        }
    }

    fun changeScreen() {
        if (isVisible) {
            findWords()
            timer.cancel()
            viewModel.timerLivedata.value = ""
            Log.e("Dataa ", words.size.toString())
            val bundle = Bundle()
            bundle.putString("randomWord", binding.words)
            findNavController().navigate(
                R.id.action_wordsAwareListeningFragment_to_wordsAwareTestOverFragment,
                bundle
            )
        }
    }

    fun updateTime(l: Long) {
        var timeLeft: String = "00:${l / 1000}"
        if ((l / 1000).toInt() < 10) {
            timeLeft = "00:0${l / 1000}"
        }
        viewModel.timerLivedata.value = timeLeft
        binding.timerProgress = l.toInt()
    }

    private fun findWords() {

        words = spokenText.split(" ")
        val result: ArrayList<String> = ArrayList()
        for (s in words) {
            if (s.startsWith(binding.nameText.text, true)) {
                result.add(s)
            }
        }

        val words12: List<String> = spokenText.split(" ")
        val result1: ArrayList<String> = ArrayList()
        for (s in words12) {
            result1.add(s)

        }


        Log.e("Result 112213 ", result1.toString() + "  // " + result1.joinToString())

        /* val newList: ArrayList<String> = ArrayList<String>()
         for (element in result) {
             if (!newList.contains(element)) {
                 newList.add(element)
             }
         }
         Log.e("newList ", newList.toString())*/

        var longestWord = ""
        for (word in result) {
            if (word.length > longestWord.length) {
                longestWord = word
            }
        }

        var shortestWord = longestWord
        for (word in result) {
            if (word.length < shortestWord.length) {
                shortestWord = word
            }
        }
        val nonRepeating: MutableSet<String> = HashSet()
        val repeating: MutableSet<String> = HashSet()

        for (i in result) {
            if (!repeating.contains(i)) {
                if (nonRepeating.contains(i)) {
                    repeating.add(i)
                    nonRepeating.remove(i)
                } else {
                    nonRepeating.add(i)
                }
            }
        }

        Log.e("Count nonRepeat", nonRepeating.toString())
        Log.e("Count Repeat", result.distinct().toString())

        AppConstant.longestWord = longestWord
        AppConstant.shortestWord = shortestWord

        Log.e("Words Repeated Size",
            result.groupingBy { it }.eachCount().filter { it.value > 1 }.values.size.toString()
        )

        AppConstant.totalWord = result.distinct().size
        AppConstant.uniqueWord = nonRepeating.size
        AppConstant.repeatWord =
            result.groupingBy { it }.eachCount().filter { it.value > 1 }.values.size
        AppConstant.wordsList.addAll(listOf(result1.joinToString()))
        Log.e("TAG", "findWords: ${AppConstant.wordsList.joinToString()}")
        val list: String =
            Arrays.toString(AppConstant.wordsList.toArray()).replace("[", "").replace("]", "")
        Log.e("Count 11", list)

    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        Log.e("Call ", "onResume ")
        if (isTimeFinish) {
            changeScreen()
            isTimeFinish = false
        }

    }
}