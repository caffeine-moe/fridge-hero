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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.ADULT_DAILY_REFERENCE_INTAKE_KCAL
import moe.caffeine.fridgehero.domain.model.NutrimentBreakdown
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import moe.caffeine.fridgehero.ui.component.item.resolveNovaGroupPainter
import moe.caffeine.fridgehero.ui.component.item.resolveNutriScorePainter
import moe.caffeine.fridgehero.ui.theme.Typography
import kotlin.math.round

@Composable
fun QuickStats(
  foodItems: List<FoodItem>,
  recipesCount: Int,
  nutrimentBreakdown: NutrimentBreakdown?,
  profile: Profile
) {
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
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
          Text("Items Saved", style = MaterialTheme.typography.labelLarge)
          Text("${foodItems.size}", style = MaterialTheme.typography.headlineLarge)
        }
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
          val avgNutriScore by remember(foodItems) {
            derivedStateOf {
              val nutriscoreHavers =
                foodItems.filterNot { it.nutriScore == NutriScore.UNKNOWN || it.isRemoved }
              if (nutriscoreHavers.isEmpty())
                NutriScore.UNKNOWN
              else
                (nutriscoreHavers.sumOf { it.nutriScore.ordinal }
                  .toDouble() / nutriscoreHavers.size.toDouble())
                  .let {
                    round(it.toDouble()).toInt()
                  }
                  .coerceIn(1, 5)
                  .let { avgNutriScoreOrdinal ->
                    NutriScore.entries.toTypedArray().first { it.ordinal == avgNutriScoreOrdinal }
                  }
            }
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
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
          val avgNovaGroup by remember(foodItems) {
            derivedStateOf {
              val novaGroupHavers =
                foodItems.filterNot { it.novaGroup == NovaGroup.UNKNOWN || it.isRemoved }
              if (novaGroupHavers.isEmpty())
                NovaGroup.UNKNOWN
              else
                (novaGroupHavers.sumOf { it.novaGroup.number }
                  .toDouble() / novaGroupHavers.size.toDouble())
                  .let {
                    round(it.toDouble()).toInt()
                  }
                  .coerceIn(1, 4)
                  .let { avgNovaGroupOrdinal ->
                    NovaGroup.entries.toTypedArray().first { it.ordinal == avgNovaGroupOrdinal }
                  }
            }
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
      Spacer(Modifier.size(8.dp))
      if (nutrimentBreakdown == null) return@ElevatedCard
      if (nutrimentBreakdown.items.isNotEmpty()) {
        val caloriesTotal by rememberSaveable(nutrimentBreakdown) {
          mutableDoubleStateOf(
            nutrimentBreakdown.totals.getValue(Nutriment.ENERGY)
          )
        }
        val daysLeftOfFood by remember(nutrimentBreakdown) {
          derivedStateOf {
            println(profile.householdComposition.first)
            caloriesTotal / ((profile.householdComposition.first * ADULT_DAILY_REFERENCE_INTAKE_KCAL))
          }
        }

        Row(Modifier.fillMaxWidth()) {
          Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text("Calories in Fridge (est.)", style = MaterialTheme.typography.labelLarge)
            Text("$caloriesTotal", style = MaterialTheme.typography.headlineLarge)
          }
          Column(Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
            Text(
              "Days of Food (est.)",
              style = MaterialTheme.typography.labelLarge,
            )
            Text(
              "$daysLeftOfFood".split(".").let { " ${it.first()}.${it.last().take(2)}" },
              style = MaterialTheme.typography.headlineLarge,
            )
          }
        }
      }
    }
  }
}
