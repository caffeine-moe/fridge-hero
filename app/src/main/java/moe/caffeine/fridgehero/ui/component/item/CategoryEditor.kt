package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryEditor(
  categories: List<String>,
  onListChanged: (List<String>) -> Unit
) {
  val latestCategories by rememberUpdatedState(newValue = categories)
  FlowRow(
    Modifier
      .padding(8.dp)
      .animateContentSize()
      .fillMaxWidth()
  ) {
    latestCategories.forEach { category ->
      Box(Modifier.padding(2.dp)) {
        InputChip(
          selected = false,
          modifier = Modifier.height(30.dp),
          label = {
            Text(category)
          },
          trailingIcon = {
            IconButton(
              modifier = Modifier.size(16.dp),
              onClick = {
                onListChanged(latestCategories - category)
              }
            ) {
              Icon(
                Icons.Outlined.RemoveCircleOutline,
                "Remove Category"
              )
            }
          },
          onClick = {

          }
        )
      }
    }
    Box(Modifier.padding(2.dp)) {
      InputChip(
        modifier = Modifier.height(30.dp),
        label = {
          Icon(
            modifier = Modifier.size(16.dp),
            imageVector = Icons.Filled.Add,
            contentDescription = null
          )
        },
        onClick = {

        },
        selected = false
      )
    }
  }
}
