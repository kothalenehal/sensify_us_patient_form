package com.sensifyawareapp.ui.auth

import android.app.Application
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.sensifyawareapp.model.base.BaseModel
import com.sensifyawareapp.ui.BaseViewModel
import com.sensifyawareapp.ui.auth.model.UserModel
import com.sensifyawareapp.utils.common.ApiConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject() constructor(application: Application) :
    BaseViewModel(application) {
    val validateLivedata: MutableLiveData<Any> = MutableLiveData()
    val forgotPassword: MutableLiveData<BaseModel> = MutableLiveData()
    val resetPassword: MutableLiveData<BaseModel> = MutableLiveData()
    val userModelLivedata: MutableLiveData<UserModel> = MutableLiveData()
    val isOldUserLivedata: MutableLiveData<UserModel> = MutableLiveData()
    val isOldUserDoneLivedata: MutableLiveData<Boolean> = MutableLiveData(true)

    var strEmail: String = ""
    var strPassword: String = ""
    var strFirstName: String = ""
    var strLastName: String = ""
    var strReEnterPassword: String = ""
    var strAge: String = ""
    var strGender: String = ""
    var strCode: String = ""

    //    var strPhoneNumber: String = ""
    var strMedicationOption: String = ""
    var strMedicationOtherOption: String = ""

    fun onSignIn(view: View) {
        if (isValid()) {
            validateLivedata.value = Constant.IS_VALID
            validateLivedata.value = ""
            checkIsOldUser()
        }
    }

    private fun checkIsOldUser() {
        val query = JSONObject()
        query.put("EmailAddress", strEmail)
        query.put("Url", "/isOldUser")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        loading.value = true
        apiManager.isOldUser(body).subscribe({
            loading.value = false
            if (it.data?.isOldUser == true) {
                isOldUserLivedata.value = it
            } else
                callSigninApi()
        }, {
            loading.value = false
            throwable.value = it
        })

    }

    fun onSignUp(view: View) {
        if (signUpValidation()) {
            validateLivedata.value = Constant.IS_VALID
            validateLivedata.value = ""
        }
    }

    fun onProfileStep1(view: View) {
        if (profileStep1Validation()) {
            validateLivedata.value = Constant.IS_VALID
            validateLivedata.value = ""
            callSignupApi()
        }
    }

    fun onProfileStep4(view: View) {
        Log.e("TAG", "onProfileStep4: ${profileStep4Validation()}")
        if (profileStep4Validation()) {
            validateLivedata.value = Constant.IS_VALID
            validateLivedata.value = ""
        }
    }

    private fun isValid(): Boolean {
        if (TextUtils.isEmpty(strEmail.trim())) {
            validateLivedata.value = Constant.IS_EMAIL_EMPTY
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail.trim()).matches()) {
            validateLivedata.value = Constant.IS_EMAIL_INVALID
            return false
        } else if (TextUtils.isEmpty(strPassword.trim())) {
            validateLivedata.value = Constant.IS_PASSWORD_EMPTY
            return false
        } else if (strPassword.trim().length < 8) {
            validateLivedata.value = Constant.IS_PASSWORD_INVALID
            return false
        }
        return true
    }

    private fun signUpValidation(): Boolean {

        val regex = Regex("^[a-zA-Z]+$")

        if (TextUtils.isEmpty(strFirstName.trim())) {
            validateLivedata.value = Constant.IS_FIRST_NAME_EMPTY
            return false
        } else if (!strFirstName.matches(regex)) {
            validateLivedata.value = Constant.IS_FIRST_NAME_CORRECT
            return false
        } else if (TextUtils.isEmpty(strLastName.trim())) {
            validateLivedata.value = Constant.IS_LAST_NAME_EMPTY
            return false
        } else if (!strLastName.matches(regex)) {
            validateLivedata.value = Constant.IS_LAST_NAME_CORRECT
            return false
        } else if (TextUtils.isEmpty(strEmail.trim())) {
            validateLivedata.value = Constant.IS_EMAIL_EMPTY
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail.trim()).matches()) {
            validateLivedata.value = Constant.IS_EMAIL_INVALID
            return false
//        } else if (TextUtils.isEmpty(strPhoneNumber.trim())) {
//            validateLivedata.value = Constant.IS_PHONE_NUMBER_EMPTY
//            return false
        } else if (TextUtils.isEmpty(strPassword.trim())) {
            validateLivedata.value = Constant.IS_PASSWORD_EMPTY
            return false
        } else if (strPassword.trim().length < 8) {
            validateLivedata.value = Constant.IS_PASSWORD_INVALID
            return false
        } else if (!isValidPassword(strPassword)) {
            validateLivedata.value = Constant.IS_PASSWORD_INVALID
            return false
        } else if (TextUtils.isEmpty(strReEnterPassword.trim())) {
            validateLivedata.value = Constant.IS_RE_PASS_EMPTY
            return false
        } else if (strReEnterPassword != strPassword) {
            validateLivedata.value = Constant.IS_PASS_NOT_MATCH
            return false
        }
        return true
    }

    private fun profileStep1Validation(): Boolean {
        if (!(0 < strAge.toString().toInt() && strAge.toString().toInt() < 120)) {
            validateLivedata.value = Constant.IS_AGE_CORRECT
            return false
        }
        if (TextUtils.isEmpty(strAge.trim())) {
            validateLivedata.value = Constant.IS_AGE_EMPTY
            return false
        }
        if (TextUtils.isEmpty(strGender.trim())) {
            validateLivedata.value = Constant.IS_GENDER_EMPTY
            return false
        }
        return true
    }

    private fun profileStep4Validation(): Boolean {
        if (TextUtils.isEmpty(strMedicationOption.trim())) {
            validateLivedata.value = Constant.IS_MEDICATION_OPTIONS_EMPTY
            return false
        }
        if (strMedicationOption == "Others" && TextUtils.isEmpty(strMedicationOtherOption.trim())) {
            validateLivedata.value = Constant.IS_MEDICATION_OPTIONS_OTHER_EMPTY
            return false
        }
        return true
    }

    fun onItemChecked(radioValue: String, checked: Boolean) {
        if (checked)
            strMedicationOption = radioValue
    }

    private fun callSignupApi() {
        val query = JSONObject()
        query.put("EmailAddress", strEmail)
        query.put("Password", strPassword)
        query.put("LastName", strLastName)
        query.put("FirstName", strFirstName)
        query.put("UserName", "$strFirstName $strLastName")
        query.put("Url", "/signup")
        query.put("Gender", strGender)
        query.put("PhoneNumber", "+918181818181")
//        query.put("IsStuffyNose", false)
//        query.put("IsThereLossOfMemory", false)
        query.put("Age", strAge.toInt())
        query.put(
            "Medication", ""
//            if (strMedicationOption == "Others") strMedicationOtherOption else strMedicationOption
        )

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        loading.value = true
        apiManager.signup(body).subscribe({
            loading.value = false
            if (it.isSuccess) {
                userModelLivedata.value = it
            } else validateLivedata.value = it.message

        }, {
            loading.value = false
            throwable.value = it
        })
    }

    private fun callSigninApi() {
        val query = JSONObject()
        query.put("EmailAddress", strEmail)
        query.put("Password", strPassword)
        query.put("Url", "/login")
        Log.d("API_CALL", "Base URL: ${ApiConstants.BASE_URL}")
        Log.d("API_CALL", "Full URL: ${ApiConstants.BASE_URL + ApiConstants.PROD}")
        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        loading.value = true
        apiManager.signin(body).subscribe({
            loading.value = false
            userModelLivedata.value = it

        }, {
            loading.value = false
            throwable.value = it
        })
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(passwordPattern)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    fun forgotPassword() {
        if (isValidEmail()) {
            validateLivedata.value = Constant.IS_VALID
            validateLivedata.value = ""
            callForgotPasswordApi()
        }
    }

    private fun callForgotPasswordApi() {
        val query = JSONObject()
        query.put("emailaddress", strEmail)
        query.put("Url", "/forget-password")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        loading.value = true
        apiManager.forgotPassword(body).subscribe({
            loading.value = false
            forgotPassword.value = it

        }, {
            loading.value = false
            throwable.value = it
        })
    }

    fun resetPassword() {
        if (isValidCodeAndPwd()) {
            validateLivedata.value = ""
            callResetPasswordApi()
        }
    }

    private fun callResetPasswordApi() {
        val query = JSONObject()
        query.put("emailaddress", strEmail)
        query.put("ConfirmationCode", strCode)
        query.put("NewPassword", strPassword)
        query.put("Url", "/change-password")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        loading.value = true
        apiManager.resetPassword(body).subscribe({
            loading.value = false
            resetPassword.value = it

        }, {
            loading.value = false
            throwable.value = it
        })
    }

    private fun isValidEmail(): Boolean {
        if (TextUtils.isEmpty(strEmail.trim())) {
            validateLivedata.value = Constant.IS_EMAIL_EMPTY
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(strEmail.trim()).matches()) {
            validateLivedata.value = Constant.IS_EMAIL_INVALID
            return false
        }
        return true
    }

    private fun isValidCodeAndPwd(): Boolean {
        if (TextUtils.isEmpty(strCode.trim())) {
            validateLivedata.value = Constant.IS_CODE_EMPTY
            return false
        } else if (TextUtils.isEmpty(strPassword.trim())) {
            validateLivedata.value = Constant.IS_PASSWORD_EMPTY
            return false
        } else if (strPassword.trim().length < 8) {
            validateLivedata.value = Constant.IS_PASSWORD_INVALID
            return false
        } else if (!isValidPassword(strPassword)) {
            validateLivedata.value = Constant.IS_PASSWORD_INVALID
            return false
        }
        return true
    }
}