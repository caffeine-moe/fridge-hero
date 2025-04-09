package moe.caffeine.fridgehero.ui.screen.home.component.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.ui.component.item.resolveNovaGroupPainter
import moe.caffeine.fridgehero.ui.component.item.resolveNutriScorePainter
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun QuickStats(foodItems: List<FoodItem>, recipesCount: Int) {
  ElevatedCard(Modifier.fillMaxWidth()) {
    Text(
      modifier = Modifier.padding(8.dp),
      style = Typography.titleMedium,
      text = "Quick Stats"
    )
    if (foodItems.isEmpty() && recipesCount == 0) {
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
      Row(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
          Text("Items Saved", style = MaterialTheme.typography.labelLarge)
          Text("${foodItems.size}", style = MaterialTheme.typography.headlineLarge)
        }
        Spacer(Modifier.size(8.dp))
        Column(Modifier.weight(1f)) {
          val nutriscoreHavers =
            foodItems.filterNot { it.nutriScore == NutriScore.UNKNOWN || it.isRemoved }
          val avgNutriScore =
            ((nutriscoreHavers.sumOf { it.nutriScore.ordinal }) / 5).coerceIn(
              1,
              5
            )
              .let { avgNutriScoreOrdinal ->
                NutriScore.entries.toTypedArray().first { it.ordinal == avgNutriScoreOrdinal }
              }
          Text(
            modifier = Modifier.padding(2.dp),
            text = "Avg. NutriScore",
            style = MaterialTheme.typography.labelLarge
          )
          Image(
            modifier = Modifier
              .width(80.dp),
            painter = resolveNutriScorePainter(avgNutriScore)(),
            contentDescription = avgNutriScore.letter
          )
        }
        Spacer(Modifier.size(8.dp))
        Row(Modifier.weight(1f)) {
          val novaGroupHavers =
            foodItems.filterNot { it.novaGroup == NovaGroup.UNKNOWN || it.isRemoved }
          val avgNovaGroup =
            ((novaGroupHavers.sumOf { it.novaGroup.number }) / 4).coerceIn(
              1,
              4
            )
              .let { avgNovaGroupOrdinal ->
                NovaGroup.entries.toTypedArray().first { it.ordinal == avgNovaGroupOrdinal }
              }
          Text(
            modifier = Modifier.padding(2.dp),
            text = "Avg.\nNova\nGroup",
            style = MaterialTheme.typography.labelLarge
          )
          Image(
            modifier = Modifier
              .size(60.dp),
            painter = resolveNovaGroupPainter(avgNovaGroup)(),
            contentDescription = avgNovaGroup.number.toString()
          )
        }
      }
      Spacer(Modifier.size(8.dp))
      Box(Modifier.align(Alignment.Start)) {
        Column {
          Text("Recipes Made", style = MaterialTheme.typography.labelLarge)
          Text("$recipesCount", style = MaterialTheme.typography.headlineLarge)
        }
      }
    }
  }
}
