package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.theme.Typography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemCard(
  item: FoodItem,
  onLongPress: () -> Unit,
  expandedContent: @Composable () -> Unit
) {
  var expanded by remember { mutableStateOf(false) }
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
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .clip(RoundedCornerShape(16.dp))
      .animateContentSize()
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
        Image(
          modifier = Modifier
            .size(80.dp, 80.dp)
            .clip(RoundedCornerShape(16.dp)),
          painter = imageBitmap ?: painterResource(
            R.drawable.ic_launcher_background
          ),
          alignment = Alignment.CenterStart,
          contentDescription = "Image of ${item.name}",
          contentScale = ContentScale.Crop
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
      if (expanded) {
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
