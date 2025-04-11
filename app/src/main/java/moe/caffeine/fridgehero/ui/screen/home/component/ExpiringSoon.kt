package moe.caffeine.fridgehero.ui.screen.home.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.helper.readableDaysUntil
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.component.ImageCard
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun ExpiringSoon(
  items: StateFlow<List<FoodItem>>,
  onItemClick: (FoodItem) -> Unit
) {
  val fridge by items.collectAsStateWithLifecycle()
  val filteredFridge by remember {
    derivedStateOf {
      fridge
        .filter { it.expiresSoon || it.isExpired }
        .sortedBy { foodItem -> foodItem.realExpiryDates.min() }
    }
  }
  if (fridge.isEmpty())
    return
  val scrollState = rememberScrollState()
  Text(
    modifier = Modifier.padding(vertical = 8.dp),
    style = Typography.titleMedium,
    text = "Expiring Soon"
  )
  Row(
    modifier = Modifier.horizontalScroll(scrollState)
  ) {
    filteredFridge
      .forEach { item ->
        val date = item.expiryDates.min()
        ElevatedCard(
          modifier = Modifier
            .padding(8.dp),
          onClick = { onItemClick(item) }
        ) {
          Box(
            Modifier
              .align(Alignment.CenterHorizontally)
          ) {
            ImageCard(
              modifier = Modifier
                .size(120.dp)
                .padding(4.dp),
              imageByteArray = item.imageByteArray
            )
          }
          Spacer(Modifier.width(10.dp))
          Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = item.name
          )
          Spacer(Modifier.width(10.dp))
          Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Expires: ${date.readableDaysUntil()}"
          )
          Spacer(Modifier.width(10.dp))
        }
      }
  }
}
