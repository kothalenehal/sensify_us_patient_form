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
import com.sensifyawareapp.databinding.ActivityAboutCongnitiveTestsBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity

class AboutCognitiveTestsActivity : BaseActivity() {

    lateinit var binding: ActivityAboutCongnitiveTestsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutCongnitiveTestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener{
            finish()

        }
    }

}