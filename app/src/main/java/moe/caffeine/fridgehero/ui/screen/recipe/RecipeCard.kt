package moe.caffeine.fridgehero.ui.screen.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.ui.component.ImageCard

@Composable
fun RecipeCard(
  recipe: Recipe,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .padding(8.dp)
      .clickable {
        onClick()
      },
  ) {
    Box(
      Modifier
        .align(Alignment.CenterHorizontally)
    ) {
      ImageCard(
        modifier = Modifier
          .size(120.dp)
          .padding(4.dp),
        imageByteArray = recipe.imageByteArray
      )
    }
    Spacer(Modifier.width(10.dp))
    Text(
      modifier = Modifier.align(Alignment.CenterHorizontally),
      text = recipe.name
    )
    Spacer(Modifier.width(10.dp))
  }
}
