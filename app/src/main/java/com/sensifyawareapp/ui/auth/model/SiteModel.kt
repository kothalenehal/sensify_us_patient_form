package com.sensifyawareapp.ui.auth.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SiteModel(
    @SerializedName("Id")
    @Expose
    val id: Int = 0,

    @SerializedName("SiteName")
    @Expose
    val siteName: String,

    @SerializedName("ClinicalTrialId")
    @Expose
    val clinicalTrialId: Int = 0,

    @SerializedName("UserId")
    @Expose
    val userId: Int = 0
)

