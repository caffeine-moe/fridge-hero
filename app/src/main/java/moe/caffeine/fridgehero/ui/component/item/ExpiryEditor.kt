package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
  small: Boolean = false,
  onShowMore: () -> Unit,
  onRequestExpiry: suspend () -> Result<Long>,
  onListChanged: (List<Long>) -> Unit
) {
  val scope = rememberCoroutineScope()
  var editableExpiryDates by rememberSaveable { mutableStateOf(expiryDates) }
  Column(Modifier.padding(8.dp)) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
      TextButton(
        enabled = !readOnly,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = {
          scope.launch {
            onRequestExpiry().onSuccess { date ->
              editableExpiryDates += date
            }
          }
        }
      ) {
        Icon(
          Icons.Filled.Add,
          "Add Expiry Date",
        )
        Text("Add Expiry Date")
      }
    }
    Card(
      Modifier
        .fillMaxWidth()
        .padding(top = 8.dp)
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
        (if (!small) editableExpiryDates else editableExpiryDates.take(3)).forEach { expiryDate ->
          ActionableSwipeToDismissBox(
            modifier = Modifier
              .fillMaxWidth()
              .defaultMinSize(minHeight = 80.dp)
              .padding(4.dp),
            onStartToEndAction = {
              if (!readOnly) {
                editableExpiryDates += expiryDate
              }
            },
            enableEndToStartDismiss = false,
            onEndToStartAction = {
              if (!readOnly) {
                editableExpiryDates -= expiryDate
              }
            },
          ) {
            Card(
              Modifier
                .fillMaxWidth()
                .padding(8.dp)
            ) {
              Box(
                Modifier
                  .fillMaxSize()
                  .defaultMinSize(minHeight = 80.dp)
                  .padding(8.dp)
                  .border(
                    BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer),
                    RoundedCornerShape(16.dp)
                  ),
                contentAlignment = Alignment.Center,
              ) {
                Text(
                  softWrap = true,
                  modifier = Modifier.padding(8.dp),
                  style = MaterialTheme.typography.bodyLarge,
                  text = "Expires: ${expiryDate.toReadableDate()}\n(${expiryDate.daysUntil()} days)"
                )
              }
            }
          }
        }
        if (editableExpiryDates.size > 3) {
          TextButton(
            onClick = onShowMore
          ) {
            Text("Show ${if (small) "More..." else "Less"}")
          }
        }
      }
    }
  }

  LaunchedEffect(expiryDates) {
    if (expiryDates != editableExpiryDates) {
      editableExpiryDates = expiryDates
    }
  }

  LaunchedEffect(editableExpiryDates) {
    if (editableExpiryDates != expiryDates) {
      onListChanged(editableExpiryDates)
    }
  }
}
