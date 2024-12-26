package com.sensifyawareapp.ui.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sensifyawareapp.databinding.ItemWalkthroughBinding
import com.sensifyawareapp.fragment.BaseFragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class WalkthroughPageFragment : BaseFragment() {
    private var animationPath: String? = null
    private var msg: String? = null
    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            animationPath = it.getString(ARG_PARAM1)
            msg = it.getString(ARG_PARAM2)
            title = it.getString(ARG_PARAM3)
        }
    }

    lateinit var binding: ItemWalkthroughBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ItemWalkthroughBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.animationView.setAnimation(animationPath)
        binding.msg = msg
        binding.title = title

    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String, msg: String, title:String) =
            WalkthroughPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, imageUrl)
                    putString(ARG_PARAM2, msg)
                    putString(ARG_PARAM3, title)
                }
            }
    }
}