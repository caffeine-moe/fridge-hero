package moe.caffeine.fridgehero.ui.component.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore

fun resolveNovaGroupPainter(novaGroup: NovaGroup): @Composable () -> Painter = @Composable {
  painterResource(
    when (novaGroup) {
      NovaGroup.UNPROCESSED -> R.drawable.nova_group_1
      NovaGroup.PROCESSED_INGREDIENTS -> R.drawable.nova_group_2
      NovaGroup.PROCESSED -> R.drawable.nova_group_3
      NovaGroup.ULTRA_PROCESSED -> R.drawable.nova_group_4
      else -> R.drawable.nova_group_unknown
    }
  )
}

fun resolveNutriScorePainter(nutriScore: NutriScore): @Composable () -> Painter = @Composable {
  painterResource(
    when (nutriScore) {
      NutriScore.A -> R.drawable.nutriscore_a
      NutriScore.B -> R.drawable.nutriscore_b
      NutriScore.C -> R.drawable.nutriscore_c
      NutriScore.D -> R.drawable.nutriscore_d
      NutriScore.E -> R.drawable.nutriscore_e
      else -> R.drawable.nutriscore_unknown
    }
  )
}
