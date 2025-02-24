package moe.caffeine.fridgehero.ui.screen.fridge

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.StateFlow
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
  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        modifier = Modifier.padding(16.dp),
        onClick = {
          emitEvent(
            Event.RequestItemSheet()
          )
        }) {
        Icon(Icons.Filled.Add, "Add Item Button")
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .animateContentSize(),
      contentPadding = innerPadding,
    ) {
      items(
        fridge,
        key = { it.realmObjectId }
      ) { listFoodItem ->
        val currentItem: FoodItem by rememberUpdatedState(newValue = listFoodItem)
        ActionableSwipeToDismissBox(
          visible = !currentItem.isRemoved,
          modifier = Modifier
            .animateItem()
            .animateContentSize(),
          onStartToEndAction = {
            emitEvent(
              Event.RequestItemSheet(
                currentItem
              )
            )
          },
          onEndToStartAction = {
            emitEvent(
              Event.SoftRemoveFoodItem(
                currentItem
              )
            )
          }
        ) {
          ItemCard(
            item = currentItem,
            onLongPress = {
              emitEvent(
                Event.RequestItemFullScreen(currentItem)
              )
            }
          ) {
            ExpiryEditor(
              expiryDates = currentItem.expiryDates.toList(),
              onRequestExpiry = {
                val completableExpiry: CompletableDeferred<Result<Long>> =
                  CompletableDeferred()
                emitEvent(
                  Event.RequestDateFromPicker(
                    completableExpiry
                  )
                )
                completableExpiry.await()
              },
              small = true,
              onShowMore = {
                emitEvent(
                  Event.RequestItemSheet(
                    currentItem,
                    expiryEditorExpanded = true
                  )
                )
              },
              onListChanged = { changedList ->
                emitEvent(
                  Event.UpsertFoodItem(
                    currentItem.copy(expiryDates = changedList)
                  )
                )
              }
            )
          }
        }
      }
    }
  }
}
