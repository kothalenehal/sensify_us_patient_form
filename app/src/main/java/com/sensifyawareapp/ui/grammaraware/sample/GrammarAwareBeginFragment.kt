package com.sensifyawareapp.ui.grammaraware.sample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentGrammarAwareBeginBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.grammaraware.GrammarAwareViewModel
import java.util.*

class GrammarAwareBeginFragment : BaseFragment() {
    private lateinit var viewModel: GrammarAwareViewModel
    private lateinit var binding: FragmentGrammarAwareBeginBinding
    var timeMilliSec: Long = 70000
    private var phrases = 0
    var noun = String()
    var verb = String()
    var adjective = String()
    var wordsString: ArrayList<String> = ArrayList()
    var isTimeRunning = false
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            adjective = it.getString("adjective").toString()
            noun = it.getString("noun").toString()
            verb = it.getString("verb").toString()
            phrases = it.getInt("level", 0)
            if (phrases != 0) {
                wordsString = it.getStringArrayList("words") as ArrayList<String>
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGrammarAwareBeginBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(requireActivity())[GrammarAwareViewModel::class.java]
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
//        val stringLatter: Array<String>
        /*  if (requireActivity().intent.getIntExtra("level", 0) == 1) {
              stringLatter = arrayOf(
                  "super supper", "dinner tiger",
                  "later busy", "paper penny",
                  "tiny over", "rabbit unit",
                  "hello crazy", "lesson summer",
                  "silent happen", "funny winter",
                  "female better", "after moment",
                  "sister finger", "chapter duty",
                  "blanket pillow", "only yellow",
                  "human seven", "river pilot",
                  "meeting wagon", "lemon eagar",
                  "finish student", "leader lazy",
                  "easy second", "sneaker humor",
                  "athlete pilgrim", "control complete",
                  "poet duet", "monster riot",
                  "halfway kitchen", "poem english",
                  "cruel giant", "kingdom inspect",
                  "e-drop double", "hoping hopping",
                  "skipped plotting", "joking wrapping",
                  "telling winning", "racing chatted",
                  "skated panting", "dusted pasted"
              )
          } else {
              stringLatter = arrayOf(
                  "super supper diner", "dinner tiger happy",
                  "later busy pretty", "paper penny zero",
                  "tiny over puppy", "rabbit unit kitten",
                  "hello crazy letter", "lesson summer open",
                  "silent happen number", "funny winter follow",
                  "female better problem", "after moment pattern",
                  "sister finger bottom", "chapter duty member",
                  "blanket pillow never", "only yellow window",
                  "human seven reason", "river pilot visit",
                  "meeting wagon planet", "lemon eagar peanut",
                  "finish student limit", "leader lazy present",
                  "easy second music", "sneaker humor minute",
                  "athlete pilgrim create", "control complete children",
                  "poet duet pumpkin", "monster riot mushroom",
                  "halfway kitchen trial", "poem english hundred",
                  "cruel giant lion", "kingdom inspect diet",
                  "e-drop double nothing", "hoping hopping leaking",
                  "skipped plotting quoted", "joking wrapping greeted",
                  "telling winning hunted", "racing chatted shouting",
                  "skated panting floated", "dusted pasted needed"
              )
          }*/

        if (phrases == 0) {
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
            binding.words = wordsString[0].replace(" ", "\n")
        } else if (phrases == 1) {
            binding.words = wordsString[1].replace(" ", "\n")
        } else {
            binding.words = wordsString[2].replace(" ", "\n")
        }
       /* val minutes: Int = timeMilliSec.toInt() / 60000
        val seconds: Int = timeMilliSec.toInt() % 60000 / 1000

        var timeLeft: String = " $minutes:"
        if (seconds < 10) timeLeft += "0"
        timeLeft += seconds
        viewModel.timerLiveData.value = timeLeft
        binding.timerProgress = timeMilliSec.toInt()*/
        binding.level = phrases.toString()
        binding.ivSpeaker.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("level", phrases)
            bundle.putStringArrayList("words", wordsString)
            bundle.putString("name", binding.words)
            findNavController().navigate(
                R.id.action_grammarAwareBeginFragment_to_grammarAwareSpeakFragment,
                bundle
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
//                totalTime = timer - time
//                Log.e("Time ", "$time // $timer  // $total")
            }

            override fun onFinish() {
                if (isVisible) {
//                    changeScreen()
//                    speechRecognizer.stopListening()
//                    totalTime = 70
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
}