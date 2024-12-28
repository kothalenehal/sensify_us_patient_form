package com.sensifyawareapp.ui.scentaware

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityPatientBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.auth.model.StudyNumber
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class PatientActivity : BaseActivity(), KeyboardObserver.OnKeyboardStateChangeListener {

    lateinit var binding: ActivityPatientBinding
    var patientList: ArrayList<String> = ArrayList()

    private lateinit var keyboardObserver: KeyboardObserver
    lateinit var lp: ConstraintLayout.LayoutParams

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch necessary data from SharedPreferences or Intent
        val studyNumber = prefUtils.getStringData(
            this,
            AppConstant.SharedPreferences.STUDY_NUMBER
        )
        val selectedKitSize = prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_KIT_SIZE)

        // Check if required values exist
        if (studyNumber.isNullOrEmpty()) {
            showAlert("Study number is required")
            return
        }

        if (selectedKitSize != 8 && selectedKitSize != 16) {
            showAlert("Invalid kit size selected")
            return
        }

        // Initialize binding variables
        binding.studyNumber = studyNumber
        binding.selectedKitSize = selectedKitSize

        // Set layout params for keyboard handling
        lp = binding.constraint.layoutParams as ConstraintLayout.LayoutParams
        lp.height = resources.getDimension(com.intuit.sdp.R.dimen._300sdp).toInt()


        //keyboardObserver = KeyboardObserver(this, this)



        binding.selectedKitSize = prefUtils.getIntData(
            this,
            AppConstant.SharedPreferences.SELECTED_KIT_SIZE
        )

        binding.txBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {

            if (binding.txStudyId.text?.isEmpty() == true) {
                binding.txStudyId.requestFocus()
                binding.txStudyId.error = getString(R.string.please_enter_patient_id)
                return@setOnClickListener
            }

            if (binding.patientIdAutoComplete.text?.isEmpty() == true) {
                binding.patientIdAutoComplete.requestFocus()
                binding.patientIdAutoComplete.error = getString(R.string.please_enter_patient_id)
                return@setOnClickListener
            }

            if (!patientList.find { it == binding.patientIdAutoComplete.text.toString() }
                    .isNullOrEmpty()) {
                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.PATIENT_ID,
                    binding.patientIdAutoComplete.text.toString()
                )

                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.STUDY_ID,
                    binding.txStudyId.text.toString()
                )
                checkIds()
            } else {
                binding.patientIdAutoComplete.requestFocus()
                binding.patientIdAutoComplete.error = getString(R.string.id_not_found)
            }
        }

        binding.txStudyId.setOnClickListener {
            showAlertDialog()
        }

        binding.patientIdAutoComplete.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (binding.txStudyId.text?.isEmpty() == true) {
                    binding.txStudyId.requestFocus()
                    binding.txStudyId.error = getString(R.string.please_enter_patient_id)
                    return
                }

                val query = JSONObject()
                query.put("FilterBy", p0)
                query.put(
                    "StudyNumber",
                    binding.txStudyId.text.toString()
                )
                query.put("Url", "/get-patientid")

                val body: RequestBody = query.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                apiManager.getPatient(body).subscribe({
                    if (!it.data.patientId.isNullOrEmpty()) {
                        patientList.clear()
                        patientList.addAll(it.data.patientId)
                        val adapter = ArrayAdapter(
                            this@PatientActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            it.data.patientId
                        )
                        binding.patientIdAutoComplete.setAdapter(adapter)
                        adapter.notifyDataSetChanged()
                    }

                }, {
                    Log.e("TAG", "onError: $it")
                })
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun showAlertDialog() {
        val sitesJsonList =
            prefUtils.getStringData(this, AppConstant.SharedPreferences.STUDY_NUMBER)
        if (sitesJsonList != null) {
            val gson = Gson()
            val siteList: ArrayList<StudyNumber> = gson.fromJson(
                sitesJsonList, object : TypeToken<ArrayList<StudyNumber>>() {}.type
            )

            val items = siteList.map { it.studyNumber }

            MaterialAlertDialogBuilder(this, R.style.MaterialThemeDialog)
                .setItems(items.toTypedArray()) { dialog_, which ->
                    binding.tvSite.text = items[which]

                    for (testId in siteList) {
                        if (testId.studyNumber == items[which]) {
                            binding.studyNumber = testId.studyNumber
                        }
                    }
                    dialog_?.dismiss()
                }
                .setBackground(getDrawable(R.drawable.dialog_background))
                .show()
        }
    }

    override fun onKeyboardOpened() {
        lp.height = resources.getDimension(com.intuit.sdp.R.dimen._300sdp).toInt() * 2
        binding.constraint.layoutParams = lp
    }

    override fun onKeyboardClosed() {
        lp.height = resources.getDimension(com.intuit.sdp.R.dimen._300sdp).toInt()
        binding.constraint.layoutParams = lp
    }

    private fun checkIds() {
        hideKeyBoard(binding.etStudyId)
        showLoader()

        val query = JSONObject()
        query.put("PatientId", binding.patientIdAutoComplete.text.toString())
        query.put(
            "StudyNumber",
            binding.txStudyId.text.toString()
        )
        query.put("Url", "/v1/verify-patient")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        apiManager.verifyPatient(body).subscribe({
            hideLoader()
            if (it.data.isPatientExist) {
                startActivity(Intent(this, HealthsQuestionsActivity::class.java))
            } else {
                binding.patientIdAutoComplete.error = getString(R.string.id_not_found)
            }
        }, {
            handleError(it)
        })

    }

}