package moe.caffeine.fridgehero.ui.screen.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.ui.component.CustomSearchBar
import moe.caffeine.fridgehero.ui.screen.Screen

@Composable
fun MainTopBar(
  screens: List<Screen>,
  currentScreenIndex: Int,
  searchBarQuery: String,
  onSearchBarFocusState: (FocusState) -> Unit,
  onQueryChanged: (String) -> Unit
) {
  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 64.dp),
    shape = MaterialTheme.shapes.medium.copy(
      topStart = CornerSize(0.dp),
      topEnd = CornerSize(0.dp)
    ),
  ) {
    Row(
      Modifier
        .fillMaxWidth()
        .height(64.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      screens.forEachIndexed { index, screen ->
        Box(
          modifier = Modifier
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          androidx.compose.animation.AnimatedVisibility(
            visible = currentScreenIndex == index,
            enter = slideInVertically(tween(500)) + fadeIn(tween(500)),
            exit = slideOutVertically(tween(500)) + fadeOut(tween(500))
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = screen.title,
                style = MaterialTheme.typography.headlineSmall,
              )
              Box(
                Modifier
                  .width(15.dp)
                  .height(3.dp)
                  .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
              )
            }
          }
        }
      }
    }
    AnimatedVisibility(
      visible = screens[currentScreenIndex].hasSearchBar,
      enter = expandVertically(tween(250), expandFrom = Alignment.Top) + fadeIn(tween(250)),
      exit = shrinkVertically(tween(250), shrinkTowards = Alignment.Top) + fadeOut(tween(250))
    ) {
      CustomSearchBar(
        query = searchBarQuery,
        onFocusState = {
          onSearchBarFocusState(it)
        }
      ) {
        onQueryChanged(it)
      }
    }
  }
}
