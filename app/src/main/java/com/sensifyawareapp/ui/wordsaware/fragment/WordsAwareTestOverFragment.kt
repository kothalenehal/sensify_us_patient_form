package com.sensifyawareapp.ui.wordsaware.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.databinding.FragmentWordsAwareTestoverBinding
import com.sensifyawareapp.fragment.BaseFragment

class WordsAwareTestOverFragment : BaseFragment() {

    private lateinit var binding: FragmentWordsAwareTestoverBinding
    lateinit var randomWords: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            randomWords = it.getString("randomWord")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWordsAwareTestoverBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.words = randomWords
        binding.btnStopTest.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("randomWord", randomWords)
            findNavController().navigate(
                com.sensifyawareapp.R.id.action_wordsAwareTestOverFragment_to_wordsAwareWaitingFragment,
                bundle
            )
        }

    }
}