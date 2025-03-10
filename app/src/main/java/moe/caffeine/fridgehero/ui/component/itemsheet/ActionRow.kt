package moe.caffeine.fridgehero.ui.component.itemsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ActionRow(
  saveResetDismissActions: List<() -> Unit>,
) {
  Row {
    Box(
      Modifier
        .fillMaxWidth(),
      contentAlignment = Alignment.Center
    ) {
      TextButton(
        modifier = Modifier.align(Alignment.CenterStart),
        onClick = {
          saveResetDismissActions[2]()
        }) {
        Text("Dismiss")
      }
      TextButton(
        onClick = {
          saveResetDismissActions[1]()
        }) {
        Text("Reset")
      }
      TextButton(
        modifier = Modifier.align(Alignment.CenterEnd),
        onClick = {
          saveResetDismissActions[0]()
        }
      ) {
        Text("Save")
      }
    }
  }
}
