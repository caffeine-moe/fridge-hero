package moe.caffeine.fridgehero.ui.screen.home.component.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SizeCounter(foodItemsCount: Int, recipesCount: Int) {
  ElevatedCard(Modifier.fillMaxWidth()) {
    if (foodItemsCount == 0 && recipesCount == 0) {
      Text("Try adding an item to the fridge!")
      return@ElevatedCard
    }
    Column(
      Modifier
        .padding(8.dp)
        .align(Alignment.CenterHorizontally),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text("Items Stored", style = MaterialTheme.typography.labelLarge)
      Text("$foodItemsCount", style = MaterialTheme.typography.headlineLarge)
      Spacer(Modifier.size(8.dp))
      Text("Recipes Made", style = MaterialTheme.typography.labelLarge)
      Text("$recipesCount", style = MaterialTheme.typography.headlineLarge)
    }
  }
}
