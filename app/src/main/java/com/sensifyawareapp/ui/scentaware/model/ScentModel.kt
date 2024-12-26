package com.sensifyawareapp.ui.scentaware.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.sensifyawareapp.ui.auth.model.StudyNumber

data class ScentModel(
    @SerializedName("IsSuccess")
    @Expose
    val isSuccess: Boolean,

    @SerializedName("Message")
    @Expose
    val message: String,

    @SerializedName("StatusCode")
    @Expose
    val statusCode: Long,

    @SerializedName("Data")
    @Expose
    val data: Data,
)

data class Data(
    @SerializedName("IsTrial")
    @Expose
    val isTrial: Boolean,

    @SerializedName("Accuracy")
    @Expose
    val accuracy: Long,

    @SerializedName("IsModerator")
    @Expose
    val isModerator: Boolean,

    @SerializedName("isUserOld")
    @Expose
    val isUserOld: Boolean,

    @SerializedName("IsPatientExist")
    @Expose
    val isPatientExist: Boolean,

    @SerializedName("LastTrial")
    @Expose
    val lastTrial: String,

    @SerializedName("TubeTest")
    @Expose
    val tubeTest: List<TubeTest>,

    @SerializedName("tubeIdAlternateDatas")
    @Expose
    val tubeIdAlternateDatas: List<TubeIdAlternateDatas>,

    @SerializedName("patientId")
    @Expose
    val patientId: List<String>,

    @SerializedName("Location")
    @Expose
    val locationList: List<LocationList>,

    @SerializedName("StudyNumber")
    @Expose
    var studyNumber: List<StudyNumber>? = null
)

data class LocationList(
    @SerializedName("Id")
    @Expose
    val id: Int,
    @SerializedName("Name")
    @Expose
    val name: String,
)

data class TubeIdAlternateDatas(
    @SerializedName("Id")
    @Expose
    val id: Int,
    @SerializedName("TubeValue")
    @Expose
    val tubeValue: String,
    @SerializedName("TubeAlternateValue")
    @Expose
    val tubeAlternateValue: String,
    @SerializedName("CreatedAt")
    @Expose
    val createdAt: String,
    @SerializedName("UpdatedAt")
    @Expose
    val updatedAt: String,
    @SerializedName("LocationId")
    @Expose
    val locationId: Int,
)

data class TubeTest(
    @SerializedName("AveragePercentage")
    @Expose
    val averagePercentage: Long,

    @SerializedName("TubeId")
    @Expose
    val tubeId: String,

    @SerializedName("TubeTestsRecords")
    @Expose
    val tubeTestsRecords: List<TubeTestsRecord>,
)

data class TubeTestsRecord(
    @SerializedName("SelectedAnswer")
    @Expose
    val selectedAnswer: String,

    @SerializedName("CorrectAnswer")
    @Expose
    val correctAnswer: String,

    @SerializedName("IsAnswerCorrect")
    @Expose
    val isAnswerCorrect: Boolean,

    )
