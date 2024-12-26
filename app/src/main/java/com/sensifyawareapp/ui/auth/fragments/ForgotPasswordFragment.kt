package com.sensifyawareapp.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentForgotPasswordBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.auth.AuthenticationViewModel
import com.sensifyawareapp.ui.auth.Constant


class ForgotPasswordFragment : BaseFragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeValidation()
    }

    var index = 1

    private fun initUI() {
        binding.viewmodel = viewModel
        binding.visibleIndex = index

        if (arguments != null && arguments?.getString("email") != null)
            viewModel.strEmail = arguments?.getString("email")!!
        binding.btnSubmit.setOnClickListener {
            if (index == 1)
                viewModel.forgotPassword()
            else
                viewModel.resetPassword()
        }
    }

    private fun observeValidation() {
        viewModel.validateLivedata.observe(viewLifecycleOwner) {
            when (it) {
                Constant.IS_EMAIL_EMPTY -> {
                    reset()
                    binding.etEmail.requestFocus()
                    binding.etEmail.error = getString(R.string.strWarnEmail)
                }

                Constant.IS_EMAIL_INVALID -> {
                    reset()
                    binding.etEmail.requestFocus()
                    binding.etEmail.error = getString(R.string.strValidEmail)
                }

                Constant.IS_CODE_EMPTY -> {
                    reset()
                    binding.etCode.requestFocus()
                    binding.etCode.error = getString(R.string.strWarnCode)
                }

                Constant.IS_PASSWORD_EMPTY -> {
                    reset()
                    binding.etPassword.requestFocus()
                    binding.etPassword.error = getString(R.string.strWarnPassword)
                }

                Constant.IS_PASSWORD_INVALID -> {
                    reset()
                    binding.etPassword.requestFocus()
                    binding.etPassword.error = getString(R.string.strValidPassword)
                }

                else -> reset()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { if (it) showLoader() else hideLoader() }
        viewModel.throwable.observe(viewLifecycleOwner) { handleError(it) }
        viewModel.forgotPassword.observe(viewLifecycleOwner) {
            showError(it.message)
            if (it.isSuccess) {
                binding.visibleIndex = ++index
                binding.btnSubmit.text = requireActivity().getString(R.string.submit)
            }
        }
        viewModel.resetPassword.observe(viewLifecycleOwner) {
            showError(it.message)
            if (it.isSuccess) {
                findNavController().popBackStack()
            }
        }
    }

    private fun reset() {
        binding.etEmail.error = null
        binding.etCode.error = null
        binding.etPassword.error = null

    }

}