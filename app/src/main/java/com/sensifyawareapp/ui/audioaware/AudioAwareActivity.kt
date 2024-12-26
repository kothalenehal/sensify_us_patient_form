package com.sensifyawareapp.ui.audioaware

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityAudioAwareBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*

class AudioAwareActivity : BaseActivity() {
    private lateinit var binding: ActivityAudioAwareBinding
    private val viewModel: AudioAwareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_aware)

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }

        val level = intent.getIntExtra("level", 0)
        binding.level = null
           /* when (level) {
                1 -> getString(R.string.easy)
                2 -> getString(R.string.intermediate)
                else -> getString(
                    R.string.hard
                )
            }*/

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.LEVEL,
            binding.level
        )
        viewModel.timerLivedata.observe(this) {
            binding.toolbar.tvTimer.text = it
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}