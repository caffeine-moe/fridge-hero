package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.theme.Typography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
  modifier: Modifier,
  item: FoodItem,
  onLongPress: () -> Unit,
  expandedContent: @Composable () -> Unit
) {
  var expanded by rememberSaveable { mutableStateOf(false) }
  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .combinedClickable(
        onClick = {
          expanded = !expanded
        },
        onLongClick = onLongPress
      ),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .align(Alignment.CenterHorizontally)
    ) {
      Row {
        ItemImageCard(
          Modifier
            .size(80.dp)
            .align(Alignment.CenterVertically),
          item
        )
        Spacer(Modifier.padding(10.dp))
        Column {
          Text(
            style = Typography.titleMedium,
            text = item.name
          )
          if (item.brand.isNotEmpty()) {
            Text(
              style = Typography.labelLarge,
              text = "Brand: ${item.brand}",
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          if (item.categories.isNotEmpty()) {
            Text(
              style = Typography.labelLarge,
              text = "Categorisation: ${item.categories.first()}",
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
      AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(
          animationSpec = tween(500)
        ) + fadeIn(tween(500)),
        exit = shrinkVertically(
          animationSpec = tween(500)
        ) + fadeOut(tween(500))
      ) {
        Column(
          Modifier
            .align(Alignment.Start)
            .fillMaxWidth()
        ) {
          expandedContent()
        }
      }
    }
  }
}
