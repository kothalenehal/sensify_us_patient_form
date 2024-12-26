package com.sensifyawareapp.ui.scentaware

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentProfileSetup4Binding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.ui.auth.AuthenticationViewModel
import com.sensifyawareapp.ui.auth.Constant

class HealthsQuestions2Activity : BaseActivity() {

    private lateinit var binding: FragmentProfileSetup4Binding
    private val viewModel: AuthenticationViewModel by viewModels()
    var selectedText = ""
    var medication = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileSetup4Binding.inflate(layoutInflater)
        setContentView(binding.root)
        SensifyAwareApplication.initTestData()

        binding.ivBack.setOnClickListener {
            finish()
        }
//        init()




        val radioButton =
            binding.radioGrp?.checkedRadioButtonId?.let { findViewById<RadioButton>(it) }
        selectedText = radioButton!!.text.toString()

        binding.radioGrp?.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                val radioButton2 = findViewById<RadioButton>(p1)
                selectedText = radioButton2.text.toString()
            }
        })

        binding.btnNext.setOnClickListener {
            if (selectedText == getString(R.string.strOption6)) {
                selectedText = if (binding.textOther?.text.toString().isEmpty()) {
//                    Toast.makeText(this, "Please Specify Medication", Toast.LENGTH_SHORT).show()
                    null.toString()
                } else {
                    binding.textOther?.text.toString()
                }
            }
            SensifyAwareApplication.scentAwareTestData.Medication = selectedText
            Log.e("TAG", "Dta 1: ${SensifyAwareApplication.scentAwareTestData.IsStuffyNose}", )

            startActivity(
                Intent(
                    this,
                    TutorialActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

    }

    private fun init() {
        binding.viewmodel = viewModel

        viewModel.validateLivedata.observe(this, Observer {
            Log.e("TAG", "init: $it")
            when (it) {
                Constant.IS_MEDICATION_OPTIONS_EMPTY -> {
                    binding.etOther.error = null
                    showError(getString(R.string.strWarnSelectMedications))
                }

                Constant.IS_MEDICATION_OPTIONS_OTHER_EMPTY -> {
                    binding.etOther.requestFocus()
                    binding.etOther.error = getString(R.string.strWarnOtherMedication)
                }

                Constant.IS_VALID -> {
                    startActivity(
                        Intent(
                            this,
                            TutorialActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                }

                else -> showError(it.toString())
            }
        })
    }

}