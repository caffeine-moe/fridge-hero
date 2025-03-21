package moe.caffeine.fridgehero.ui.component

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
  saveResetDismissActions: List<Pair<String, () -> Unit>>,
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
          saveResetDismissActions[2].second()
        }) {
        Text(saveResetDismissActions[2].first)
      }
      TextButton(
        onClick = {
          saveResetDismissActions[1].second()
        }) {
        Text(saveResetDismissActions[1].first)
      }
      TextButton(
        modifier = Modifier.align(Alignment.CenterEnd),
        onClick = {
          saveResetDismissActions[0].second()
        }
      ) {
        Text(saveResetDismissActions[0].first)
      }
    }
  }
}
