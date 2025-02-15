package moe.caffeine.fridgehero.ui.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.item.components.ItemEditor

@Composable
fun ItemFull(
  foodItem: FoodItem,
  onDismiss: () -> Unit
) {
  Surface(
    Modifier
      .fillMaxSize()
      .systemBarsPadding()
  ) {
    Column {
      Text(foodItem.name)
      TextButton(onDismiss) { Text("Back") }
      ItemEditor(foodItem, readOnly = true)
    }
  }
}
