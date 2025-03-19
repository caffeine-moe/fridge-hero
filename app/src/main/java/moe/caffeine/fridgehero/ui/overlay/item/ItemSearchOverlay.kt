package moe.caffeine.fridgehero.ui.overlay.item

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.component.item.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSearchOverlay(
  visible: Boolean,
  foodItems: StateFlow<List<FoodItem>>,
  onComplete: (Result<FoodItem>) -> Unit
) {
  val searchable by foodItems.collectAsStateWithLifecycle()
  AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(tween(500), initialOffsetX = { 2 * it }) + fadeIn(tween(500)),
    exit = slideOutHorizontally(tween(500), targetOffsetX = { 2 * it }) + fadeOut(tween(500))
  ) {
    if (!visible) return@AnimatedVisibility
    BackHandler {
      onComplete(Result.failure(Throwable("Dismissed")))
    }

    var query by rememberSaveable { mutableStateOf("") }
    Surface(
      Modifier
        .fillMaxSize()
        .systemBarsPadding()
    ) {
      Column {
        SearchBar(
          modifier = Modifier
            .fillMaxWidth(),
          shape = SearchBarDefaults.inputFieldShape,
          expanded = false,
          inputField = {
            SearchBarDefaults.InputField(
              expanded = false,
              query = query,
              onQueryChange = { query = it },
              onSearch = {},
              onExpandedChange = {}
            )
          },
          onExpandedChange = { }
        ) { }
        LazyColumn {
          items(searchable) {
            AnimatedVisibility(it.name.any { character -> query.contains(character) }) {
              ElevatedCard(
                Modifier
                  .padding(4.dp)
              ) {
                ItemCard(
                  modifier = Modifier.clickable {
                    onComplete(Result.success(it))
                  },
                  item = it
                )
              }
            }
          }
        }
      }
    }
  }
}
