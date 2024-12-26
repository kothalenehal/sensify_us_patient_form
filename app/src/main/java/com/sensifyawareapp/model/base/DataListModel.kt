package com.sensifyawareapp.model.base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DataListModel {
    @SerializedName("IsSuccess")
    @Expose
    var isSuccess = false

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("StatusCode")
    @Expose
    var statusCode: Int = 0

    @SerializedName("Data")
    @Expose
    var dataList =  DataModel()
}