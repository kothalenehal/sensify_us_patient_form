package com.sensifyawareapp.ui.renaware

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.Result
import com.sensifyawareapp.R
import com.sensifyawareapp.databinding.ActivityRenAwareQrcodeBinding
import com.sensifyawareapp.ui.BaseActivity
import java.util.regex.Pattern


class RenAwareQRCodeActivity : BaseActivity() {

    lateinit var binding: ActivityRenAwareQrcodeBinding

    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRenAwareQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        codeScanner = CodeScanner(this, binding.scannerView)

        binding.title = getString(R.string.ren_aware_wellness_check)
        binding.toolbar.ivBack.setImageResource(R.drawable.ic_close_cross)
        binding.toolbar.ivBack.setOnClickListener {
            showCloseDialog()
        }

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS
        codeScanner.setAutoFocusInterval(500)
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false
        codeScanner.isTouchFocusEnabled = true
        codeScanner.zoom = 4
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
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
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onBackPressed() {
        showCloseDialog()
    }

    private fun processScannedResult(result: Result) {

        if (!result.text.matches(Regex("[A-Za-z].+"))) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Handler(Looper.getMainLooper()).postDelayed({
                    codeScanner.startPreview()
                }, 1000)
            }
            showError("Can not recognize Code!")
            return//return from here as it is some unknown qr code, format of qr code looks like below
            //000001|Coconut|Banana|Walnut|Cherry|No Odor detected|Banana
            //Renaware | Cartridge ID 00001
        }
        val p = Pattern.compile("[0-9]{5}")
        val m = p.matcher(result.text)
        var cartridgeId: String? = null
        while (m.find())
            cartridgeId = m.group()

        startActivity(
            Intent(this, RenAwareCaptureColorActivity::class.java).putExtra("cartridgeId", cartridgeId)
        )
    }

}