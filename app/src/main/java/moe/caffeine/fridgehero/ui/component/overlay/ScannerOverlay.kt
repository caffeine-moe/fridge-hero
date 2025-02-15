package moe.caffeine.fridgehero.ui.component.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import com.google.android.datatransport.BuildConfig
import moe.caffeine.fridgehero.domain.Event

@Composable
fun ScannerOverlay(
  barcodeScanRequest: Event.RequestBarcodeFromScanner?,
  onDismiss: () -> Unit
) {
  AnimatedVisibility(barcodeScanRequest != null) {
    barcodeScanRequest?.result?.let { completable ->
      var barcode = "5941143028832"
      if (!BuildConfig.DEBUG) {
        /*        Scanner(
                scannerPadding
              ) {
                barcode = it
              }*/
      }
      completable.complete(Result.success(barcode))
      onDismiss()
    }
  }
}
