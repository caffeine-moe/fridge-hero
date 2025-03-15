package moe.caffeine.fridgehero.ui.screen.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.screen.Screen
import moe.caffeine.fridgehero.ui.screen.main.component.MainBottomBar
import moe.caffeine.fridgehero.ui.screen.main.component.MainTopBar

@Composable
fun MainScaffold(
  screens: List<Screen> = listOf(),
  profile: Profile,
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit
) {
  val navController: NavHostController = rememberNavController()
  val scope = rememberCoroutineScope()
  var currentScreenIndex by rememberSaveable { mutableIntStateOf(0) }
  val title = screens.getOrNull(currentScreenIndex)?.title ?: ""
  var navigatedLeft by rememberSaveable { mutableStateOf(false) }

  Scaffold(
    modifier = Modifier
      .systemBarsPadding()
      .fillMaxSize(),
    topBar = { MainTopBar(currentScreenIndex, title) },
    floatingActionButton = {
      AnimatedVisibility(
        screens.any { it.hasFloatingActionButton && it.title == title },
        enter = slideInHorizontally(tween(500), initialOffsetX = { 2 * it }),
        exit = slideOutHorizontally(tween(500), targetOffsetX = { 2 * it })
      ) {
        FloatingActionButton(
          modifier = Modifier.padding(8.dp),
          onClick = {
            scope.launch {
              when (currentScreenIndex) {
                //fridge
                1 -> Event.RequestItemSheet()
                  .apply(emitEvent).result.await()
                  .onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }

                //recipes
                2 -> {
                  //
                }
              }
            }
          }
        ) {
          Icon(Icons.Filled.Add, "")
        }
      }
    },
    bottomBar = {
      MainBottomBar(
        navController,
        screens,
        currentScreenIndex,
        onNavigate = { navigatedIndex ->
          navigatedLeft = navigatedIndex < currentScreenIndex
          currentScreenIndex = navigatedIndex
        }
      )
    },
  ) { paddingValues ->
    //screens compose in here
    Column(
      Modifier.padding(paddingValues)
    ) {
      MainNavGraph(
        navController,
        navigatedLeft,
        profile,
        foodItems,
        emitEvent
      )
    }
  }
}
