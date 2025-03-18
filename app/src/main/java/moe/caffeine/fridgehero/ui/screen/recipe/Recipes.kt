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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.domain.model.Recipe

var persistentRecipes = listOf("ONION SALAD")

@Composable
fun Recipes(
  recipes: StateFlow<List<Recipe>>,
) {
  val recipesList by recipes.collectAsStateWithLifecycle()
  LazyVerticalGrid(
    columns = GridCells.FixedSize(128.dp)
  ) {
    items(recipesList) { recipe ->
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
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "test"
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
  }
}
