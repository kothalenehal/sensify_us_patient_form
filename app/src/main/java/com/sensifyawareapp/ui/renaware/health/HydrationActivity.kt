package com.sensifyawareapp.ui.renaware.health

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityRenAwareHealthBinding
import com.sensifyawareapp.databinding.ActivityRenalHealthBinding
import com.sensifyawareapp.ui.BaseActivity

class HydrationActivity : BaseActivity() {
    lateinit var binding: ActivityRenAwareHealthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareHealthBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
    private fun showColorCard() {
        binding.title = getString(R.string.hydration)

        binding.llCard2.mainLayout.visibility = View.GONE
        binding.llCard3.mainLayout.visibility = View.GONE
        binding.llCard4.mainLayout.visibility = View.GONE

        binding.llCard1.index = 1

        binding.llCard1.title = "Specific gravity"

        binding.llCard1.content = "| Normal"

        binding.llCard1.layout1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.specific_gravity_1)
        binding.llCard1.layout2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.specific_gravity_2)
        binding.llCard1.layout3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.specific_gravity_3)
        binding.llCard1.layout4.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.specific_gravity_4)
        binding.llCard1.layout5.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.specific_gravity_5)
        binding.llCard1.layout6.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.specific_gravity_6)
        binding.llCard1.layout7.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.specific_gravity_7)


        binding.llCard1.constraint.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        binding.llCard1.ivArrow.setOnClickListener {

            if (binding.llCard1.details.visibility == View.GONE) {
                binding.llCard1.ivArrow.setImageDrawable(getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24))
                binding.llCard1.details.visibility = View.VISIBLE
            } else {
                binding.llCard1.details.visibility = View.GONE
                binding.llCard1.ivArrow.setImageDrawable(getDrawable(R.drawable.baseline_keyboard_arrow_right_24))
            }
            TransitionManager.beginDelayedTransition(binding.llCard1.constraint, AutoTransition())
        }

    }

}