package com.sensifyawareapp.ui.grammaraware

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityGrammarAwareLevelBinding
import com.sensifyawareapp.ui.BaseActivity

class GrammarAwareLevelActivity : BaseActivity() {
    private lateinit var binding: ActivityGrammarAwareLevelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grammar_aware_level)

        binding.toolbar.ivBack.setOnClickListener { finish() }

        binding.cardEasy.setOnClickListener {
            startActivity(
                Intent(
                    this,
                        GrammarAwareIntroActivity::class.java
                ).putExtra("levelValue", 1)
            )
        }

        binding.cardHard.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    GrammarAwareIntroActivity::class.java
                ).putExtra("levelValue", 2)
            )
        }

    }
}