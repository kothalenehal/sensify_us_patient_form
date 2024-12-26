package com.sensifyawareapp.ui.grammaraware

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.databinding.ActivityGrammarAwareCheckPointBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant


class GrammarAwareCheckPointActivity : BaseActivity() {
    private lateinit var binding: ActivityGrammarAwareCheckPointBinding
    var level = 0
    private var levelRemain = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            com.sensifyawareapp.R.layout.activity_grammar_aware_check_point
        )

        level = intent.getIntExtra("level", 0)
        levelRemain = intent.getIntExtra("levelRemain", 0)
        binding.ivBack.setOnClickListener {
            showCloseDialog()
        }
        val bundle = Bundle()
        bundle.putInt("level", level)
        bundle.putInt("levelRemain", levelRemain)

        binding.remainingSentences = levelRemain.toString()
        binding.btnSubmit.setOnClickListener {
            if (level == 3) {
                val avgTime = (AppConstant.Time1 + AppConstant.Time2 + AppConstant.Time3) / 3F
                startActivity(
                    Intent(
                        this,
                        GrammarAwareResultActivity::class.java
                    ).putExtra("avgTime", avgTime)
                )
            } else {

                startActivity(
                    Intent(
                        this,
                        GrammarAwareSpeakActivity::class.java
                    ).putExtra("level", level).putExtra("levelRemain", levelRemain)
                )
            }
        }

    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}