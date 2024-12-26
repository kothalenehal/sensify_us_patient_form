package com.sensifyawareapp.ui.auth.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StudyNumber (
    @SerializedName("Id")
    @Expose
    val id: Int = 0,

    @SerializedName("StudyNumber")
    @Expose
    val studyNumber: String,

)