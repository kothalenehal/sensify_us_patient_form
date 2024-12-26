package com.sensifyawareapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityChangePasswordBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.auth.AuthActivity
import com.sensifyawareapp.utils.Utils
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        binding.toolbar.ivBack.setOnClickListener { finish() }
        binding.btnSubmit.setOnClickListener {
            if (isValid()) {
                callApi()
            }
        }
    }

    private fun callApi() {
        val query = JSONObject()
        query.put(
            "emailaddress",
            prefUtils.getStringData(this, AppConstant.SharedPreferences.EMAIL)
        )
        query.put("NewPassword", binding.etNewPassword.editText?.text.toString())
        query.put("CurrentPassword", binding.etPassword.editText?.text.toString())
        query.put(
            "UserId",
            prefUtils.getIntData(this, AppConstant.SharedPreferences.USER_ID)
        )
        query.put("Url", "/change-new-password")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        showLoader()
        apiManager.changePassword(body).subscribe(
            {
                hideLoader()
                if (it.isSuccess) {

                    showError("Password updated successfully")
                    Handler(Looper.getMainLooper()).postDelayed({
                        finish()
                        logout()

                    }, 1000)
                } else
                    showError(it.message)
            }, {
                hideLoader()
                handleError(it)
            }
        )
    }

    private fun logout() {
        Utils.logout(this, prefUtils)
        startActivity(Intent(this, AuthActivity::class.java).putExtra("showLogin", true))
        finishAffinity()
    }

    private fun isValid(): Boolean {
        val password = binding.etPassword.editText?.text.toString()
        val newPassword = binding.etNewPassword.editText?.text.toString()
        val rePassword = binding.etReenterPassword.editText?.text.toString()

        if (TextUtils.isEmpty(password.trim())) {
            resetErrorMessages()
            binding.etPassword.requestFocus()
            binding.etPassword.error = getString(R.string.strWarnOldPassword)
            return false
        } else if (password == newPassword) {
            resetErrorMessages()
            binding.etNewPassword.requestFocus()
            binding.etNewPassword.error = getString(R.string.strOldnNewPassword)
            return false
        } else if (TextUtils.isEmpty(newPassword.trim())) {
            resetErrorMessages()
            binding.etNewPassword.requestFocus()
            binding.etNewPassword.error = getString(R.string.strWarnNewPassword)
            return false
        } else if (newPassword.trim().length < 8) {
            resetErrorMessages()
            binding.etNewPassword.requestFocus()
            binding.etNewPassword.error = getString(R.string.strValidNewPassword)
            return false
        } else if (!isValidPassword(newPassword)) {
            resetErrorMessages()
            binding.etNewPassword.requestFocus()
            binding.etNewPassword.error = getString(R.string.password_helper_text)
            return false
        } else if (TextUtils.isEmpty(rePassword.trim())) {
            resetErrorMessages()
            binding.etReenterPassword.requestFocus()
            binding.etReenterPassword.error = getString(R.string.strWarnRePassword)
            return false
        } else if (newPassword != rePassword) {
            resetErrorMessages()
            binding.etReenterPassword.requestFocus()
            binding.etReenterPassword.error = getString(R.string.strMatchPassword)
            return false
        }
        return true
    }

    private fun resetErrorMessages() {
        binding.etPassword.error = null
        binding.etNewPassword.error = null
        binding.etReenterPassword.error = null
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

}