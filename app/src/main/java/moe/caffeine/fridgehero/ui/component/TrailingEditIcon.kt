package moe.caffeine.fridgehero.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TrailingEditIcon(visible: Boolean = true) {
  if (visible) {
    Icon(
      Icons.Outlined.Edit,
      null,
      Modifier.size(16.dp)
    )
  }
}
