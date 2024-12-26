package com.sensifyawareapp.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.api.ApiInterface
import com.sensifyawareapp.api.ApiManager
import com.sensifyawareapp.databinding.DialogCancelTestBinding
import com.sensifyawareapp.model.base.CustomError
import com.sensifyawareapp.utils.NetworkUtils
import com.sensifyawareapp.utils.PrefUtils
import com.sensifyawareapp.utils.common.ApiConstants
import com.sensifyawareapp.utils.common.AppConstant
import com.sensifyawareapp.utils.permission.PermissionUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.*
import javax.inject.Inject


abstract class BaseActivity : AppCompatActivity() {

    @Inject
    lateinit var apiInterface: ApiInterface

    protected var mInputMethodManager: InputMethodManager? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mLastClickTime: Long = 0
    var isClinicalTestVersion: Boolean = false

    var apiManager: ApiManager = SensifyAwareApplication.getAppComponent().provideApiManager()
    var prefUtils: PrefUtils = SensifyAwareApplication.getAppComponent().providePrefUtil()
    var networkUtils: NetworkUtils = SensifyAwareApplication.getAppComponent().provideNetworkUtils()
    var permissionUtil: PermissionUtil =
        SensifyAwareApplication.getAppComponent().providePermissionUtil()


    open fun handleError(throwable: Throwable) {
        hideLoader()
        when (throwable) {
            is HttpException -> {
                val httpException: HttpException = throwable as HttpException
                handleHttpError(httpException)
            }
            is SocketTimeoutException, is ConnectException -> {
                handleNetworkError()
            }
            is CustomError -> {
                handleCustomError(throwable)
            }
            else -> {
                throwable.printStackTrace()
                showError(getString(R.string.api_failure))
            }
        }
    }

    private fun handleNetworkError() {
        //ConnectException only occurs when
        //either internet was not connected before calling api
        //or internet was turned off in the middle of outgoing request
        showError(getString(R.string.internet_not_available))
    }

    private fun handleHttpError(exception: HttpException) {
        Log.e(
            "---",
            "handleHttpError called code :" + exception.code()
                .toString() + " message : " + exception.message()
        )
        //for HttpException, use code() method to get Http code.
        //you can switch over code and handle different errors
        val code: Int = exception.code()
        when (code) {
            ApiConstants.ResponseCode.NOT_FOUND -> showError(getString(R.string.resource_not_found))
            ApiConstants.ResponseCode.CONFLICT -> showError(getString(R.string.server_conflict))
            else -> showError(getString(R.string.unknown_error_occurred))
        }
    }

    private fun handleCustomError(exception: CustomError) {
        Log.d("---", "handleCustomError() called with: exception = [" + exception.message + "]")

        showError(exception.error)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SensifyAwareApplication.getAppComponent().inject(this)
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setMessage(getString(R.string.loading))
        mProgressDialog!!.setCancelable(false)
        Log.e("base app", "onCreate: baseapp activity")
        mInputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?

        if (isTokenExpired()) {
            refreshToken()
        }
        isClinicalTestVersion =
            prefUtils.getBooleanData(this, AppConstant.SharedPreferences.CLINICAL_TEST_VERSION)
    }

    private fun isTokenExpired(): Boolean {
        return if (prefUtils.getStringData(this, AppConstant.SharedPreferences.ID_TOKEN) != null) {
            val tokenExpiredOn: Long =
                prefUtils.getLongData(this, AppConstant.SharedPreferences.TOKEN_EXPIRE_ON)
            Calendar.getInstance().timeInMillis > tokenExpiredOn
        } else false
    }

    private fun refreshToken() {
        val query = JSONObject()
        query.put(
            "RefreshToken",
            prefUtils.getStringData(this, AppConstant.SharedPreferences.REFRESH_TOKEN)
        )
        query.put("Url", "/refreshtoken")
        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.refreshToken(body).subscribe({
            if (it.isSuccess) {
                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.ID_TOKEN,
                    it.data?.tokens?.idToken
                )
                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.ACCESS_TOKEN,
                    it.data?.tokens?.accessToken
                )
                val calendar = Calendar.getInstance()
                it.data?.tokens?.expiresIn?.let { it1 -> calendar.add(Calendar.SECOND, it1) }
                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.TOKEN_EXPIRE_ON,
                    calendar.timeInMillis
                )
            }
        }, {
            handleError(it)
        })
    }

    fun showLoader() {
        if (!mProgressDialog!!.isShowing) {
            mProgressDialog!!.show()
        }
    }

    fun hideLoader() {
        if (mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }
    }

    fun showAlert(msg: String?) {
        if (msg == null) return
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun showError(msg: String?) {
        if (msg == null) return
        Snackbar.make(findViewById(android.R.id.content), msg, BaseTransientBottomBar.LENGTH_SHORT)
            .show()
    }

    fun hideKeyBoard(view: View?) {
        if (view != null) {
            mInputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyBoard(view: View?) {
        if (view != null) {
            mInputMethodManager!!.showSoftInput(view, 0)
        }
    }

    /**
     * it will return true if consecutive click occurs within [AppConstant.Delays.MIN_TIME_BETWEEN_CLICKS]
     *
     * @return true indicating do not allow any click, false otherwise
     */
    val isClickDisabled: Boolean
        get() = if (SystemClock.elapsedRealtime() - mLastClickTime < AppConstant.Delays.MIN_TIME_BETWEEN_CLICKS) true else {
            mLastClickTime = SystemClock.elapsedRealtime()
            false
        }

    /**
     * to add fragment in container
     * tag will be same as class name of fragment
     *
     * @param containerId    id of fragment container
     * @param addToBackStack should be added to backstack?
     */
    fun addFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean) {
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(containerId, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        }
        fragmentTransaction.commit()
    }

    /**
     * to replace fragment in container
     * tag will be same as class name of fragment
     *
     * @param containerId        id of fragment container
     * @param isAddedToBackStack should be added to backstack?
     */
    fun replaceFragment(fragment: Fragment, containerId: Int, isAddedToBackStack: Boolean) {
        val fragmentManager: FragmentManager = getSupportFragmentManager()
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, fragment)
        if (isAddedToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        }
        fragmentTransaction.commit()
    }

    fun showCloseDialog() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        val binding = DialogCancelTestBinding.inflate(
            LayoutInflater.from(this),
            ConstraintLayout(this),
            false
        )
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        binding.cardYes.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        dialog.show()
    }
}