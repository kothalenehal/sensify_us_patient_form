package com.sensifyawareapp.ui.audioaware

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityAudioAwareResultBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*
import kotlin.math.roundToInt

class AudioAwareResultActivity : BaseActivity() {
    private lateinit var binding: ActivityAudioAwareResultBinding
    private val viewModel: AudioAwareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_audio_aware_result)

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.END_TIME,
            Calendar.getInstance().timeInMillis
        )

        val correctAnswerCount = intent.getIntExtra("correctWordsCount", 0)
        var ansStars = ((correctAnswerCount * 4) / 10.0).roundToInt()
        if (ansStars == 0) ansStars = 1
        binding.correctAnswers = ansStars
        val level = when (intent.getIntExtra("level", 0)) {
            1 -> getString(R.string.easy)
            2 -> getString(R.string.intermediate)
            else -> getString(
                R.string.hard
            )
        }
        binding.level = level


        val typeface = ResourcesCompat.getFont(this, R.font.ws_bold)
        val evaluation: String
        when (ansStars) {
            4 -> {
                binding.tv4.typeface = typeface
                evaluation = getString(R.string.excellent)
            }
            3 -> {
                binding.tv6.typeface = typeface
                evaluation = getString(R.string.good)
            }
            2 -> {
                binding.tv8.typeface = typeface
                evaluation = getString(R.string.average)
            }
            else -> {
                binding.tv10.typeface = typeface
                evaluation = getString(R.string.poor)
            }
        }
        viewModel.insertRoomRecord(correctAnswerCount, level, evaluation)

        binding.ivBack.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        binding.btnRetryAudioaware.setOnClickListener {
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

        viewModel.uploadData(
            intent.getStringExtra(
                "selectedWords"
            )!!,
            intent.getStringArrayListExtra(
                "wordsToSpeakList"
            )!!,
            intent.getStringExtra(
                "wordsToDisplayList"
            )!!,
            correctAnswerCount
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