package com.sensifyawareapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import com.sensifyawareapp.R
import com.sensifyawareapp.ui.auth.AuthActivity
import com.sensifyawareapp.utils.common.AppConstant

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val window: Window = window
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)

        Log.e("TAG", "onCreate: $packageName")
        Handler(Looper.getMainLooper()).postDelayed({
            if (prefUtils.getStringData(
                    this,
                    AppConstant.SharedPreferences.EMAIL
                ) == null || !prefUtils.getBooleanData(
                    this,
                    AppConstant.SharedPreferences.IS_VERIFIED
                )
            ) {
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            } else startActivity(Intent(this@SplashActivity, MainActivity::class.java))

            finish()
        }, 2000)
    }

}