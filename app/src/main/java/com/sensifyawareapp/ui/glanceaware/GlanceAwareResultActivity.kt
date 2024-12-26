package com.sensifyawareapp.ui.glanceaware

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityGlanceAwareResultBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*

class GlanceAwareResultActivity : BaseActivity() {
    private lateinit var binding: ActivityGlanceAwareResultBinding
    private lateinit var viewModel: GlanceAwareViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_glance_aware_result)
        viewModel = ViewModelProviders.of(this)[GlanceAwareViewModel::class.java]

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.END_TIME,
            Calendar.getInstance().timeInMillis
        )
        val correctAnsCount = intent.getIntExtra("correctAnsCount", 0)
        val currentIndex = intent.getIntExtra("currentIndex", 0)

        binding.correctCount = correctAnsCount.toString()
        binding.incorrectCount = (currentIndex - correctAnsCount).toString()
        binding.total = currentIndex.toString()

        binding.ivBack.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        binding.btnRetryGlanceaware.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        binding.tvBackToHomescreen.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        viewModel.insertRoomRecord(
            correctAnsCount, currentIndex, when (intent.getIntExtra("level", 0)) {
                1 -> getString(R.string.easy)
                2 -> getString(R.string.intermediate)
                else -> getString(R.string.hard)
            }
        )

        viewModel.uploadData(
            when (intent.getIntExtra("level", 0)) {
                1 -> getString(R.string.easy)
                2 -> getString(R.string.intermediate)
                else -> getString(R.string.hard)
            }
        )
    }
}