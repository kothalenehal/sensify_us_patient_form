package com.sensifyawareapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentReportsBinding
import com.sensifyawareapp.ui.MainViewModel
import com.sensifyawareapp.ui.trackprogress.ProgressReportActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*

class ReportsFragment : BaseFragment() {
    lateinit var binding: FragmentReportsBinding
    private val viewModel: MainViewModel by viewModels()
    var isClinicalTestVersion: Boolean = false
    var canTestScentAware: Boolean = true
    var canTestTraceAware: Boolean = true
    var canTestAudioAware: Boolean = true
    var canTestGlanceAware: Boolean = true
    var canTestWordsAware: Boolean = true
    var canTestGrammarAware: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReportsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.userName = "Reports Under Progress"

        isClinicalTestVersion =
            prefUtils.getBooleanData(
                requireContext(),
                AppConstant.SharedPreferences.CLINICAL_TEST_VERSION
            )

        binding.scentAwareLast8Test = getString(R.string.not_tested_yet)
        binding.scentAwareLast16Test = getString(R.string.not_tested_yet)
        binding.audioAwareLastTest = getString(R.string.not_tested_yet)
        binding.traceAwareLastTest = getString(R.string.not_tested_yet)
        binding.glanceAwareLastTest = getString(R.string.not_tested_yet)
        binding.wordsAwareLastTest = getString(R.string.not_tested_yet)
        binding.grammarAwareLastTest = getString(R.string.not_tested_yet)
        binding.canTestScentAware = true
        binding.canTestTraceAware = true
        binding.canTestAudioAware = true
        binding.canTestGlanceAware = true
        binding.canTestWordsAware = true
        binding.canTestGramarAware = true
        binding.scentAware8 = "ScentAware - 8 scent"
        binding.scentAware16 = "ScentAware - 16 scent"


        /*if (Locale.getDefault().displayLanguage.equals("English")) {
            binding.llGrammarAware.cardMenu.visibility = View.VISIBLE
            binding.llWordsAware.cardMenu.visibility = View.VISIBLE
        } else {
            binding.llGrammarAware.cardMenu.visibility = View.GONE
            binding.llWordsAware.cardMenu.visibility = View.GONE
        }*/

        getLastTestDates()
        binding.llScentaware8.constrainLayout.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProgressReportActivity::class.java
                ).putExtra("testType", "scent8")
            )
        }

        binding.llScentaware16.constrainLayout.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProgressReportActivity::class.java
                ).putExtra("testType", "scent16")
            )
        }
        binding.llTraceAware.constrainLayout.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProgressReportActivity::class.java
                ).putExtra("testType", "traceAware")
            )
        }
        binding.llAudioAware.constrainLayout.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProgressReportActivity::class.java
                ).putExtra("testType", "audioAware").putExtra("level", getString(R.string.easy))
            )
        }
        binding.llGlanceAware.constrainLayout.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProgressReportActivity::class.java
                ).putExtra("testType", "glanceAware")
            )
        }
        binding.llWordsAware.constrainLayout.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProgressReportActivity::class.java
                ).putExtra("testType", "wordsAware")
            )
        }
        binding.llGrammarAware.constrainLayout.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProgressReportActivity::class.java
                ).putExtra("testType", "grammarAware")
            )
        }

        /*binding.llGlanceAware.cardMenu.setOnClickListener {
            startActivity(Intent(requireContext(), GlanceReportActivity::class.java).putExtra("testType","glanceAware"))
        }*/
    }

    private fun getLastTestDates() {
        viewModel.scentAware8Date.observe(viewLifecycleOwner) {
            if (canTestScentAware && isClinicalTestVersion)
                binding.scentAwareLast8Test = getString(R.string.ready_to_test)
            else
                binding.scentAwareLast8Test = it

        }

        viewModel.scentAware16Date.observe(viewLifecycleOwner) {
            if (canTestScentAware && isClinicalTestVersion)
                binding.scentAwareLast16Test = getString(R.string.ready_to_test)
            else
                binding.scentAwareLast16Test = it
            Log.e("TAG", "getLastTestDates: $it", )

        }
        viewModel.traceAwareDate.observe(viewLifecycleOwner) {
            if (canTestTraceAware && isClinicalTestVersion)
                binding.traceAwareLastTest = getString(R.string.ready_to_test)
            else binding.traceAwareLastTest = it
        }
        viewModel.audioAwareDate.observe(viewLifecycleOwner) {
            if (canTestAudioAware && isClinicalTestVersion)
                binding.audioAwareLastTest = getString(R.string.ready_to_test)
            else binding.audioAwareLastTest = it
        }
        viewModel.glanceAwareDate.observe(viewLifecycleOwner) {
            if (canTestGlanceAware && isClinicalTestVersion)
                binding.glanceAwareLastTest = getString(R.string.ready_to_test)
            else binding.glanceAwareLastTest = it


        }

        viewModel.wordsAwareDate.observe(viewLifecycleOwner) {
            if (canTestWordsAware && isClinicalTestVersion)
                binding.wordsAwareLastTest = getString(R.string.ready_to_test)
            else binding.wordsAwareLastTest = it
        }
        viewModel.grammarAwareDate.observe(viewLifecycleOwner) {
            if (canTestGrammarAware && isClinicalTestVersion)
                binding.grammarAwareLastTest = getString(R.string.ready_to_test)
            else binding.grammarAwareLastTest = it
        }

        if (isClinicalTestVersion) {
            viewModel.canTestScentAware.observe(viewLifecycleOwner) {
                binding.canTestScentAware = it
                canTestScentAware = it
            }
            viewModel.canTestTraceAware.observe(viewLifecycleOwner) {
                binding.canTestTraceAware = it
                canTestTraceAware = it
            }
            viewModel.canTestAudioAware.observe(viewLifecycleOwner) {
                binding.canTestAudioAware = it
                canTestAudioAware = it
            }
            viewModel.canTestGlanceAware.observe(viewLifecycleOwner) {
                binding.canTestGlanceAware = it
                canTestGlanceAware = it
            }
            viewModel.canTestWordsAware.observe(viewLifecycleOwner) {
                binding.canTestWordsAware = it
                canTestWordsAware = it
            }
            viewModel.canTestGrammarAware.observe(viewLifecycleOwner) {
                binding.canTestGlanceAware = it
                canTestGrammarAware = it
            }
        }
    }
}