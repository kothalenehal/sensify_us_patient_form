package com.sensifyawareapp.ml.barcode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.Camera
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions.ZoomCallback
import com.google.mlkit.vision.barcode.common.Barcode
import com.sensifyawareapp.ml.barcode.utils.BarcodeScannerProcessor
import com.sensifyawareapp.ml.barcode.utils.BarcodeScanningResultListener
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityQrcodeScanBinding

import com.sensifyawareapp.ml.barcode.utils.GraphicOverlay
import com.sensifyawareapp.ml.barcode.utils.VisionImageProcessor
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.scentaware.QuestionAnswerActivity
import com.sensifyawareapp.ui.scentawaretraining.RetrainingActivity
import com.sensifyawareapp.utils.common.AppConstant
import org.json.JSONArray
import org.json.JSONObject

class QRCodeScanActivity : BaseActivity(), BarcodeScanningResultListener {

    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null

    lateinit var binding: ActivityQrcodeScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode_scan)

        binding.title =
            if (prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_MENU) == 1)
                getString(R.string.odor_identification_test)
            else getString(R.string.smell_retraining)

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }

        binding.ivBack.setOnClickListener {
            showCloseDialog()
        }

        binding.selectedKitSize = prefUtils.getIntData(
            this,
            AppConstant.SharedPreferences.SELECTED_KIT_SIZE
        )

        Log.e(TAG, "onCreate QR Code: ${binding.selectedKitSize}", )

        previewView = binding.previewView
        if (previewView == null) {
            Log.d(TAG, "previewView is null")
        }
        graphicOverlay = binding.graphicOverlay
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

        ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[CameraXViewModel::class.java]
            .processCameraProvider
            .observe(
                this,
                Observer { provider: ProcessCameraProvider? ->
                    cameraProvider = provider
                    bindAllCameraUseCases()
                }
            )

        startCamera()
    }

    private fun startCamera() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            if (permissionUtil.checkMissingPermission(
                    this, 102,
                    Manifest.permission.CAMERA,
                )
            ) {
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val showRationale = shouldShowRequestPermissionRationale(permissions[0])
        if (requestCode == 102) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED))
                startCamera()
            else if (!showRationale)
                showError(getString(R.string.rational_msg_camera_permission))

        }
    }

    override fun onResume() {
        super.onResume()
        bindAllCameraUseCases()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        }
    }

    override fun onPause() {
        super.onPause()

        imageProcessor?.run { this.stop() }
    }

    public override fun onDestroy() {
        super.onDestroy()
        imageProcessor?.run { this.stop() }
    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.getSurfaceProvider())
        camera =
            cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, previewUseCase)
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        var zoomCallback: ZoomCallback? = null
        zoomCallback = ZoomCallback { zoomLevel: Float ->
            Log.i(TAG, "Set zoom ratio $zoomLevel")
            val ignored = camera!!.cameraControl.setZoomRatio(zoomLevel)
            true
        }
        val scanner = BarcodeScannerProcessor(this, zoomCallback)
        scanner.setResultListener(this);
        imageProcessor = scanner

        val builder = ImageAnalysis.Builder()
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(this),
            ImageAnalysis.Analyzer { imageProxy: ImageProxy ->
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        graphicOverlay!!.setImageSourceInfo(imageProxy.width, imageProxy.height, isImageFlipped)
                    } else {
                        graphicOverlay!!.setImageSourceInfo(imageProxy.height, imageProxy.width, isImageFlipped)
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
                } catch (e: MlKitException) {
                    Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        )
        camera = cameraProvider!!.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector!!, analysisUseCase)
    }

    override fun onBarcodeScanningResultSuccess(barcodes: List<Barcode>) {
        if(barcodes.isEmpty()) {
            return;
        }
        var text = barcodes[0].rawValue
        if(text != null && text.trim().isNotEmpty()) {
            Log.v(
                "onBarcodeScanningResult",
                text
            )
            processScannedResult(text)
        }

    }

    private fun processScannedResult(result: String) {
        if(result.matches(Regex("[0-9]{6}\\|([A-Za-z]).*"))) {
            val withTimer =
                prefUtils.getBooleanData(this, AppConstant.SharedPreferences.WITH_TIMER)
            val previousData =
                prefUtils.getStringData(
                    this,
                    if (withTimer) AppConstant.SharedPreferences.PREF_SCANNED_RESULT_WITH_TIMER else AppConstant.SharedPreferences.PREF_SCANNED_RESULT
                )
            if (previousData == null) {
                //this is first tube
                val scannedTubes = JSONArray()
                scannedTubes.put(result)
                val ob = JSONObject()
                ob.put("scannedTubes", scannedTubes)
                prefUtils.saveData(
                    this,
                    if (withTimer) AppConstant.SharedPreferences.PREF_SCANNED_RESULT_WITH_TIMER else AppConstant.SharedPreferences.PREF_SCANNED_RESULT,
                    ob.toString()
                )
            } else {
                val options = result.split("|")

                val ob = JSONObject(previousData)
                val scannedTubes = ob.getJSONArray("scannedTubes")

                //return from here if the QR code is already scanned
                var alreadyScannedQRCode = false
                for (i in 0 until scannedTubes.length()) {
                    val item = scannedTubes.getString(i)
                    val id = item.split("|")[0]
                    if (id == options[0]) {
                        alreadyScannedQRCode = true
                        break
                    }
                }
                if (alreadyScannedQRCode) {
                    showError(getString(R.string.please_select_different_tube))
                    return
                }

                scannedTubes.put(result)

                val ob2 = JSONObject()
                ob2.put("scannedTubes", scannedTubes)
                prefUtils.saveData(
                    this,
                    if (withTimer) AppConstant.SharedPreferences.PREF_SCANNED_RESULT_WITH_TIMER else AppConstant.SharedPreferences.PREF_SCANNED_RESULT,
                    ob2.toString()
                )
            }
            finish()

            if (prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_MENU) == 2) {
                startActivity(
                    Intent(
                        this,
                        RetrainingActivity::class.java
                    ).putExtra(AppConstant.BundleExtra.SCANNED_RESULT, result)
                )
            } else {
                Log.e("TAG", "processScannedResult: // $result")
                startActivity(
                    Intent(
                        this,
                        QuestionAnswerActivity::class.java
                    ).putExtra(AppConstant.BundleExtra.SCANNED_RESULT, result)
                )
            }

        }else{
            showError("Can not recognize tube!")
            return//return from here as it is some unknown qr code, format of qr code looks like below
            //000001|Coconut|Banana|Walnut|Cherry|No Odor detected|Banana
        }
    }

    override fun onBarcodeScanningResultFailure(e: Exception) {

    }

    override fun onBackPressed() {
        showCloseDialog()
    }

    companion object {
        private const val TAG = "CameraXLivePreview"
    }

}