package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.helper.daysUntil
import moe.caffeine.fridgehero.domain.helper.expiryImminent
import moe.caffeine.fridgehero.domain.helper.readableDaysUntil
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.ui.component.ImageCard
import moe.caffeine.fridgehero.ui.theme.Typography
import kotlin.math.abs

@Composable
fun ItemCard(
  modifier: Modifier = Modifier,
  elevation: CardElevation = CardDefaults.elevatedCardElevation(),
  item: FoodItem,
  extraContent: @Composable () -> Unit = {},
  expanded: Boolean = false,
  expandedContent: @Composable () -> Unit = {}
) {
  val tint =
    if (item.isExpired)
      Color.Red
    else if (item.expiresSoon)
      Color.hsl(
        30f,
        1f,
        0.5f
      )
    else CardDefaults.elevatedCardColors().containerColor

  ElevatedCard(
    elevation = elevation,
    modifier = modifier
      .fillMaxWidth()
  ) {
    Box(
      Modifier
        .fillMaxSize()
        .background(tint.copy(alpha = 0.1f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Row {
          Box(
            contentAlignment = Alignment.Center
          ) {
            ImageCard(
              Modifier
                .size(80.dp)
                .align(Alignment.Center),
              item.imageByteArray
            )
            if (item.nutriScore != NutriScore.UNKNOWN) {
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .padding(top = 8.dp)
              ) {
                Image(
                  modifier = Modifier.width(50.dp),
                  painter = item.nutriScoreVectorPainter,
                  contentDescription = "NutriScore"
                )
              }
            }
          }
          Spacer(Modifier.padding(10.dp))
          Column {
            Text(
              style = Typography.titleMedium,
              text = item.name + (
                      " - ${item.brand}".takeIf { item.brand.isNotEmpty() } ?: ""
                      )
            )
            if (item.expiryDates.any { it != -1L } && !item.isExpired) {
              Text(
                style = Typography.labelLarge,
                text = "Expires: ${
                  item.expiryDates.min().readableDaysUntil()
                }",
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            if (item.isExpired) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Warning, null)
                Spacer(Modifier.size(4.dp))
                Text(
                  modifier = Modifier.fillMaxWidth(),
                  softWrap = true,
                  style = MaterialTheme.typography.labelMedium,
                  text = "This item has expired${
                    if (item.expiryDates.min().expiryImminent())
                      " ${
                        abs(
                          item.expiryDates.min().daysUntil()
                        )
                      }d ago, inspect before consumption."
                    else
                      ", you may need to throw it out."
                  }"
                )
              }
            }
            if (item.expiresSoon && !item.isExpired) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Warning, null)
                Spacer(Modifier.size(4.dp))
                Text(
                  softWrap = true,
                  style = MaterialTheme.typography.labelMedium,
                  text = "This item expires soon."
                )
              }
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
        extraContent()
      }
    }
  }
}
