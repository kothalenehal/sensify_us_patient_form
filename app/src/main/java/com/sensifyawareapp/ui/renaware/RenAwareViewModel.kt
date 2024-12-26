package com.sensifyawareapp.ui.renaware

import android.app.Application
import android.util.Log
import com.sensifyawareapp.ui.BaseViewModel
import com.sensifyawareapp.utils.common.AppConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RenAwareViewModel @Inject() constructor(application: Application) :
    BaseViewModel(application) {

    fun uploadData(renAwareColorInfo: JSONObject, cartridgeId: String?) {
        /*val dateFormat =
            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())*/
//        UUID.randomUUID().toString(
        val renAware = JSONObject()
        renAware.put("SessionId", "${UUID.randomUUID()}")

        renAware.put(
            "UserId", prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
        )
        Log.e("TAG", "uploadData: $cartridgeId")
        Log.e("TAG", "uploadData: $renAwareColorInfo")

        renAware.put("CartridgeId", cartridgeId)
        renAware.put("RenAwareColorInfo", renAwareColorInfo)

        val query = JSONObject()
        query.put("RenAware", renAware)
        query.put("Url", "/add-reno-aware")

        val body: RequestBody =
            query.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveRenAware(body).subscribe({
            it.isSuccess
        }, {})

    }
}

