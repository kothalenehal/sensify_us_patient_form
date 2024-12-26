package com.sensifyawareapp.model.base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class DataModel {

    @SerializedName("IsTrial")
    @Expose
    var isTrial = false

    @SerializedName("Accuracy")
    @Expose
    var accuracy: Int = 0
}