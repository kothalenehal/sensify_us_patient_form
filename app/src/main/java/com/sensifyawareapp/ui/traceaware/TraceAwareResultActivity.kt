package com.sensifyawareapp.ui.traceaware

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityTraceAwareResultBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*
import kotlin.math.roundToInt

class TraceAwareResultActivity : BaseActivity() {

    private lateinit var binding: ActivityTraceAwareResultBinding
    private lateinit var viewModel: TraceAwareViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trace_aware_result)
        viewModel = ViewModelProviders.of(this)[TraceAwareViewModel::class.java]

        val selectedTraces = intent.getIntExtra(AppConstant.BundleExtra.TRACES, 10)

        Log.e(
            "tracingAccuracy",
            "${SensifyAwareApplication.dsstScore} // $selectedTraces // ${((SensifyAwareApplication.dsstScore) * 100 / (selectedTraces * 3))}"
        )

        Log.e(
            "recallAccuracy",
            "${SensifyAwareApplication.recallDsstScore} // $selectedTraces // ${((SensifyAwareApplication.dsstScore) * 100 / selectedTraces).roundToInt()}"
        )

        binding.tracingAccuracy =
            ((SensifyAwareApplication.dsstScore) * 100 / (selectedTraces * 3)).roundToInt()
                .toString()
        binding.recallAccuracy =
            ((SensifyAwareApplication.recallDsstScore) * 100 / selectedTraces).roundToInt()
                .toString()

       /* val tracingAccuracy_per =
            (((SensifyAwareApplication.dsstScore) * 100 / (selectedTraces * 3)).roundToInt() * 100) / (selectedTraces * 3)

        val scale_tracing_accuracy_per = ((tracingAccuracy_per - 30) / 3) * 5
        //scale_tracing_accuracy_per < 0 ? 0 : scale_tracing_accuracy_per > 100 ? 100 : scale_tracing_accuracy_per
        binding.tracingAccuracy = if (scale_tracing_accuracy_per < 0) 0.toString()
        else if (scale_tracing_accuracy_per > 100) 100.toString()
        else scale_tracing_accuracy_per.toString()

        val recallAccuracy_per =
            (((SensifyAwareApplication.recallDsstScore) * 100 / selectedTraces).roundToInt() * 100) / selectedTraces

        val scale_recall_accuracy_per = ((recallAccuracy_per - 30) / 3) * 5
        //scale_recall_accuracy_per < 0 ? 0 : scale_recall_accuracy_per > 100 ? 100 : scale_recall_accuracy_per
        binding.recallAccuracy = if (scale_recall_accuracy_per < 0) 0.toString()
        else if (scale_recall_accuracy_per > 100) 100.toString()
        else scale_recall_accuracy_per.toString()*/

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        binding.btnTrySmellEvaluation.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        binding.tvBackToHomescreen.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.END_TIME,
            Calendar.getInstance().timeInMillis
        )

        val difference = (prefUtils.getLongData(
            this,
            AppConstant.SharedPreferences.END_TIME
        ) - prefUtils.getLongData(
            this,
            AppConstant.SharedPreferences.START_TIME
        ))
        val days = (difference / (1000 * 60 * 60 * 24))
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60))
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours) / (1000 * 60)
        val sec =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours - 1000 * 60 * min) / 1000

        val time = ((min * 60) + sec).toFloat() / 40.0f
        binding.time = time

        callApiToSaveData(time, selectedTraces)
    }

    private fun callApiToSaveData(time: Float, selectedTraces: Int) {

        viewModel.uploadData(when (selectedTraces) {
            6 -> getString(R.string.easy)
            8 -> getString(
                R.string.intermediate
            )
            else -> getString(R.string.hard)
        })

        viewModel.insertRoomRecord(
            time,
            selectedTraces,
            when (selectedTraces) {
                6 -> getString(R.string.easy)
                8 -> getString(
                    R.string.intermediate
                )
                else -> getString(R.string.hard)
            }
        )

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

}