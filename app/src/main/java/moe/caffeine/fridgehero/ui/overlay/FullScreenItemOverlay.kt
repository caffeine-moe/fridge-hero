package moe.caffeine.fridgehero.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.component.item.ItemFull

@Composable
fun FullScreenItemOverlay(
  fullScreenItem: FoodItem?,
  onDismiss: () -> Unit
) {
  AnimatedVisibility(fullScreenItem != null) {
    fullScreenItem?.let {
      ItemFull(
        fullScreenItem,
        onDismiss
      )
    }
  }
}
