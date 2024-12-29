// LocaleManager.kt
package com.sensifyawareapp.utils
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LocaleManager {
    private const val LANGUAGE_KEY = "app_language"

    fun setLocale(context: Context, languageCode: String) {
        // Save language preference
        context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
            .edit()
            .putString(LANGUAGE_KEY, languageCode)
            .apply()

        // Update configuration
        updateResources(context, languageCode)
    }

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
            .getString(LANGUAGE_KEY, "en") ?: "en"
    }

    fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)
            context.createConfigurationContext(configuration)
        } else {
            configuration.locale = locale
            configuration.setLayoutDirection(locale)
            context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
            context
        }
    }
}

// BaseActivity.kt
