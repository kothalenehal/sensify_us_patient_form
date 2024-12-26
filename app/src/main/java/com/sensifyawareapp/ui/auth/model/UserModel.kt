package com.sensifyawareapp.ui.auth.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.sensifyawareapp.model.base.BaseObjectModel
import com.sensifyawareapp.ui.scentaware.model.TubeTestsRecord

class UserModel : BaseObjectModel<UserModel>() {
    @SerializedName("EmailAddress")
    @Expose
    var emailAddress: String? = null

    @SerializedName("UserId")
    @Expose
    var userId = 0

    @SerializedName("UserName")
    @Expose
    var userName: String? = null

    @SerializedName("Gender")
    @Expose
    var gender: String? = null

    @SerializedName("Age")
    @Expose
    var age: String? = null

    @SerializedName("IsTrial")
    @Expose
    var isTrial: Boolean? = null

    @SerializedName("Accuracy")
    @Expose
    var accuracy = 0

    @SerializedName("Tokens")
    @Expose
    var tokens: Tokens? = null

    @SerializedName("IsModerator")
    @Expose
    var isModerator: Boolean? = null

    @SerializedName("isUserOld")
    @Expose
    var isOldUser: Boolean? = null

    @SerializedName("Password")
    @Expose
    var password: String? = null

    @SerializedName("StudyNumber")
    @Expose
    var studyNumber:List<StudyNumber>? = null

    @SerializedName("Sites")
    @Expose
    val siteModel: List<SiteModel>? = null

}