package com.sensifyawareapp.ui.renaware

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.sensifyawareapp.databinding.ActivityRenAwareResultBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.MainActivity
import com.sensifyawareapp.ui.renaware.health.LiverHealthActivity
import com.sensifyawareapp.ui.renaware.health.RenalHealthActivity
import com.sensifyawareapp.utils.common.AppConstant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID

class RenAwareResultActivity : BaseActivity() {
    lateinit var binding: ActivityRenAwareResultBinding

    var data = ArrayList<Int>()
    var rgb = ArrayList<String>()
    var colorCode = ArrayList<String>()
    var name = StringBuilder()

    private val viewModel: RenAwareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        data = intent.getIntegerArrayListExtra("pixel") as ArrayList<Int>


        showLoader()

        binding.btnBackTests.setOnClickListener {
            finish()
            startActivity(
                Intent(
                    this,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        binding.llRenalHealth.constraint.setOnClickListener {
            startActivity(Intent(this, RenalHealthActivity::class.java))
        }
        binding.llLiverHealth.constraint.setOnClickListener {
            startActivity(Intent(this, LiverHealthActivity::class.java))
        }

        binding.llHydration.constraint.setOnClickListener {
            changeScreen("Hydration")
        }

        binding.llEnergyBalance.constraint.setOnClickListener {
            changeScreen("Energy")
        }

        binding.llImmunity.constraint.setOnClickListener {
            changeScreen("Immunity")
        }
        binding.llBoneHealth.constraint.setOnClickListener {
            changeScreen("Bone")
        }

        for (i in data) {
            val r = Color.red(i)
            val g = Color.green(i)
            val b = Color.blue(i)
//            Color.rgb(Color.red(i), Color.green(i), Color.blue(i))
            rgb.add("$r, $g, $b")
            Log.e("TAG", "onCreate: ${Color.rgb(Color.red(i), Color.green(i), Color.blue(i))}")

        }

        for (i in data) {
            colorCode.add(Integer.toHexString(i))
        }
        val renAwareColorInfo = JSONObject()
        renAwareColorInfo.put("Box1", rgb[0])
        renAwareColorInfo.put("Box2", rgb[1])
        renAwareColorInfo.put("Box3", rgb[2])
        renAwareColorInfo.put("Box4", rgb[3])
        renAwareColorInfo.put("Box5", rgb[4])
        renAwareColorInfo.put("Box6", rgb[5])
        renAwareColorInfo.put("Box7", rgb[6])
        renAwareColorInfo.put("Box8", rgb[7])
        renAwareColorInfo.put("Box9", rgb[8])
        renAwareColorInfo.put("Box10", rgb[9])
        renAwareColorInfo.put("Box11", rgb[10])
        renAwareColorInfo.put("Box12", rgb[11])

        uploadData(renAwareColorInfo)

    }

    fun uploadData(renAwareColorInfo: JSONObject) {
        val renAware = JSONObject()
        renAware.put("SessionId", "${UUID.randomUUID()}")

        renAware.put(
            "UserId", prefUtils.getIntData(getApplication(), AppConstant.SharedPreferences.USER_ID)
        )

        Log.e("TAG", "uploadData: ${intent.getStringExtra("cartridgeId")}")

        renAware.put("CartridgeId", intent.getStringExtra("cartridgeId"))
        renAware.put("RenAwareColorInfo", renAwareColorInfo)

        val query = JSONObject()
        query.put("RenAware", renAware)
        query.put("Url", "/add-reno-aware")

        val body: RequestBody =
            query.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        apiManager.saveRenAware(body).subscribe({
            hideLoader()
            if (it.isSuccess == true) {
                binding.overallWellness = it.data?.renawareResponseData?.overallWellness.toString()
                Log.e(
                    "TAG",
                    "overallWellness: ${it.data?.renawareResponseData?.healthData?.get(0)?.healthScore?.toString()}"
                )

                binding.renalScore =
                    it.data?.renawareResponseData?.healthData?.get(0)?.healthScore?.toFloat()
                binding.liverScore = 8.2F
                binding.hydrationScore = 6.1F
                binding.energyScore = 10F
                binding.immunityScore = 7.5F
                binding.boneScore = 10F

                binding.renalScoreValue = "7.8"
                binding.liverScoreValue = "8.2"
                binding.hydrationScoreValue = "6.1"
                binding.energyScoreValue = "10"
                binding.immunityScoreValue = "7.5"
                binding.boneScoreValue = "10"
            }
        }, {
            handleError(it)
        })
    }

    private fun changeScreen(name: String) {
        startActivity(Intent(this, RenAwareHealthActivity::class.java).putExtra("Name", name))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        startActivity(
            Intent(
                this,
                MainActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }
}