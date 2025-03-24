package moe.caffeine.fridgehero.ui.component

import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun ImageCard(
  modifier: Modifier = Modifier,
  imageByteArray: ByteArray = byteArrayOf(),
  contentScale: ContentScale = ContentScale.FillBounds
) {
  val imageBitmap by remember(imageByteArray) {
    derivedStateOf {
      if (imageByteArray.isNotEmpty()) BitmapPainter(
        BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
          .asImageBitmap()
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
        .fillMaxSize(),
      painter = imageBitmap ?: rememberVectorPainter(Icons.Outlined.NoFood),
      alignment = Alignment.Center,
      contentDescription = null,
      contentScale = contentScale
    )
  }
}
