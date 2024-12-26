package com.sensifyawareapp.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentProfileSetup2Binding
import kotlin.math.roundToInt

class ProfileSetUp2Fragment : Fragment() {
    private lateinit var binding: FragmentProfileSetup2Binding

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
            R.layout.fragment_profile_setup2,
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

        binding.ivBack.setOnClickListener {

        }


        binding.btnYes.setOnClickListener {
//            SensifyAwareApplication.scentAwareTestData.StuffyNose = 1


            findNavController().navigate(ProfileSetUp2FragmentDirections.actionProfileSetUp2FragmentToProfileSetup3Fragment())

        }
        binding.btnNo.setOnClickListener {
//            SensifyAwareApplication.scentAwareTestData.StuffyNose = 0
            findNavController().navigate(ProfileSetUp2FragmentDirections.actionProfileSetUp2FragmentToProfileSetup3Fragment())
        }

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileSetUp2Fragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}