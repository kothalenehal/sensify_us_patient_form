package com.sensifyawareapp.ui.auth

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityAuthBinding
import com.sensifyawareapp.ui.BaseActivity

class AuthActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment)

        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph_auth)

        if (intent.getBooleanExtra("showLogin", false))
            graph.setStartDestination(R.id.loginFragment)
        else
            graph.setStartDestination(R.id.walkThroughFragment)

        navHostFragment.navController.graph = graph

    }
}