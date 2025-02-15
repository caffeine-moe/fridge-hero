package moe.caffeine.fridgehero.ui.item.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.ui.components.ActionableSwipeToDismissBox

@Composable
fun ExpiryEditor(
  expiryDates: List<Long>,
  readOnly: Boolean = false,
  onExpiryAddRequest: () -> Unit = {},
  onExpiryDuplicateRequest: (Long) -> Unit = {},
  onExpiryRemoveRequest: (Long) -> Unit = {},
) {
  Column {
    TextButton(
      enabled = !readOnly,
      onClick = onExpiryAddRequest
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
        for (expiryDate in expiryDates) {
          ActionableSwipeToDismissBox(
            modifier = Modifier
              .fillMaxWidth()
              .height(80.dp)
              .padding(4.dp),
            onStartToEndAction = {
              if (!readOnly)
                onExpiryDuplicateRequest(expiryDate)
            },
            enableEndToStartDismiss = false,
            onEndToStartAction = {
              if (!readOnly)
                onExpiryRemoveRequest(expiryDate)
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
                  text = "Expires: $expiryDate"
                )
              }
            }
          }
        }
      }
    }
  }
}
