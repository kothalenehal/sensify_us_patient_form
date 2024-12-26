package com.sensifyawareapp.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentProfileSetup4Binding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.auth.AuthenticationViewModel
import com.sensifyawareapp.ui.auth.Constant
import com.sensifyawareapp.ui.auth.VerifyAccountActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*


class ProfileSetup4Fragment : BaseFragment() {
    private lateinit var binding: FragmentProfileSetup4Binding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.strFirstName = it.getString("firstName")!!
            viewModel.strLastName = it.getString("lastName")!!
            viewModel.strEmail = it.getString("email")!!
            viewModel.strPassword = it.getString("password")!!
            viewModel.strAge = it.getString("age")!!
            viewModel.strGender = it.getString("gender")!!
//            viewModel.strPhoneNumber = it.getString("phoneNumber")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile_setup4,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initListener()
    }

    private fun init() {
        binding.viewmodel = viewModel

        viewModel.validateLivedata.observe(viewLifecycleOwner, Observer {
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

                }
                else -> showError(it.toString())
            }
        })
    }

    private fun initListener() {
        viewModel.loading.observe(viewLifecycleOwner) { if (it) showLoader() else hideLoader() }
        viewModel.throwable.observe(viewLifecycleOwner) { handleError(it) }
        viewModel.userModelLivedata.observe(viewLifecycleOwner) {
            showError(it.message)
            if (it.isSuccess) {
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.ID_TOKEN,
                    it.data?.tokens?.idToken
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.ACCESS_TOKEN,
                    it.data?.tokens?.accessToken
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.REFRESH_TOKEN,
                    it.data?.tokens?.refreshToken
                )
                val calendar = Calendar.getInstance()
                it.data?.tokens?.expiresIn?.let { it1 -> calendar.add(Calendar.SECOND, it1) }
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.TOKEN_EXPIRE_ON,
                    calendar.timeInMillis
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.USER_ID,
                    it.data?.userId ?: 0
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.EMAIL,
                    it.data?.emailAddress
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.USER_NAME,
                    it.data?.userName
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    requireActivity().finishAffinity()
                    startActivity(
                        Intent(
                            context,
                            VerifyAccountActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                }, 1000)

            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileSetup4Fragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}