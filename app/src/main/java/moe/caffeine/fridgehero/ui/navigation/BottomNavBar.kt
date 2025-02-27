package moe.caffeine.fridgehero.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import moe.caffeine.fridgehero.ui.screen.Screen

@Composable
fun BottomNavBar(
  navController: NavController,
  navBarItems: List<Screen>,
  onNavigate: (navigatedScreen: Screen, navigatedLeft: Boolean) -> Unit
) {
  var currentIndex by rememberSaveable { mutableIntStateOf(0) }
  BottomAppBar(
    modifier = Modifier
      .systemBarsPadding(),
    contentPadding = PaddingValues(10.dp),
  ) {
    BackHandler(enabled = true) { }
    navBarItems.forEachIndexed { index, screen ->
      NavigationBarItem(
        modifier = Modifier
          .fillMaxSize(),
        selected = currentIndex == index,
        onClick = {
          if (currentIndex == index) return@NavigationBarItem
          navController.popBackStack()
          navController.navigate(screen.route)
          onNavigate(screen, index < currentIndex)
          currentIndex = index
        },
        icon = {
          Box(
            Modifier
              .align(Alignment.CenterVertically)
              .wrapContentSize(),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              if (currentIndex == index) {
                Image(
                  imageVector = screen.selectedIcon,
                  contentDescription = "${screen.title} Button",
                  colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
              } else {
                Image(
                  imageVector = screen.unselectedIcon,
                  "${screen.title} Button",
                  colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
              }
            }
          }
        },
        label = {
          AnimatedVisibility(
            visible = currentIndex != index,
            enter = expandVertically(
              animationSpec = tween(500)
            ) + fadeIn(tween(250)),
            exit = shrinkVertically(
              animationSpec = tween(500)
            ) + fadeOut(tween(250))
          ) {
            Text(
              color = MaterialTheme.colorScheme.onSurface,
              text = screen.title,
              style = MaterialTheme.typography.labelSmall
            )
          }
        }
      )
    }
  }
}
