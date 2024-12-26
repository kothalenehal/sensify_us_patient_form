package com.sensifyawareapp.ui.audioaware.fragments

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentAudioAwareCountdownBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.audioaware.AudioAwareViewModel
import kotlin.random.Random


class AudioAwareCountdownFragment : BaseFragment() {

    private lateinit var timer: CountDownTimer
    private lateinit var wordsToDisplayList: ArrayList<String>
    private lateinit var wordsToSpeakList: ArrayList<String>

    private lateinit var binding: FragmentAudioAwareCountdownBinding
    private lateinit var viewModel: AudioAwareViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioAwareCountdownBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(requireActivity())[AudioAwareViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                viewModel.timerLivedata.value = "00:0${millisUntilFinished / 1000}"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.progressTimer.setProgress((millisUntilFinished / 1000).toInt(), true)
                } else binding.timerProgress = (millisUntilFinished / 1000).toInt()

            }

            override fun onFinish() {
                if (isVisible) {
                    /*  val hs = LinkedHashSet<String>()
                      hs.addAll(wordsToSpeakList)
                      wordsToSpeakList.clear()
                      wordsToSpeakList.addAll(hs)
                      Log.e("11", "wordsToSpeakList:${wordsToSpeakList.size} // $wordsToSpeakList")

                      val hs2 = LinkedHashSet<String>()
                      hs2.addAll(wordsToDisplayList)
                      wordsToDisplayList.clear()
                      wordsToDisplayList.addAll(hs2)
                      Log.e("22", "wordsToDisplayList:${wordsToDisplayList.size} // $wordsToDisplayList")*/

                    val bundle = Bundle()
                    Log.e(
                        "TAG",
                        "setupList: 12345 ${wordsToDisplayList.size} // ${wordsToSpeakList.size}"
                    )

                    bundle.putStringArrayList("wordsToSpeakList", wordsToSpeakList)
                    bundle.putStringArrayList("wordsToDisplayList", wordsToDisplayList)
                    findNavController().navigate(
                        R.id.action_AudioAwareCountdownFragment_to_AudioAwareListingFragment,
                        bundle
                    )
                }
            }
        }
        timer.start()

        readWordsFromFile()
    }

    override fun onDestroy() {
        timer.cancel();
        super.onDestroy()
    }

    private fun readWordsFromFile() {
        try {
            /*val words = context?.assets?.open("words_for_audio_aware.txt")?.bufferedReader().use {
                it?.readText()
            }*/

            val words = context?.assets?.open(getString(R.string.changelog_file))?.bufferedReader()
                .use {
                    it?.readText()
                }
            words?.let { pickRandom20Words(it) }
        } catch (e: Exception) {
            showError(getString(R.string.unknown_error_occurred))
            e.printStackTrace()
        }
    }

    private fun pickRandom20Words(words: String) {
        val wordsList = words.split("\n")
        val hs = LinkedHashSet<String>()
        val hs2 = LinkedHashSet<String>()
        wordsToSpeakList = ArrayList()
        val random = Random(System.nanoTime())
        while (wordsToSpeakList.size < 20) {
            val nextInt = (wordsList.indices).random(random)
            if (!wordsToSpeakList.contains(wordsList[nextInt].trim())) {
                wordsToSpeakList.add(wordsList[nextInt].trim())
                hs.add(wordsList[nextInt].trim())
            }

        }

        wordsToDisplayList = ArrayList()
        while (wordsToDisplayList.size < 20) {
            val nextInt = (wordsList.indices).random(random)
            if (!wordsToDisplayList.contains(wordsList[nextInt].trim())) {
                if (!wordsToSpeakList.contains(wordsList[nextInt].trim())) {
                    wordsToDisplayList.add(wordsList[nextInt].trim())
                    hs2.add(wordsList[nextInt].trim())
                }
            }

        }
        wordsToSpeakList.clear()
        wordsToSpeakList.addAll(hs)

        wordsToDisplayList.clear()
        wordsToDisplayList.addAll(hs2)

        val hs3 = LinkedHashSet<String>()
        hs3.addAll(wordsToDisplayList)
        hs3.addAll(wordsToSpeakList)
        Log.e("11", "List:${hs3.size} // $hs3 ")

    }


    override fun onPause() {
        super.onPause()
        viewModel.timerLivedata.value = ""
    }
}