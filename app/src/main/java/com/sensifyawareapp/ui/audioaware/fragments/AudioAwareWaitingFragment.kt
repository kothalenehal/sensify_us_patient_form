package com.sensifyawareapp.ui.audioaware.fragments

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentAudioAwareWaitingBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.audioaware.AudioAwareActivity


class AudioAwareWaitingFragment : BaseFragment() {

    private lateinit var timer: CountDownTimer
    private lateinit var binding: FragmentAudioAwareWaitingBinding
    private var minute = 0
    var isTimeFinish = false
    private lateinit var wordsToDisplayList: ArrayList<String>
    private lateinit var wordsToSpeakList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            minute = it.getInt("minute")
            wordsToSpeakList = it.getStringArrayList("wordsToSpeakList")!!
            wordsToDisplayList = it.getStringArrayList("wordsToDisplayList")!!
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioAwareWaitingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.minute = minute
        binding.maxProgress = minute * 60

        timer = object : CountDownTimer(((minute * 60) * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.progressbar.setProgress((millisUntilFinished / 1000).toInt(), true)
                } else binding.timerProgress = (millisUntilFinished / 1000).toInt()
                val seconds: Long = (millisUntilFinished / 1000) % 60
                /*binding.time =
                    "0${(millisUntilFinished / 1000) / 60}:${
                        ((millisUntilFinished / 1000) % 60)
                    }"*/

                var timeLeft: String = "0${(millisUntilFinished / 1000) / 60}:"
                if (seconds < 10) timeLeft += "0"
                timeLeft += seconds
                binding.time = timeLeft

                Log.e("Time Audio ", timeLeft)
            }

            override fun onFinish() {
                if (isVisible) {
                    Log.e("Call ", "onFinish ")
                    val mediaPlayer: MediaPlayer = MediaPlayer.create(requireActivity(), R.raw.beep)
                    mediaPlayer.start()
                    isTimeFinish = true
                    val bundle = Bundle()
                    bundle.putStringArrayList("wordsToSpeakList", wordsToSpeakList)
                    bundle.putStringArrayList("wordsToDisplayList", wordsToDisplayList)
                    findNavController().navigate(
                        R.id.action_AudioAwareWaitingFragment_to_AudioAwareRecallFragment,
                        bundle
                    )
                }
            }
        }
        timer.start()

        binding.btnStopTest.setOnClickListener { (requireActivity() as AudioAwareActivity).showCloseDialog() }
    }

    override fun onResume() {
        super.onResume()
        Log.e("CALL" ,"RESUME CALL")
        if (isTimeFinish) {
            val bundle = Bundle()
            bundle.putStringArrayList("wordsToSpeakList", wordsToSpeakList)
            bundle.putStringArrayList("wordsToDisplayList", wordsToDisplayList)
            findNavController().navigate(
                R.id.action_AudioAwareWaitingFragment_to_AudioAwareRecallFragment,
                bundle
            )
            isTimeFinish = false
        }
    }

    override fun onDestroy() {
        timer.cancel();
        super.onDestroy()
    }
}