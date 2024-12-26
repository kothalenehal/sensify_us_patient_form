package com.sensifyawareapp.model.base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

open class BaseObjectModel<T> : BaseModel() {
    @SerializedName("Data")
    @Expose
    var data: T? = null
        private set

    fun setData(data: T) {
        this.data = data
    }
}