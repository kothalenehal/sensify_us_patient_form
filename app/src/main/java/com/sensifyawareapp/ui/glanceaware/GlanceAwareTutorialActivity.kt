package com.sensifyawareapp.ui.glanceaware

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityGlanceAwareTutorialBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.audioaware.AudioAwareActivity
import com.sensifyawareapp.ui.audioaware.SelectAwareLevelActivity
import com.sensifyawareapp.ui.glanceaware.fragments.GlanceAwarePageFragment
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle

class GlanceAwareTutorialActivity : BaseActivity() {

    private lateinit var binding: ActivityGlanceAwareTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_glance_aware_tutorial)

        init()
        binding.ivBack.setOnClickListener { finish() }
        binding.btnSkip.setOnClickListener {
            finish()
            startActivity(Intent(this, GlanceAwareActivity::class.java)
                .putExtra("level", 1))
//            startActivity(Intent(this, SelectAwareLevelActivity::class.java))
        }
    }

    private fun init() {
        val list: ArrayList<Item> = ArrayList()
        list.add(
            Item(
                R.drawable.glance_aware_tutorial_1,
                getString(R.string.glanceaware_tutorial_1)
            )
        )
        list.add(
            Item(
                R.drawable.glance_aware_tutorial_2,
                getString(R.string.glanceaware_tutorial_2)
            )
        )
        list.add(
            Item(
                R.drawable.glance_aware_tutorial_3,
                getString(R.string.glanceaware_tutorial_3)
            )
        )
        list.add(
            Item(
                R.drawable.glance_aware_tutorial_4,
                getString(R.string.glanceaware_tutorial_4)
            )
        )
        list.add(
            Item(
                R.drawable.glance_aware_tutorial_5,
                getString(R.string.glanceaware_tutorial_5)
            )
        )
        list.add(
            Item(
                R.drawable.glance_aware_tutorial_6,
                getString(R.string.glanceaware_tutorial_6)
            )
        )

        binding.viewPager.adapter = MyPagerAdapter(this, list)

        binding.indicatorView.apply {
            setSliderColor(
                ContextCompat.getColor(context, R.color.blue_25),
                ContextCompat.getColor(context, R.color.blue)
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
                if (position == list.size - 1) binding.buttonText =
                    getString(R.string.begin) else binding.buttonText = getString(R.string.skip)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.indicatorView.onPageSelected(position)
            }
        })
    }

    class MyPagerAdapter(
        fragmentManager: GlanceAwareTutorialActivity,
        private val list: ArrayList<Item>,
    ) :
        FragmentStateAdapter(fragmentManager) {

        override fun getItemCount(): Int {
            return list.size
        }

        override fun createFragment(position: Int): Fragment {
            return GlanceAwarePageFragment.newInstance(
                list[position].res,
                list[position].message
            )
        }
    }

    class Item(val res: Int, val message: String)

}