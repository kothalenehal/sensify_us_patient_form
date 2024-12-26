package com.sensifyawareapp.ui.settings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityUpdateProfileBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class UpdateProfileActivity : BaseActivity() {
    private var strGender = String()
    private lateinit var binding: ActivityUpdateProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_profile)
        Log.e(
            "TAG",
            "onCreate: ${prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.GENDER
            )}",
        )
        if (prefUtils.getStringData(this, AppConstant.SharedPreferences.GENDER) != null) {
            strGender = prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.GENDER
            )!!
        }

        for (i in 0 until binding.radioGrp.childCount) {
            val radioButton = binding.radioGrp.getChildAt(i) as RadioButton
            val radioButtonData =
                radioButton.text.toString() // Get the text associated with the radio button

            if (radioButtonData == strGender) {
                Log.e("TAG", "onCreate: CAll")
                radioButton.isChecked = true
                radioButton.typeface = ResourcesCompat.getFont(this, R.font.ws_bold)
                break // If a match is found, break the loop
            }
        }

        binding.etUserName.editText?.setText(
            prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.USER_NAME
            )
        )
        binding.etAge.editText?.setText(
            prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.AGE
            )
        )
        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener {
            if (isValid()) {
                callApi()
            }
        }

        initGender()
    }

    private fun initGender() {
        binding.btnMale.setOnClickListener {
            binding.btnMale.typeface = ResourcesCompat.getFont(this, R.font.ws_bold)
            binding.btnFemale.typeface = ResourcesCompat.getFont(this, R.font.ws_regular)
            binding.btnOthers.typeface = ResourcesCompat.getFont(this, R.font.ws_regular)

        }
        binding.btnFemale.setOnClickListener {
            binding.btnFemale.typeface = ResourcesCompat.getFont(this, R.font.ws_bold)
            binding.btnMale.typeface = ResourcesCompat.getFont(this, R.font.ws_regular)
            binding.btnOthers.typeface = ResourcesCompat.getFont(this, R.font.ws_regular)

        }
        binding.btnOthers.setOnClickListener {
            binding.btnOthers.typeface = ResourcesCompat.getFont(this, R.font.ws_bold)
            binding.btnMale.typeface = ResourcesCompat.getFont(this, R.font.ws_regular)
            binding.btnFemale.typeface = ResourcesCompat.getFont(this, R.font.ws_regular)
        }
    }

    private fun callApi() {
        Log.e("TAG", "callApi: ${binding.radioGrp.checkedRadioButtonId}")
        if (binding.radioGrp.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show()
            return
        }
        val radioButton: RadioButton =
            findViewById(binding.radioGrp.checkedRadioButtonId)

        strGender = radioButton.text.toString()

        Log.e("TAG", "callApi: $strGender")
        val query = JSONObject()
        query.put(
            "emailaddress",
            prefUtils.getStringData(this, AppConstant.SharedPreferences.EMAIL)
        )
        val split = binding.etUserName.editText?.text.toString().split(" ")
//        query.put("UserName",  binding.etUserName.editText?.text.toString())
        query.put("firstname", split[0])
        query.put("lastName", if (split.size > 1) split[1] else "")
        query.put("age", binding.etAge.editText?.text.toString())
        query.put("gender", strGender)
        query.put(
            "Id",
            prefUtils.getIntData(this, AppConstant.SharedPreferences.USER_ID)
        )

        val query2 = JSONObject()
        query2.put("userProfile", query)
        query2.put("url", "/update-user-profile")
        val body: RequestBody = query2.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        Log.e("TAG", "callApi: ${query2}")

        showLoader()
        apiManager.updateProfile(body).subscribe(
            {
                hideLoader()

                if (it.isSuccess) {
                    showError("Profile updated successfully")
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.AGE,
                        binding.etAge.editText?.text.toString()
                    )
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.GENDER,
                        strGender
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                    }, 1000)
                } else
                    showError(it.message)
            }, {
                hideLoader()
                handleError(it)
            }
        )
    }

    private fun isValid(): Boolean {
        if (TextUtils.isEmpty(binding.etUserName.editText?.text)) {
            binding.etUserName.requestFocus()
            binding.etUserName.error = getString(R.string.strWarnUserName)
            return false
        }
        if (TextUtils.isEmpty(binding.etAge.editText?.text)) {
            binding.etAge.requestFocus()
            binding.etAge.error = getString(R.string.strWarnAge)
            return false
        }

        if (binding.ageTx.text.toString().toInt() !in 1..119){
            binding.etAge.requestFocus()
            binding.etAge.error = getString(R.string.strCorrectAge)
            return false
        }
        return true
    }
}