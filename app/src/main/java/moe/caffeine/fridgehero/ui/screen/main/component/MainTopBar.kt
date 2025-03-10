package moe.caffeine.fridgehero.ui.screen.main.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

@Composable
fun MainTopBar(
  currentScreenIndex: Int,
  title: String
) {
  ElevatedCard(
    modifier = Modifier
      .fillMaxWidth()
      .height(64.dp),
    shape = MaterialTheme.shapes.medium.copy(
      topStart = CornerSize(0.dp),
      topEnd = CornerSize(0.dp)
    ),
  ) {
    BoxWithConstraints(
      modifier = Modifier
        .fillMaxSize(),
      contentAlignment = Alignment.CenterStart
    ) {
      val density = LocalDensity.current
      val groupWidth by remember { mutableStateOf(maxWidth / 3) }
      var measuredTextWidth by remember { mutableStateOf(0.dp) }
      val targetOffset by remember(currentScreenIndex) {
        derivedStateOf {
          val centerPosition = currentScreenIndex * groupWidth + (groupWidth / 2)
          centerPosition - (measuredTextWidth / 2)
        }
      }
      val animatedOffset by animateDpAsState(
        targetValue = targetOffset,
        animationSpec = tween(durationMillis = 500)
      )
      Column(
        modifier = Modifier
          .offset { IntOffset(animatedOffset.roundToPx(), 0) },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        AnimatedContent(
          targetState = title,
          transitionSpec = {
            fadeIn(tween(500)) togetherWith fadeOut(tween(500))
          },
        ) {
          Text(
            text = it,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
              .onGloballyPositioned { coordinates ->
                measuredTextWidth = with(density) { coordinates.size.width.toDp() }
              }
          )
        }
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
