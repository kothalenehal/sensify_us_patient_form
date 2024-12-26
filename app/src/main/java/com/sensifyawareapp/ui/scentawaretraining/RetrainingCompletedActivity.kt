package com.sensifyawareapp.ui.scentawaretraining

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityRetrainingCompletedBinding
import com.sensifyawareapp.databinding.DialogCancelTestBinding
import com.sensifyawareapp.databinding.LayoutReminderDialogBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.ui.calendar.ReminderActivity
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RetrainingCompletedActivity : BaseActivity() {
    private lateinit var binding: ActivityRetrainingCompletedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_retraining_completed)

        binding.btnRestart.setOnClickListener {
            val selectedKitSize =
                prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_KIT_SIZE)

            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.SELECTED_MENU,
                2
            )
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                if (selectedKitSize == 8) 8 else 16
            )

            SensifyAwareApplication.initTestData()
            startActivity(
                Intent(
                    this,
                    TutorialActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }

        binding.btnFinishTraining.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        uploadData()
    }

    fun uploadData() {
        val selectedKitSize = intent.getIntExtra("selectedKitSize", 0)
        Log.e("TAG", "uploadData: $selectedKitSize")
        val dateFormat =
            SimpleDateFormat(AppConstant.SharedPreferences.DateFormat, Locale.getDefault())
        val scentAwareTrainingResult = JSONObject()

        scentAwareTrainingResult.put("TestType", if (selectedKitSize == 8) "eight" else "sixteen")
        scentAwareTrainingResult.put("SessionId", UUID.randomUUID().toString())
        scentAwareTrainingResult.put("TubeInfo", SensifyAwareApplication.jsonArrayScentTraining)
        scentAwareTrainingResult.put("Date", dateFormat.format(Date()))
        scentAwareTrainingResult.put(
            "UserId",
            prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
        )

//        Log.e("TAG", " scentAwareTrainingResult :  $scentAwareTrainingResult")

        val query = JSONObject()
        query.put("Url", "/v1/set-tube-result")
        query.put("TubeTestResult", scentAwareTrainingResult)

        Log.e("TAG", "  query :  $query")

        val body: RequestBody = query.toString()
            .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveTrainingTubeInformation(body).subscribe({
            Log.e("TAG", "information: $it")

            //            val adapter = ScentAnswerAdapter(it.data.tubeTest)
            //            binding.recyclerView.adapter = adapter
        }, {})
    }

    fun showReminderDialog() {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        val binding = LayoutReminderDialogBinding.inflate(
            LayoutInflater.from(this),
            ConstraintLayout(this),
            false
        )
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.btnNo.setOnClickListener {
            dialog.dismiss()
        }
        binding.cardYes.setOnClickListener {
            dialog.dismiss()
            setData()

            finish()
            startActivity(
                Intent(
                    this,
                    ReminderActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        dialog.show()
    }

    private fun setData() {
        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.SELECTED_TEXT,
            null
        )

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.START_DATE,
            null
        )
        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.END_DATE,
            null
        )
        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.REMINDER_TIME,
            null
        )

        prefUtils.saveData(
            this,
            AppConstant.SharedPreferences.REPEAT,
            null
        )

    }
}