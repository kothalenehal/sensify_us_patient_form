package com.sensifyawareapp.ui.audioaware.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.databinding.FragmentAudioAwareBeginningBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*

class AudioAwareBeginningFragment : BaseFragment() {

    private lateinit var binding: FragmentAudioAwareBeginningBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioAwareBeginningBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefUtils.saveData(
            requireActivity(),
            AppConstant.SharedPreferences.START_TIME,
            Calendar.getInstance().timeInMillis
        )
        Log.e("TAG", "onViewCreated: Time ${Calendar.getInstance().timeInMillis}")
        binding.btnSubmit.setOnClickListener {
            findNavController().navigate(AudioAwareBeginningFragmentDirections.actionAudioAwareBeginningFragmentToAudioAwareCountdownFragment())
        }
    }
}