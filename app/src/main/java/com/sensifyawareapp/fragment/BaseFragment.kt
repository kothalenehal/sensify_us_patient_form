package com.sensifyawareapp.fragment

import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.api.ApiInterface
import com.sensifyawareapp.api.ApiManager
import com.sensifyawareapp.model.base.CustomError
import com.sensifyawareapp.ui.grammaraware.model.Sentences
import com.sensifyawareapp.utils.NetworkUtils
import com.sensifyawareapp.utils.PrefUtils
import com.sensifyawareapp.utils.common.ApiConstants
import com.sensifyawareapp.utils.permission.PermissionUtil
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var apiInterface: ApiInterface

    protected var mInputMethodManager: InputMethodManager? = null

    var apiManager: ApiManager = SensifyAwareApplication.getAppComponent().provideApiManager()
    var prefUtils: PrefUtils = SensifyAwareApplication.getAppComponent().providePrefUtil()
    var networkUtils: NetworkUtils = SensifyAwareApplication.getAppComponent().provideNetworkUtils()
    var permissionUtil: PermissionUtil =
        SensifyAwareApplication.getAppComponent().providePermissionUtil()


    open fun handleError(throwable: Throwable) {
        hideLoader()
        if (throwable is HttpException) {
            val httpException: HttpException = throwable as HttpException
            handleHttpError(httpException)
        } else if (throwable is SocketTimeoutException || throwable is ConnectException) {
            handleNetworkError()
        } else if (throwable is CustomError) {
            handleCustomError(throwable)
        } else {
            throwable.printStackTrace()
            showError(getString(R.string.api_failure))
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


    fun showLoader() {
        (activity as BaseActivity).showLoader()
    }

    fun hideLoader() {
        (activity as BaseActivity).hideLoader()
    }

    fun showAlert(msg: String?) {
        (activity as BaseActivity).showAlert(msg)
    }

    fun showError(msg: String?) {
        (activity as BaseActivity).showError(msg)
    }

    fun hideKeyboard(view: View?) {
        (activity as BaseActivity).hideKeyBoard(view)
    }

    fun showKeyboard(view: View?) {
        (activity as BaseActivity).showKeyBoard(view)
    }

    val isClickDisabled: Boolean
        get() = (activity as BaseActivity).isClickDisabled

    /**
     * this method calls [BaseActivity.addFragment].
     * So, it will add fragment in Activity's container
     */
    fun addFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean) {
        (activity as BaseActivity).addFragment(
            fragment,
            containerId,
            addToBackStack
        )
    }

    /**
     * this method calls [BaseActivity.replaceFragment].
     * So, it will replace fragment in Activity's container
     */
    fun replaceFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean) {
        (activity as BaseActivity).replaceFragment(
            fragment,
            containerId,
            addToBackStack
        )
    }

    /**
     * this method uses [.getChildFragmentManager] and adds nested fragment inside Fragment's container.
     * using it with activity's container will throw [IllegalStateException] or may cause other errors.
     */
    protected fun addChildFragment(fragment: Fragment, containerId: Int, addToBackStack: Boolean) {
        val fragmentManager = this.childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(containerId, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        }
        fragmentTransaction.commit()
    }

    /**
     * this method uses [.getChildFragmentManager] and replaces nested fragment inside Fragment's container
     * using it with activity's container will throw [IllegalStateException] or may cause other errors.
     */
    protected fun replaceChildFragment(
        fragment: Fragment,
        containerId: Int,
        addToBackStack: Boolean
    ) {
        val fragmentManager = this.childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(containerId, fragment)
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        }
        fragmentTransaction.commit()
    }
}