package com.sensifyawareapp.ui.scentaware

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityCheckPointBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.utils.common.AppConstant

class CheckPointActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckPointBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_point)

        binding.toolbar.ivBack.visibility = View.GONE

        binding.btnResult.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ScentAwareTestResultActivity::class.java
                ).putExtra("isSkipRetest", true)
                    .putExtra("progress", intent.getIntExtra("progress", 0))
            )
        }

        binding.btnReTest.setOnClickListener {
            finish()
            if (intent.getBooleanExtra("checkPointOption", false)) {
                startActivity(
                    Intent(
                        this,
                        TutorialActivity::class.java
                    ).putExtra("odorDifferentiationTutorial", true)
                )

            } else if (intent.getBooleanExtra("checkPointOptionIntensity", false)) {
                startActivity(
                    Intent(
                        this,
                        TutorialActivity::class.java
                    ).putExtra("odorIntensityTutorial", true)
                )
            } else {
                startActivity(
                    Intent(
                        this,
                        TutorialActivity::class.java
                    ).putExtra("OdorIdentificationReTest", true)
                )
            }
        }

        binding.completedCheckpoints = intent.getIntExtra("completedCheckpoints", 0)
        binding.checkpointProgress = intent.getIntExtra("progress", 0)
        binding.checkPointOption = intent.getBooleanExtra(
            "checkPointOption",
            false
        ) || intent.getBooleanExtra("checkPointOptionIntensity", false)
        binding.showIntensityMsg = intent.getBooleanExtra("checkPointOptionIntensity", false)
        binding.totalQuestions = prefUtils.getIntData(
            this,
            AppConstant.SharedPreferences.SELECTED_KIT_SIZE
        )
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}