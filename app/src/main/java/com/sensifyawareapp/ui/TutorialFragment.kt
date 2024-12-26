package com.sensifyawareapp.ui

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sensifyawareapp.databinding.FragmentTutorialBinding
import com.sensifyawareapp.fragment.BaseFragment

private const val ARG_PARAM1 = "param1"

class TutorialFragment : BaseFragment() {
    private var animationPath: String? = null
    private lateinit var animationComplete: AnimationComplete

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            animationPath = it.getString(ARG_PARAM1)
        }
    }

    lateinit var binding: FragmentTutorialBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
                if (::animationComplete.isInitialized)
                    animationComplete.onAnimationCompleted()
            }
        })
        binding.animationView.setAnimation(animationPath)

    }

    companion object {
        @JvmStatic
        fun newInstance(imageUrl: String, animationComplete1: AnimationComplete) =
            TutorialFragment().apply {
                animationComplete = animationComplete1
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, imageUrl)
                }
            }
    }
}