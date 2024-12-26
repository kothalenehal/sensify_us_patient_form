package com.sensifyawareapp.ui.wordsaware.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentWordsAwareCountdownBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.utils.common.AppConstant

class WordsAwareCountdownFragment : BaseFragment() {
    private lateinit var binding: FragmentWordsAwareCountdownBinding
    private lateinit var timer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordsAwareCountdownBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppConstant.wordsList = ArrayList()
        timer = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timerProgress = (millisUntilFinished / 1000).toInt()
                binding.tvTimer.text = "0${millisUntilFinished / 1000}"
            }

            override fun onFinish() {
                if (isVisible) {
                    findNavController().navigate(WordsAwareCountdownFragmentDirections.actionWordsAwareCountdownFragmentToWordsAwareListeningFragment(    ))
                }
            }
        }
        timer.start()
    }

}