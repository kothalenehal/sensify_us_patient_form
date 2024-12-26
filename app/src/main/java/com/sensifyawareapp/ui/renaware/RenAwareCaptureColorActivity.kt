package com.sensifyawareapp.ui.renaware

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityRenAwareCaptureColorBinding
import com.sensifyawareapp.ui.BaseActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RenAwareCaptureColorActivity : BaseActivity() {
    lateinit var binding: ActivityRenAwareCaptureColorBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    val listXY = ArrayList<String>()
    var data = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareCaptureColorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.cameraCaptureButton.setOnClickListener {
            takePhoto()
            showLoader()
        }

        binding.ivBack.setOnClickListener {
            showCloseDialog()
        }


    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val TAG = "CameraXGFG"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    // checks the camera permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    private fun takePhoto() {
        val x = binding.gridLayout.x
        val y = binding.gridLayout.y
        Log.e(TAG, "X:$x  Y:$y")

        val width = binding.view1.width / 2
        val height = binding.view1.height / 2
        Log.e(TAG, "width:$width  height:$height ")

        val view1x = x + binding.view1.x + width
        val view1y = y + binding.view1.y + height
        val view2x = x + binding.view2.x + width
        val view3x = x + binding.view3.x + width
        val view4y = y + binding.view4.y + height
        val view7y = y + binding.view7.y + height
        val view10y = y + binding.view10.y + height
        Log.e(TAG, "onCreate 1: $view1x  //  $view1y ")
        Log.e(TAG, "onCreate 2: $view2x  //  $view1y ")
        Log.e(TAG, "onCreate 3: $view3x  //  $view1y ")
        Log.e(TAG, "onCreate 4: $view1x  //  $view4y ")
        Log.e(TAG, "onCreate 5: $view2x  //  $view4y ")
        Log.e(TAG, "onCreate 6: $view3x  //  $view4y ")
        Log.e(TAG, "onCreate 7: $view1x  //  $view7y ")
        Log.e(TAG, "onCreate 8: $view2x  //  $view7y ")
        Log.e(TAG, "onCreate 9: $view3x  //  $view7y ")
        Log.e(TAG, "onCreate 10: $view1x  //  $view10y ")
        Log.e(TAG, "onCreate 11: $view2x  //  $view10y ")
        Log.e(TAG, "onCreate 12: $view3x  //  $view10y ")

        listXY.add(0, "$view1x, $view1y")
        listXY.add(1, "$view2x, $view1y")
        listXY.add(2, "$view3x, $view1y")
        listXY.add(3, "$view1x, $view4y")
        listXY.add(4, "$view2x, $view4y")
        listXY.add(5, "$view3x, $view4y")
        listXY.add(6, "$view1x, $view7y")
        listXY.add(7, "$view2x, $view7y")
        listXY.add(8, "$view3x, $view7y")
        listXY.add(9, "$view1x, $view10y")
        listXY.add(10, "$view2x, $view10y")
        listXY.add(11, "$view3x, $view10y")


        val imageCapture = imageCapture ?: return
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, savedUri)
                    val luminosity = calculateLuminosity(bitmap)
                    Log.e(TAG, "Luminosity Up: $luminosity" )
//                    findColor(bitmap)

//                    changeImageDirection(bitmap,savedUri)
                    try {
                        val exif = savedUri.path?.let { ExifInterface(it) }
                        val orientation: Int =
                            exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
                        Log.e("EXIF", "Exif: $orientation")
                        val matrix = Matrix()
                        if (orientation == 6) {
                            matrix.postRotate(90f)
                        } else if (orientation == 3) {
                            matrix.postRotate(180f)
                        } else if (orientation == 8) {
                            matrix.postRotate(270f)
                        }
                        bitmap = Bitmap.createBitmap(
                            bitmap,
                            0,
                            0,
                            bitmap.width,
                            bitmap.height,
                            matrix,
                            true
                        ) // rotating bitmap
                    } catch (e: java.lang.Exception) {
                    }

                    binding.imageView.setImageBitmap(bitmap)
                    binding.imageView.isDrawingCacheEnabled = true
//                    binding.imageView.visibility = View.VISIBLE
//                    binding.cameraView.visibility = View.INVISIBLE
                    binding.imageView.buildDrawingCache(true)

                    val imgBit = binding.imageView.drawingCache

                    data.clear()

                    for (i in listXY) {
//                        Log.e(TAG, "onImageSaved:11 $i")
                        val separated: List<String> = i.split(", ")
                        val s1 = separated[0].toFloat()
                        val s2 = separated[1].toFloat()
//                        Log.e(TAG, "For Loop: ${separated[0]} // ${separated[1]} ")
                        data.add(imgBit.getPixel(s1.toInt(), s2.toInt()))
                        val r = Color.red(imgBit.getPixel(s1.toInt(), s2.toInt()))
                        val g = Color.green(imgBit.getPixel(s1.toInt(), s2.toInt()))
                        val b = Color.blue(imgBit.getPixel(s1.toInt(), s2.toInt()))
                        Log.e(TAG, "onImageSaved: 12  $r $g  $b ")
                    }

                    hideLoader()

                    val intent = Intent(
                        this@RenAwareCaptureColorActivity,
                        RenAwareColorPickerActivity::class.java
                    )
                    intent.putExtra("pixel", data)
                        .putExtra("cartridgeId", intent.getStringExtra("cartridgeId"))
                        .putExtra("sensorValue", luminosity)
                    startActivity(intent)

                    Log.e(TAG, "Image Size:  // $data")
                }
            })

    }

    private fun calculateLuminosity(bitmap: Bitmap): Double {
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var luminanceSum = 0
        for (pixel in pixels) {
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            val luminance = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
            luminanceSum += luminance
        }

        val luminosity = luminanceSum.toDouble() / (bitmap.width * bitmap.height)
        return luminosity
    }

   /* private fun changeImageDirection(bitmap: Bitmap, savedUri: Uri): Bitmap {
        try {
            val exif = savedUri.path?.let { ExifInterface(it) }
            val orientation: Int =
                exif!!.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)
            Log.e("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
            } else if (orientation == 3) {
                matrix.postRotate(180f)
            } else if (orientation == 8) {
                matrix.postRotate(270f)
            }
            bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            ) // rotating bitmap
        } catch (e: java.lang.Exception) {
        }
        return bitmap
    }*/

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()

    }

    override fun onBackPressed() {
        showCloseDialog()
        cameraExecutor.shutdown()

    }


}