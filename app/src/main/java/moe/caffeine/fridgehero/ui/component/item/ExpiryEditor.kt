package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.mapper.daysUntil
import moe.caffeine.fridgehero.domain.mapper.toReadableDate
import moe.caffeine.fridgehero.ui.component.ActionableSwipeToDismissBox

@Composable
fun ExpiryEditor(
  expiryDates: List<Long>,
  readOnly: Boolean = false,
  small: Boolean = false,
  onShowMore: () -> Unit,
  onRequestExpiry: suspend () -> Result<Long>,
  onListChanged: (List<Long>) -> Unit
) {
  val scope = rememberCoroutineScope()
  val currentExpiryDates by rememberUpdatedState(expiryDates)
  Column(Modifier.padding(8.dp)) {
    Row(Modifier.fillMaxWidth()) {
      Box(Modifier.fillMaxWidth()) {
        Text(
          modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(8.dp),
          text = "Expiry",
          style = MaterialTheme.typography.titleMedium
        )
        TextButton(
          modifier = Modifier.align(Alignment.CenterEnd),
          enabled = !readOnly,
          onClick = {
            scope.launch {
              onRequestExpiry().onSuccess { date ->
                onListChanged(currentExpiryDates + date)
              }
            }
          }
        ) {
          Icon(
            Icons.Filled.Add,
            "Add Expiry Date",
          )
        }
      }
    }
    Column(
      Modifier
        .fillMaxWidth()
        .align(Alignment.CenterHorizontally)
        .animateContentSize()
    ) {
      (if (!small) currentExpiryDates else currentExpiryDates.take(3)).forEach { expiryDate ->
        val expired by remember { mutableStateOf(expiryDate.daysUntil() <= -1) }
        Box(Modifier.padding(8.dp)) {
          ActionableSwipeToDismissBox(
            onStartToEndAction = {
              if (!readOnly) {
                onListChanged(currentExpiryDates + expiryDate)
              }
            },
            enableEndToStartDismiss = false,
            onEndToStartAction = {
              if (!readOnly) {
                onListChanged(currentExpiryDates - expiryDate)
              }
            },
          ) {
            Card {
              Column(
                Modifier
                  .fillMaxWidth()
                  .padding(8.dp)
              ) {
                Text(
                  softWrap = true,
                  style = MaterialTheme.typography.titleLarge,
                  fontWeight = FontWeight.Bold,
                  text = expiryDate.toReadableDate()
                )
                Spacer(Modifier.size(4.dp))
                Text(
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  text = if (!expired) "${expiryDate.daysUntil() + 1}d left" else "Expired.",
                  color = if (expired) Color.Red else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }
      if (currentExpiryDates.size > 3) {
        TextButton(
          onClick = onShowMore
        ) {
          Text("Show ${if (small) "More..." else "Less"}")
        }
      }
    }
  }
}
