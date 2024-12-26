package com.sensifyawareapp.ui.traceaware.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PresignedFileModel() {
    @SerializedName("PathwithFilename")
    @Expose
    var pathwithFilename: String? = null

    @SerializedName("Presignedurl")
    @Expose
    var presignedurl: String? = null

    @SerializedName("orb_similarity_score")
    @Expose
    var orb_similarity_score: Float? = null

    @SerializedName("similarity_score")
    @Expose
    var similarity_score: Float? = null

}