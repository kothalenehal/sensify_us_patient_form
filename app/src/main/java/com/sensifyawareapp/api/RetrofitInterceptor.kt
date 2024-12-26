package com.sensifyawareapp.api

import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.utils.PrefUtils
import com.sensifyawareapp.utils.common.ApiConstants
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Interceptor for Retrofit to add auth key to header
 */
class RetrofitInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        val token = PrefUtils().getStringData(
            SensifyAwareApplication.getAppComponent().provideApplication(),
            AppConstant.SharedPreferences.ID_TOKEN
        )
        request = request.newBuilder()
            .addHeader(ApiConstants.params.AUTHORIZATION, "$token")
            .addHeader("Connection", "keep-alive")
            .addHeader("Accept", "application/json")
            .addHeader("Accept-Encoding", "gzip, deflate, br")
            .addHeader("Accept", "*/*")
            .build()
        return chain.proceed(request)
    }
}