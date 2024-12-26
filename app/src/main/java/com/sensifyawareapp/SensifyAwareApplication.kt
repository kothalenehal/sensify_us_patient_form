package com.sensifyawareapp

import android.app.Application
import androidx.multidex.MultiDex
import com.sensifyawareapp.di.component.AppComponent
import com.sensifyawareapp.di.component.DaggerAppComponent
import com.sensifyawareapp.di.module.AppModule
import com.sensifyawareapp.di.module.NetworkModule
import com.sensifyawareapp.di.module.TwitterModule
import com.sensifyawareapp.room.AlternateData
import com.sensifyawareapp.room.Reminder
import com.sensifyawareapp.ui.glanceaware.fragments.model.GlanceModel
import com.sensifyawareapp.ui.scentaware.Odor
import com.sensifyawareapp.ui.scentaware.TestData
import com.sensifyawareapp.ui.scentaware.model.TubeTestsRecord
import org.json.JSONArray
import org.json.JSONObject

class SensifyAwareApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        component = DaggerAppComponent.builder()
            .twitterModule(TwitterModule())
            .appModule(AppModule(this))
            .networkModule(NetworkModule())
            .build()
        /*if (AppConstant.IS_DEBUGGABLE) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }*/
    }


    companion object {
        private lateinit var component: AppComponent

        var glanceModelList: ArrayList<GlanceModel>? = null
        val tubeTestsList: ArrayList<TubeTestsRecord> = ArrayList()
        val alternateDataList: ArrayList<Pair<String?, String?>> = ArrayList()
        var jsonArray = JSONArray()
        var jsonArrayScent = JSONArray()
        var jsonArrayScentTraining = JSONArray()
        var jsonArrayGrammar = JSONArray()
        var jsonArrayGlance = JSONArray()
        lateinit var scentAwareTestData: TestData //add all the details of scent aware evaluate test
        var dsstScore = 0.0f
        var recallDsstScore = 0.0f

        fun getAppComponent(): AppComponent {
            return component
        }

        fun addTraceAwareObjectInJSON(jsonObject: JSONObject) {
            jsonArray.put(jsonObject)
        }

        fun clearTraceAwareObjectFromJSON() {
            jsonArray = JSONArray()
        }



        fun addGlanceAwareObjectInJSON(jsonObject: JSONObject) {
            jsonArrayGlance.put(jsonObject)
        }

        fun clearGlanceAwareObjectFromJSON() {
            jsonArrayGlance = JSONArray()
        }

        fun addScentAwareObjectInJSON(jsonObject: JSONObject) {
            jsonArrayScent.put(jsonObject)
        }


        fun clearScentAwareObjectFromJSON() {
            jsonArrayScent = JSONArray()
        }

        fun addScentAwareTrainingObjectInJSON(jsonObject: JSONObject) {
            jsonArrayScentTraining.put(jsonObject)
        }


        fun clearScentAwareTrainingObjectFromJSON() {
            jsonArrayScentTraining = JSONArray()
        }

        fun addGrammarAwareObjectInJSON(jsonObject: JSONObject) {
            jsonArrayGrammar.put(jsonObject)
        }

        fun clearGrammarAwareObjectFromJSON() {
            jsonArrayGrammar = JSONArray()
        }

        fun initTestData() {//init variable from here and start saving data
            scentAwareTestData = TestData()
            //insert default values for all 16 tubes, if 8 is selected rest of object will have 0
            for (i in 0..15) {
                scentAwareTestData.odors.add(Odor())
            }
        }

        fun addGlanceData(glanceModel: GlanceModel) {
            if (glanceModelList == null) glanceModelList = ArrayList()
            glanceModelList?.add(glanceModel)
        }
    }
}