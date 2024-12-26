package com.sensifyawareapp.ui.renaware.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RenAwareModel {

    @SerializedName("IsSuccess")
    @Expose
    val isSuccess: Boolean? = null

    @SerializedName("Message")
    @Expose
    val message: String? = null

    @SerializedName("StatusCode")
    @Expose
    val statusCode: Long? = null

    @SerializedName("Data")
    @Expose
    val data: RenawareData? = null
}

class RenawareData {


    @SerializedName("IsTrial")
    @Expose
    val isTrial: Boolean? = null

    @SerializedName("Accuracy")
    @Expose
    val accuracy: Long? = null

    @SerializedName("RenawareResponseData")
    @Expose
    val renawareResponseData: RenawareResponseData? = null
}

class RenawareResponseData {
    @SerializedName("OverallWellness")
    @Expose
    val overallWellness: String? = null

    @SerializedName("HealthData")
    @Expose
    val healthData: List<HealthDaum>? = null
}

class HealthDaum {
    @SerializedName("Name")
    @Expose
    val name: String? = null

    @SerializedName("HealthDescription")
    @Expose
    val healthDescription: String? = null

    @SerializedName("HealthScore")
    @Expose
    val healthScore: String? = null

    @SerializedName("HealthColorData")
    @Expose
    val healthColorData: List<HealthColorDaum>? = null
}

class HealthColorDaum {
    @SerializedName("Name")
    @Expose
    val name: String? = null

    @SerializedName("Status")
    @Expose
    val status: String? = null

    @SerializedName("ProgressColors")
    @Expose
    val progressColors: List<ProgressColor>? = null

    @SerializedName("ProgressValue")
    @Expose
    val progressValue: String? = null

    @SerializedName("Description")
    @Expose
    val description: String? = null
}

class ProgressColor {
    @SerializedName("ColorCode")
    @Expose
    val colorCode: String? = null

    @SerializedName("Status")
    @Expose
    val status: Boolean? = null
}
