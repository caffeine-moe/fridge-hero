package moe.caffeine.fridgehero.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LibraryAddCheck
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ActionableSwipeToDismissBox(
  modifier: Modifier = Modifier,
  enableStartToEndDismiss: Boolean = false,
  onStartToEndAction: () -> Unit = {},
  startToEndIcon: ImageVector? = Icons.Filled.LibraryAddCheck,
  startToEndColor: Color = Color.Green,
  enableEndToStartDismiss: Boolean = true,
  onEndToStartAction: () -> Unit = {},
  endToStartIcon: ImageVector? = Icons.Filled.Delete,
  endToStartColor: Color = Color.Red,
  content: @Composable () -> Unit,
) {
  val state = rememberSwipeToDismissBoxState(
    confirmValueChange = { value ->
      when (value) {
        StartToEnd -> {
          onStartToEndAction()
          enableStartToEndDismiss
        }

        EndToStart -> {
          onEndToStartAction()
          enableEndToStartDismiss
        }

        else -> true
      }
    }
  )
  SwipeToDismissBox(
    modifier = modifier,
    state = state,
    backgroundContent = {
      val colour = when (state.dismissDirection) {
        StartToEnd -> startToEndColor
        EndToStart -> endToStartColor
        else -> Color.Transparent
      }
      Box(
        modifier = modifier
          .fillMaxSize()
          .clip(MaterialTheme.shapes.medium)
          .background(colour),
      ) {
        when (state.dismissDirection) {
          StartToEnd -> {
            startToEndIcon?.let {
              Icon(
                imageVector = startToEndIcon,
                modifier = Modifier.align(Alignment.CenterStart),
                contentDescription = "Swipe to add new"
              )
            }
          }

          EndToStart -> {
            endToStartIcon?.let {
              Icon(
                imageVector = endToStartIcon,
                modifier = Modifier.align(Alignment.CenterEnd),
                contentDescription = "Swipe to remove"
              )
            }
          }

          else -> {}
        }
      }
    }
  ) {
    content()
  }
}
