package com.sensifyawareapp.ui.glanceaware

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityGlanceAwareBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*

class GlanceAwareActivity : BaseActivity() {
    private lateinit var binding: ActivityGlanceAwareBinding
    private val viewModel: GlanceAwareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_glance_aware)

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.START_TIME,
            Calendar.getInstance().timeInMillis
        )

        binding.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.ivBack.setOnClickListener {
            showCloseDialog()
        }

        val level = intent.getIntExtra("level", 0)
        viewModel.level = level
//        binding.level = null
            /*when (level) {
                1 -> getString(R.string.easy)
                2 -> getString(R.string.intermediate)
                else -> getString(R.string.hard)
            }*/

        viewModel.timerLivedata.observe(this) {
            binding.tvTimer.text = it
        }
        viewModel.IndexLivedata.observe(this) {
            binding.tvIndex.visibility = if (it.trim().isEmpty()) View.GONE else View.VISIBLE
            binding.index = it
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}