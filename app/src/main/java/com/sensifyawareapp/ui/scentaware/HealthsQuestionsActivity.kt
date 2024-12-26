package com.sensifyawareapp.ui.scentaware

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProviders
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.FragmentProfileSetup2Binding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.trackprogress.TrackProgressViewModel
import com.sensifyawareapp.utils.common.AppConstant

class HealthsQuestionsActivity : BaseActivity() {

    private lateinit var binding: FragmentProfileSetup2Binding
    private lateinit var trackViewModel: TrackProgressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentProfileSetup2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        SensifyAwareApplication.initTestData()
        AppConstant.scanToScanDate = null

        binding.ivBack.setOnClickListener {
            finish()
        }
        trackViewModel = ViewModelProviders.of(this)[TrackProgressViewModel::class.java]

        binding.btnYes.setOnClickListener {
            SensifyAwareApplication.scentAwareTestData.IsStuffyNose = true
//            Log.e("TAG", "Dta 1: ${SensifyAwareApplication.scentAwareTestData.IsStuffyNose}", )
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.IsStuffyNose,
                true
            )

            startActivity(Intent(this, HealthsQuestions2Activity::class.java))

        }
        binding.btnNo.setOnClickListener {
            SensifyAwareApplication.scentAwareTestData.IsStuffyNose = false
//            Log.e("TAG", "Dta 1: ${SensifyAwareApplication.scentAwareTestData.IsStuffyNose}", )
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.IsStuffyNose,
                false
            )
            startActivity(Intent(this, HealthsQuestions2Activity::class.java))
        }
        getData()
    }

    private fun getData() {

//        Log.e("TAG", "alternateDataList Size question : ${SensifyAwareApplication.alternateDataList.size} ", )

        for (dataa in SensifyAwareApplication.alternateDataList){
            Log.e("TAG", "getData: ${dataa.first} // ${dataa.second}", )
        }
       /* trackViewModel.alternateDataList.observe(this){
            SensifyAwareApplication.alternateDataList.clear()
            for (tubes in it) {
                val dataMap = Pair(
                    tubes.tubeValue,
                    tubes.tubeAlternateValue
                )
                SensifyAwareApplication.alternateDataList.add(dataMap)
            }
            Log.e("TAG", "alternateDataList Size : ${SensifyAwareApplication.alternateDataList.size} ", )
        }

        trackViewModel.getAlternateData()*/

       /* lifecycleScope.launch {
            try {
                SensifyAwareApplication.alternateDataList.clear()
                val dbHelper: AppDatabase = AppDatabase.getInstance(application)
                dbHelper.awareDao().getAlternateData().collect {
                    for (tubes in it) {
                        val dataMap = Pair(
                            tubes.tubeValue,
                            tubes.tubeAlternateValue
                        )
                        SensifyAwareApplication.alternateDataList.add(dataMap)
                    }
                    Log.e("TAG", "alternateDataList Size : ${SensifyAwareApplication.alternateDataList.size} ", )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }*/
    }

}