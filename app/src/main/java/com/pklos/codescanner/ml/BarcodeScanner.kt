package com.pklos.codescanner.ml

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeScanner(val callback: () -> Unit): ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        val mediaImage = image.image
        mediaImage?.let {
            // TODO: crop image from barcode box and send to scanner

            val mediaImg = InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient(options)
            scanner.process(mediaImg)
                .addOnSuccessListener { barcodes ->
                    changeBarcodeText = barcodes.size > 0
                    for (barcode in barcodes) {
                        barcodeValue = barcode.rawValue
                    }
                    callback()

                    image.close()
                }
                .addOnFailureListener {
                    image.close()
                }
        }
    }

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_AZTEC, Barcode.FORMAT_CODABAR, Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39, Barcode.FORMAT_CODE_93, Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_ITF, Barcode.FORMAT_PDF417,
            Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
        .build()

    companion object {
        var changeBarcodeText = false
        var barcodeValue: String? = ""
    }
}