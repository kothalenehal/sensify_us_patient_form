package com.sensifyawareapp.api

import PatientRequest
import PatientResponse
import android.util.Log
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.model.base.BaseModel
import com.sensifyawareapp.model.base.CustomError
import com.sensifyawareapp.model.base.DataListModel
import com.sensifyawareapp.ui.auth.model.UserModel
import com.sensifyawareapp.ui.renaware.RenAwareViewModel
import com.sensifyawareapp.ui.renaware.model.RenAwareModel
import com.sensifyawareapp.ui.scentaware.model.ScentModel
import com.sensifyawareapp.ui.traceaware.model.PresignedFileModel
import com.sensifyawareapp.ui.traceaware.model.PresignedUrlModel
import com.sensifyawareapp.utils.NetworkUtils
import com.sensifyawareapp.utils.common.AppConstant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.functions.Supplier
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import java.net.ConnectException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiManager @Inject constructor(private val apiInterface: ApiInterface, private val patientApiService: ApiInterface ) {
    private val mNetworkUtils: NetworkUtils =
        SensifyAwareApplication.getAppComponent().provideNetworkUtils()

    private fun <T : Any> call(modelObservable: Observable<T>): @NonNull Observable<T> {
        return modelObservable
            .startWith(Observable.defer(Supplier defer@{
                //before calling each api, network connection is checked.
                if (!mNetworkUtils.isConnected) {
                    //if network is not available, it will return error observable with ConnectException.
                    return@defer Observable.error(ConnectException("Device is not connected to network"))
                } else {
                    //if it is available, it will return empty observable. Empty observable just emits onCompleted() immediately
                    return@defer Observable.empty()
                }
            }))
            .flatMap<T>(Function flatMap@{ response: T ->
                if (response is BaseModel) {
                    val baseResponse: BaseModel = response as BaseModel
                    if (!baseResponse.isSuccess) {
                        baseResponse.message?.let {
                            if (it.contains("Confirmation Code sent to")) {
                                return@flatMap Observable.just(response)
                            }
                        }
                        Log.e(
                            "TAG",
                            "call: Data ${
                                baseResponse.message?.let {
                                    CustomError(
                                        baseResponse.statusCode,
                                        it
                                    )
                                }
                            }",
                        )
                        val customApiError =
                            baseResponse.message?.let { CustomError(baseResponse.statusCode, it) }
                        return@flatMap Observable.error(customApiError!!)
                    }
                    return@flatMap Observable.just(response)
                }
                Observable.just(response)
            })
            .doOnNext { response: T ->
                //logging response on success
                //you can change to to something else
                //for example, if all your apis returns error codes in success, then you can throw custom exception here
//                if (AppConstant.IS_DEBUGGABLE) {
//                    Log.e("---", "Response :\n$response")
//                }
            }
            .doOnError { throwable: Throwable ->
                //printing stack trace on error
                if (AppConstant.IS_DEBUGGABLE) {
                    throwable.printStackTrace()
                }
            }
    }

    private fun <T : Any> applySchedulers(): ObservableTransformer<T, T> {
        return ObservableTransformer<T, T> { observable: Observable<T> ->
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    fun saveTubeInformation(scentAwareTestResult: RequestBody): Observable<ScentModel> {
        return call(apiInterface.saveTubeInformation(scentAwareTestResult)).compose(applySchedulers())
    }

    fun saveTrainingTubeInformation(scentAwareTrainingResult: RequestBody): Observable<ScentModel> {
        return call(apiInterface.saveTrainingTubeInformation(scentAwareTrainingResult)).compose(
            applySchedulers()
        )
    }

    fun uploadPresignedImage(
        body: RequestBody,
    ): Observable<PresignedUrlModel> {
        return call(apiInterface.uploadPresignedImage(body)).compose(applySchedulers())
    }

    fun getPresignedImage(
        body: RequestBody,
    ): Observable<PresignedUrlModel> {
        return call(apiInterface.getPresignedImage(body)).compose(applySchedulers())
    }

    fun saveTraceAwareData(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.saveTraceAwareData(body)).compose(applySchedulers())
    }



    fun dsst(body: RequestBody): Observable<PresignedFileModel> {
        return call(apiInterface.dsst(body)).compose(applySchedulers())
    }

    fun signup(body: RequestBody): Observable<UserModel> {
        return call(apiInterface.signup(body)).compose(applySchedulers())
    }

    fun verifyEmail(body: RequestBody): Observable<UserModel> {
        return call(apiInterface.verifyEmail(body)).compose(applySchedulers())
    }

    fun signin(body: RequestBody): Observable<UserModel> {
        return call(apiInterface.signin(body)).compose(applySchedulers())
    }

    fun isOldUser(body: RequestBody): Observable<UserModel> {
        return call(apiInterface.isOldUser(body)).compose(applySchedulers())
    }

    fun refreshToken(body: RequestBody): Observable<UserModel> {
        return call(apiInterface.refreshToken(body)).compose(applySchedulers())
    }

    fun saveAudioAware(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.saveAudioAware(body)).compose(applySchedulers())
    }

    fun saveGlanceAware(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.saveGlanceAware(body)).compose(applySchedulers())
    }

    fun saveWordsAware(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.saveWordsAware(body)).compose(applySchedulers())
    }

    fun saveGrammarAware(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.saveGrammarAware(body)).compose(applySchedulers())
    }

    fun calculateAccuracy(body: RequestBody): Observable<DataListModel> {
        return call(apiInterface.calculateAccuracy(body)).compose(applySchedulers())
    }

    fun changePassword(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.changePassword(body)).compose(applySchedulers())
    }

    fun updateProfile(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.updateProfile(body)).compose(applySchedulers())
    }

    fun forgotPassword(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.forgotPassword(body)).compose(applySchedulers())
    }

    fun resetPassword(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.resetPassword(body)).compose(applySchedulers())
    }

    fun deleteAccount(body: RequestBody): Observable<BaseModel> {
        return call(apiInterface.deleteAccount(body)).compose(applySchedulers())
    }

    fun saveRenAware(body: RequestBody): Observable<RenAwareModel> {
        return call(apiInterface.saveRenAware(body)).compose(applySchedulers())
    }

    fun verifyPatient(body: RequestBody): Observable<ScentModel> {
        return call(apiInterface.verifyPatient(body)).compose(applySchedulers())
    }

    fun getPatient(body: RequestBody): Observable<ScentModel> {
        return call(apiInterface.getPatient(body)).compose(applySchedulers())
    }

    fun getAlternateData(body: RequestBody): Observable<ScentModel> {
        return call(apiInterface.getAlternateData(body)).compose(applySchedulers())
    }

    fun getLocation(body: RequestBody): Observable<ScentModel> {
        return call(apiInterface.getLocation(body)).compose(applySchedulers())
    }

    fun getStudyNumber(body: RequestBody): Observable<ScentModel> {
        return call(apiInterface.getStudyNumber(body)).compose(applySchedulers())
    }

    fun createPatient(patientRequest: PatientRequest): Call<PatientResponse> {
        return patientApiService.createPatient(patientRequest)
    }
}