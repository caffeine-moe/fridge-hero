package moe.caffeine.fridgehero.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyser(
    private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private var isScanning = true

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        image.image?.let { imageToAnalyze ->
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
            val barcodeScanner = BarcodeScanning.getClient(options)
            val imageToProcess =
                InputImage.fromMediaImage(imageToAnalyze, image.imageInfo.rotationDegrees)

            barcodeScanner.process(imageToProcess)
                .addOnSuccessListener { barcodes ->
                    when {
                        barcodes.isNotEmpty() && isScanning -> {
                            isScanning = false
                            barcodes.first { it.rawValue != null }.rawValue?.let {
                                onBarcodeDetected(
                                    it
                                )
                            }
                        }
                    }
                }
                .addOnCompleteListener {
                    image.close()
                }
        }
    }
}