package com.sensifyawareapp.ui.auth.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Tokens {
    @SerializedName("IdToken")
    @Expose
    var idToken: String? = null

    @SerializedName("AccessToken")
    @Expose
    var accessToken: String? = null

    @SerializedName("ExpiresIn")
    @Expose
    var expiresIn = 0

    @SerializedName("RefreshToken")
    @Expose
    var refreshToken: String? = null
}