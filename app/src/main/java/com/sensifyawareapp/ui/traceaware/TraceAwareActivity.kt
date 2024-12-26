package com.sensifyawareapp.ui.traceaware

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityTraceAwareBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.utils.common.AppConstant
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class TraceAwareActivity : BaseActivity() {
    private var completedTraces: Int = 0
    private var showResultScreen = false
    private lateinit var binding: ActivityTraceAwareBinding
    private lateinit var viewModel: TraceAwareViewModel

    private var drawingTimeStart: Long = 0
    private var drawingTimeEnd: Long = 0
    private lateinit var gridVisibility: String
    private var fingerTouchCount: Int = 0
    private var referenceImageId: Int = -1
    private var selectedTraces = 10

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_trace_aware)
        viewModel = ViewModelProviders.of(this)[TraceAwareViewModel::class.java]
        selectedTraces = intent.getIntExtra(AppConstant.BundleExtra.TRACES, selectedTraces)
        binding.selectedTraces = selectedTraces

        binding.enableButton = false
        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }

        binding.ivDraw.setOnTouchListener { view, event ->
            if (view.id == R.id.iv_draw) {
                if (drawingTimeStart == 0L) {
                    drawingTimeStart = Calendar.getInstance().timeInMillis
                }
                binding.enableButton = true
                view.parent.requestDisallowInterceptTouchEvent(true)
                when (event.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
        var currentQuestion = prefUtils.getIntData(
            this,
            AppConstant.SharedPreferences.CURRENT_QUESTION,
        )
        completedTraces =
            prefUtils.getIntData(this, AppConstant.SharedPreferences.COMPLETED_TRACES)

        generateRandomImages()
        generateRandomQuestionIndex()

        val jsonArray = JSONArray(
            prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.GENERATED_RANDOM_QUESTION_NUMBERS,
            )!!
        )

        referenceImageId = jsonArray.get(currentQuestion).toString().toInt()
        binding.number = jsonArray.get(currentQuestion).toString()
        binding.currentQuestion = currentQuestion + 1

        val numberr = JSONArray(
            prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.GENERATED_RANDOM_IMAGES_INDEX,
            )!!
        )

        Log.e("TAG     ", "onCreate: ${binding.number}")
        Log.e(
            "---",
            "generateRandomImages: new data = $numberr"
        )

        Log.e("onCreate", "1212: ${numberr.get(binding.number.toString().toInt())}")

        binding.dotsAlpha =
            if (completedTraces == 0 || completedTraces >= selectedTraces * 4) 1f else if (completedTraces <= selectedTraces * 2) 0.5f else 0f
        gridVisibility =
            if (completedTraces == 0 || completedTraces >= selectedTraces * 4) "High" else if (completedTraces <= selectedTraces * 2) "Medium" else "Low"
        binding.showIntroMsg =
            prefUtils.getBooleanData(this, AppConstant.SharedPreferences.SHOW_RECALL_INTRO_MSG)

        binding.btnSubmit.setOnClickListener {
            if (prefUtils.getBooleanData(
                    this,
                    AppConstant.SharedPreferences.SHOW_RECALL_INTRO_MSG
                )
            ) {
                prefUtils.saveData(this, AppConstant.SharedPreferences.SHOW_RECALL_INTRO_MSG, false)
                binding.showIntroMsg = false
            } else {

                drawingTimeEnd = Calendar.getInstance().timeInMillis
                prefUtils.saveData(
                    this,
                    AppConstant.SharedPreferences.CURRENT_QUESTION,
                    ++currentQuestion
                )
                apiCall(completedTraces)
                if (currentQuestion >= selectedTraces) {
                    if (completedTraces >= selectedTraces * 4) {
                        showLoader()
                        showResultScreen = true
                    } else {
                        finish()
                        prefUtils.saveData(
                            this,
                            AppConstant.SharedPreferences.GENERATED_RANDOM_QUESTION_NUMBERS,
                            null
                        )
                        startActivity(
                            Intent(
                                this,
                                TraceAwareCheckPointActivity::class.java
                            ).putExtra(AppConstant.BundleExtra.TRACES, selectedTraces)
                        )
                    }
                } else {
                    finish()
                    startActivity(
                        Intent(
                            this,
                            TraceAwareActivity::class.java
                        ).putExtra(AppConstant.BundleExtra.TRACES, selectedTraces)
                    )
                }
            }
        }
    }

    private fun apiCall(completedTraces: Int) {
        fingerTouchCount = binding.ivDraw.fingerTouchCount
        var file: File?
        viewModel.showResultScreen.observe(this) {
            if (showResultScreen) {
                hideLoader()
                finish()
                startActivity(
                    Intent(this, TraceAwareResultActivity::class.java).putExtra(
                        AppConstant.BundleExtra.TRACES,
                        selectedTraces
                    )
                )
            }
        }

        val numberr = JSONArray(
            prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.GENERATED_RANDOM_IMAGES_INDEX,
            )!!
        )

        Log.e("onCreate", "5555: ${numberr.get(binding.number.toString().toInt())}")

        val fileName = UUID.randomUUID()
        saveDrawAsImg(fileName)?.let { it1 ->
            file = it1
            viewModel.uploadImage(
                file!!,
                fileName,
                numberr.get(binding.number.toString().toInt()).toString().toInt(),
                drawingTimeStart,
                drawingTimeEnd,
                fingerTouchCount,
                gridVisibility,
                completedTraces > selectedTraces * 3,
            )
        }
    }

    private fun saveDrawAsImg(fileName: UUID): File? {
        val image =
            Bitmap.createBitmap(
                binding.ivDraw.width,
                binding.ivDraw.height,
                Bitmap.Config.ARGB_8888
            )
        val canvas = Canvas(image)
        canvas.drawColor(Color.WHITE)
        binding.ivDraw.draw(canvas)
//        val uri: String = MediaStore.Images.Media.insertImage(contentResolver, image, "title", null)
//        Log.e("uri", uri)

        try {
            val folder = File(cacheDir, "/traceAwareImages/")
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val file = File(folder, "${fileName}.png")

            val stream = FileOutputStream(file)
            image.compress(CompressFormat.JPEG, 10, stream)
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun generateRandomImages() {
        if (prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.GENERATED_RANDOM_IMAGES_INDEX
            ) == null
        ) {
            val arrayListImg = ArrayList<Int>()
            val jsonArray = JSONArray()
            val random = Random(System.nanoTime())

            while (arrayListImg.size < selectedTraces) {

                 val nextInt = (0..30).random(random)
                 if (!arrayListImg.contains(nextInt) && nextInt != 10) {//skip 10, bcz 10th position image is at 0th position
                     arrayListImg.add(nextInt)
                     jsonArray.put(nextInt)
                 }

              /*  for (i in 0..10) {
                    if (!arrayListImg.contains(i) && i != 10) {//skip 10, bcz 10th position image is at 0th position
                        arrayListImg.add(i)
                        jsonArray.put(i)
                    }

                }*/

               /* val nextInt1 = (0..5).random()
                if (!arrayListImg.contains(nextInt1) && nextInt1 != 10) {//skip 10, bcz 10th position image is at 0th position
                    arrayListImg.add(nextInt1)
                    jsonArray.put(nextInt1)
                }
*/
                Log.e("TAG", "generateRandomImages: $arrayListImg")

            }
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.GENERATED_RANDOM_IMAGES_INDEX,
                jsonArray.toString()
            )

        }
        setImages()
    }

    //set images in grid layout which have been generated when test start
    private fun setImages() {

        val imageIndex = JSONArray(
            prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.GENERATED_RANDOM_IMAGES_INDEX,
            )!!
        )
        val iconImages = ArrayList<Drawable>()
        for (i in 0 until imageIndex.length()) {
            iconImages.add(getResource("trace_aware_${imageIndex[i]}")!!)
        }


        binding.rvTraces.layoutManager = GridLayoutManager(this, (iconImages.size / 2))
        binding.rvTraces.adapter = TraceAdapter(iconImages, completedTraces <= selectedTraces * 3)
    }

    private fun getResource(name: String): Drawable? {
        val resID = this.resources.getIdentifier(name, "drawable", this.packageName)
        return ActivityCompat.getDrawable(this, resID)
    }

    private fun generateRandomQuestionIndex() {
        if (prefUtils.getStringData(
                this,
                AppConstant.SharedPreferences.GENERATED_RANDOM_QUESTION_NUMBERS
            ) == null
        ) {
            val arrayListImg = ArrayList<Int>()
            val jsonArray = JSONArray()
            while (arrayListImg.size < selectedTraces) {
                val nextInt = Random.nextInt(selectedTraces)
                if (!arrayListImg.contains(nextInt)) {
                    arrayListImg.add(nextInt)
                    jsonArray.put(nextInt)
                }
            }
            prefUtils.saveData(
                this,
                AppConstant.SharedPreferences.GENERATED_RANDOM_QUESTION_NUMBERS,
                jsonArray.toString()
            )
        }
    }

    override fun onBackPressed() {
        showCloseDialog()
    }

}