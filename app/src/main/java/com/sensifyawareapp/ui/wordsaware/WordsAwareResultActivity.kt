package com.sensifyawareapp.ui.wordsaware

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityWordsAwareResultBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*


class WordsAwareResultActivity : BaseActivity() {
    private lateinit var binding: ActivityWordsAwareResultBinding
    private val viewModel: WordsAwareViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.END_TIME,
            Calendar.getInstance().timeInMillis
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_words_aware_result)
        binding.words = intent.getStringExtra("randomWord").toString()
        binding.uniqueWords =AppConstant.uniqueWord.toString()
        binding.longestWord = AppConstant.longestWord
        binding.shortestWord = AppConstant.shortestWord
        binding.repeatWords =AppConstant.repeatWord.toString()

        viewModel.insertRoomRecord()

        binding.ivBack.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        binding.btnRetryWordsaware.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        viewModel.uploadData(
            intent.getStringExtra("randomWord").toString()
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }
}