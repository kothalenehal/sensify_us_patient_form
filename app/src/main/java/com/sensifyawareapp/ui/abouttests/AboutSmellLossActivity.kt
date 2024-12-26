package com.sensifyawareapp.ui.abouttests

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.Log
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityAboutSmellLossBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.utils.CustomTypefaceSpan

class AboutSmellLossActivity : BaseActivity() {
    lateinit var binding: ActivityAboutSmellLossBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutSmellLossBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llAnosmia.tvMessage.text = setText(getString(R.string.this_refers_to), 17, 27)
        binding.llHyposmia.tvMessage.text = setText(getString(R.string.partial_loss), 17, 29)
        binding.llParosmia.tvMessage.text = setText(getString(R.string.distorted_sense_of_smell), 10, 20)
        binding.llPhantosmia    .tvMessage.text = getString(R.string.condition_that_causes)




        binding.ivBack.setOnClickListener {
            finish()
        }

    }

    fun setText(message: String, start: Int, end: Int): SpannableString {

        val spanText = SpannableString(message)
        spanText.setSpan(
            BackgroundColorSpan(resources.getColor(R.color.scent_color)),
            start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val customTypefaceSpan = CustomTypefaceSpan(resources.getFont(R.font.ws_bold))

        spanText.setSpan(customTypefaceSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return spanText
    }


}