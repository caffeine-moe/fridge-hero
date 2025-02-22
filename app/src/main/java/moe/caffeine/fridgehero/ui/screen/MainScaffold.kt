package moe.caffeine.fridgehero.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.ui.navigation.BottomNavBar
import moe.caffeine.fridgehero.ui.navigation.BottomNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
  navController: NavHostController,
  navBarItems: List<Screen>,
  onPaddingCreated: (PaddingValues) -> Unit,
  profile: Profile,
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit
) {
  var destination by rememberSaveable { mutableStateOf("Home") }
  var navLeft by rememberSaveable { mutableStateOf(false) }
  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .systemBarsPadding(),
    bottomBar = {
      BottomNavBar(
        navController,
        navBarItems
      ) { title, left ->
        destination = title
        navLeft = left
      }
    },
    topBar = {
      TopAppBar(
        modifier = Modifier
          .background(Color.Black),
        title = {
          Text(
            destination,
            style = MaterialTheme.typography.headlineLarge
          )
        },
      )
    }
  ) { paddingValues ->
    onPaddingCreated(paddingValues)
    Column(
      Modifier.padding(paddingValues)
    ) {
      BottomNavGraph(
        navController,
        navLeft,
        profile,
        foodItems,
        emitEvent
      )
    }
  }
}
