package com.sensifyawareapp.ui.wordsaware

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityWordsAwareBinding
import com.sensifyawareapp.ui.BaseActivity

class WordsAwareActivity : BaseActivity() {
    private lateinit var binding: ActivityWordsAwareBinding
    private val viewModel: WordsAwareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_words_aware)
        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }

        viewModel.timerLivedata.observe(this) {
            binding.toolbar.tvTimer.text = it
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}