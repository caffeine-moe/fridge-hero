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

      //only accept barcodes used for product identification
      val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
          Barcode.FORMAT_CODE_128, //scan directly from openfoodfacts.org
          Barcode.FORMAT_EAN_13,
          Barcode.FORMAT_EAN_8,
          Barcode.FORMAT_UPC_A,
          Barcode.FORMAT_UPC_E
        )
        .build()
      val barcodeScanner = BarcodeScanning.getClient(options)

      val imageToProcess =
        InputImage.fromMediaImage(imageToAnalyze, image.imageInfo.rotationDegrees)

      barcodeScanner.process(imageToProcess)
        .addOnSuccessListener { barcodes ->
          val now = Clock.System.now().toEpochMilliseconds()
          //small delay to prevent accidents while maintaining speed
          if (now - lastScannedTimestamp < 200) return@addOnSuccessListener
          lastScannedTimestamp = Clock.System.now().toEpochMilliseconds()
          barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue?.let {
            //only accept if it recognises the same barcode twice to reduce error
            if (it == lastScanned) {
              onBarcodeDetected(it)
            }
            lastScanned = it
          }
        }
        .addOnCompleteListener {
          image.close()
        }
    }
  }
}
