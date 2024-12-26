package com.sensifyawareapp.ui.scentaware

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityOdorIntensityBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant

class OdorIntensityActivity : BaseActivity() {
    private lateinit var binding: ActivityOdorIntensityBinding
    private var showingIntro = true
    private var isCorrectAnswerSelected = false
    private var questionStartTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_odor_intensity)

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }
        binding.checkedOption = 0

        var currentQuestion =
            prefUtils.getIntData(this, AppConstant.SharedPreferences.ODOR_INTENSITY_QUESTION)

        val totalQuestionsToAsk = 2

        binding.showIntro = showingIntro
        binding.questionsCount = totalQuestionsToAsk
        binding.totalQuestions =
            prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_KIT_SIZE)
        binding.questionId = ++currentQuestion
        binding.checkpointProgress =
            48 + (8 * currentQuestion)//48 progress completed in previous screen | multiply by 8 to manage progress bar

        //this answers are hardcoded, for the q1 blue square is correct answer,
        // for q2 orange heart is correct answer and these both are at 3rd and 1st position
        val correctAnswer = if (currentQuestion == 1) 3 else 1

        binding.cardOption1.setOnClickListener {
            binding.checkedOption = 1
            isCorrectAnswerSelected = correctAnswer == 1
        }
        binding.cardOption2.setOnClickListener {
            binding.checkedOption = 2
        }
        binding.cardOption3.setOnClickListener {
            binding.checkedOption = 3
            isCorrectAnswerSelected = correctAnswer == 3
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
                    SensifyAwareApplication.scentAwareTestData.Smell_Intensity_Score++
                }

                val totalQuestionTime = System.currentTimeMillis() - questionStartTime
                finish()
                if (currentQuestion < totalQuestionsToAsk) {
                    SensifyAwareApplication.scentAwareTestData.time_for_question_one =
                        totalQuestionTime.toFloat() / 1000.toFloat()
                    startActivity(Intent(this, OdorIntensityActivity::class.java))
                } else {
                    SensifyAwareApplication.scentAwareTestData.time_for_question_two =
                        totalQuestionTime.toFloat() / 1000.toFloat()
                    startActivity(Intent(this, ScentAwareTestResultActivity::class.java))
                }
                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.ODOR_INTENSITY_QUESTION,
                    currentQuestion
                )
            }
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }
}