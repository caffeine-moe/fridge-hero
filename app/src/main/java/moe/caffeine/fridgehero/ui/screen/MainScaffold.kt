package moe.caffeine.fridgehero.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.navigation.BottomNavBar
import moe.caffeine.fridgehero.ui.navigation.BottomNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
  screens: List<Screen> = listOf(),
  onPaddingCreated: (PaddingValues) -> Unit = {},
  profile: Profile,
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit
) {
  val navController: NavHostController = rememberNavController()
  val scope = rememberCoroutineScope()
  var title by rememberSaveable { mutableStateOf(Screen.Home.title) }
  var navigatedLeft by rememberSaveable { mutableStateOf(false) }
  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .systemBarsPadding(),
    topBar = {
      TopAppBar(
        title = {
          Text(
            title,
            style = MaterialTheme.typography.headlineLarge
          )
        }
      )
    },
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
              when (title) {
                Screen.Fridge.title -> Event.RequestItemSheet()
                  .apply(emitEvent).result.await()
                  .onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }

                Screen.Recipes.title -> {
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
      BottomNavBar(
        navController,
        screens,
        onNavigate = { navigatedScreen, left ->
          title = navigatedScreen.title
          navigatedLeft = left
        }
      )
    },
  ) { paddingValues ->
    onPaddingCreated(paddingValues)
    Column(
      Modifier.padding(paddingValues)
    ) {
      BottomNavGraph(
        navController,
        navigatedLeft,
        profile,
        foodItems,
        emitEvent
      )
    }
  }
}
