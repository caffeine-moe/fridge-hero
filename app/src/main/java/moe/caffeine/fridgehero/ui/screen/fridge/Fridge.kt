package moe.caffeine.fridgehero.ui.screen.fridge

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.component.ActionableSwipeToDismissBox
import moe.caffeine.fridgehero.ui.component.item.ExpiryEditor
import moe.caffeine.fridgehero.ui.component.item.ItemCard

@Composable
fun Fridge(
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit,
) {
  val fridge by foodItems.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .animateContentSize(tween(500)),
  ) {
    items(
      fridge,
      key = { it.realmObjectId }
    ) { listFoodItem ->
      val currentItem: FoodItem by rememberUpdatedState(newValue = listFoodItem)
      ActionableSwipeToDismissBox(
        visible = !currentItem.isRemoved,
        modifier = Modifier
          .animateItem(tween(500), tween(500), tween(500)),
        onStartToEndAction = {
          scope.launch {
            Event.RequestItemSheet(currentItem)
              .apply(emitEvent).result.await()
              .onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }
          }
        },
        onEndToStartAction = {
          Event.SoftRemoveFoodItem(currentItem)
            .apply(emitEvent)
        }
      ) {
        ItemCard(
          item = currentItem,
          onLongPress = {
            Event.RequestItemFullScreen(currentItem)
              .apply(emitEvent)
          }
        ) {
          ExpiryEditor(
            expiryDates = currentItem.expiryDates.toList(),
            onRequestExpiry = {
              Event.RequestDateFromPicker()
                .apply(emitEvent).result.await()
            },
            small = true,
            onShowMore = {
              scope.launch {
                Event.RequestItemSheet(currentItem, expiryEditorExpanded = true)
                  .apply(emitEvent).result.await()
                  .onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }
              }
            },
            onListChanged = { changedList ->
              Event.UpsertFoodItem(currentItem.copy(expiryDates = changedList))
                .apply(emitEvent)
            }
          )
        }
      }
    }
  }
}
