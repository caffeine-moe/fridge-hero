package moe.caffeine.fridgehero.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingOverlay(
  visible: Boolean = false,
  enter: EnterTransition = fadeIn(animationSpec = tween(durationMillis = 600)) +
          scaleIn(initialScale = 0.5f, animationSpec = tween(durationMillis = 600)),
  exit: ExitTransition = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(durationMillis = 500)
  ) + fadeOut(animationSpec = tween(durationMillis = 500)),
  showProgressIndicator: Boolean = true,
  content: @Composable () -> Unit = {
    Row {
      Icon(
        imageVector = Icons.Default.Kitchen,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.size(16.dp))
      Text(
        text = "Fridge Hero.",
        style = MaterialTheme.typography.headlineMedium
      )
    }
  }
) {
  AnimatedVisibility(
    visible = visible,
    enter = enter,
    exit = exit,
    modifier = Modifier.fillMaxSize()
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      content()
      if (showProgressIndicator) {
        Spacer(modifier = Modifier.height(32.dp))
        CircularProgressIndicator()
      }
    }
  }
}
