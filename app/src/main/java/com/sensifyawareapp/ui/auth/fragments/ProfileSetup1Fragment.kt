package com.sensifyawareapp.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentProfileSetup1Binding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.ui.auth.AuthenticationViewModel
import com.sensifyawareapp.ui.auth.Constant
import com.sensifyawareapp.ui.auth.VerifyAccountActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*

class ProfileSetup1Fragment : BaseFragment() {
    private lateinit var binding: FragmentProfileSetup1Binding
    private val viewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            viewModel.strFirstName = it.getString("firstName")!!
            viewModel.strLastName = it.getString("lastName")!!
            viewModel.strEmail = it.getString("email")!!
            viewModel.strPassword = it.getString("password")!!
//            viewModel.strPhoneNumber = it.getString("phoneNumber")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile_setup1,
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
            if (it.toString().trim().isNotEmpty())
                when (it) {
                    Constant.IS_AGE_EMPTY -> {
                        binding.etAge.requestFocus()
                        binding.etAge.error = getString(R.string.strWarnAge)
                    }
                    Constant.IS_AGE_CORRECT -> {
                        binding.etAge.requestFocus()
                        binding.etAge.error = getString(R.string.strCorrectAge)
                    }
                    Constant.IS_GENDER_EMPTY -> {
                        binding.etAge.error = null
                        showError(getString(R.string.strWarnGender))
                    }

                    Constant.IS_VALID -> {
//                    val bundle = Bundle()
//                    bundle.putString("age", viewModel.strAge)
//                    bundle.putString("gender", viewModel.strGender)
//                    bundle.putString("firstName", viewModel.strFirstName)
//                    bundle.putString("lastName", viewModel.strLastName)
//                    bundle.putString("email", viewModel.strEmail)
////                    bundle.putString("phoneNumber", viewModel.strPhoneNumber)
//                    bundle.putString("password", viewModel.strPassword)
//                    findNavController().navigate(
//                        R.id.action_profileSetup1Fragment_to_profileSetup4Fragment,
//                        bundle
//                    )
                    }

                    else -> showError(it.toString())
                }
        })

    }

    private fun initListener() {
        binding.btnMale.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_bold)
        viewModel.strGender = ""
        val radioButton: RadioButton =
            requireActivity().findViewById(binding.radioGrp.checkedRadioButtonId)
        viewModel.strGender = radioButton.text.toString()
        Log.e("TAG", "initListener: ${viewModel.strGender}", )

        binding.btnMale.setOnClickListener {
            viewModel.strGender = "Male"
            binding.btnMale.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_bold)
            binding.btnFemale.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_regular)
            binding.btnOthers.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_regular)

        }
        binding.btnFemale.setOnClickListener {
            viewModel.strGender = "Female"
            binding.btnFemale.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_bold)
            binding.btnMale.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_regular)
            binding.btnOthers.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_regular)

        }
        binding.btnOthers.setOnClickListener {
            viewModel.strGender = "Other"
            binding.btnOthers.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_bold)
            binding.btnMale.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_regular)
            binding.btnFemale.typeface = ResourcesCompat.getFont(requireContext(), R.font.ws_regular)
        }

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
                prefUtils.saveData(
                    requireActivity(),
                    AppConstant.SharedPreferences.IS_VERIFIED,
                    false
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    requireActivity().finish()
                    startActivity(
                        Intent(
                            context,
                            VerifyAccountActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                }, 1000)

            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileSetup1Fragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}