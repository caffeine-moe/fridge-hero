package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.helper.isExpired
import moe.caffeine.fridgehero.domain.helper.readableDaysUntil
import moe.caffeine.fridgehero.domain.helper.toReadableDate
import moe.caffeine.fridgehero.ui.component.ActionableSwipeToDismissBox

@Composable
fun ExpiryEditor(
  expiryDates: List<Long>,
  readOnly: Boolean = false,
  small: Boolean = false,
  onShowMore: () -> Unit,
  onRequestExpiry: suspend (prefill: Long?, handle: Result<Long>.() -> Unit) -> Unit,
  onListChanged: (List<Long>) -> Unit
) {
  val scope = rememberCoroutineScope()
  val currentExpiryDates by rememberUpdatedState(expiryDates)

  fun edit(index: Int, expiryDate: Long) {
    scope.launch {
      onRequestExpiry(expiryDate) {
        onSuccess {
          onListChanged(currentExpiryDates.toMutableList().apply { set(index, it) })
        }
      }
    }
  }

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
              onRequestExpiry(null) {
                onSuccess { date ->
                  onListChanged(currentExpiryDates + date)
                }
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
      (if (!small) currentExpiryDates else currentExpiryDates.take(3)).forEachIndexed { index, expiryDate ->
        key(expiryDate, index) {
          Box(Modifier.padding(8.dp)) {
            ActionableSwipeToDismissBox(
              modifier = Modifier.clickable {
                edit(index, expiryDate)
              },
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
                Box(Modifier.fillMaxWidth()) {
                  Column(
                    Modifier
                      .fillMaxWidth()
                      .padding(8.dp)
                  ) {
                    Text(
                      softWrap = true,
                      style = MaterialTheme.typography.titleLarge,
                      fontWeight = FontWeight.Bold,
                      text = expiryDate.toReadableDate().takeUnless { expiryDate == -1L }
                        ?: "Never."
                    )
                    if (expiryDate != -1L) {
                      Spacer(Modifier.size(4.dp))
                      Text(
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        text = expiryDate.readableDaysUntil(),
                        color = if (expiryDate.isExpired()) Color.Red else MaterialTheme.colorScheme.onSurface
                      )
                    }
                  }
                  Row(
                    Modifier
                      .align(Alignment.CenterEnd)
                      .padding(8.dp)
                  ) {
                    IconButton(
                      onClick = {
                        edit(index, expiryDate)
                      }
                    ) {
                      Icon(
                        Icons.Default.Edit,
                        "Edit Expiry Date"
                      )
                    }
                    IconButton(
                      onClick = {
                        onListChanged(currentExpiryDates - expiryDate)
                      }
                    ) {
                      Icon(
                        Icons.Default.Delete,
                        "Delete Expiry Date"
                      )
                    }
                  }
                }
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
