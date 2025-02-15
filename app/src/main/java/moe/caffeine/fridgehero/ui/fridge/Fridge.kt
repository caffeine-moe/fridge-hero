package moe.caffeine.fridgehero.ui.fridge

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.data.realm.FoodItem
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.ui.components.ActionableSwipeToDismissBox
import moe.caffeine.fridgehero.ui.item.ItemCard

@Composable
fun Fridge(
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit,
) {
  val fridge by foodItems.collectAsStateWithLifecycle()
  val lazyListState = rememberLazyListState()
  val scope = rememberCoroutineScope()

  Scaffold(
    floatingActionButton = {
      FloatingActionButton(
        modifier = Modifier.padding(16.dp),
        onClick = {
          emitEvent(
            Event.RequestItemSheet(
              FoodItem(),
              upsertResult = true
            )
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
        key = { it._id.toHexString() }
      ) { listFoodItem ->
        ActionableSwipeToDismissBox(
          visible = !listFoodItem.isRemoved,
          modifier = Modifier.animateItem(),
          onStartToEndAction = {
            emitEvent(
              Event.RequestItemSheet(
                listFoodItem,
                upsertResult = true,
              )
            )
          },
          onEndToStartAction = {
            emitEvent(
              Event.RequestLiveFoodItemOperation(
                listFoodItem,
                {
                  it.expiryDates.clear()
                }
              )
            )
          }
        ) {
          ItemCard(
            item = listFoodItem,
            onShowMore = {
              emitEvent(
                Event.RequestItemSheet(
                  listFoodItem,
                  upsertResult = true
                )
              )
            },
            onLongPress = {
              emitEvent(
                Event.RequestItemFullScreen(listFoodItem)
              )
            },
            onExpiryAddRequest = {
              scope.launch {
                val completableExpiry: CompletableDeferred<Result<Long>> =
                  CompletableDeferred()
                emitEvent(
                  Event.RequestDateFromPicker(
                    completableExpiry
                  )
                )
                completableExpiry.await().onSuccess { expiryDate ->
                  emitEvent(
                    Event.RequestLiveFoodItemOperation(
                      listFoodItem,
                      { foodItem ->
                        foodItem.expiryDates += expiryDate
                      }
                    )
                  )
                }
              }
            },
            onExpiryDuplicateRequest = { expiryDate ->
              emitEvent(
                Event.RequestLiveFoodItemOperation(
                  listFoodItem,
                  { foodItem ->
                    foodItem.expiryDates += expiryDate
                  }
                )
              )
            },
            onExpiryRemoveRequest = { expiry ->
              emitEvent(
                Event.RequestLiveFoodItemOperation(
                  listFoodItem,
                  {
                    it.expiryDates -= expiry
                  }
                )
              )
            }
          )
        }
      }
    }
  }
}
