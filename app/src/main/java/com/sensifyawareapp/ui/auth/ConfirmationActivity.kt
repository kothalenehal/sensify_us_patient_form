package com.sensifyawareapp.ui.auth

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityConfirmationBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.auth.fragments.LoginFragment


class ConfirmationActivity : BaseActivity() {
    lateinit var binding: ActivityConfirmationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirmation.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    AuthActivity::class.java
                ).putExtra("showLogin", true)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

    }

}