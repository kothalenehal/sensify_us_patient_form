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

class LiverHealthActivity : BaseActivity() {
    lateinit var binding: ActivityRenAwareHealthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareHealthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showColorCard()
    }

    private fun showColorCard() {
        binding.title = getString(R.string.liver_health)

        binding.llCard1.index = 1
        binding.llCard2.index = 2

        binding.llCard1.title = "Urobilinogen"
        binding.llCard2.title = "Bilirubin"

        binding.llCard1.content = "| Normal"
        binding.llCard2.content = "| Negative"

        binding.llCard1.linear6.visibility = View.GONE
        binding.llCard1.linear7.visibility = View.GONE
        binding.llCard1.layout1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.urobilinogen_1)
        binding.llCard1.layout2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.urobilinogen_2)
        binding.llCard1.layout3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.urobilinogen_3)
        binding.llCard1.layout4.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.urobilinogen_4)
        binding.llCard1.layout5.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.urobilinogen_5)


        binding.llCard2.linear5.visibility = View.GONE
        binding.llCard2.linear6.visibility = View.GONE
        binding.llCard2.linear7.visibility = View.GONE
        binding.llCard2.layout1.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.bilirubin_1)
        binding.llCard2.layout2.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.bilirubin_2)
        binding.llCard2.layout3.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.bilirubin_3)
        binding.llCard2.layout4.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.bilirubin_4)

        binding.llCard3.mainLayout.visibility = View.GONE
        binding.llCard4.mainLayout.visibility = View.GONE
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