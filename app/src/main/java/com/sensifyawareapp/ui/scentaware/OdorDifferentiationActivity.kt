package com.sensifyawareapp.ui.scentaware

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityOdorDifferentiationBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant

class OdorDifferentiationActivity : BaseActivity() {

    private lateinit var binding: ActivityOdorDifferentiationBinding
    private var showingIntro = true
    private var isCorrectAnswerSelected = false
    private var questionStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_odor_differentiation)

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }
        binding.checkedOption = 0

        var currentQuestion =
            prefUtils.getIntData(this, AppConstant.SharedPreferences.ODOR_DIFF_QUESTION)

        val selectedKitSize =
            prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_KIT_SIZE)
        val totalQuestionsToAsk = 2

        binding.showIntro = showingIntro
        binding.questionsCount = totalQuestionsToAsk
        binding.totalQuestions = selectedKitSize
        binding.questionId = ++currentQuestion
        binding.checkpointProgress =
            (selectedKitSize * 2) + (8 * currentQuestion)//32 questions asked in previous screen | multiply by 8 to manage progress bar
        Log.e("TAG ", "onCreate: ${binding.questionId}")
        //this answers are hardcoded, for the q1 green heart is correct answer,
        // for q2 yellow triangle is correct answer and these both are at 2nd position
        val correctAnswer = if (currentQuestion == 1) 2 else 2

        binding.cardOption1.setOnClickListener {
            binding.checkedOption = 1
        }
        binding.cardOption2.setOnClickListener {
            binding.checkedOption = 2
            isCorrectAnswerSelected = correctAnswer == 2
        }
        binding.cardOption3.setOnClickListener {
            binding.checkedOption = 3
        }
        binding.cardOption4.setOnClickListener {
            binding.checkedOption = 4
        }

        binding.btnSubmit.setOnClickListener {
            if (showingIntro) {
                showingIntro = false
                binding.showIntro = showingIntro
                questionStartTime = System.currentTimeMillis()
            } else {
                if (isCorrectAnswerSelected) {
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.CORRECT_ANSWER_COUNT,
                        prefUtils.getIntData(
                            this,
                            AppConstant.SharedPreferences.CORRECT_ANSWER_COUNT
                        ) + 1
                    )
                    SensifyAwareApplication.scentAwareTestData.smell_discrimination_score++
                }

                val totalQuestionTime = System.currentTimeMillis() - questionStartTime
                finish()
                if (currentQuestion < totalQuestionsToAsk) {
                    SensifyAwareApplication.scentAwareTestData.time_for_question_one1 =
                        totalQuestionTime.toFloat() / 1000.toFloat()
                    startActivity(Intent(this, OdorDifferentiationActivity::class.java))
                } else {
                    SensifyAwareApplication.scentAwareTestData.time_for_question_two1 =
                        totalQuestionTime.toFloat() / 1000.toFloat()
                    startActivity(
                        Intent(this, CheckPointActivity::class.java).putExtra(
                            "progress",
                            48
                        )
                            .putExtra("checkPointOptionIntensity", true)
                            .putExtra("completedCheckpoints", 3)

                    )
                }
                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.ODOR_DIFF_QUESTION,
                    currentQuestion
                )
            }
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}