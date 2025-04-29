package moe.caffeine.fridgehero.ui.screen.fridge

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.matches
import moe.caffeine.fridgehero.ui.component.ActionableSwipeToDismissBox
import moe.caffeine.fridgehero.ui.component.item.ExpiryEditor
import moe.caffeine.fridgehero.ui.component.item.ItemCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Fridge(
  query: String,
  showHidden: Boolean,
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit,
) {
  val fridge by foodItems.collectAsStateWithLifecycle()
  val currentQuery by rememberUpdatedState(query)
  val filteredItems by remember(fridge, currentQuery) {
    derivedStateOf {
      fridge.filter { it.matches(currentQuery) }
    }
  }
  val scope = rememberCoroutineScope()
  val focusManager = LocalFocusManager.current
  BackHandler {
    focusManager.clearFocus()
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(4.dp)
  ) {
    items(
      filteredItems,
      key = { it.realmId }
    ) { listFoodItem ->
      val currentFoodItem by rememberUpdatedState(newValue = listFoodItem)

      AnimatedVisibility(
        modifier = Modifier
          .animateItem(tween(500), tween(500), tween(500)),
        visible = showHidden || !currentFoodItem.isRemoved,
        exit = fadeOut(tween(500))
      ) {
        ElevatedCard(
          Modifier
            .padding(4.dp)
        ) {
          var expanded by rememberSaveable { mutableStateOf(false) }
          ActionableSwipeToDismissBox(
            modifier = Modifier
              .animateItem(tween(500), tween(500), tween(500)),
            onStartToEndAction = {
              scope.launch {
                Event.RequestItemSheet(currentFoodItem) {
                  onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }
                }
                  .apply(emitEvent)
              }
            },
            onEndToStartAction = {
              expanded = false
              Event.SoftRemoveFoodItem(currentFoodItem)
                .apply(emitEvent)
            },
            enableEndToStartDismiss = !currentFoodItem.isRemoved
          ) {
            ItemCard(
              modifier = Modifier
                .animateItem(tween(500), tween(500), tween(500))
                .combinedClickable(
                  onClick = {
                    expanded = !expanded
                  },
                  onLongClick = {
                    Event.RequestItemFullScreen(currentFoodItem)
                      .apply(emitEvent)
                  }
                ),
              item = currentFoodItem,
              expanded = expanded
            ) {
              ExpiryEditor(
                expiryDates = currentFoodItem.expiryDates,
                onRequestExpiry = { prefill, it ->
                  Event.RequestDateFromPicker(prefill) {
                    apply(it)
                  }.apply(emitEvent)
                },
                small = true,
                onShowMore = {
                  scope.launch {
                    Event.RequestItemSheet(currentFoodItem, expiryEditorExpanded = true) {
                      onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }
                    }
                      .apply(emitEvent)
                  }
                },
                onListChanged = { changedList ->
                  Event.UpsertFoodItem(currentFoodItem.copy(expiryDates = changedList))
                    .apply(emitEvent)
                }
              )
            }
          }
        }
      }
    }
  }
}
