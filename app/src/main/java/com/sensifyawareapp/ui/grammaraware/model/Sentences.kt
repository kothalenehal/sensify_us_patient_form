package com.sensifyawareapp.ui.grammaraware.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
class Sentences {
        @SerializedName("Sentences")
        var sentences: String? = null
        @SerializedName("ReferenceWords")
        var referenceWords: String? = null
        @SerializedName("SpeakStartTime")
        var speakStartTime: String? = null
        @SerializedName("SpeakEndTime")
        var speakEndTime: String? = null

}

