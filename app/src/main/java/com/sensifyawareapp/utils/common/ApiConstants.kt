package com.sensifyawareapp.utils.common

object ApiConstants {
//    const val BASE_URL_OLD = "https://5qrg7as0r4.execute-api.us-east-1.amazonaws.com/"
//    const val PROD_OLD = "isticks/"
 //   var BASE_URL = "https://3vvm8kk6z7.execute-api.us-east-1.amazonaws.com/"
  var BASE_URL = "https://qiggre9qr1.execute-api.us-east-1.amazonaws.com/"
    const val PROD = "dev"
    const val BASE_URL_DSST =
        "https://7nb0rznkuk.execute-api.us-east-1.amazonaws.com/default/dsst-test-docker"

    /**
     * api endpoint listed hereF
     */
    interface EndPoints {

        companion object {
            const val SET_TUBE_INFORMATION = "set-tube-information"
            const val SET_TUBE_RESULT = ""
            const val UPLOAD_PRESIGNED_URL = "upload-presigned-url"
            const val GET_PRESIGNED_URL = "get-presigned-url"
            const val SAVE_DSST = "savedsst"
            const val DSST = "dsst-test-docker" /*"dsst"*/
            const val SIGNUP = "signup"
            const val CONFIRM_USER = "confirm-user"
            const val SIGNIN = "signin"
            const val REFRESH_TOKEN = "refreshtoken"
            const val SET_AUDIO_AWARE = "set-audio-aware"
            const val SET_GLANCE_AWARE = "set-face-aware"
            const val CHANGE_PASSWORD = "changepassword"
            const val UPDATE_USER_PROFILE_APP = "updateuserprofileapp"
            const val FORGET_PASSWORD = "forget-password"
            const val RESET_PASSWORD = "change-password"
            const val DELETE_USER = "delete-user"
            const val Add_RENO_AWARE = "add-reno-aware"
            const val VERIFY_PATIENT = "v1/verify-patient"
            const val ALTERNATE_DATA = "get-tubeid-alternate-data"
        }
    }

    /**
     * api parameter listed here
     */
    interface params {
        companion object {
            const val AUTHORIZATION = "Authorization"
            const val SCENT_AWARE_TEST_RESULT = "ScentAwareTestResult"
            const val URL = "url"

            const val FULL_NAME = "name"
            const val NICK_NAME = "nick_name"
            const val PHONE = "phone"
            const val HIDE_NAME = "is_hide_name"

            const val NEW_PASSWORD = "new_password"
            const val NEW_C_PASSWORD = "new_confirm_password"
            const val OLD_PASSWORD = "old_password"
            const val TOKEN = "token"
            const val TYPE = "type"
        }
    }

    interface ResponseCode {
        companion object {
            const val RESPONSE_SUCCESS = 1
            const val RESPONSE_FAIL = 0
            const val NOT_FOUND = 404
            const val CONFLICT = 400
            const val HTTP_SUCCESS = 200
            const val HTTP_INTERNAL_SERVER_ERROR = 500
            const val ACCESS_TOKEN_EXPIRE = 104
            const val HTTP_REQUEST_TIMEOUT = 408
        }
    }
}