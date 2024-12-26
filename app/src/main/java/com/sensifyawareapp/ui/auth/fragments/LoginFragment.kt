package com.sensifyawareapp.ui.auth.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentLoginBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.model.base.CustomError
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.ui.auth.AuthenticationViewModel
import com.sensifyawareapp.ui.auth.Constant
import com.sensifyawareapp.ui.auth.Constant.Companion.IS_PASSWORD_EMPTY
import com.sensifyawareapp.ui.auth.Constant.Companion.IS_PASSWORD_INVALID
import com.sensifyawareapp.ui.auth.Constant.Companion.IS_VALID
import com.sensifyawareapp.ui.auth.VerifyAccountActivity
import com.sensifyawareapp.ui.auth.model.UserModel
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*


class LoginFragment : BaseFragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initListener()
        observeValidation()
    }

    private fun observeValidation() {
        viewModel.validateLivedata.observe(viewLifecycleOwner) {

            when (it) {
                Constant.IS_EMAIL_EMPTY -> {
                    binding.etLoginId.requestFocus()
                    binding.etLoginId.error = getString(R.string.strWarnEmail)
                }

                Constant.IS_EMAIL_INVALID -> {
                    binding.etLoginId.requestFocus()
                    binding.etLoginId.error = getString(R.string.strValidEmail)
                }

                IS_PASSWORD_EMPTY -> {
                    binding.etPassword.requestFocus()
                    binding.etLoginId.error = null
                    binding.etPassword.error = getString(R.string.strWarnPassword)
                }

                IS_PASSWORD_INVALID -> {
                    binding.etPassword.requestFocus()
                    binding.etLoginId.error = null
                    binding.etPassword.error = getString(R.string.strValidPassword)
                }

                IS_VALID -> {
                }
            }
        }
    }

    private fun init() {
        binding.viewmodel = viewModel
    }

    private fun initListener() {
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
        }
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment())
        }

        viewModel.loading.observe(viewLifecycleOwner) { if (it) showLoader() else hideLoader() }
        viewModel.throwable.observe(viewLifecycleOwner) {
            if (it is CustomError && it.error.contains("Confirmation Code sent to")) {
                Handler(Looper.getMainLooper()).postDelayed({
                    requireActivity().finish()
                    startActivity(
                        Intent(
                            context,
                            VerifyAccountActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("fromLogin", true)
                    )
                }, 1000)
            } else
                handleError(it)
        }

        viewModel.userModelLivedata.observe(viewLifecycleOwner) {
            showError(it.message)
            Log.e(
                "TAG",
                "initListener: ${it.data?.isModerator} // ${it.data?.siteModel}  // ${it.data}",
            )
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
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.AGE,
                    it.data?.age
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.GENDER,
                    it.data?.gender
                )
                Log.e("Gender ", it.data?.gender.toString())
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.CLINICAL_TEST_VERSION,
                    it.data?.isTrial ?: false
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.IS_VERIFIED,
                    true
                )
                val gson = Gson()
                val jsonList = gson.toJson(it.data?.siteModel)
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.SITES,
                    jsonList
                )
                val jsonList2 = Gson().toJson(it.data?.studyNumber)
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.STUDY_NUMBER,
                    jsonList2
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.IS_MODERATOR,
                    it.data?.isModerator!!
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.ACCURACY,
                    it.data?.accuracy!!
                )

                requireActivity().finish()
                startActivity(
                    Intent(
                        context,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
            } else if (it.message!!.contains("Confirmation Code sent to")) {
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.USER_ID,
                    it.data?.userId ?: 0
                )
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.EMAIL,
                    binding.etLoginId.editText?.text.toString()
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(
                        Intent(
                            context,
                            VerifyAccountActivity::class.java
                        ).putExtra("fromLogin", true)
                    )
                }, 1000)
            }
        }

        viewModel.isOldUserLivedata.observe(viewLifecycleOwner) {
            if (viewModel.isOldUserDoneLivedata.value == true) {
                showAlertDialog(it)
                viewModel.isOldUserDoneLivedata.value = false
            }
        }
    }

    private fun showAlertDialog(userModel: UserModel) {
        val alertDialog: AlertDialog = AlertDialog.Builder(requireActivity()).create()
        alertDialog.setTitle(getString(R.string.app_name))
        alertDialog.setMessage("For security reasons, we need to confirm your email address.")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Ok"
        ) { dialog, _ ->
            dialog.dismiss()
            val intent = Intent(requireActivity(), VerifyAccountActivity::class.java)
                .putExtra("isOldUser", true)
                .putExtra("userId", userModel.data?.userId)
                .putExtra("password", userModel.data?.password)
                .putExtra("email", binding.etLoginId.editText?.text.toString())
            resultLauncher.launch(intent)
        }
        alertDialog.show()
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null && data.getStringExtra("data") == "reset") {
                    val bundle = Bundle()
                    bundle.putString("email", data.getStringExtra("email"))
                    findNavController().navigate(
                        R.id.action_loginFragment_to_forgotPasswordFragment,
                        bundle
                    )
                }
            }
        }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}