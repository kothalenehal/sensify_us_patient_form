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

class BoneHealthActivity : BaseActivity() {
    lateinit var binding: ActivityRenAwareHealthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareHealthBinding.inflate(layoutInflater)
        setContentView(binding.root)
showColorCard()
    }

    private fun showColorCard() {
        binding.title = getString(R.string.bone_health)

        binding.llCard2.mainLayout.visibility = View.GONE
        binding.llCard3.mainLayout.visibility = View.GONE
        binding.llCard4.mainLayout.visibility = View.GONE

        binding.llCard1.index = 1

        binding.llCard1.title = "Calcium"
        binding.llCard1.content = "| Normal"

        binding.llCard1.linear6.visibility = View.GONE
        binding.llCard1.linear7.visibility = View.GONE
        binding.llCard1.layout1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.calcium_1)
        binding.llCard1.layout2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.calcium_2)
        binding.llCard1.layout3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.calcium_3)
        binding.llCard1.layout4.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.calcium_4)
        binding.llCard1.layout5.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.calcium_5)


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