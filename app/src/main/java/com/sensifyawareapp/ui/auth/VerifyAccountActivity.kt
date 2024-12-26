package com.sensifyawareapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityVerifyAccountBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class VerifyAccountActivity : BaseActivity() {
    private lateinit var binding: ActivityVerifyAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify_account)


        binding.toolbar.ivBack.visibility = View.GONE

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.IS_VERIFIED,
            false
        )

        binding.btnVerify.setOnClickListener {
            if (binding.etCode.editText?.text.isNullOrEmpty()) {
                binding.etCode.error = getString(R.string.msg_enter_verification_code)
                return@setOnClickListener
            }

            var email = prefUtils.getStringData(this, AppConstant.SharedPreferences.EMAIL)
            var userId = prefUtils.getIntData(this, AppConstant.SharedPreferences.USER_ID)
            if (intent.getBooleanExtra("isOldUser", false)) {
                email = intent.getStringExtra("email")
                userId = intent.getIntExtra("userId", 0)
            }
            val query = JSONObject()
            query.put("EmailAddress", email)
            query.put("Url", "/confirmsignup")
            query.put("UserId", userId)
            query.put("ConfirmationCode", binding.etCode.editText?.text)
            val body: RequestBody = query.toString()
                .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            showLoader()

            apiManager.verifyEmail(body).subscribe({
                hideLoader()
                if (it.isSuccess) {
                    if (intent.getBooleanExtra("isOldUser", false)) {
                        showAlertDialog(email)
                    } else {
                        finish()
                        if (!intent.getBooleanExtra("fromLogin", false)) {
                            prefUtils.saveData(
                                this,
                                AppConstant.SharedPreferences.IS_VERIFIED,
                                true
                            )
                            startActivity(
                                Intent(
                                    this,
                                    ConfirmationActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            )
                        }
                    }
                } else showError(it.message)
            }, {
                hideLoader()
                handleError(it)
            })
        }
    }

    private fun showAlertDialog(email: String?) {
        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(getString(R.string.app_name))
        alertDialog.setMessage("Your password has expired. Please reset your password.")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Ok"
        ) { dialog, _ ->
            dialog.dismiss()
            val intent = Intent()
            intent.putExtra("data", "reset")
            intent.putExtra("email", email)
            setResult(RESULT_OK, intent)
            finish()
        }
        alertDialog.show()
    }

}