package com.sensifyawareapp.ui.traceaware

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityTraceAwareTutorialBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class TraceAwareTutorialActivity : BaseActivity() {
    private lateinit var traceAdapter: TraceAdapter
    private lateinit var binding: ActivityTraceAwareTutorialBinding

    private var selectedTraces = 10

    private val jsonArrayImage = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trace_aware_tutorial)

        binding.selectedTraces = selectedTraces
        binding.isLast = false

        binding.toolbar.ivBack.setOnClickListener { finish() }

        generateRandomImages()
        val currentQuestion = Random.nextInt(selectedTraces)

        binding.number = currentQuestion.toString()
        binding.currentQuestion = currentQuestion + 1

        traceAdapter.updateBackgroundBorder(currentQuestion)

        binding.btnNext.setOnClickListener {
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.START_TIME,
                Calendar.getInstance().timeInMillis
            )
            finish()
            if (!isClinicalTestVersion)
//                startActivity(Intent(this, SelectTraceAwareLevelActivity::class.java))
            startActivity(Intent(this, TraceAwareActivity::class.java).putExtra(AppConstant.BundleExtra.TRACES, 6))
            else
                startActivity(
                    Intent(
                        this,
                        TraceAwareActivity::class.java
                    ).putExtra(AppConstant.BundleExtra.TRACES, 6)
                )

        }
    }

    private fun generateRandomImages() {

        val arrayListImg = ArrayList<Int>()
        val random = Random(System.nanoTime())

        while (arrayListImg.size < selectedTraces) {
            val nextInt = (0..30).random(random)
            if (!arrayListImg.contains(nextInt) && nextInt != 10) {//skip 10, bcz 10th position image is at 0th position
                arrayListImg.add(nextInt)
                jsonArrayImage.put(nextInt)
            }
        }
        setImages()
    }

    //set images in grid layout which have been generated when test start
    private fun setImages() {

        val iconImages = ArrayList<Drawable>()
        for (i in 0 until jsonArrayImage.length()) {
            iconImages.add(getResource("trace_aware_${jsonArrayImage[i]}")!!)
        }

        binding.rvTraces.layoutManager = GridLayoutManager(this, (iconImages.size / 2))
        traceAdapter = TraceAdapter(iconImages, true)
        binding.rvTraces.adapter = traceAdapter
    }

    private fun getResource(name: String): Drawable? {
        val resID = this.resources.getIdentifier(name, "drawable", this.packageName)
        return ActivityCompat.getDrawable(this, resID)
    }
}