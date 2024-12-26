package com.sensifyawareapp.ui.scentaware

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityQrcodeScanBinding
import com.sensifyawareapp.ui.BaseActivity
import com.sensifyawareapp.ui.scentawaretraining.RetrainingActivity
import com.sensifyawareapp.utils.common.AppConstant
import org.json.JSONArray
import org.json.JSONObject
import java.util.Timer


class OLDQRCodeScanActivity : BaseActivity() {

    lateinit var binding: ActivityQrcodeScanBinding

    private lateinit var codeScanner: CodeScanner
    private var zoomed = false
    private var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode_scan)
        codeScanner = CodeScanner(this, binding.scannerView)

        binding.title =
            if (prefUtils.getIntData(this, AppConstant.SharedPreferences.SELECTED_MENU) == 1)
                getString(R.string.odor_identification_test)
            else getString(R.string.smell_retraining)

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = mutableListOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.setAutoFocusInterval(3000)
        codeScanner.scanMode = ScanMode.CONTINUOUS
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
        codeScanner.isTouchFocusEnabled = true
        codeScanner.zoom = 0
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                showError("Camera Decoded Message")
                processScannedResult(it)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                showError("Camera initialization error: ${it.message}")
            }
        }

        binding.scannerView.setOnClickListener {
            startCamera()
        }


        startCamera()

    }

    private fun startTimer() {
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                if(codeScanner.zoom < 140) {
//                    codeScanner.zoom = codeScanner.zoom + 70
//
//                }
//                else{
//                    timer.cancel()
//                }
//
//            }
//        }, 6000)
    }

    private fun startCamera() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            codeScanner.startPreview()
        } else {
            if (permissionUtil.checkMissingPermission(
                    this, 102,
                    Manifest.permission.CAMERA,
                )
            ) {
                codeScanner.startPreview()
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
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            codeScanner.startPreview()
        }
        startTimer()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        timer.cancel()
        super.onPause()
    }

    override fun onBackPressed() {
        showCloseDialog()
    }

    private fun processScannedResult(result: Result) {
        if(zoomed) {
            if (!result.text.matches(Regex("[0-9]{6}\\|([A-Za-z]).*"))) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        codeScanner.startPreview()
                    }, 1000)
                }
                showError("Can not recognize tube!")
                return//return from here as it is some unknown qr code, format of qr code looks like below
                //000001|Coconut|Banana|Walnut|Cherry|No Odor detected|Banana
            }

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
                scannedTubes.put(result.text)
                val ob = JSONObject()
                ob.put("scannedTubes", scannedTubes)
                prefUtils.saveData(
                    this,
                    if (withTimer) AppConstant.SharedPreferences.PREF_SCANNED_RESULT_WITH_TIMER else AppConstant.SharedPreferences.PREF_SCANNED_RESULT,
                    ob.toString()
                )
            } else {
                val options = result.text.split("|")

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
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            codeScanner.startPreview()
                        }, 1000)
                    }
                    showError(getString(R.string.please_select_different_tube))
                    return
                }

                scannedTubes.put(result.text)

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
                    ).putExtra(AppConstant.BundleExtra.SCANNED_RESULT, result.text)
                )
            } else {
                Log.e("TAG", "processScannedResult: // ${result.text}")
                startActivity(
                    Intent(
                        this,
                        QuestionAnswerActivity::class.java
                    ).putExtra(AppConstant.BundleExtra.SCANNED_RESULT, result.text)
                )
            }
        }
        else{
            codeScanner.zoom = codeScanner.zoom + 70
            Handler(Looper.getMainLooper()).postDelayed({
                zoomed = true
            }, 100)


        }
    }

}