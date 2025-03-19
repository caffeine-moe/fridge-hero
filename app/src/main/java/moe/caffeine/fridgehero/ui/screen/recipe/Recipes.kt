package moe.caffeine.fridgehero.ui.screen.recipe

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.R
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.Recipe

var persistentRecipes = listOf("ONION SALAD")

@Composable
fun Recipes(
  recipes: StateFlow<List<Recipe>>,
  emitEvent: (Event) -> Unit
) {
  val recipesList by recipes.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()

  LazyVerticalGrid(
    columns = GridCells.FixedSize(128.dp)
  ) {
    items(recipesList) { recipe ->
      val currentRecipe by rememberUpdatedState(recipe)
      Card(
        modifier = Modifier
          .padding(10.dp)
          .clickable {
            scope.launch {
              Event.RequestRecipeEditor(currentRecipe).apply(emitEvent).result.await().onSuccess {
                Event.UpsertRecipe(it).apply(emitEvent)
              }
            }
          },
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
