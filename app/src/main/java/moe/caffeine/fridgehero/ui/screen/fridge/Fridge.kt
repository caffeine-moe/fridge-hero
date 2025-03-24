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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.helper.fuzzyMatch
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.component.ActionableSwipeToDismissBox
import moe.caffeine.fridgehero.ui.component.item.ExpiryEditor
import moe.caffeine.fridgehero.ui.component.item.ItemCard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Fridge(
  query: String,
  searchBarHasFocus: Boolean,
  foodItems: StateFlow<List<FoodItem>>,
  emitEvent: (Event) -> Unit,
) {
  val fridge by foodItems.collectAsStateWithLifecycle()
  val scope = rememberCoroutineScope()
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
    items(
      fridge,
      key = { it.realmId }
    ) { listFoodItem ->
      val currentFoodItem by rememberUpdatedState(newValue = listFoodItem)
      val currentQuery by rememberUpdatedState(newValue = query)
      val showHidden by rememberUpdatedState(newValue = searchBarHasFocus)
      val matches by remember(currentQuery) {
        derivedStateOf {
          {
            if (currentQuery.isEmpty())
              showHidden || !currentFoodItem.isRemoved
            else {
              fuzzyMatch(currentFoodItem.name, currentQuery)
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
            },
            enableEndToStartDismiss = !currentFoodItem.isRemoved
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
                    Event.RequestItemFullScreen(currentFoodItem)
                      .apply(emitEvent)
                  }
                ),
              item = currentFoodItem,
              expanded = expanded
            ) {
              ExpiryEditor(
                expiryDates = currentFoodItem.expiryDates,
                onRequestExpiry = {
                  Event.RequestDateFromPicker()
                    .apply(emitEvent).result.await()
                },
                small = true,
                onShowMore = {
                  scope.launch {
                    Event.RequestItemSheet(currentFoodItem, expiryEditorExpanded = true)
                      .apply(emitEvent).result.await()
                      .onSuccess { Event.UpsertFoodItem(it).apply(emitEvent) }
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
