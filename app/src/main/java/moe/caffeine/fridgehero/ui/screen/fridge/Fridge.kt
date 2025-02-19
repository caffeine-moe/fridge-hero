package moe.caffeine.fridgehero.ui.screen.fridge

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
  val lazyListState = rememberLazyListState()
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
      state = lazyListState,
      contentPadding = innerPadding,
    ) {
      items(
        fridge,
        key = { it.realmObjectId.toHexString() }
      ) { listFoodItem ->
        ActionableSwipeToDismissBox(
          visible = !listFoodItem.isRemoved,
          modifier = Modifier.animateItem(),
          onStartToEndAction = {
            emitEvent(
              Event.RequestItemSheet(
                listFoodItem
              )
            )
          },
          onEndToStartAction = {
            emitEvent(
              Event.SoftRemoveFoodItem(
                listFoodItem
              )
            )
          }
        ) {
          ItemCard(
            item = listFoodItem,
            onLongPress = {
              emitEvent(
                Event.RequestItemFullScreen(listFoodItem)
              )
            }
          ) {
            ExpiryEditor(
              expiryDates = listFoodItem.expiryDates,
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
                    listFoodItem
                  )
                )
              },
              onListChanged = { changedList ->
                emitEvent(
                  Event.UpsertFoodItem(
                    listFoodItem.copy(expiryDates = changedList)
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
