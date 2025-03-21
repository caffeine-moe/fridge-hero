package moe.caffeine.fridgehero.ui.overlay.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.ui.component.ActionRow
import moe.caffeine.fridgehero.ui.component.FloatingActionBar
import moe.caffeine.fridgehero.ui.component.item.ExpiryEditor
import moe.caffeine.fridgehero.ui.component.item.ItemEditor
import moe.caffeine.fridgehero.ui.component.itemsheet.ScannerFloatingActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSheetOverlay(
  prefill: FoodItem,
  expiryEditorExpandedInitial: Boolean,
  sheetState: SheetState,
  onComplete: (Result<FoodItem>) -> Unit,
  emitEvent: (Event) -> Unit,
) {
  val sheetScaffoldState = rememberBottomSheetScaffoldState(sheetState)
  val scrollState = rememberScrollState()
  val scope = rememberCoroutineScope()

  var editableFoodItem by rememberSaveable(prefill) { mutableStateOf(prefill) }

  var expiryEditorExpanded by rememberSaveable { mutableStateOf(expiryEditorExpandedInitial) }

  val visibleButNotExpanded =
    sheetState.isVisible && sheetState.currentValue != SheetValue.Expanded

  fun barcodeAction() {
    if (editableFoodItem.isSaved) return
    scope.launch {
      sheetState.hide()
      Event.RequestFoodItemFromBarcode(
        (Event.RequestBarcodeFromScanner()
          .apply(emitEvent).result.await()
          .also { sheetState.expand() }
          .onSuccess { barcode ->
            editableFoodItem = editableFoodItem.copy(barcode = barcode)
          }.getOrNull() ?: return@launch)
      ).apply(emitEvent).result.await()
        .onSuccess { retrievedItem ->
          editableFoodItem = retrievedItem
        }
    }
  }

  val actions: List<Pair<String, () -> Unit>> = listOf(
    ("Save" to {
      run {
        if (editableFoodItem.name.isBlank()) {
          Event.DisplayToast("Please enter a name for this item.").apply(emitEvent)
          return@run
        }
        scope.launch {
          sheetState.hide()
          onComplete(
            Result.success(
              editableFoodItem
            )
          )
          editableFoodItem = FoodItem()
        }
      }
    }),
    ("Reset" to { editableFoodItem = prefill }),
    ("Dismiss" to {
      scope.launch {
        sheetState.hide()
        onComplete(Result.failure(Throwable("Dismissed")))
        editableFoodItem = FoodItem()
      }
      Unit
    })
  )
  Column {
    AnimatedVisibility(
      visible = sheetState.targetValue == SheetValue.Hidden,
      enter = expandVertically(tween(durationMillis = 300, easing = FastOutSlowInEasing)),
      exit = shrinkVertically(tween(durationMillis = 300, easing = FastOutSlowInEasing))
    ) {
      Box(
        Modifier
          .weight(1f)
          .fillMaxSize(),
      )
    }
    Box(
      Modifier
        .fillMaxSize()
        .background(Color.Transparent)
        .systemBarsPadding(),
      contentAlignment = Alignment.Center
    ) {
      BottomSheetScaffold(
        scaffoldState = sheetScaffoldState,
        sheetPeekHeight = 360.dp,
        sheetContent = {
          Column {
            AnimatedVisibility(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
              visible = visibleButNotExpanded,
              enter = expandVertically(tween(500))
                      + slideInVertically(tween(500)) { it },
              exit = shrinkVertically(tween(500))
                      + slideOutVertically(tween(500)) { it }
            ) {
              ActionRow(actions)
            }
            Surface(
              modifier = Modifier
                .verticalScroll(scrollState)
                .padding(
                  bottom = WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding() + 80.dp
                )
            ) {
              Column(
                Modifier
                  .padding(8.dp)
                  .fillMaxSize()
              ) {
                ItemEditor(
                  foodItem = editableFoodItem,
                  compact = visibleButNotExpanded,
                  onScannerRequest = {
                    barcodeAction()
                  },
                  onValueChanged = { editedFoodItem ->
                    editableFoodItem = editedFoodItem
                  }
                )
                Spacer(Modifier.size(8.dp))
                ElevatedCard {
                  ExpiryEditor(
                    editableFoodItem.expiryDates,
                    onRequestExpiry = {
                      Event.RequestDateFromPicker().apply(emitEvent).result.await()
                    },
                    small = !expiryEditorExpanded,
                    onShowMore = {
                      expiryEditorExpanded = !expiryEditorExpanded
                    },
                    onListChanged = {
                      editableFoodItem = editableFoodItem.copy(expiryDates = it)
                    }
                  )
                }
                Spacer(Modifier.size(8.dp))
                ElevatedCard(
                  modifier = Modifier
                    .border(
                      BorderStroke(2.dp, Color.Red),
                      MaterialTheme.shapes.medium
                    )
                ) {
                  Column(Modifier.padding(8.dp)) {
                    Box(Modifier.fillMaxWidth()) {
                      Text(
                        modifier = Modifier
                          .align(Alignment.CenterStart)
                          .padding(8.dp),
                        text = "Danger Zone",
                        style = MaterialTheme.typography.titleMedium
                      )
                    }
                    Button(
                      enabled = editableFoodItem.isSaved,
                      modifier = Modifier
                        .align(Alignment.Start)
                        .padding(8.dp),
                      colors = ButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.Black,
                        disabledContentColor = Color.LightGray,
                        disabledContainerColor = Color.DarkGray
                      ),
                      onClick = {
                        Event.DeleteFoodItem(editableFoodItem).apply(emitEvent)
                        actions[2].second()
                      }
                    ) {
                      Text(
                        "PERMANENTLY DELETE"
                      )
                    }
                  }
                }
              }
            }
          }
        },
        snackbarHost = {
          val visible = sheetState.isVisible &&
                  sheetState.targetValue == SheetValue.Expanded &&
                  sheetState.currentValue == SheetValue.Expanded
          Column {
            androidx.compose.animation.AnimatedVisibility(
              modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 16.dp),
              visible = visible && !editableFoodItem.isSaved,
              enter = slideInVertically(
                tween(250),
                initialOffsetY = { 2 * it }) + fadeIn(tween(250)),
              exit = slideOutHorizontally(tween(250), targetOffsetX = { 2 * it }) + fadeOut(
                tween(
                  250
                )
              )
            ) {
              Column(Modifier.align(Alignment.End)) {
                ScannerFloatingActionButton(onClick = { barcodeAction() })
              }
            }
            FloatingActionBar(
              visible = visible,
              actions = actions
            )
          }
        }
      ) {}
    }
  }
}
