package com.sensifyawareapp.ui.grammaraware.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentGrammarAwareIntroBinding
import com.sensifyawareapp.fragment.BaseFragment
import com.sensifyawareapp.utils.common.AppConstant

class GrammarAwareIntroFragment : BaseFragment() {
    private lateinit var binding: FragmentGrammarAwareIntroBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGrammarAwareIntroBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SensifyAwareApplication.clearGrammarAwareObjectFromJSON()
        AppConstant.arrayS = ArrayList()
        AppConstant.Accuracy = ArrayList()
        AppConstant.wordsString.clear()
        AppConstant.wordsString =ArrayList()
        val bundle = Bundle()
        try {
            val adjectives = context?.assets?.open("Adjectives.txt")?.bufferedReader().use {
                it?.readText()
            }
            adjectives?.let {
                bundle.putString("adjective", it)
            }
            val nouns = context?.assets?.open("Nouns.txt")?.bufferedReader().use {
                it?.readText()
            }
            nouns?.let {
                bundle.putString("noun", it)
            }
            val verbs = context?.assets?.open("Verbs.txt")?.bufferedReader().use {
                it?.readText()
            }
            verbs?.let {
                bundle.putString("verb", it)
            }
        } catch (e: Exception) {
            showError(getString(R.string.unknown_error_occurred))
            e.printStackTrace()
        }

        if (requireActivity().intent.getIntExtra("level", 0) == 1) {
            binding.nameText.text = getString(R.string.words2)
        } else {
            binding.nameText.text = getString(R.string.words3)
        }

        binding.btnSubmit.setOnClickListener {
            findNavController().navigate(R.id.action_grammarAwareIntroFragment_to_grammarAwareSpeakFragment,bundle)
        }
    }
}