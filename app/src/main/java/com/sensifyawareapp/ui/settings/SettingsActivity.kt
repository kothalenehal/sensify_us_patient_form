package com.sensifyawareapp.ui.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivitySettingsBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.auth.AuthActivity
import com.sensifyawareapp.ui.auth.model.SiteModel
import com.sensifyawareapp.utils.Utils
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Locale

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)


        binding.siteName =
            prefUtils.getStringData(this, AppConstant.SharedPreferences.SITE_NAME)

        binding.txBack.setOnClickListener {
            finish()
        }
        binding.tvLogout.setOnClickListener {
            logoutAlert()
        }
        binding.tvPrivacyPolicy.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sensifyaware.com/privacypolicy"))
            startActivity(browserIntent)
        }
        binding.tvChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }
        binding.tvUpdateAge.setOnClickListener {
            startActivity(Intent(this, UpdateProfileActivity::class.java))
        }
        binding.tvDeleteAccount.setOnClickListener {
            deleteAccount()
        }
        binding.clickSite.setOnClickListener {
//            showAlertDialog()
        }

        /* if (prefUtils.getBooleanData(this, AppConstant.SharedPreferences.IS_MODERATOR)){
             binding.clickSite.visibility = View.VISIBLE
         }else{
             binding.clickSite.visibility = View.GONE
         }*/


        // Language change click handler
        findViewById<View>(R.id.tv_language_change).setOnClickListener {
            // Show a dialog to select language
            showLanguageSelectionDialog()
        }

        binding.tvShare.setOnClickListener {
            val intent = Intent()
            intent.action = "android.intent.action.SEND"
            val sb: String = getString(R.string.description) + applicationContext.packageName
            intent.putExtra("android.intent.extra.TEXT", sb)
            intent.type = "text/plain"
            startActivity(intent)
        }

        binding.tvRate.setOnClickListener {
            val str = "android.intent.action.VIEW"
            val sb = "market://details?id=" +
                    packageName
            val intent = Intent(str, Uri.parse(sb))
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(intent)
            } catch (unused: ActivityNotFoundException) {
                val sb2 = "https://play.google.com/store/apps/details?id=" +
                        packageName
                startActivity(Intent(str, Uri.parse(sb2)))
            }
        }

        try {
            val versionName: String = packageManager
                .getPackageInfo(packageName, 0).versionName
            binding.version = "Version $versionName"

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

//    object LocaleHelper {
//
//        // Change the app locale
//        fun setLocale(context: Context, language: String) {
//            val locale = Locale(language)
//            Locale.setDefault(locale)
//            val config = Configuration(context.resources.configuration)
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                config.setLocale(locale)
//                context.createConfigurationContext(config)
//            } else {
//                config.locale = locale
//            }
//
//            context.resources.updateConfiguration(config, context.resources.displayMetrics)
//        }
//    }
    private fun showAlertDialog() {
        val sitesJsonList = prefUtils.getStringData(
            this,
            AppConstant.SharedPreferences.SITES
        )
        Log.e("TAG", "showAlertDialog: ${sitesJsonList?.isEmpty()}")
        if (sitesJsonList != null) {
            val gson = Gson()
            val siteList: ArrayList<SiteModel> = gson.fromJson(
                sitesJsonList, object : TypeToken<ArrayList<SiteModel>>() {}.type
            )

            val items = siteList.map { it.siteName }

            MaterialAlertDialogBuilder(this, R.style.MaterialThemeDialog)
                .setItems(items.toTypedArray()) { dialog_, which ->
                    Log.e("TAG", "showAlertDialog: ${items[which]}")
                    binding.tvSite.text = items[which]

                    for (testId in siteList) {
                        if (testId.siteName == items[which]) {
                            Log.e("TAG", "showAlertDialog: ID ${testId.id}")
                            prefUtils.saveData(
                                this,
                                AppConstant.SharedPreferences.SITE_ID,
                                testId.id
                            )

                            prefUtils.saveData(
                                this,
                                AppConstant.SharedPreferences.SITE_NAME,
                                testId.siteName
                            )
                        }
                    }
                    dialog_?.dismiss()
                }
                .setBackground(getDrawable(R.drawable.dialog_background))
                .show()
        }
    }

    private fun logout() {
        Utils.logout(this, prefUtils)
        startActivity(Intent(this, AuthActivity::class.java).putExtra("showLogin", true))
        finishAffinity()
    }

    private fun logoutAlert() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.are_you_sure_you_want_to_logout))
            .setPositiveButton(
                R.string.yes
            ) { _, _ ->
                logout()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun deleteAccount() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_your_account))
            .setPositiveButton(
                R.string.yes
            ) { _, _ ->
                deleteAccountApiCall()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showLanguageSelectionDialog() {
        val languages = arrayOf(
            "Japanese (JA)", "English (EN)", "French (FR)", "German (DE)",
            "Italian (IT)", "Spanish (ES)", "Dutch (NL)", "Swahili (SW)", "Arabic (AR)"
        )
        val languageCodes = arrayOf("ja", "en", "fr", "de", "it", "es", "nl", "sw", "ar")

        // Get current language to show the current selection
        val currentLanguage = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
            .getString("app_language", "en") ?: "en"
        val currentIndex = languageCodes.indexOf(currentLanguage)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.change_language))
            .setSingleChoiceItems(languages, currentIndex) { dialog, which ->
                val selectedLanguage = languageCodes[which]
                changeLanguage(selectedLanguage)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun changeLanguage(languageCode: String) {
        // Save the selected language to SharedPreferences
//        val sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
//        sharedPreferences.edit().putString("SelectedLanguage", languageCode).apply()

        // Change the app's locale
        //LocaleHelper.setLocale(this, languageCode)

        saveLanguage(languageCode)

        // Restart the activity to apply the language change
//        val intent = Intent(this, SettingsActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(intent)
//        finish()
        restartApp()
    }



    private fun deleteAccountApiCall() {
        val query = JSONObject()
        query.put("UserId", prefUtils.getIntData(this, AppConstant.SharedPreferences.USER_ID))
        query.put(
            "emailaddress",
            prefUtils.getStringData(this, AppConstant.SharedPreferences.EMAIL)
        )
        query.put(
            "AccessToken",
            prefUtils.getStringData(this, AppConstant.SharedPreferences.ACCESS_TOKEN)
        )
        query.put("Url", "/Delete-User")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        showLoader()

        apiManager.deleteAccount(body).subscribe({
            hideLoader()
            if (it.isSuccess) {
                logout()
            } else showError(it.message)
        }, {
            hideLoader()
            handleError(it)
        })
    }
}