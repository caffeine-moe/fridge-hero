package moe.caffeine.fridgehero.ui.component.itemsheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FloatingActionBar(
  visible: Boolean = false,
  actions: List<() -> Unit>,
  showScannerButton: Boolean,
  onScannerRequest: () -> Unit
) {
  Box {
    Box(
      Modifier
        .padding(16.dp)
        .width(IntrinsicSize.Min)
        .height(IntrinsicSize.Max),
      contentAlignment = Alignment.Center
    ) {
      Column(
        modifier = Modifier
          .align(Alignment.Center)
          .padding(
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
          )
      ) {
        AnimatedVisibility(
          modifier = Modifier
            .align(Alignment.End),
          visible = visible && showScannerButton,
          enter = slideInVertically(tween(250), initialOffsetY = { 2 * it }) + fadeIn(tween(250)),
          exit = slideOutHorizontally(tween(250), targetOffsetX = { 2 * it }) + fadeOut(tween(250))
        ) {
          Column(Modifier.align(Alignment.End)) {
            ScannerFloatingActionButton(onClick = onScannerRequest)
          }
        }
        Spacer(Modifier.size(8.dp))
        AnimatedVisibility(
          visible = visible,
          enter = slideInVertically(tween(250)) { 2 * it },
          exit = slideOutVertically(tween(250)) { 2 * it }
        ) {
          Snackbar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f),
            shape = MaterialTheme.shapes.extraLarge
          ) {
            Box(
              Modifier
                .fillMaxWidth(),
              contentAlignment = Alignment.Center
            ) {
              ActionRow(actions)
            }
          }
        }
      }
    }
  }
}
