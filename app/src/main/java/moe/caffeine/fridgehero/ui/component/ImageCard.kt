package moe.caffeine.fridgehero.ui.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ImageCard(
  modifier: Modifier = Modifier,
  imageByteArray: ByteArray = byteArrayOf(),
  contentScale: ContentScale = ContentScale.FillBounds
) {
  val imageBitmap by remember(imageByteArray) { derivedStateOf { imageByteArray } }

  Card(
    modifier = modifier,
    border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondaryContainer),
    elevation = CardDefaults.elevatedCardElevation()
  ) {
    AsyncImage(
      model = imageBitmap,
      contentDescription = null,
      modifier = Modifier.fillMaxSize(),
      contentScale = contentScale,
      placeholder = rememberVectorPainter(Icons.Outlined.NoFood),
      error = rememberVectorPainter(Icons.Outlined.NoFood),
      colorFilter = if (imageByteArray.isEmpty()) {
        ColorFilter.tint(MaterialTheme.colorScheme.outlineVariant)
      } else null
    )
  }
}
