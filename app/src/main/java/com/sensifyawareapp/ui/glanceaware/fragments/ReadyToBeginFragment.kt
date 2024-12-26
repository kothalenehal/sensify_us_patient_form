package com.sensifyawareapp.ui.glanceaware.fragments

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.databinding.FragmentReadyToBeginBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.glanceaware.GlanceAwareViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ReadyToBeginFragment : BaseFragment() {
    private lateinit var binding: FragmentReadyToBeginBinding
    private lateinit var viewModel: GlanceAwareViewModel

    private var minute = 0
    private lateinit var timer: CountDownTimer
    private var param1: String? = null
    private var param2: String? = null
    var isTimeFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReadyToBeginBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(requireActivity())[GlanceAwareViewModel::class.java]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.level == 2) minute = 2 else minute = 10
        binding.maxProgress = minute * 60
        binding.minute = minute

        binding.showTimer = false


        timer = object : CountDownTimer(((minute * 60) * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    binding.progressbar.setProgress((millisUntilFinished / 1000).toInt(), true)
                } else binding.timerProgress = (millisUntilFinished / 1000).toInt()
                val seconds: Long = (millisUntilFinished / 1000) % 60
                /* binding.time =
                     "0${(millisUntilFinished / 1000) / 60}:${
                         ((millisUntilFinished / 1000) % 60)
                     }"*/


                var timeLeft: String = "0${(millisUntilFinished / 1000) / 60}:"
                if (seconds < 10) timeLeft += "0"
                timeLeft += seconds
                binding.time = timeLeft
                Log.e("Time ", timeLeft)
            }

            override fun onFinish() {
                Log.e("Call ", "OnFinish")
                if (isVisible) {
                    isTimeFinish = true
                    Log.e("Call ", "isVisible")
                    findNavController().navigate(
                        ReadyToBeginFragmentDirections.actionReadyToBeginFragmentToResponseFragment()
                    )
                }
            }
        }

        binding.btnBegin.setOnClickListener {
            if (viewModel.level != 1) {
                binding.showTimer = true
                timer.start()
            } else
                findNavController().navigate(ReadyToBeginFragmentDirections.actionReadyToBeginFragmentToResponseFragment())
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ReadyToBeginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        Log.e("Call " , "onResume ")
        if (isTimeFinish){
            findNavController().navigate(
                ReadyToBeginFragmentDirections.actionReadyToBeginFragmentToResponseFragment()
            )
            isTimeFinish = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}