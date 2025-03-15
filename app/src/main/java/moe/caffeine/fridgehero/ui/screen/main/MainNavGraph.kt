package moe.caffeine.fridgehero.ui.screen.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.screen.Screen
import moe.caffeine.fridgehero.ui.screen.fridge.Fridge
import moe.caffeine.fridgehero.ui.screen.home.Home
import moe.caffeine.fridgehero.ui.screen.recipe.Recipes

@Composable
fun MainNavGraph(
  navController: NavHostController,
  navigatedLeft: Boolean,
  profile: Profile,
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit
) {
  val slideDirection by remember(navigatedLeft) {
    derivedStateOf {
      if (navigatedLeft) {
        AnimatedContentTransitionScope.SlideDirection.Right
      } else AnimatedContentTransitionScope.SlideDirection.Left
    }
  }
  NavHost(
    navController = navController,
    startDestination = Screen.Home.route,
    enterTransition = {
      slideIntoContainer(
        slideDirection,
        animationSpec = tween(500)
      ) + fadeIn(tween(250))
    },
    exitTransition = {
      slideOutOfContainer(
        slideDirection,
        animationSpec = tween(500)
      ) + fadeOut(tween(250))
    },
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
