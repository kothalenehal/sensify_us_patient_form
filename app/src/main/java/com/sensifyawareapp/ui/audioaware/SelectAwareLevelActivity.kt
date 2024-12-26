package com.sensifyawareapp.ui.audioaware

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivitySelectAwareLevelBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.glanceaware.GlanceAwareActivity
import com.sensifyawareapp.utils.common.AppConstant

class SelectAwareLevelActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectAwareLevelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_aware_level)

        val selectedMenu = prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_MENU)
        binding.toolbar.ivBack.setOnClickListener { finish() }

        binding.cardEasy.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    if (selectedMenu == 5)
                        GlanceAwareActivity::class.java
                    else AudioAwareActivity::class.java
                ).putExtra("level", 1)
            )
        }

        binding.cardIntermediate.setOnClickListener {
            startActivity(
                Intent(
                    this, if (selectedMenu == 5)
                        GlanceAwareActivity::class.java
                    else AudioAwareActivity::class.java
                ).putExtra("level", 2)
            )
        }
        binding.cardHard.setOnClickListener {
            startActivity(
                Intent(
                    this, if (selectedMenu == 5)
                        GlanceAwareActivity::class.java
                    else AudioAwareActivity::class.java
                ).putExtra("level", 3)
            )
        }

    }
}