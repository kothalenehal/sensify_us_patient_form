package com.sensifyawareapp.utils.common

import com.sensifyawareapp.ui.grammaraware.model.Sentences
import org.json.JSONArray
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

object AppConstant {
    const val IS_DEBUGGABLE = true
    var wordsList: ArrayList<String> = ArrayList()
    var Time1 : Int = 0
    var Time2  : Int = 0
    var Time3  : Int = 0
    var arrayS: java.util.ArrayList<Int> = java.util.ArrayList()
    var longestWord : String =""
    var shortestWord : String = ""
    var repeatWord : Int = 0
    var uniqueWord : Int = 0
    var totalWord : Int = 0
    var scanToScanDate : Date? = null
    var Accuracy: ArrayList<Int> = ArrayList()
    var wordsString: java.util.ArrayList<String> = java.util.ArrayList()
//    var level: Int = 0
    interface SharedPreferences {
        companion object {
            const val ACCESS_TOKEN = "accessToken"
            const val ID_TOKEN = "idToken"
            const val REFRESH_TOKEN = "refreshToken"
            const val TOKEN_EXPIRE_ON = "tokenExpireOn"
            const val EMAIL = "emailAddress"
            const val USER_NAME = "userName"
            const val AGE = "age"
            const val GENDER = "gender"
            const val USER_ID = "userId"
            const val IS_VERIFIED = "isVerified"
            const val CLINICAL_TEST_VERSION = "clinicalTestVersion"
            const val ACCURACY = "accuracy"
            const val SITES = "sites"
            const val SITE_NAME = "siteName"
            const val SITE_ID = "siteId"
            const val PATIENT_ID = "patientId"
            const val CLINICAL_TRIAL_ID = "clinicalTrialId"
            const val IS_MODERATOR = "isModerator"
            const val STUDY_NUMBER = "studyNumber"
            const val STUDY_ID = "studyID"
            const val IS_FIRST_TIME = "isFirstTime"
            const val IS_FIRST_TIME2 = "isFirstTime2"

            //smell test
            const val SELECTED_MENU = "SelectedMenu"
            const val SELECTED_TIME = "SelectedTime"
            const val SELECTED_KIT_SIZE = "SelectedKitSize"
            const val PREF_SCANNED_RESULT = "PrefScanResult"
            const val PREF_SCANNED_RESULT_WITH_TIMER = "PrefScanResultWithTimer"
            const val PREF_SELECTED_ANSWERS = "PrefSelectedAnswers"
            const val CORRECT_ANSWER_COUNT = "correctAnswerCount"
            const val WITH_TIMER = "withTimer"
            const val ODOR_DIFF_QUESTION = "odorDiffQuestion"
            const val ODOR_INTENSITY_QUESTION = "odorIntensityQuestion"
            const val START_TIME_SCENT = "startTimeScent"
            const val START_TIME = "startTime"
            const val END_TIME_SCENT= "endTimeScent"
            const val END_TIME = "endTime"
            const val AUDIO_START_TIME = "audioStartTime"
            const val AUDIO_END_TIME = "audioEndTime"
            const val LEVEL = "level"
            const val IsStuffyNose = "IsStuffyNose"
            const val DateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
            const val  DateFormat1 = "MM/dd/YYYY HH:mm"
            const val  DateFormat3 = "dd-MMM-yyyy"
            const val  DateFormat4 = "dd-MMM-yyyy hh:mm a"


            //trace aware
            const val GENERATED_RANDOM_QUESTION_NUMBERS =
                "generatedRandomNumbers"//questions have been asked
            const val GENERATED_RANDOM_IMAGES_INDEX = "generatedRandomImages"
            const val CURRENT_QUESTION = "currentQuestion"
            const val COMPLETED_TRACES = "completedTraces"
            const val SHOW_RECALL_INTRO_MSG = "showRecallIntroMsg"

            // Grammar Aware
            const val Level = "level"
            const val LONGESTWORD = "longestWord"
            const val SHORTESTWORD = "shortestWord"
            const val UNIQUEWORD = "uniqueWord"
            const val REPEATWORD = "repeatWord"

//calender
            const val SELECTED_TEXT = "selectedText"
            const val START_DATE = "startDate"
            const val END_DATE = "endDate"
            const val REMINDER_TIME = "reminderTime"
            const val REPEAT = "repeat"
            const val channelId = "sensify_id"

        }
    }

    interface DateTime {
        companion object {
            const val DD_MM_YYYY = "dd/MM/yyyy"
            const val MMM_YYYY = "MMM yyyy"
        }
    }

    interface BundleExtra {
        companion object {
            const val SCANNED_RESULT = "ScanResult"
            const val RETRAINING = "Retraining"
            const val TRACES = "traces"
        }
    }

    interface Delays {
        companion object {
            const val MIN_TIME_BETWEEN_CLICKS: Long = 200

        }
    }
}