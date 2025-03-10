package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import moe.caffeine.fridgehero.domain.model.FoodItem

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
      ItemEditor(foodItem, foodItem.categories, foodItem.expiryDates, readOnly = true)
    }
  }
}
