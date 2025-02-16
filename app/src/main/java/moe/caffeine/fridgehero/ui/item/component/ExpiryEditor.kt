package moe.caffeine.fridgehero.ui.item.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.mapper.daysUntil
import moe.caffeine.fridgehero.domain.mapper.toReadableDate
import moe.caffeine.fridgehero.ui.component.ActionableSwipeToDismissBox

@Composable
fun ExpiryEditor(
  expiryDates: List<Long>,
  readOnly: Boolean = false,
  onRequestExpiry: suspend () -> Result<Long>,
  onListChanged: (List<Long>) -> Unit
) {
  val scope = rememberCoroutineScope()
  var current by remember { mutableStateOf(expiryDates) }
  //val firstThreeExpiryDates = expiryDates.take(3)
  Column {
    TextButton(
      enabled = !readOnly,
      onClick = {
        scope.launch {
          onRequestExpiry().onSuccess { date ->
            current = current.toMutableList() + date
            onListChanged(current)
          }
        }
      }
    ) {
      Text("Add Expiry Date")
    }
    Card(
      Modifier
        .fillMaxWidth()
        .border(
          BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer),
          RoundedCornerShape(16.dp)
        )
    ) {
      Column(
        Modifier
          .fillMaxWidth()
          .align(Alignment.CenterHorizontally)
          .animateContentSize()
          .defaultMinSize(minHeight = 80.dp)
      ) {
        current.forEach { expiryDate ->
          ActionableSwipeToDismissBox(
            modifier = Modifier
              .fillMaxWidth()
              .height(80.dp)
              .padding(4.dp),
            onStartToEndAction = {
              if (!readOnly) {
                current = current.toMutableList() + expiryDate
                onListChanged(
                  current
                )
              }
            },
            enableEndToStartDismiss = false,
            onEndToStartAction = {
              if (!readOnly) {
                current = current.toMutableList() - expiryDate
                onListChanged(
                  current
                )
              }
            },
          ) {
            Card(
              Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .border(
                  BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer),
                  RoundedCornerShape(16.dp)
                )
            ) {
              Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
              ) {
                Text(
                  text = "Expires: ${expiryDate.toReadableDate()} (${expiryDate.daysUntil()} days)"
                )
              }
            }
          }
        }
      }
    }
  }
}
