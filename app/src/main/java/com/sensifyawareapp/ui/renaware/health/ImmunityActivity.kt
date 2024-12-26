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

class ImmunityActivity : BaseActivity() {
    lateinit var binding: ActivityRenAwareHealthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareHealthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showColorCard()
    }

    private fun showColorCard() {
        binding.title = getString(R.string.immunity)

        binding.llCard1.index = 1
        binding.llCard2.index = 2
        binding.llCard3.index = 3
        binding.llCard4.index = 2

        binding.llCard1.title = "Leucocytes"
        binding.llCard2.title = "Nitrites"
        binding.llCard3.title = "Blood"
        binding.llCard4.title = "pH"

        binding.llCard1.content = "| Trace"
        binding.llCard2.content = "| Negative"
        binding.llCard3.content = "| Normal"
        binding.llCard4.content = "| Normal"

        binding.llCard1.linear6.visibility = View.GONE
        binding.llCard1.linear7.visibility = View.GONE
        binding.llCard1.layout1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.leucocytes_1)
        binding.llCard1.layout2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.leucocytes_2)
        binding.llCard1.layout3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.leucocytes_3)
        binding.llCard1.layout4.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.leucocytes_4)
        binding.llCard1.layout5.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.leucocytes_5)


        binding.llCard2.linear4.visibility = View.GONE
        binding.llCard2.linear5.visibility = View.GONE
        binding.llCard2.linear6.visibility = View.GONE
        binding.llCard2.linear7.visibility = View.GONE
        binding.llCard2.layout1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.nitrites_1)
        binding.llCard2.layout2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.nitrites_2)
        binding.llCard2.layout3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.nitrites_3)

        binding.llCard3.layout1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.blood_1)
        binding.llCard3.layout2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.blood_2)
        binding.llCard3.layout3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.blood_3)
        binding.llCard3.layout4.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.blood_4)
        binding.llCard3.layout5.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.blood_5)
        binding.llCard3.layout6.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.blood_6)
        binding.llCard3.layout7.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.blood_7)

        binding.llCard4.layout1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.ph_1)
        binding.llCard4.layout2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.ph_2)
        binding.llCard4.layout3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.ph_3)
        binding.llCard4.layout4.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.ph_4)
        binding.llCard4.layout5.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.ph_5)
        binding.llCard4.layout6.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.ph_6)
        binding.llCard4.layout7.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.ph_7)

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