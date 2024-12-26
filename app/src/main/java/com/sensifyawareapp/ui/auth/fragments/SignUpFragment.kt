package com.sensifyawareapp.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentSignUpBinding
import com.sensifyawareapp.ui.auth.AuthenticationViewModel
import com.sensifyawareapp.ui.auth.Constant


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_sign_up,
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

    private fun init() {
        binding.viewmodel = viewModel
        binding.alreadyUser.setOnClickListener {
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
        }
    }

    private fun initListener() {
    }

    private fun observeValidation() {
        viewModel.validateLivedata.observe(viewLifecycleOwner) {
            when (it) {
                Constant.IS_FIRST_NAME_EMPTY -> {
                    resetErrorMessages()
                    binding.etFirstName.requestFocus()
                    binding.etFirstName.error = getString(R.string.strWarnFirstName)
                }
                Constant.IS_FIRST_NAME_CORRECT ->{
                    resetErrorMessages()
                    binding.etFirstName.requestFocus()
                    binding.etFirstName.error = "Please enter Correct name"
                }
                Constant.IS_LAST_NAME_EMPTY -> {
                    resetErrorMessages()
                    binding.etLastName.requestFocus()
                    binding.etLastName.error = getString(R.string.strWarnLastname)
                }

                Constant.IS_LAST_NAME_CORRECT ->{
                    resetErrorMessages()
                    binding.etLastName.requestFocus()
                    binding.etLastName.error = "Please enter Correct name"
                }
                Constant.IS_EMAIL_EMPTY -> {
                    resetErrorMessages()
                    binding.etEmail.requestFocus()
                    binding.etEmail.error = getString(R.string.strWarnEmail)
                }
                Constant.IS_EMAIL_INVALID -> {
                    resetErrorMessages()
                    binding.etEmail.requestFocus()
                    binding.etEmail.error = getString(R.string.strValidEmail)
                }
//                Constant.IS_PHONE_NUMBER_EMPTY -> {
//                    resetErrorMessages()
//                    binding.etPhone.requestFocus()
//                    binding.etPhone.error = getString(R.string.strWarnPhoneNumber)
//                }
                Constant.IS_PASSWORD_EMPTY -> {
                    resetErrorMessages()
                    binding.etPassword.requestFocus()
                    binding.etPassword.error = getString(R.string.strWarnPassword)
                }
                Constant.IS_PASSWORD_INVALID -> {
                    resetErrorMessages()
                    binding.etPassword.requestFocus()
                    binding.etPassword.error = getString(R.string.password_helper_text)
                }
                Constant.IS_RE_PASS_EMPTY -> {
                    resetErrorMessages()
                    binding.etReenterPassword.requestFocus()
                    binding.etReenterPassword.error = getString(R.string.strWarnRePassword)
                }
                Constant.IS_PASS_NOT_MATCH -> {
                    resetErrorMessages()
                    binding.etReenterPassword.requestFocus()
                    binding.etReenterPassword.error = getString(R.string.strMatchPassword)
                }
                Constant.IS_VALID -> {
                    val bundle = Bundle()
                    bundle.putString("firstName", viewModel.strFirstName)
                    bundle.putString("lastName", viewModel.strLastName)
                    bundle.putString("email", viewModel.strEmail)
//                    bundle.putString("phoneNumber", viewModel.strPhoneNumber)
                    bundle.putString("password", viewModel.strPassword)
                    findNavController().navigate(
                        R.id.action_signUpFragment_to_profileSetup1Fragment,
                        bundle
                    )
                }
            }
        }
    }

    private fun resetErrorMessages() {
        binding.etFirstName.error = null
        binding.etLastName.error = null
        binding.etEmail.error = null
        binding.etPassword.error = null
        binding.etReenterPassword.error = null
        binding.etPhone.error = null
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}