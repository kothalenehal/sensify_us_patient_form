package com.sensifyawareapp.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Patterns
import com.sensifyawareapp.utils.common.AppConstant
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern


object Utils {
    fun validEmail(email: String): Boolean {
        val pattern: Pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    fun convertToLocalTime(
        timestamp: Long,
        format: String = AppConstant.DateTime.DD_MM_YYYY
    ): String {
        val utcDate = Date(timestamp * 1000)
        val df: DateFormat = SimpleDateFormat(format, Locale.getDefault())
        df.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        return df.format(utcDate)
    }

    @JvmStatic
    fun formatDateTime(
        date: String,
        toFormat: String,
        fromFormat: String
    ): String {
        val dateFormat =
            SimpleDateFormat(fromFormat, Locale.getDefault())
        val objDate = dateFormat.parse(date)
        val dateFormat2 = SimpleDateFormat(toFormat, Locale.getDefault())
        val finalDate = dateFormat2.format(objDate)
        return finalDate
    }

    fun dpToPx(context: Context, dp: Int): Int {
        val displayMetrics: DisplayMetrics = context.getResources().getDisplayMetrics()
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

     fun logout(context: Context, prefUtils: PrefUtils) {
        prefUtils.saveData(context, AppConstant.SharedPreferences.ID_TOKEN, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.ACCESS_TOKEN, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.REFRESH_TOKEN, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.TOKEN_EXPIRE_ON, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.EMAIL, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.USER_ID, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.AGE, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.GENDER, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.SITE_ID, 0)
        prefUtils.saveData(context, AppConstant.SharedPreferences.PATIENT_ID, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.SITE_NAME, null)
        prefUtils.saveData(context, AppConstant.SharedPreferences.STUDY_NUMBER, null)
    }

}