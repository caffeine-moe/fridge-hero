package moe.caffeine.fridgehero.ui.screen.fridge

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.helper.fuzzyMatch
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.component.ActionableSwipeToDismissBox
import moe.caffeine.fridgehero.ui.component.CustomSearchBar
import moe.caffeine.fridgehero.ui.component.item.ExpiryEditor
import moe.caffeine.fridgehero.ui.component.item.ItemCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Fridge(
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit,
) {
  val fridge by foodItems.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()
  var query by rememberSaveable { mutableStateOf("") }
  var showHidden by rememberSaveable { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current
  BackHandler {
    focusManager.clearFocus()
  }
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(4.dp)
      .animateContentSize(tween(500)),
  ) {
    stickyHeader {
      CustomSearchBar(
        modifier = Modifier.onFocusChanged {
          showHidden = it.isFocused
        },
        query = query,
        onClear = {
          if (query.isEmpty()) {
            focusManager.clearFocus()
          } else
            query = ""
        },
        onTextChanged = {
          query = it
        }
      )
    }
    items(
      fridge,
      key = { it.realmId }
    ) { listFoodItem ->
      val matches by remember {
        derivedStateOf {
          {
            if (query.isEmpty())
              showHidden || !listFoodItem.isRemoved
            else {
              fuzzyMatch(listFoodItem.name, query)
            }
          }
        }
      }
      AnimatedVisibility(
        modifier = Modifier
          .animateItem(tween(500), tween(500), tween(500)),
        visible = matches(),
        exit = slideOutHorizontally(
          tween(500),
          targetOffsetX = { -it },
        ) + fadeOut(tween(500))
      ) {
        val currentFoodItem by rememberUpdatedState(newValue = listFoodItem)
        ElevatedCard(
          Modifier
            .padding(4.dp)
        ) {
          ActionableSwipeToDismissBox(
            modifier = Modifier
              .animateItem(tween(500), tween(500), tween(500)),
            onStartToEndAction = {
              scope.launch {
                Event.RequestItemSheet(currentFoodItem)
                  .apply(emitEvent).result.await()
                  .onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }
              }
            },
            onEndToStartAction = {
              Event.SoftRemoveFoodItem(currentFoodItem)
                .apply(emitEvent)
            }
          ) {
            var expanded by rememberSaveable { mutableStateOf(false) }
            ItemCard(
              modifier = Modifier
                .animateItem(tween(500), tween(500), tween(500))
                .combinedClickable(
                  onClick = {
                    expanded = !expanded
                  },
                  onLongClick = {
                    Event.RequestItemFullScreen(listFoodItem)
                      .apply(emitEvent)
                  }
                ),
              item = listFoodItem,
              expanded = expanded
            ) {
              ExpiryEditor(
                expiryDates = listFoodItem.expiryDates,
                onRequestExpiry = {
                  Event.RequestDateFromPicker()
                    .apply(emitEvent).result.await()
                },
                small = true,
                onShowMore = {
                  scope.launch {
                    Event.RequestItemSheet(listFoodItem, expiryEditorExpanded = true)
                      .apply(emitEvent).result.await()
                      .onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }
                  }
                },
                onListChanged = { changedList ->
                  Event.UpsertFoodItem(listFoodItem.copy(expiryDates = changedList))
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
