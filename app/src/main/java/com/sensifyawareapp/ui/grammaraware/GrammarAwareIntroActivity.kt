package com.sensifyawareapp.ui.grammaraware

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentGrammarAwareIntroBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*
import kotlin.collections.ArrayList

class GrammarAwareIntroActivity:BaseActivity() {
    private lateinit var binding: FragmentGrammarAwareIntroBinding
    var noun = String()
    var verb = String()
    var adjective = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.fragment_grammar_aware_intro
        )
        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener{
            showCloseDialog()
        }
        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.START_TIME,
            Calendar.getInstance().timeInMillis
        )

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }

        SensifyAwareApplication.clearGrammarAwareObjectFromJSON()
        AppConstant.arrayS = ArrayList()
        AppConstant.Accuracy = ArrayList()
        AppConstant.wordsString.clear()
        AppConstant.wordsString =ArrayList()
        AppConstant.Time1 = 0
        AppConstant.Time2 = 0
        AppConstant.Time3 = 0
        val bundle = Bundle()
        try {
            /*val adjectives = this.assets?.open("Adjectives.txt")?.bufferedReader().use {
                it?.readText()
            }
            adjectives?.let {
                adjective = it
                bundle.putString("adjective", it)
            }
            val nouns = this.assets?.open("Nouns.txt")?.bufferedReader().use {
                it?.readText()
            }
            nouns?.let {
                noun = it
                bundle.putString("noun", it)
            }
            val verbs = this.assets?.open("Verbs.txt")?.bufferedReader().use {
                it?.readText()
            }*/

            val adjectives =
                this.assets?.open(getString(R.string.adjective))?.bufferedReader().use {
                    it?.readText()
                }
            adjectives?.let {
                adjective = it
                bundle.putString("adjective", it)
            }
            val nouns = this.assets?.open(getString(R.string.nouns))?.bufferedReader().use {
                it?.readText()
            }
            nouns?.let {
                noun = it
                bundle.putString("noun", it)
            }
            val verbs = this.assets?.open(getString(R.string.verbs))?.bufferedReader().use {
                it?.readText()
            }
            verbs?.let {
                verb = it
                bundle.putString("verb", it)
            }
        } catch (e: Exception) {
            showError(getString(R.string.unknown_error_occurred))
            e.printStackTrace()
        }

        if (intent.getIntExtra("level", 0) == 1) {
            binding.nameText.text = getString(R.string.words2)
        } else {
            binding.nameText.text = getString(R.string.words3)
        }

        binding.btnSubmit.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    GrammarAwareSpeakActivity::class.java
                ).putExtra("verb", verb).putExtra("noun",noun).putExtra("adjective",adjective)
                    .putExtra("levelValue",intent.getIntExtra("levelValue",0))
            )
        }
    }
    override fun onBackPressed() {
        showCloseDialog()
    }
}