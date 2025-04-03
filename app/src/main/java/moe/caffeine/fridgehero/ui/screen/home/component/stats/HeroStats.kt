package moe.caffeine.fridgehero.ui.screen.home.component.stats

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun HeroStats(foodItemsFlow: StateFlow<List<FoodItem>>, recipesFlow: StateFlow<List<Recipe>>) {
  val foodItems by foodItemsFlow.collectAsStateWithLifecycle()
  val recipes by recipesFlow.collectAsStateWithLifecycle()
  Text(
    modifier = Modifier.padding(vertical = 8.dp),
    style = Typography.titleMedium,
    text = "Hero Stats"
  )
  SizeCounter(foodItems.size, recipes.size)
}
