package moe.caffeine.fridgehero.ui.screen.home.component.stats


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun PieChart(
  data: Map<Nutriment, Double>,
  itemsCalculated: Int,
  itemsTotal: Int
) {
  val sortedData = data.entries.sortedByDescending { it.value }

  Text(
    modifier = Modifier.padding(8.dp),
    style = Typography.titleMedium,
    text = "NutriVision"
  )
  val totalSum = sortedData.sumOf { it.value }
  val floatValue = mutableListOf<Float>()

  sortedData.forEachIndexed { index, entry ->
    floatValue.add(index, 360 * entry.value.toFloat() / totalSum.toFloat())
  }

  val colours = listOf(
    MaterialTheme.colorScheme.onPrimary,
    MaterialTheme.colorScheme.primaryContainer,
    MaterialTheme.colorScheme.onSecondary,
    MaterialTheme.colorScheme.secondaryContainer,
    MaterialTheme.colorScheme.onTertiary,
    MaterialTheme.colorScheme.tertiaryContainer,
    MaterialTheme.colorScheme.tertiary,
    MaterialTheme.colorScheme.secondary,
    MaterialTheme.colorScheme.inversePrimary
  )

  var lastValue = 0f

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(top = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      contentAlignment = Alignment.Center
    ) {
      Text("For $itemsCalculated of $itemsTotal items.")
      Canvas(
        modifier = Modifier
          .align(Alignment.Center)
          .size(200.dp)
      ) {
        floatValue.forEachIndexed { index, value ->
          drawArc(
            color = colours[index],
            lastValue,
            value,
            useCenter = false,
            style = Stroke(24.dp.toPx(), cap = StrokeCap.Butt)
          )
          lastValue += value
        }
      }
    }

    PieChartDetails(
      data = sortedData,
      colours = colours
    )

  }

}

@Composable
fun PieChartDetails(
  data: List<Map.Entry<Nutriment, Double>>,
  colours: List<Color>
) {
  Column(
    modifier = Modifier
      .padding(top = 24.dp)
      .fillMaxWidth()
  ) {
    data.forEachIndexed { index, entry ->
      DetailsPieChartItem(
        data = Pair(entry.key.title, entry.value),
        colour = colours[index]
      )
    }

  }
}

@Composable
fun DetailsPieChartItem(
  data: Pair<String, Double>,
  height: Dp = 45.dp,
  colour: Color
) {

  Surface(
    modifier = Modifier
      .padding(vertical = 8.dp, horizontal = 8.dp),
    color = Color.Transparent
  ) {

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {

      Box(
        modifier = Modifier
          .background(
            color = colour,
            shape = RoundedCornerShape(10.dp)
          )
          .size(height)
      )

      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          modifier = Modifier.padding(start = 15.dp),
          text = data.first/*.lowercase()
            .split("_")
            .joinToString(" ") {
              it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            }*/,
          fontWeight = FontWeight.Medium,
          fontSize = 22.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          modifier = Modifier.padding(start = 15.dp),
          text = "${data.second} g",
          fontWeight = FontWeight.Medium,
          fontSize = 22.sp,
          color = Color.Gray
        )
      }

    }

  }

}
