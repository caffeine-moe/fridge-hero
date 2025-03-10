package moe.caffeine.fridgehero.ui.component.itemsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp

@Composable
fun ScannerFloatingActionButton(
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  ExtendedFloatingActionButton(
    modifier = modifier,
    onClick = onClick
  ) {
    Image(
      imageVector = Icons.Outlined.QrCodeScanner,
      contentDescription = "Scanner Icon",
      colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
    )
    Spacer(Modifier.size(8.dp))
    Text("Scan Barcode")
  }
}
