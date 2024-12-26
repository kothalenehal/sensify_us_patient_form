package com.sensifyawareapp.ui.wordsaware.fragment

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sensifyawareapp.databinding.FragmentWordsAwareWaitingBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.wordsaware.WordsAwareResultActivity

class WordsAwareWaitingFragment : BaseFragment() {
    private lateinit var binding: FragmentWordsAwareWaitingBinding
    lateinit var randomWords: String
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            randomWords = it.getString("randomWord")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentWordsAwareWaitingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timer = object : CountDownTimer(3000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

            }

            override fun onFinish() {
                if (isVisible) {
                    startActivity(
                        Intent(requireActivity(), WordsAwareResultActivity::class.java)
                            .putExtra("randomWord", randomWords)
                    )
                }
            }
        }
        timer.start()
    }
}