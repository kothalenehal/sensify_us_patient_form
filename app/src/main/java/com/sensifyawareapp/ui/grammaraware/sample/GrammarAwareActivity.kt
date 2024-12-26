package com.sensifyawareapp.ui.grammaraware

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityGrammarAwareBinding
import com.sensifyawareapp.ui.BaseActivity

class GrammarAwareActivity : BaseActivity() {
    private lateinit var binding: ActivityGrammarAwareBinding
    private val viewModel: GrammarAwareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_grammar_aware)
        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener{
            showCloseDialog()
        }
        viewModel.timerLiveData.observe(this) {
            binding.toolbar.tvTimer.text = it
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}