package com.sensifyawareapp.ui.glanceaware.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.sensifyawareapp.databinding.FragmentGlanceAwarePageBinding
import com.sensifyawareapp.databinding.ItemWalkthroughBinding
import com.sensifyawareapp.fragment.BaseFragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GlanceAwarePageFragment : BaseFragment() {
    private var image: Int? = null
    private var description: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            image = it.getInt(ARG_PARAM1)
            description = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentGlanceAwarePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGlanceAwarePageBinding.inflate(inflater, container, false)
        binding.description = description
        binding.res = ContextCompat.getDrawable(requireActivity(), image!!)
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: Int, description: String) =
            GlanceAwarePageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, imageUrl)
                    putString(ARG_PARAM2, description)
                }
            }
    }
}