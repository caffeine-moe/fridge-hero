package moe.caffeine.fridgehero.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.fridge.Fridge
import moe.caffeine.fridgehero.ui.home.Home
import moe.caffeine.fridgehero.ui.recipe.Recipes

@Composable
fun BottomNavGraph(
  navController: NavHostController,
  profile: Profile,
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit
) {
  NavHost(
    navController = navController,
    startDestination = Screen.Home.route
  ) {
    composable(Screen.Home.route) {
      Home(profile)
    }
    composable(Screen.Fridge.route) {
      Fridge(foodItems, emitEvent)
    }
    composable(Screen.Recipes.route) {
      Recipes()
    }
  }
}
