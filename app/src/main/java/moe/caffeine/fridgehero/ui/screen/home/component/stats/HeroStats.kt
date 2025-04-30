package moe.caffeine.fridgehero.ui.screen.home.component.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.NutrimentBreakdown
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun HeroStats(
  foodItems: List<FoodItem>,
  recipes: List<Recipe>,
  nutrimentBreakdown: NutrimentBreakdown?,
  profile: Profile
) {
  Text(
    modifier = Modifier.padding(vertical = 8.dp),
    style = Typography.titleMedium,
    text = "Hero Stats"
  )
  Column {
    QuickStats(foodItems, recipes.size, nutrimentBreakdown, profile)
    Spacer(Modifier.size(8.dp))
    ElevatedCard {
      if (nutrimentBreakdown != null) {
        if (nutrimentBreakdown.items.isNotEmpty())
          PieChart(
            data = nutrimentBreakdown.totals
              .filterNot { it.key == Nutriment.ENERGY },
            middleText = "For ${nutrimentBreakdown.items.sumOf { it.expiryDates.size }} of ${foodItems.sumOf { it.expiryDates.size }} items.",
          )
      }
    }
  }
}
