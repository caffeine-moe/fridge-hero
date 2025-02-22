package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.FoodItem

@Composable
fun ItemEditor(
  editableFoodItem: FoodItem,
  readOnly: Boolean = false,
  onScannerRequest: (replaceAll: Boolean) -> Unit = {},
  onFieldChanged: (FoodItem) -> Unit = {},
) {
  Box(
    Modifier
      .padding(8.dp)
  ) {
    Row(
      Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
    ) {
      Column(Modifier.weight(0.5f)) {
        OutlinedTextField(
          value = editableFoodItem.name,
          readOnly = readOnly,
          onValueChange = {
            onFieldChanged(editableFoodItem.copy(name = it))
          },
          label = { Text("Name") },
          singleLine = true
        )
        OutlinedTextField(
          value = editableFoodItem.brand,
          readOnly = readOnly,
          onValueChange = {
            onFieldChanged(editableFoodItem.copy(brand = it))
          },
          label = { Text("Brand") },
          singleLine = true
        )
        OutlinedTextField(
          value = editableFoodItem.barcode,
          readOnly = readOnly,
          onValueChange = {
            onFieldChanged(editableFoodItem.copy(barcode = it))
          },
          label = { Text("Barcode") },
          singleLine = true
        )
      }
      Spacer(Modifier.size(8.dp))
      Box(
        Modifier
          .weight(0.5f)
          .padding(top = 6.dp)
          .fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        ItemImageCard(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .size(120.dp),
          editableFoodItem
        )
        Button(
          modifier = Modifier
            .align(Alignment.BottomCenter),
          enabled = !readOnly,
          onClick = {
            onScannerRequest(true)
          }) {
          Image(
            imageVector = Icons.Outlined.QrCodeScanner,
            contentDescription = "Scanner Icon",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
          )
          Spacer(Modifier.size(8.dp))
          Text("Scan Barcode")
        }
      }
    }
  }
}
