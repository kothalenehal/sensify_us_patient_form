package com.sensifyawareapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityTutorialBinding
import com.sensifyawareapp.ml.barcode.QRCodeScanActivity
import com.sensifyawareapp.ui.audioaware.SelectAwareLevelActivity
import com.sensifyawareapp.ui.grammaraware.GrammarAwareLevelActivity
import com.sensifyawareapp.ui.scentaware.OdorDifferentiationActivity
import com.sensifyawareapp.ui.scentaware.OdorIntensityActivity
//import com.sensifyawareapp.ui.scentaware.OLDQRCodeScanActivity
import com.sensifyawareapp.ui.traceaware.SelectTraceAwareLevelActivity
import com.sensifyawareapp.utils.common.AppConstant
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import java.util.*

class TutorialActivity : BaseActivity(), AnimationComplete {
    private lateinit var binding: ActivityTutorialBinding

    private val listAnimation = ArrayList<String>()
    private val listMsg = ArrayList<String>()
    private var isAutoScrollingCompleted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tutorial)

        binding.toolbar.tvTitle.setTextColor(Color.WHITE)
        binding.ivBack.setOnClickListener {
            if (intent.getBooleanExtra(
                    "odorDifferentiationTutorial",
                    false
                ) || intent.getBooleanExtra(
                    "odorIntensityTutorial",
                    false
                ) || intent.getBooleanExtra("OdorIdentificationReTest", false)
            ) {
                showCloseDialog()
            } else finish()

        }
        binding.isEnabled = true

        val selectedMenu = prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_MENU)
        if (selectedMenu == 1) {
            if (intent.getBooleanExtra("odorDifferentiationTutorial", false)) {
                binding.title = getString(R.string.title_tutorial_odor_diff_test)
                fillDataInListOdorDiff()
            } else if (intent.getBooleanExtra("odorIntensityTutorial", false)) {
                binding.title = getString(R.string.title_tutorial_odor_intensity_test)
                fillDataInListOdorIntensity()
            } else if (intent.getBooleanExtra("OdorIdentificationReTest", false)) {
                binding.title = getString(R.string.title_tutorial_re_test)
                fillDataInListOdorIdentificationRetest()
            } else {
                binding.title = getString(R.string.title_tutorial_scent_aware)
                fillDataInListOdorIdentification()
            }
        } else if (selectedMenu == 2) {
            binding.title = getString(R.string.title_retraining)
            fillDataInListSmellRetraining()
        } else if (selectedMenu == 3) {
            binding.title = getString(R.string.trace_aware)
        } else if (selectedMenu == 4) {
            binding.title = getString(R.string.how_to_use_audio_aware)
        }

        binding.btnSkip.setOnClickListener {
            beginTest(selectedMenu)
        }
        binding.btnNext.setOnClickListener {
            beginTest(selectedMenu)
        }
    }

    private fun beginTest(selectedMenu: Int) {
        if (selectedMenu == 1) {
            if (intent.getBooleanExtra("odorDifferentiationTutorial", false)) {
                startActivity(Intent(this, OdorDifferentiationActivity::class.java))
            } else if (intent.getBooleanExtra("odorIntensityTutorial", false)) {
                startActivity(Intent(this, OdorIntensityActivity::class.java))
            } else {
                startActivity(Intent(this, QRCodeScanActivity::class.java))
                if (!intent.getBooleanExtra("OdorIdentificationReTest", false))
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.START_TIME,
                        Calendar.getInstance().timeInMillis
                    )
            }
        } else if (selectedMenu == 2) {
            startActivity(Intent(this, QRCodeScanActivity::class.java))
        } else if (selectedMenu == 3) {
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.START_TIME,
                Calendar.getInstance().timeInMillis
            )
            startActivity(Intent(this, SelectTraceAwareLevelActivity::class.java))
        } else if (selectedMenu == 4) {
            startActivity(Intent(this, SelectAwareLevelActivity::class.java))
        } else if (selectedMenu == 5) {
            startActivity(Intent(this, SelectAwareLevelActivity::class.java))
        } else if (selectedMenu == 7) {
            startActivity(Intent(this, GrammarAwareLevelActivity::class.java))
        }
    }

    private fun fillDataInListSmellRetraining() {
        listMsg.add("Pick any tube from the box.")
        listMsg.add("Scan the QR code on\nthe tube.")
        listMsg.add("Smell the sample. The name\nof the scent will be displayed \non your device.")
        listMsg.add("Repeat with other tubes\nuntil all the tubes are sampled.")

        listAnimation.add("animation/odor_identification/test/slide_1.json")
        listAnimation.add("animation/odor_identification/test/slide_2.json")
        listAnimation.add("animation/odor_identification/test/slide_3.json")
        listAnimation.add("animation/odor_identification/test/slide_5.json")
        setupViewPager(listMsg, listAnimation)

    }

    private fun fillDataInListOdorIdentification() {
        listMsg.add("Pick any tube from the box.")
        listMsg.add("Scan the QR code on\nthe tube.")
        listMsg.add("Smell the sample.")
        listMsg.add("Select your response\non your mobile device.")
        listMsg.add("Repeat with other tubes\nuntil all the tubes are sampled.")
        listMsg.add("Collect the sampled tubes\nin the lid container.")

        listAnimation.add("animation/odor_identification/test/slide_1.json")
        listAnimation.add("animation/odor_identification/test/slide_2.json")
        listAnimation.add("animation/odor_identification/test/slide_3.json")
        listAnimation.add("animation/odor_identification/test/slide_4.json")
        listAnimation.add("animation/odor_identification/test/slide_5.json")
        listAnimation.add("animation/odor_identification/test/slide_6.json")
        setupViewPager(listMsg, listAnimation)
    }

    private fun fillDataInListOdorIdentificationRetest() {
        listMsg.add("Pick any tube from the \nlid container.")
        listMsg.add("Scan the QR code on\nthe tube.")
        listMsg.add("Smell the sample.")
        listMsg.add("Select your response,\nbefore the timer runs out.")
        listMsg.add("Place the sampled tubes\nback in the foam tray.")
        listMsg.add("Check your score after completing\nall the tests.")

        listAnimation.add("animation/odor_identification/retest/slide_1.json")
        listAnimation.add("animation/odor_identification/retest/slide_2.json")
        listAnimation.add("animation/odor_identification/retest/slide_3.json")
        listAnimation.add("animation/odor_identification/retest/slide_4.json")
        listAnimation.add("animation/odor_identification/retest/slide_5.json")
        listAnimation.add("animation/odor_identification/retest/slide_6.json")
        setupViewPager(listMsg, listAnimation)
    }

    private fun fillDataInListOdorDiff() {
        listMsg.add("Pick the tubes from the box,\nwith the icons displayed on your device.")
        listMsg.add("Open the cap and\nsmell the tubes\none at a time.")
        listMsg.add("Open the cap and\nsmell the tubes\none at a time.")
        listMsg.add("Identify the scent\nthat is different from\nthe others.")
        listMsg.add("Select your response,\nwhen prompted on the screen.")

        listAnimation.add("animation/odor_differentiation/slide_1.json")
        listAnimation.add("animation/odor_differentiation/slide_2.json")
        listAnimation.add("animation/odor_differentiation/slide_3.json")
        listAnimation.add("animation/odor_differentiation/slide_4.json")
        listAnimation.add("animation/odor_differentiation/slide_5.json")
        setupViewPager(listMsg, listAnimation)
    }

    private fun fillDataInListOdorIntensity() {
        listMsg.add("Pick the tubes from the box,\nwith the icons displayed on your device.")
        listMsg.add("Open the cap and\nsmell the tubes\none at a time.")
        listMsg.add("Open the cap and\nsmell the tubes\none at a time.")
        listMsg.add("Identify the smell that\nis the strongest.")
        listMsg.add("Select your response.")
        listMsg.add("Check your score after completing\nall the tests.")

        listAnimation.add("animation/odor_intensity/slide_1.json")
        listAnimation.add("animation/odor_intensity/slide_2.json")
        listAnimation.add("animation/odor_intensity/slide_3.json")
        listAnimation.add("animation/odor_intensity/slide_4.json")
        listAnimation.add("animation/odor_intensity/slide_5.json")
        listAnimation.add("animation/odor_intensity/slide_6.json")
        setupViewPager(listMsg, listAnimation)
    }


    private fun setupViewPager(list: List<String>, animation: List<String>) {
        binding.viewpager.adapter = MyPagerAdapter(this, animation, this)
        binding.indicatorView.apply {
            setSliderColor(
                ContextCompat.getColor(this@TutorialActivity, R.color.blue_88),
                ContextCompat.getColor(this@TutorialActivity, R.color.color_light)
            )
            setSliderWidth(resources.getDimension(com.intuit.sdp.R.dimen._6sdp))
            setSliderHeight(resources.getDimension(com.intuit.sdp.R.dimen._6sdp))
            setSlideMode(IndicatorSlideMode.WORM)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setPageSize(binding.viewpager.adapter!!.itemCount)
            notifyDataChanged()
        }

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                binding.indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.e("TAG", "onPageSelected: ${position == list.size - 1}")
                binding.indicatorView.onPageSelected(position)
                binding.isEnabled = position == list.size - 1
                binding.message = list[position]
            }
        })
    }

    class MyPagerAdapter(
        fragmentManager: TutorialActivity,
        private val list: List<String>,
        private val animationComplete: AnimationComplete
    ) :
        FragmentStateAdapter(fragmentManager) {

        override fun getItemCount(): Int {
            return list.size
        }

        override fun createFragment(position: Int): Fragment {
            return TutorialFragment.newInstance(list[position], animationComplete)
        }
    }

    override fun onBackPressed() {
        if (intent.getBooleanExtra(
                "odorDifferentiationTutorial",
                false
            ) || intent.getBooleanExtra("odorIntensityTutorial", false)
        ) {
            showCloseDialog()
        } else super.onBackPressed()
    }

    override fun onAnimationCompleted() {
        if (!isAutoScrollingCompleted) {
            isAutoScrollingCompleted = binding.viewpager.currentItem == listAnimation.size - 1
            binding.viewpager.currentItem = binding.viewpager.currentItem + 1
        }
    }
}

interface AnimationComplete {
    fun onAnimationCompleted()
}