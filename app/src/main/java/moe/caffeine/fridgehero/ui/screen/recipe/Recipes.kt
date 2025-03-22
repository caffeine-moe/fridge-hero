package moe.caffeine.fridgehero.ui.screen.recipe

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.Recipe

var persistentRecipes = listOf("ONION SALAD")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Recipes(
  recipes: StateFlow<List<Recipe>>,
  emitEvent: (Event) -> Unit
) {
  val recipesList by recipes.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()

  FlowRow {
    recipesList.forEach { recipe ->
      val currentRecipe by rememberUpdatedState(recipe)
      RecipeCard(currentRecipe) {
        scope.launch {
          Event.RequestRecipeEditor(currentRecipe)
            .apply(emitEvent)
            .result.await()
            .onSuccess {
              Event.UpsertRecipe(it).apply(emitEvent)
            }
        }
      }
    }
  }
}
