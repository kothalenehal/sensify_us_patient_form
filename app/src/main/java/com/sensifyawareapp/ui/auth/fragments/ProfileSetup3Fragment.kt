package com.sensifyawareapp.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentProfileSetup3Binding
import com.sensifyawareapp.fragment.BaseFragment
import kotlin.math.roundToInt


class ProfileSetup3Fragment : BaseFragment() {
    private lateinit var binding: FragmentProfileSetup3Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile_setup3,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initListener()
    }

    private fun init() {

    }

    private fun initListener() {
        binding.btnNext.setOnClickListener {
            findNavController().navigate(ProfileSetup3FragmentDirections.actionProfileSetup3FragmentToProfileSetup4Fragment())
        }
        binding.cvYes.setOnClickListener {
            binding.cvYes.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.text_color)
            binding.cvNo.strokeColor = ContextCompat.getColor(requireContext(), R.color.green)
            binding.cvYes.strokeWidth = resources.getDimension(R.dimen.strokeWidth).roundToInt()
            binding.cvNo.strokeWidth = 0
        }
        binding.cvNo.setOnClickListener {
            binding.cvNo.strokeWidth = resources.getDimension(R.dimen.strokeWidth).roundToInt()
            binding.cvYes.strokeWidth = 0
            binding.cvYes.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.text_color)
            binding.cvNo.strokeColor =
                ContextCompat.getColor(requireContext(), R.color.green)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileSetup3Fragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}