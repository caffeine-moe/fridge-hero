package moe.caffeine.fridgehero.ui.screen.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import moe.caffeine.fridgehero.R

var persistentRecipes = listOf("ONION SALAD")

@Composable
fun Recipes() {
  var recipes by remember { mutableStateOf(persistentRecipes) }
  LazyVerticalGrid(
    columns = GridCells.FixedSize(128.dp)
  ) {
    items(recipes) { recipe ->
      Card(
        modifier = Modifier.padding(10.dp),
      ) {
        Box(
          Modifier
            .align(Alignment.CenterHorizontally)
        ) {
          Image(
            modifier = Modifier
              .padding(10.dp),
            contentScale = ContentScale.FillBounds,
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "test"
          )
        }
        Spacer(Modifier.width(10.dp))
        Text(
          modifier = Modifier.align(Alignment.CenterHorizontally),
          text = recipe
        )
        Spacer(Modifier.width(10.dp))
      }
    }
  }
}
