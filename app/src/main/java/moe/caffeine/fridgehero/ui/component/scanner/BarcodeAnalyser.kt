package moe.caffeine.fridgehero.ui.component.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.datetime.Clock

class BarcodeAnalyser(
  private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {
  private var lastScannedTimestamp = Clock.System.now().toEpochMilliseconds()
  private var lastScanned = lastScannedTimestamp.toString()


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
          val now = Clock.System.now().toEpochMilliseconds()
          if (now - lastScannedTimestamp < 250) return@addOnSuccessListener
          lastScannedTimestamp = Clock.System.now().toEpochMilliseconds()
          barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue?.let {
            if (it != lastScanned) {
              lastScanned = it
              return@addOnSuccessListener
            }
            onBarcodeDetected(it)
          }
        }
        .addOnCompleteListener {
          image.close()
        }
    }
  }
}
