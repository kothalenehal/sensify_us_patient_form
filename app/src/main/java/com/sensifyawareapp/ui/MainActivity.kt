package com.sensifyawareapp.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityMain2Binding
import com.sensifyawareapp.ui.settings.SettingsActivity
import com.sensifyawareapp.utils.common.AppConstant


class MainActivity : BaseActivity() {
    private var navHostFragment = NavHostFragment()
    private lateinit var navController: NavController
    private lateinit var configuration: AppBarConfiguration
    private lateinit var binding: ActivityMain2Binding
    lateinit var reviewManager: ReviewManager

    private var UPDATE_CODE = 23
    lateinit var appUpdateManager: AppUpdateManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("----", "onCreate: MainActivity", )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
//        setSupportActionBar(binding.toolBar)
        navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment)
        appUpdateManager = AppUpdateManagerFactory.create(this)
        navController = navHostFragment.navController

        reviewManager = ReviewManagerFactory.create(this);
        appUpdate()
        val cornerRadius = resources.getDimension(com.intuit.sdp.R.dimen._25sdp)
        val bottomBarBackground = binding.bottomNavigation.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel = bottomBarBackground.shapeAppearanceModel
            .toBuilder()
            .setTopRightCorner(CornerFamily.ROUNDED, cornerRadius)
            .setTopLeftCorner(CornerFamily.ROUNDED, cornerRadius)
            .build()

//        if (OpenCVLoader.initDebug()) Log.e("TAG", "OpenCVLoader: Success")
//        else Log.e("TAG", "OpenCVLoader: Failed")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.bottomNavigation.itemActiveIndicatorColor =
                getColorStateList(R.color.font_color)
        }

        configuration = AppBarConfiguration.Builder(
            R.id.trainingFragment, R.id.testsFragment, R.id.reportsFragment, R.id.calendarFragment
        ).build()
        binding.bottomNavigation.itemIconTintList = null

        /* val radius = resources.getDimension(R.dimen._25sdp)
         val bottomBarBackground = binding.bottomNavigation.background as MaterialShapeDrawable
         bottomBarBackground.shapeAppearanceModel =
             bottomBarBackground.shapeAppearanceModel.toBuilder()
                 .setTopRightCorner(CornerFamily.ROUNDED, radius)
                 .setTopLeftCorner(CornerFamily.ROUNDED, radius).build()*/

//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        NavigationUI.setupActionBarWithNavController(this, navController, configuration)

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.trainingFragment) {
                prefUtils.saveData(this, AppConstant.SharedPreferences.SELECTED_MENU, 2)
                navController.navigate(
                    R.id.trainingFragment,
                    null,
                    NavOptions.Builder().setLaunchSingleTop(true)
                        .setPopUpTo(R.id.trainingFragment, true).build()
                )
            } else if (item.itemId == R.id.testsFragment) {
                navController.navigate(
                    R.id.testsFragment,
                    null,
                    NavOptions.Builder().setLaunchSingleTop(true)
                        .setPopUpTo(R.id.testsFragment, true).build()
                )
            } else if (item.itemId == R.id.reportsFragment) {
                navController.navigate(
                    R.id.reportsFragment,
                    null,
                    NavOptions.Builder().setLaunchSingleTop(true)
                        .setPopUpTo(R.id.reportsFragment, true).build()
                )
            } else if (item.itemId == R.id.calendarFragment) {
                navController.navigate(
                    R.id.calendarFragment,
                    null,
                    NavOptions.Builder().setLaunchSingleTop(true)
                        .setPopUpTo(R.id.calendarFragment, true).build()
                )
            }
            false
        }
        showRateApp()

        /*val crashButton = Button(this)

        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT))*/

        /* FirebaseApp.initializeApp(this);
         val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
         mFirebaseAnalytics.setUserId("yourUserTag")*/

        binding.ivMenu.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navController, configuration) || super.onSupportNavigateUp()
    }

    private fun appUpdate() {
//        appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                // Request the update.
                Log.e("TAG ", "appUpdate: Call")
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo, AppUpdateType.IMMEDIATE, this, UPDATE_CODE
                )
            }
        }
    }

    private fun showRateApp() {
        val request: Task<ReviewInfo> = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            Log.e("TAG", "showRateApp: ${task.isSuccessful}")
            if (task.isSuccessful) {
                // Getting the ReviewInfo object
                val reviewInfo: ReviewInfo = task.result
                val flow: Task<Void> = reviewManager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    it,
                    AppUpdateType.IMMEDIATE,
                    this,
                    UPDATE_CODE
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
//appUpdate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("Update Failed", "Update flow failed")
            }
        }
    }


}