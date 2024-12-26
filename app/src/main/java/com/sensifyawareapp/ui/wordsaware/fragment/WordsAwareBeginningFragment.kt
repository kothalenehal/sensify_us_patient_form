package com.sensifyawareapp.ui.wordsaware.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.databinding.FragmentWordsAwareBeginningBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.utils.common.AppConstant
import java.util.*

class WordsAwareBeginningFragment : BaseFragment() {
    private lateinit var binding: FragmentWordsAwareBeginningBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordsAwareBeginningBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefUtils.saveData(
            requireActivity(),
            AppConstant.SharedPreferences.START_TIME,
            Calendar.getInstance().timeInMillis
        )
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                1
            )
        }
        AppConstant.totalWord = 0
        AppConstant.repeatWord = 0
        AppConstant.uniqueWord = 0
        AppConstant.longestWord = ""
        AppConstant.shortestWord = ""
        binding.btnSubmit.setOnClickListener {
            findNavController().navigate(WordsAwareBeginningFragmentDirections.actionWordsAwareBeginningFragmentToWordsAwareCountdownFragment())
        }
    }
}
