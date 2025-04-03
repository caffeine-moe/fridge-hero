package moe.caffeine.fridgehero.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
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
  recipesFlow: StateFlow<List<Recipe>>
) {
  val scrollState = rememberScrollState()
  Column(
    modifier = Modifier
      .padding(10.dp)
      .verticalScroll(scrollState)
      .fillMaxSize(),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.Start
  ) {
    Greeting(profile.firstName)
    ExpiringSoon(foodItemsFlow)
    AvailableRecipes(recipesFlow)
    HeroStats(foodItemsFlow, recipesFlow)
  }
}
