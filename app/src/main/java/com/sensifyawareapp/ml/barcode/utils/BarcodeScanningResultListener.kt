package com.sensifyawareapp.ml.barcode.utils

import com.google.mlkit.vision.barcode.common.Barcode

interface BarcodeScanningResultListener {

    fun onBarcodeScanningResultSuccess(barcodes: List<Barcode>);

    fun onBarcodeScanningResultFailure(e: Exception);

}