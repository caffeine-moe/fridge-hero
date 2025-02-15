package moe.caffeine.fridgehero.ui.item.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.domain.model.FoodItem

@Composable
fun ItemEditor(
  editableFoodItem: FoodItem,
  readOnly: Boolean = false,
  onScannerRequest: (replaceAll: Boolean) -> Unit = {},
  onFieldChanged: (FoodItem) -> Unit = {},
) {
  val imageBitmap by remember(editableFoodItem) {
    derivedStateOf {
      if (editableFoodItem.imageByteArray.isNotEmpty()) BitmapPainter(
        editableFoodItem.imageBitmap
      )
      else {
        null
      }
    }
  }
  Box(
    Modifier
      .padding(8.dp)
      .defaultMinSize(minHeight = 240.dp)
      .fillMaxWidth()
  ) {
    Column(
      Modifier
    ) {
      Row(
        Modifier
          .align(Alignment.Start)
          .fillMaxWidth()
      ) {
        Column(
          Modifier.fillMaxWidth(0.5f)
        ) {
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
        }
        Box(
          modifier = Modifier.fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Column(
            Modifier
              .align(Alignment.Center)
          ) {
            Box(
              modifier = Modifier
                .padding(8.dp),
              contentAlignment = Alignment.Center
            ) {
              Image(
                modifier = Modifier
                  .clip(RoundedCornerShape(16.dp))
                  .size(120.dp),
                painter = imageBitmap ?: painterResource(
                  R.drawable.ic_launcher_background
                ),
                alignment = Alignment.CenterStart,
                contentDescription = "Image of ${editableFoodItem.name}",
                contentScale = ContentScale.Crop
              )
            }
          }
        }
      }
      Row(Modifier.fillMaxWidth()) {
        Box(
          Modifier.fillMaxWidth(0.5f)
        ) {
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
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
          contentAlignment = Alignment.Center
        ) {
          Button(
            modifier = Modifier
              .align(Alignment.Center),
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
}
