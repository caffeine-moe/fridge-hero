package moe.caffeine.fridgehero.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.NutrimentBreakdown

@Composable
fun NutrimentBreakdown(
  breakdown: NutrimentBreakdown
) {
  Box(Modifier
    .padding(8.dp)
    .fillMaxWidth()) {
    Column {
      for (total in breakdown.totals) {
        Text("${total.key.name} : ${total.value}")
      }
    }
  }
}
