package moe.caffeine.fridgehero.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.NutrimentBreakdown
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.screen.home.component.AvailableRecipes
import moe.caffeine.fridgehero.ui.screen.home.component.ExpiringSoon
import moe.caffeine.fridgehero.ui.screen.home.component.Greeting
import moe.caffeine.fridgehero.ui.screen.home.component.stats.HeroStats

@Composable
fun Home(
  profile: Profile,
  foodItemsFlow: StateFlow<List<FoodItem>>,
  recipesFlow: StateFlow<List<Recipe>>,
  emitEvent: (Event) -> Unit
) {
  val scrollState = rememberScrollState()
  val foodItems by foodItemsFlow.collectAsStateWithLifecycle()
  val recipes by recipesFlow.collectAsStateWithLifecycle()
  var nutrimentBreakdown: NutrimentBreakdown? by remember { mutableStateOf(null) }
  LaunchedEffect(foodItems) {
    Event.RequestNutrimentBreakdown(
      foodItems.filterNot { it.isRemoved }
    ) {
      onSuccess {
        nutrimentBreakdown = it
      }
    }.apply(emitEvent)
  }
  Column(
    modifier = Modifier
      .verticalScroll(scrollState)
      .fillMaxSize()
      .padding(8.dp),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.Start
  ) {
    Greeting(profile.firstName)
    ExpiringSoon(foodItemsFlow) { item ->
      Event.RequestItemSheet(item) {
        onSuccess {
          Event.UpsertFoodItem(it).apply(emitEvent)
        }
      }.apply(emitEvent)
    }
    AvailableRecipes(recipesFlow) { recipe ->
      Event.RequestRecipeEditor(recipe) {
        onSuccess {
          Event.UpsertRecipe(it).apply(emitEvent)
        }
      }.apply(emitEvent)
    }
    HeroStats(foodItems, recipes, nutrimentBreakdown)
  }
}
