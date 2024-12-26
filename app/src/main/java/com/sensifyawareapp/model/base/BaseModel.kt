package com.sensifyawareapp.model.base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

//import com.google.auto.value.AutoValue;
//import com.google.gson.Gson;
//import com.google.gson.TypeAdapter;
//@AutoValue
open class BaseModel {
    @SerializedName("IsSuccess")
    @Expose
    var isSuccess = false

    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("StatusCode")
    @Expose
    var statusCode: Int = 0

    /*@SerializedName("Data")
    @Expose
    var dataList =  DataModel()*/
}