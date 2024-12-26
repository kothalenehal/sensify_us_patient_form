package com.sensifyawareapp.api

import PatientRequest
import PatientResponse
import com.sensifyawareapp.model.base.BaseModel
import com.sensifyawareapp.model.base.DataListModel
import com.sensifyawareapp.ui.auth.model.UserModel
import com.sensifyawareapp.ui.renaware.RenAwareViewModel
import com.sensifyawareapp.ui.renaware.model.RenAwareModel
import com.sensifyawareapp.ui.scentaware.model.ScentModel
import com.sensifyawareapp.ui.traceaware.model.PresignedFileModel
import com.sensifyawareapp.ui.traceaware.model.PresignedUrlModel
import com.sensifyawareapp.utils.common.ApiConstants
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


/**
 * list of api goes here
 */
interface ApiInterface {
    @POST(ApiConstants.PROD)
    fun saveTubeInformation(
        @Body body: RequestBody,
    ): Observable<ScentModel>

    @POST(ApiConstants.PROD)
    fun saveTrainingTubeInformation(
        @Body body: RequestBody,
    ): Observable<ScentModel>

    @POST(ApiConstants.PROD)
    fun uploadPresignedImage(
        @Body body: RequestBody
    ): Observable<PresignedUrlModel>

    @POST(ApiConstants.PROD)
    fun getPresignedImage(
        @Body body: RequestBody
    ): Observable<PresignedUrlModel>

    @POST(ApiConstants.PROD)
    fun saveTraceAwareData(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun dsst(@Body body: RequestBody): Observable<PresignedFileModel>

    @POST(ApiConstants.PROD)
    fun signup(@Body body: RequestBody): Observable<UserModel>

    @POST(ApiConstants.PROD)
    fun verifyEmail(@Body body: RequestBody): Observable<UserModel>

    @POST(ApiConstants.PROD)
    fun signin(@Body body: RequestBody): Observable<UserModel>

    @POST(ApiConstants.PROD)
    fun isOldUser(@Body body: RequestBody): Observable<UserModel>

    @POST(ApiConstants.PROD)
    fun refreshToken(@Body body: RequestBody): Observable<UserModel>

    @POST(ApiConstants.PROD)
    fun saveAudioAware(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun saveGlanceAware(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun saveWordsAware(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun saveGrammarAware(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun calculateAccuracy(@Body body: RequestBody): Observable<DataListModel>

    @POST(ApiConstants.PROD)
    fun changePassword(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun updateProfile(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun forgotPassword(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun resetPassword(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun deleteAccount(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun saveRenAware(@Body body: RequestBody): Observable<RenAwareModel>

    @POST(ApiConstants.PROD)
    fun verifyPatient(@Body body: RequestBody): Observable<ScentModel>

    @POST(ApiConstants.PROD)
    fun getPatient(@Body body: RequestBody): Observable<ScentModel>

    @POST(ApiConstants.PROD)
    fun getAlternateData(@Body body: RequestBody): Observable<ScentModel>

    @POST(ApiConstants.PROD)
    fun getLocation(@Body body: RequestBody): Observable<ScentModel>

    @POST(ApiConstants.PROD)
    fun getStudyNumber(@Body body: RequestBody): Observable<ScentModel>

    @POST(ApiConstants.PROD)
    fun createParticipant(@Body body: RequestBody): Observable<BaseModel>

    @POST(ApiConstants.PROD)
    fun createPatient(@Body patientRequest: PatientRequest): Call<PatientResponse>


    @GET
    fun getDSSt()
}
