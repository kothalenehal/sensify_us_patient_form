package com.sensifyawareapp.ui.abouttests

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.TypefaceSpan
import androidx.core.content.res.ResourcesCompat
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityAboutScentAwareBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity


class AboutScentAwareActivity : BaseActivity() {

    lateinit var binding: ActivityAboutScentAwareBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAboutScentAwareBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val typeface = resources.getFont(R.font.ws_bold)
//        binding.textView.typeface = typeface

        val textBold = ResourcesCompat.getFont(this, R.font.ws_bold);

        val spanText = SpannableString(binding.textView.text)

        spanText.setSpan(
            BackgroundColorSpan(resources.getColor(R.color.highlight_color)),
            3, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        spanText.setSpan(textBold?.let { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            TypefaceSpan(it)
        } 
        }, 3, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textView.text = spanText

        binding.ivBack.setOnClickListener{
            finish()
        }

    }
}