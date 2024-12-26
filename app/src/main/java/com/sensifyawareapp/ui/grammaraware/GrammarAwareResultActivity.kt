package com.sensifyawareapp.ui.grammaraware

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityGrammarAwareResultBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.lang.String
import java.util.*

class GrammarAwareResultActivity : BaseActivity() {
    private lateinit var binding: ActivityGrammarAwareResultBinding
    private val viewModel: GrammarAwareViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.END_TIME,
            Calendar.getInstance().timeInMillis
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grammar_aware_result)

        binding.longestWord = AppConstant.arrayS.max().toString() + " " + getString(R.string.words)
//            intent.getIntExtra("LongestWords", 0).toString() + " " + getString(R.string.words)
        binding.shortestWord = AppConstant.arrayS.min().toString() + " " + getString(R.string.words)
//            intent.getIntExtra("ShortestWords", 0).toString() + " " + getString(R.string.words)
        val time = String.format("%.02f", intent.getFloatExtra("avgTime", 0F))
        binding.avgTime =
            time + " " + getString(R.string.secs)

        viewModel.insertRoomRecord(time)
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

        binding.correctAnswers = (AppConstant.Accuracy.sum() * 5) / 300F
        Log.e("Sum ", binding.correctAnswers.toString())
        viewModel.uploadData()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            )
        )
    }
}