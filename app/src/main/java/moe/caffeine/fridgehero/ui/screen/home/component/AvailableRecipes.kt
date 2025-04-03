package moe.caffeine.fridgehero.ui.screen.home.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.ui.screen.recipe.RecipeCard
import moe.caffeine.fridgehero.ui.theme.Typography

@Composable
fun AvailableRecipes(
  recipesFlow: StateFlow<List<Recipe>>
) {
  val recipes by recipesFlow.collectAsStateWithLifecycle()
  if (recipes.isEmpty()) return
  val scrollState = rememberScrollState()
  Text(
    modifier = Modifier.padding(vertical = 8.dp),
    style = Typography.titleMedium,
    text = "Available Recipes"
  )
  Row(
    modifier = Modifier.horizontalScroll(scrollState)
  ) {
    recipes
      .filter { it.ingredients.all { !it.isExpired && !it.isRemoved } }
      .sortedBy { it.ingredients.count { it.expiresSoon } }
      .forEach { recipe ->
        RecipeCard(recipe) { }
      }
  }
}
