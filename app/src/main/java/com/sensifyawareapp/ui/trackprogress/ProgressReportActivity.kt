package com.sensifyawareapp.ui.trackprogress

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.multidex.BuildConfig
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.google.android.datatransport.cct.internal.LogEvent
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sensifyawareapp.R
import com.sensifyawareapp.SensifyAwareApplication
import com.sensifyawareapp.databinding.ActivityReportProgressBinding
import com.sensifyawareapp.databinding.LayoutBottomSheetBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.TutorialActivity
import com.sensifyawareapp.ui.audioaware.AudioAwareActivity
import com.sensifyawareapp.ui.glanceaware.GlanceAwareTutorialActivity
import com.sensifyawareapp.ui.scentaware.HealthsQuestionsActivity
import com.sensifyawareapp.ui.scentaware.PatientActivity
import com.sensifyawareapp.ui.traceaware.TraceAwareTutorialActivity
import com.sensifyawareapp.utils.common.AppConstant
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ProgressReportActivity : BaseActivity() {
    private lateinit var binding: ActivityReportProgressBinding
    private lateinit var viewModel: TrackProgressViewModel
    private val dataList = ArrayList<ProgressModel>()
    private lateinit var adapter: ProgressDataAdapter
    private var testType: String? = null
    private var level: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_progress)
        viewModel = ViewModelProviders.of(this)[TrackProgressViewModel::class.java]

        testType = intent.getStringExtra("testType")
        level = intent.getStringExtra("level")
        binding.loading = false
        binding.chart.setNoDataText(getString(R.string.no_data_found))
        binding.chart.animateY(800)
        val p = binding.chart.getPaint(Chart.PAINT_INFO)
        p.textSize = 50f
        p.color = ContextCompat.getColor(this, R.color.orange)
        p.typeface = ResourcesCompat.getFont(this, R.font.ws_bold)

        binding.noDataFound = false
        binding.model = viewModel

        binding.chart.setOnTouchListener { v, event ->
            if (v.id == R.id.chart) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }

        binding.ivBack.setOnClickListener {
            super.onBackPressed()
        }

        initClicks()

        adapter = ProgressDataAdapter(dataList)
        binding.rvTests.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, true)
        binding.rvTests.adapter = adapter

        when (testType) {
            "scent8" -> {
                scent8()
            }
            "scent16" -> {
                scent16()
            }
            "traceAware" -> {
                traceAware()
            }
            "audioAware" -> {
                audioAware()
            }
            "glanceAware" -> {
                glanceAware()
            }
            "wordsAware" -> {
                wordsAware()
            }
            "grammarAware" -> {
                grammarAware()
            }
        }

        binding.btnStart?.setOnClickListener {
            Log.e("TAG", "onCreate: $testType", )
            when (testType) {
                "scent8" -> {
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        1
                    )
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                        8
                    )
                    if (prefUtils.getBooleanData(
                           this,
                            AppConstant.SharedPreferences.IS_MODERATOR
                        )
                    ) {
                        startActivity(
                            Intent(
                                this, PatientActivity::class.java

                            )
                        )
                    } else {

                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.SITE_ID,
                            0
                        )

                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.PATIENT_ID,
                            null
                        )

                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.SITE_NAME,
                            null
                        )
                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.STUDY_NUMBER,
                            null
                        )

                        startActivity(
                            Intent(
                                this,
                                HealthsQuestionsActivity::class.java
                            )
                        )

                    }

                }
                "scent16" -> {

                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        1
                    )
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.SELECTED_KIT_SIZE,
                        16
                    )
                    if (prefUtils.getBooleanData(
                            this,
                            AppConstant.SharedPreferences.IS_MODERATOR
                        )
                    ) {
                        startActivity(
                            Intent(
                                this, PatientActivity::class.java

                            )
                        )
                    } else {

                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.SITE_ID,
                            0
                        )

                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.PATIENT_ID,
                            null
                        )

                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.SITE_NAME,
                            null
                        )
                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.STUDY_NUMBER,
                            null
                        )

                        startActivity(
                            Intent(
                                this,
                                HealthsQuestionsActivity::class.java
                            )
                        )

                    }
                }
                "traceAware" -> {
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        3
                    )
                    startActivity(
                        Intent(
                            this,
                            TraceAwareTutorialActivity::class.java
                        )
                    )
                }
                "audioAware" -> {
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        4
                    )
                    startActivity( Intent(
                        this,
                        AudioAwareActivity::class.java
                    ).putExtra(
                        "level",
                        1
                    ))

                }
                "glanceAware" -> {
                    prefUtils.saveData(
                        this,
                        AppConstant.SharedPreferences.SELECTED_MENU,
                        5
                    )
                    startActivity(
                        Intent(
                            this,
                            GlanceAwareTutorialActivity::class.java
                        )/*.putExtra(
                                "level",
                                1
                            )*/
                    )

                }
                "wordsAware" -> {
                    wordsAware()
                }
                "grammarAware" -> {
                    grammarAware()
                }
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setDataInGraph(values: ArrayList<Entry>, color: Int, drawable: Int) {
        Log.e("TAG", "setDataInGraph:123  ${values.isEmpty()}", )
        if (values.isEmpty()) return
        adapter.notifyDataSetChanged()

        val xAxis: XAxis = binding.chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        val yAxis: YAxis = binding.chart.axisRight
        yAxis.setDrawLabels(false)
        if (testType != "traceAware")
            binding.chart.axisLeft.granularity = 1f

        binding.chart.legend.isEnabled = false
        binding.chart.description = null

        val set1: LineDataSet
        if (binding.chart.data != null &&
            binding.chart.data.dataSetCount > 0
        ) {

            set1 = binding.chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
            binding.chart.invalidate()
        } else {
            Log.e("TAG", "setDataInGraph: Call", )
            set1 = LineDataSet(values, "")
            set1.setDrawIcons(false)
            set1.enableDashedLine(0f, 1f, 0f)
            set1.setDrawCircleHole(true)
            set1.circleHoleColor = Color.WHITE
            set1.circleHoleRadius = 4f
            set1.setCircleColor(ContextCompat.getColor(this, color))
            set1.circleRadius = 5f
            set1.setDrawValues(false)
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { _, _ ->
                    binding.chart.axisLeft.axisMinimum
                }

            if (Utils.getSDKInt() >= 18) {
                set1.fillDrawable =
                    ContextCompat.getDrawable(this, drawable)
            } else {
                set1.fillColor = Color.WHITE
            }
            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(set1)
            val data = LineData(dataSets)
            binding.chart.data = data
            binding.chart.invalidate()
        }
    }

    private fun initClicks() {
        binding.ivShare.setOnClickListener {
            val c = Calendar.getInstance()
            fileName =
                "${testType}_${c[Calendar.DAY_OF_MONTH]}-${c[Calendar.MONTH] + 1}-${c[Calendar.YEAR]}-" +
                        "${c[Calendar.HOUR_OF_DAY]}-${c[Calendar.MINUTE]}-${c[Calendar.SECOND]}"

            downloadClick()//first download the file and then share
            val share = Intent(Intent.ACTION_SEND)
            share.type = "*/*"
            val extBaseDir = Environment.getExternalStorageDirectory()
            val file =
                File(extBaseDir.absolutePath + "/DCIM/SensifyAwareApp/" + fileName + ".jpg")
            if (file.exists()) {
                share.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file
                    )
                );
                startActivity(Intent.createChooser(share, "Share Image"));
            }
        }

        binding.ivDownload.setOnClickListener {
            val c = Calendar.getInstance()
            fileName =
                "${testType}_${c[Calendar.DAY_OF_MONTH]}-${c[Calendar.MONTH] + 1}-${c[Calendar.YEAR]}-" +
                        "${c[Calendar.HOUR_OF_DAY]}-${c[Calendar.MINUTE]}-${c[Calendar.SECOND]}"

            downloadClick()
        }
    }

    private var fileName = ""
    private fun downloadClick() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            downloadImage()
        } else {
            if (permissionUtil.checkMissingPermission(
                    this, 102,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            ) {
                downloadImage()
            }
        }
    }

    private fun downloadImage() {
        val imagesFolder = File("SensifyAwareApp")
        if (!imagesFolder.exists()) {
            imagesFolder.mkdirs()
        }
        val saveToGallery =
            binding.chart.saveToGallery(
                fileName,
                imagesFolder.path,
                "Sensify Aware App $testType chart",
                Bitmap.CompressFormat.JPEG,
                80
            )
        showAlert("File Saved at /DCIM/SensifyAwareApp/$fileName.jpg")
        Log.e("---", "initClicks: $saveToGallery")
    }

    private fun audioAware() {
        binding.title = getString(R.string.audio_aware)
        binding.subtitle = "Level : $level"
        binding.color = ContextCompat.getColor(this, R.color.text_color)
        Log.e("TAG", "audioAware: 11 ", )
        binding.loading = true
        Log.e("TAG", "audioAware: 22 $level ", )
        when (level) {
            getString(R.string.easy) -> {
                Log.e("TAG", "audioAware: 33 Call", )
                viewModel.audioAwareEasyList.observe(this) {
                    dataList.clear()
                    binding.loading = false
                    Log.e("TAG", "audioAware: 44 ${binding.loading} ", )
                    binding.noDataFound = it.isEmpty()
                    val values: ArrayList<Entry> = ArrayList()
                    for (i in 0 until it.size) {
                        dataList.add(
                            ProgressModel(
                                it[i].startDate!!,
                                it[i].endDate!!,
                                it[i].score!!,
                                10
                            )
                        )
                        values.add(
                            Entry(
                                i + 1.toFloat(),
                                it[i].score?.toFloat() ?: 0f,
                                null
                            )
                        )
                    }
                    setDataInGraph(
                        values,
                        R.color.green,
                        R.drawable.graph_audio_aware_easy_bg
                    )
                }
            }
            getString(R.string.intermediate) -> {
                viewModel.audioAwareIntermediateList.observe(this) {
                    dataList.clear()
                    binding.loading = false
                    binding.noDataFound = it.isEmpty()
                    val values: ArrayList<Entry> = ArrayList()
                    for (i in 0 until it.size) {
                        dataList.add(
                            ProgressModel(
                                it[i].startDate!!,
                                it[i].endDate!!,
                                it[i].score!!,
                                10
                            )
                        )
                        values.add(
                            Entry(
                                i + 1.toFloat(),
                                it[i].score?.toFloat() ?: 0f,
                                null
                            )
                        )
                    }
                    setDataInGraph(
                        values,
                        R.color.chart_purple,
                        R.drawable.graph_audio_aware_intermediate_bg,
                    )
                }
            }
            getString(R.string.hard) -> {
                viewModel.audioAwareHardList.observe(this) {
                    dataList.clear()
                    binding.loading = false
                    binding.noDataFound = it.isEmpty()
                    val values: ArrayList<Entry> = ArrayList()
                    for (i in 0 until it.size) {
                        dataList.add(
                            ProgressModel(
                                it[i].startDate!!,
                                it[i].endDate!!,
                                it[i].score!!,
                                10
                            )
                        )
                        values.add(
                            Entry(
                                i + 1.toFloat(),
                                it[i].score?.toFloat() ?: 0f,
                                null
                            )
                        )
                    }
                    setDataInGraph(
                        values,
                        R.color.chart_orange,
                        R.drawable.graph_audio_aware_hard_bg
                    )
                }
            }
        }
        viewModel.getAudioAwareData(level!!)
    }

    private fun traceAware() {
        binding.title = getString(R.string.trace_aware)
        binding.subtitle = "Your score is among the top 50% of subjects."
        binding.color = ContextCompat.getColor(this, R.color.blue)

        binding.loading = true

        viewModel.traceAwareList.observe(this) {
            dataList.clear()
            binding.loading = false
            binding.noDataFound = it.isEmpty()
            if (it.isEmpty()) {
                binding.subtitle = ""
            } else {
                binding.subtitle = "Your score is among the top 50% of subjects."
                val values: ArrayList<Entry> = ArrayList()
                for (i in 0 until it.size) {
                    dataList.add(
                        ProgressModel(
                            it[i].startDate!!,
                            it[i].endDate!!,
                            0,
                            40,
                            true,
                            false,
                            it[i].tracingAccuracy,
                            it[i].recallAccuracy
                        )
                    )
                    values.add(Entry(i + 1.toFloat(), it[i].avgTime ?: 0f, null))
                }
                setDataInGraph(values, R.color.blue, R.drawable.graph_trace_aware_bg)
            }
        }
        viewModel.getTraceAwareData()
    }

    private fun glanceAware() {
        binding.title = getString(R.string.glance_aware)
        binding.subtitle = "Your score is among the top 50% of subjects."
        binding.color = ContextCompat.getColor(this, R.color.blue)

        binding.loading = true
        viewModel.glanceAwareList.observe(this) {
            dataList.clear()
            binding.loading = false
            binding.noDataFound = it.isEmpty()
            if (it.isEmpty()) {
                binding.subtitle = ""
            } else {
                binding.subtitle = "Your score is among the top 50% of subjects."
                val values: ArrayList<Entry> = ArrayList()
                for (i in 0 until it.size) {
                    dataList.add(
                        ProgressModel(
                            it[i].startDate!!,
                            it[i].endDate!!,
                            it[i].correctAnswer!!,
                            it[i].totalQ!!,
                            false,
                            true
                        )
                    )
                    values.add(Entry(i + 1.toFloat(), it[i].correctAnswer?.toFloat() ?: 0f, null))
                }
                setDataInGraph(values, R.color.blue, R.drawable.graph_trace_aware_bg)
            }
        }
        viewModel.getGlanceAwareData()
    }

    private fun wordsAware() {
        binding.title = getString(R.string.words_aware)
        binding.subtitle = "Your score is among the top 50% of subjects."
        binding.color = ContextCompat.getColor(this, R.color.blue)

        binding.loading = true
        viewModel.wordsAwareList.observe(this) {
            dataList.clear()
            binding.loading = false
            binding.noDataFound = it.isEmpty()
            if (it.isEmpty()) {
                binding.subtitle = ""
            } else {
                binding.subtitle = "Your score is among the top 50% of subjects."
                val values: ArrayList<Entry> = ArrayList()
                for (i in 0 until it.size) {
                    Log.e(
                        "Data Call ",
                        it[i].totalWords!!.toString() + " // " + it[i].referenceWords!!
                    )
                    dataList.add(
                        ProgressModel(
                            it[i].startDate!!,
                            it[i].endDate!!,
                            it[i].referenceWords!!,
                            it[i].totalWords!!,
                            false,
                        )
                    )
                    values.add(Entry(i + 1.toFloat(), it[i].referenceWords?.toFloat() ?: 0f, null))
                }
                setDataInGraph(values, R.color.blue, R.drawable.graph_trace_aware_bg)
            }
        }
        viewModel.getWordsAwareData()
    }

    private fun grammarAware() {
        binding.title = getString(R.string.grammar_aware)
        binding.subtitle = "Your score is among the top 50% of subjects."
        binding.color = ContextCompat.getColor(this, R.color.blue)

        binding.loading = true
        viewModel.grammarAwareList.observe(this) {
            dataList.clear()
            binding.loading = false
            binding.noDataFound = it.isEmpty()
            if (it.isEmpty()) {
                binding.subtitle = ""
            } else {
                binding.subtitle = "Your score is among the top 50% of subjects."
                val values: ArrayList<Entry> = ArrayList()
                for (i in 0 until it.size) {
                    Log.e("Data Call ", it[i].accuracy!!.toString() + " // " + it[i].startDate)
                    dataList.add(
                        ProgressModel(
                            it[i].startDate!!,
                            it[i].endDate!!,
                            it[i].accuracy!!, 5,
                            false,
                            false,
                            0,
                            0,
                            it[i].avgTime!!

                        )
                    )
                    values.add(Entry(i + 1.toFloat(), it[i].accuracy?.toFloat() ?: 0f, null))
                }
                setDataInGraph(values, R.color.blue, R.drawable.graph_trace_aware_bg)
            }
        }
        viewModel.getGrammarAwareData()
    }

    private fun scent16() {
        binding.title = getString(R.string.scentaware_16_scent_kit)
        binding.subtitle = getString(R.string.low_scores_consult_your_doctor)
        binding.color = ContextCompat.getColor(this, R.color.blue)

        binding.loading = true
        viewModel.scentAware16List.observe(this) {
            dataList.clear()
            binding.loading = false
            binding.noDataFound = it.isEmpty()
            if (it.isEmpty()) {
                binding.subtitle = "Hmm...smells a little lonely here."
                binding.chart.clear()
            } else {
                binding.subtitle = getString(R.string.low_scores_consult_your_doctor)
                val values: ArrayList<Entry> = ArrayList()
                for (i in 0 until it.size) {
                    dataList.add(
                        ProgressModel(
                            it[i].startDate!!,
                            it[i].endDate!!,
                            it[i].score!!,
                            it[i].totalQuestion!!
                        )
                    )
                    values.add(
                        Entry(
                            i + 1.toFloat(),
                            it[i].score?.toFloat() ?: 0f,
                            null
                        )
                    )
                }
                Log.e("TAG", "scent16: ${values.size}")
                setDataInGraph(values, R.color.blue, R.drawable.graph_scent_16_bg)
            }
        }
        Log.e("TAG", "scent16: Call")
        viewModel.getScentAwareData(16)
    }

    private fun scent8() {
        binding.title = getString(R.string.scentaware_8_scent_kit)
        binding.subtitle = getString(R.string.your_scores_are_improving)
        binding.color = ContextCompat.getColor(this, R.color.orange)

        binding.loading = true
        viewModel.scentAware8List.observe(this) {
            dataList.clear()
            binding.loading = false
            binding.noDataFound = it.isEmpty()
            if (it.isEmpty()) {
                binding.subtitle = "Hmm...smells a little lonely here."
                binding.chart.clear()
            } else {
                binding.subtitle = getString(R.string.your_scores_are_improving)
                val values: ArrayList<Entry> = ArrayList()
                for (i in 0 until it.size) {
                    dataList.add(
                        ProgressModel(
                            it[i].startDate!!,
                            it[i].endDate!!,
                            it[i].score!!,
                            it[i].totalQuestion!!
                        )
                    )
                    values.add(
                        Entry(
                            i + 1.toFloat(),
                            it[i].score?.toFloat() ?: 0f,
                            null
                        )
                    )
                }
                Log.e("TAG", "scent8: ${values.size}")
                setDataInGraph(values, R.color.orange, R.drawable.graph_scent_8_bg)
            }
            Log.e("TAG", "scent8: Call")
        }
        viewModel.getScentAwareData(8)
    }
}