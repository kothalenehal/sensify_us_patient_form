package com.sensifyawareapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentTrainingBinding
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.ui.scentaware.HealthsQuestionsActivity
import com.sensifyawareapp.utils.common.AppConstant
import kotlin.math.ceil

class TrainingFragment : BaseFragment() {
    lateinit var binding: FragmentTrainingBinding
    var isClinicalTestVersion: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrainingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isClinicalTestVersion =
            prefUtils.getBooleanData(
                requireContext(),
                AppConstant.SharedPreferences.CLINICAL_TEST_VERSION
            )

        val selectedMenu = prefUtils.getIntData(
            requireContext(),
            AppConstant.SharedPreferences.SELECTED_MENU
        )

        binding.isClinicalTestVersion = isClinicalTestVersion
        val myInteger = ceil(3.25)
        Log.e("Values ",myInteger.toString())

        if (isClinicalTestVersion) {
            Log.e("Call ", isClinicalTestVersion.toString() )
            binding.ivBack.visibility = View.VISIBLE
            binding.txAlert.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
        }

        binding.llScentAwareReTraining8.cardMenu.setOnClickListener {
            if (!networkUtils.isConnected) {
                showError(getString(R.string.internet_not_available))
            } else {
                prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.SELECTED_KIT_SIZE, 8)
                if (selectedMenu == 1 && !isClinicalTestVersion) startActivity(
                    Intent(
                        requireContext(),
                        HealthsQuestionsActivity::class.java
                    )
                )
                else {
                    SensifyAwareApplication.initTestData()
                    startActivity(Intent(requireContext(), TutorialActivity::class.java))
                }
            }
        }

        binding.llScentAwareReTraining16.cardMenu.setOnClickListener {
            if (!networkUtils.isConnected) {
                showError(getString(R.string.internet_not_available))
            } else {
                prefUtils.saveData(requireContext(), AppConstant.SharedPreferences.SELECTED_KIT_SIZE, 16)
                if (selectedMenu == 1 && !isClinicalTestVersion) startActivity(
                    Intent(
                        requireContext(),
                        HealthsQuestionsActivity::class.java))
                else {
                    SensifyAwareApplication.initTestData()
                    startActivity(Intent(requireContext(), TutorialActivity::class.java))
                }
            }
        }
    }
}