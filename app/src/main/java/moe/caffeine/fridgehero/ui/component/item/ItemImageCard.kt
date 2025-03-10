package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.FoodItem

@Composable
fun ItemImageCard(
  modifier: Modifier,
  item: FoodItem,
) {
  val imageBitmap by remember(item) {
    derivedStateOf {
      if (item.imageByteArray.isNotEmpty()) BitmapPainter(
        item.imageBitmap
      )
      else
        null
    }
  }
  Card(
    modifier = modifier,
    border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer),
    elevation = CardDefaults.elevatedCardElevation()
  ) {
    Image(
      colorFilter = if (imageBitmap == null) {
        ColorFilter.tint(MaterialTheme.colorScheme.outlineVariant)
      } else null,
      modifier = modifier
        .clip(MaterialTheme.shapes.medium)
        .fillMaxSize(),
      painter = imageBitmap ?: rememberVectorPainter(Icons.Outlined.NoFood),
      alignment = Alignment.Center,
      contentDescription = "Image of ${item.name}",
      contentScale = ContentScale.FillBounds
    )
  }
}
