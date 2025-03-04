package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.FoodItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemEditor(
  foodItem: FoodItem,
  categories: List<String>,
  readOnly: Boolean = false,
  onScannerRequest: (replaceAll: Boolean) -> Unit = {},
  onFieldChanged: (FoodItem) -> Unit = {},
  onCategoriesChanged: (List<String>) -> Unit = {}
) {
  var expanded by rememberSaveable { mutableStateOf(false) }
  Box(
    Modifier
      .padding(8.dp)
  ) {
    Column(
      Modifier
        .wrapContentSize()
    ) {
      Row(
        Modifier
          .fillMaxWidth()
          .height(IntrinsicSize.Min)
      ) {
        Column(Modifier.weight(0.5f)) {
          OutlinedTextField(
            value = foodItem.name,
            readOnly = readOnly,
            onValueChange = {
              onFieldChanged(foodItem.copy(name = it))
            },
            label = { Text("Name") },
            singleLine = true
          )
          OutlinedTextField(
            value = foodItem.brand,
            readOnly = readOnly,
            onValueChange = {
              onFieldChanged(foodItem.copy(brand = it))
            },
            label = { Text("Brand") },
            singleLine = true
          )
          OutlinedTextField(
            value = foodItem.barcode,
            readOnly = readOnly,
            onValueChange = {
              onFieldChanged(foodItem.copy(barcode = it))
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
            foodItem
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
      Spacer(Modifier.size(8.dp))
      AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(
          animationSpec = tween(500)
        ) + fadeIn(tween(500)),
        exit = shrinkVertically(
          animationSpec = tween(500)
        ) + fadeOut(tween(500))
      ) {
        Card {
          CategoryEditor(
            categories = categories,
            onListChanged = onCategoriesChanged
          )
        }
      }
      TextButton(
        onClick = {
          expanded = !expanded
        }
      ) {
        Text("Show ${if (!expanded) "More..." else "Less"}")
      }
    }
  }
}
