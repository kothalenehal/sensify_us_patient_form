package com.sensifyawareapp.ui.traceaware

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityTraceAwareCheckPointBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant

class TraceAwareCheckPointActivity : BaseActivity() {
    private lateinit var binding: ActivityTraceAwareCheckPointBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trace_aware_check_point)
        val selectedTraces = intent.getIntExtra(AppConstant.BundleExtra.TRACES, 10)

        binding.selectedTraces = selectedTraces
        binding.toolbar.ivBack.visibility = View.GONE

        var completedTraces =
            prefUtils.getIntData(this, AppConstant.SharedPreferences.COMPLETED_TRACES)
        if (completedTraces == 0) completedTraces = selectedTraces

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.COMPLETED_TRACES,
            completedTraces + selectedTraces
        )
        binding.remainingTrace = (selectedTraces * 4) - completedTraces
        binding.checkpointProgress = completedTraces

        when (completedTraces) {
            selectedTraces -> binding.message = getString(R.string.msg_trace_aware_checkpoint_1)
            selectedTraces * 2 -> binding.message = getString(R.string.msg_trace_aware_checkpoint_2)
            selectedTraces * 3 -> {
                binding.message = getString(R.string.msg_trace_aware_checkpoint_3)
                prefUtils.saveData(this, AppConstant.SharedPreferences.SHOW_RECALL_INTRO_MSG, true)
            }
        }

        binding.btnSubmit.setOnClickListener {
            finish()
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.CURRENT_QUESTION, 0
            )
            startActivity(
                Intent(
                    this,
                    TraceAwareActivity::class.java
                ).putExtra(AppConstant.BundleExtra.TRACES, selectedTraces)
            )
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}