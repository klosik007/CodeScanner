package com.pklos.codescanner.ml

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeScanner: ImageAnalysis.Analyzer {
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        image.image?.let {
            val img = InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient(options)

            scanner.process(img)
                .addOnSuccessListener { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.displayValue
                    }
                    image.close()
                }
                .addOnFailureListener {
                    image.close()
                }
        }
    }
}