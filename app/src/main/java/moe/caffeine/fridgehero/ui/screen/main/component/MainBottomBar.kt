package moe.caffeine.fridgehero.ui.screen.main.component

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.ui.screen.Screen

@Composable
fun MainBottomBar(
  navBarItems: List<Screen>,
  currentIndex: Int,
  onNavigate: (screen: Screen, index: Int) -> Unit
) {
  ElevatedCard(
    shape = MaterialTheme.shapes.medium.copy(
      bottomStart = CornerSize(0.dp),
      bottomEnd = CornerSize(0.dp)
    ),
    modifier = Modifier
      .height(80.dp)
  ) {
    Row {
      BackHandler(enabled = true) { }
      navBarItems.forEachIndexed { index, screen ->
        NavigationBarItem(
          modifier = Modifier
            .fillMaxSize(),
          selected = currentIndex == index,
          onClick = {
            if (currentIndex == index) return@NavigationBarItem
            onNavigate(screen, index)
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
}
