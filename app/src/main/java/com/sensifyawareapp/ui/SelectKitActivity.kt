package com.sensifyawareapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivitySelectKitBinding
import com.sensifyawareapp.ui.scentaware.HealthsQuestionsActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.Date

class SelectKitActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectKitBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_kit)
        binding.isClinicalTestVersion = isClinicalTestVersion
        AppConstant.scanToScanDate = null

        binding.toolbar.ivBack.setOnClickListener { finish() }
        val selectedMenu = prefUtils.getIntData(
            this,
            AppConstant.SharedPreferences.SELECTED_MENU
        )
        binding.title = if (selectedMenu == 1) getString(R.string.scentaware_evaluate)
        else getString(R.string.smell_retraining)

        binding.cardKit8.setOnClickListener {
            prefUtils.saveData(this, AppConstant.SharedPreferences.SELECTED_KIT_SIZE, 8)
            if (selectedMenu == 1 && !isClinicalTestVersion) startActivity(
                Intent(
                    this,
                    HealthsQuestionsActivity::class.java
                )
            )
            else {
                SensifyAwareApplication.initTestData()
                startActivity(Intent(this, TutorialActivity::class.java))
            }
        }
        binding.cardKit16.setOnClickListener {
            prefUtils.saveData(this, AppConstant.SharedPreferences.SELECTED_KIT_SIZE, 16)
            if (selectedMenu == 1 && !isClinicalTestVersion) startActivity(
                Intent(
                    this,
                    HealthsQuestionsActivity::class.java
                )
            )
            else {
                SensifyAwareApplication.initTestData()
                startActivity(Intent(this, TutorialActivity::class.java))
            }
        }
    }
}