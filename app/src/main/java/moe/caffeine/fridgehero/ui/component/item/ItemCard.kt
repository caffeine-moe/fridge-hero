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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.theme.Typography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
  item: FoodItem,
  onLongPress: () -> Unit,
  expandedContent: @Composable () -> Unit
) {
  var expanded by rememberSaveable { mutableStateOf(false) }
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .clip(RoundedCornerShape(16.dp))
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
    ) {
      Row {
        ItemImageCard(
          Modifier.size(80.dp),
          item
        )
        Spacer(Modifier.padding(10.dp))
        Column {
          Text(
            style = Typography.titleMedium,
            text = item.name
          )
          Text(
            style = Typography.labelLarge,
            text = item.brand,
            color = Color.LightGray
          )
        }
      }
      AnimatedVisibility(
        visible = expanded,
        enter = fadeIn(tween(500)) + expandVertically(),
        exit = fadeOut(tween(250)) + shrinkVertically()
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
