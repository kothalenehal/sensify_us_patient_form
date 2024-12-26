package com.sensifyawareapp.ui.auth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.FragmentWalkThroughBinding
import com.sensifyawareapp.ui.auth.WalkThroughItem
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle


class WalkThroughFragment : Fragment() {
    private lateinit var binding: FragmentWalkThroughBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalkThroughBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initListener()
    }

    private fun init() {
        val list: ArrayList<WalkThroughItem> = ArrayList()
        list.add(
            WalkThroughItem(
                "animation/intro/smell_test.json",
                getString(R.string.strWalkThroughTitle1),
                getString(R.string.strWalkThrough1)
            )
        )
        list.add(
            WalkThroughItem(
                "animation/intro/traceaware.json",
                getString(R.string.strWalkThroughTitle2),
                getString(R.string.strWalkThrough2)
            )
        )
        list.add(
            WalkThroughItem(
                "animation/intro/memory.json",
                getString(R.string.strWalkThroughTitle3),
                getString(R.string.strWalkThrough3)
            )
        )

        binding.viewPager.adapter = MyPagerAdapter(this, list)

        binding.indicatorView.apply {
            setSliderColor(
                ContextCompat.getColor(context, R.color.blue_88),
                ContextCompat.getColor(context, R.color.color_light)
            )
            setSliderWidth(resources.getDimension(com.intuit.sdp.R.dimen._6sdp))
            setSliderHeight(resources.getDimension(com.intuit.sdp.R.dimen._6sdp))
            setSlideMode(IndicatorSlideMode.WORM)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setPageSize(binding.viewPager.adapter!!.itemCount)
            notifyDataChanged()
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                binding.indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels)
                Log.e("TAG", "onPageScrolled: $position")
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.indicatorView.onPageSelected(position)
                Log.e("TAG", "onPageSelected: $position")
                if (position == 2) {
                    binding.btnNext.visibility   = View.GONE
                    binding.btnSkip.visibility = View.GONE
                    binding.indicatorView.visibility = View.GONE
                    binding.btnStart.visibility = View.VISIBLE
                } else {
                    binding.btnNext.visibility = View.VISIBLE
                    binding.btnSkip.visibility = View.VISIBLE
                    binding.indicatorView.visibility = View.VISIBLE
                    binding.btnStart.visibility = View.GONE
                }
            }
        })
    }

    private fun initListener() {

        Log.e("TAG", "initListener: ${binding.viewPager.currentItem}")

        binding.btnStart.setOnClickListener {
            changeScreen()
        }
        binding.btnSkip.setOnClickListener {
            changeScreen()
        }

        binding.btnNext.setOnClickListener {
            Log.e("TAG", "initListener:12  ${binding.viewPager.currentItem}")

            binding.btnNext.setOnClickListener {
                if (binding.viewPager.currentItem != 2) {
                    binding.viewPager.currentItem++
                }/* else {
                    val navBuilder = NavOptions.Builder()
                    val navOptions: NavOptions =
                        navBuilder.setPopUpTo(R.id.walkThroughFragment, true).build()
                    findNavController().navigate(
                        R.id.action_walkThroughFragment_to_loginFragment,
                        null,
                        navOptions
                    )
                }*/
            }
        }
    }

    fun changeScreen() {
        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions =
            navBuilder.setPopUpTo(R.id.walkThroughFragment, true).build()
        findNavController().navigate(
            R.id.action_walkThroughFragment_to_loginFragment,
            null,
            navOptions
        )
    }

    class MyPagerAdapter(
        fragmentManager: WalkThroughFragment,
        private val list: ArrayList<WalkThroughItem>,
    ) :
        FragmentStateAdapter(fragmentManager) {

        override fun getItemCount(): Int {
            return list.size
        }

        override fun createFragment(position: Int): Fragment {
            return WalkthroughPageFragment.newInstance(
                list[position].image,
                list[position].description,
                list[position].title
            )
        }
    }
}