package com.sensifyawareapp.ui.traceaware

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivitySelectTraceAwareLevelBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant.BundleExtra.Companion.TRACES

class SelectTraceAwareLevelActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectTraceAwareLevelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_trace_aware_level)

        binding.toolbar.ivBack.setOnClickListener { finish() }

        binding.cardEasy.setOnClickListener {
            startActivity(Intent(this, TraceAwareActivity::class.java).putExtra(TRACES, 6))
        }

        binding.cardIntermediate.setOnClickListener {
            startActivity(Intent(this, TraceAwareActivity::class.java).putExtra(TRACES, 8))
        }
        binding.cardHard.setOnClickListener {
            startActivity(Intent(this, TraceAwareActivity::class.java).putExtra(TRACES, 10))
        }

    }
}