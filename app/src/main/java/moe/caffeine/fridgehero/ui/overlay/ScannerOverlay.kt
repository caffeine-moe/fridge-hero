package moe.caffeine.fridgehero.ui.overlay

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.ui.component.scanner.Scanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerOverlay(
  barcodeScanRequest: Event.RequestBarcodeFromScanner?,
  onDismiss: () -> Unit
) {
  AnimatedVisibility(modifier = Modifier, visible = barcodeScanRequest != null) {
    barcodeScanRequest?.result?.let { completable ->
      Scaffold(
        topBar = {
          TopAppBar({
            Row(Modifier.fillMaxWidth()) {
              Box(
                Modifier
                  .align(Alignment.CenterVertically)
                  .fillMaxWidth(),
                contentAlignment = Alignment.Center
              ) {
                TextButton(
                  modifier = Modifier.align(Alignment.CenterStart),
                  onClick = onDismiss
                ) {
                  Text("Back")
                }
                Text("Scanner")
              }
            }
          })
        }
      ) {
        Surface(Modifier.padding(it)) {
          BackHandler(enabled = true) {
            onDismiss()
          }
          Scanner(
            onDismiss = onDismiss
          ) { barcode ->
            completable.complete(Result.success(barcode))
            onDismiss()
          }
        }
      }
    }
  }
}
