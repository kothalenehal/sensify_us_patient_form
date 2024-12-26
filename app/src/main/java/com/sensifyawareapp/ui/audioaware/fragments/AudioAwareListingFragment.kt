package com.sensifyawareapp.ui.audioaware.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentAudioAwareListingBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.audioaware.AudioAwareActivity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale


class AudioAwareListingFragment : BaseFragment() {
    private lateinit var wordsToDisplayList: ArrayList<String>
    private lateinit var wordsToSpeakList: ArrayList<String>
    private lateinit var binding: FragmentAudioAwareListingBinding
    private var isPaused = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            wordsToSpeakList = it.getStringArrayList("wordsToSpeakList")!!
            wordsToDisplayList = it.getStringArrayList("wordsToDisplayList")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioAwareListingBinding.inflate(inflater)
        binding.ivDone.bringToFront()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnStopTest.setOnClickListener { (requireActivity() as AudioAwareActivity).showCloseDialog() }
        binding.btnNext.setOnClickListener {
            val bundle = Bundle()
            bundle.putStringArrayList("wordsToSpeakList", wordsToSpeakList)
            bundle.putStringArrayList("wordsToDisplayList", wordsToDisplayList)

            if (requireActivity().intent.getIntExtra("level", 0) == 1) {
                findNavController().navigate(
                    R.id.action_AudioAwareListingFragment_to_AudioAwareRecallFragment,
                    bundle
                )
            } else {
                bundle.putInt(
                    "minute",
                    if (requireActivity().intent.getIntExtra("level", 0) == 2) 2 else 10
                )
                findNavController().navigate(
                    R.id.action_AudioAwareListingFragment_to_AudioAwareWaitingFragment,
                    bundle
                )
            }
        }
        startSpeaking()
    }

    private lateinit var textToSpeech: TextToSpeech

    @OptIn(DelicateCoroutinesApi::class)
    private fun startSpeaking() {
        textToSpeech = TextToSpeech(requireActivity()) { i ->
            if (i != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.US
//                textToSpeech.setSpeechRate(0.8f)

//                textToSpeech.setPitch(1.0F)
                speak()
                /*TextToSpeech.OnInitListener {

                }*/

                /*   Handler(Looper.getMainLooper()).postDelayed({

                   }, 2000)*/

                textToSpeech.setOnUtteranceProgressListener(object :
                    UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.e("---", "onStart: $utteranceId")
                    }

                    override fun onDone(utteranceId: String?) {
                        binding.progress = index
                        GlobalScope.launch { // launch new coroutine in background and continue
                            if (!isPaused) {
                                delay(1000L)
                                speak()
                            }
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e("---", "onError: $utteranceId")
                    }
                })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        speak()
        isPaused = false
    }

    override fun onPause() {
        super.onPause()
        textToSpeech.stop()
        isPaused = true
        if (index != wordsToDisplayList.size - 1)
            index--
    }

    override fun onStop() {
        super.onStop()
        textToSpeech.stop()
        isPaused = true
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.stop()
        isPaused = true
    }

    var index = 0

    private fun speak() {
//        Log.e("TAG  ", "speak: ${wordsToSpeakList[index]}", )
        if (index < wordsToSpeakList.size) {
            Log.e("TAG  ", "speak: 1122 ${wordsToSpeakList[index]}")

            if (index == 0) {
//                Log.e("TAG", "speak: INDEX 0")
                Handler(Looper.getMainLooper()).postDelayed({
                    if (index >= 0 && index < wordsToSpeakList.size)
                        textToSpeech.speak(
                            wordsToSpeakList[index],
                            TextToSpeech.QUEUE_ADD,
                            null,
                            wordsToSpeakList[index++]
                        )
                    textToSpeech.playSilentUtterance(1500, TextToSpeech.QUEUE_ADD, null)
                }, 2000)

            } else {
//                Log.e("TAG", "speak: INDEX NOT")
                textToSpeech.speak(
                    wordsToSpeakList[index],
                    TextToSpeech.QUEUE_ADD,
                    null,
                    wordsToSpeakList[index++]
                )
                textToSpeech.playSilentUtterance(1500, TextToSpeech.QUEUE_ADD, null)
            }

            /* textToSpeech.speak(
                 wordsToSpeakList[index],
                 TextToSpeech.QUEUE_ADD,
                 null,
                 wordsToSpeakList[index++]
             )*/


        } else {
            Log.e("TAG ", "speak: Call")
            //reading done:)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.isCompleted = true
            }, 1000)

        }
    }
}