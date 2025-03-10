package moe.caffeine.fridgehero.ui.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
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
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.component.item.ItemEditor
import moe.caffeine.fridgehero.ui.component.itemsheet.ActionRow
import moe.caffeine.fridgehero.ui.component.itemsheet.FloatingActionBar

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

  val actions = listOf(
    //save
    {
      if (editableFoodItem.name.isBlank()) {
        Event.DisplayToast("Please enter a name for this item.").apply(emitEvent)
        return@listOf
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
      Unit
    },
    //reset
    { editableFoodItem = prefill },
    //dismiss
    {
      scope.launch {
        sheetState.hide()
        onComplete(Result.failure(Throwable("Dismissed")))
        editableFoodItem = FoodItem()
      }
      Unit
    }
  )

  Box(
    Modifier
      .fillMaxSize()
      .background(Color.Transparent)
  ) {
    BottomSheetScaffold(
      scaffoldState = sheetScaffoldState,
      sheetPeekHeight = 360.dp,
      modifier = Modifier.fillMaxWidth(),
      sheetContent = {
        Column {
          AnimatedVisibility(
            modifier = Modifier
              .fillMaxWidth(),
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
                categories = editableFoodItem.categories,
                expiryDates = editableFoodItem.expiryDates,
                expiryEditorExpandedInitial = expiryEditorExpandedInitial,
                imageSectionExpanded = !visibleButNotExpanded,
                compact = !visibleButNotExpanded,
                onScannerRequest = {
                  barcodeAction()
                },
                onDatePickerRequest = {
                  Event.RequestDateFromPicker().apply(emitEvent).result.await()
                },
                onValueChanged = { editedFoodItem ->
                  editableFoodItem = editedFoodItem
                }
              )
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
                      Event.HardRemoveFoodItem(editableFoodItem).apply(emitEvent)
                      actions[2]()
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
        FloatingActionBar(
          visible =
          sheetState.isVisible &&
                  sheetState.targetValue == SheetValue.Expanded &&
                  sheetState.currentValue == SheetValue.Expanded,
          actions = actions,
          showScannerButton = !editableFoodItem.isSaved,
          onScannerRequest = {
            barcodeAction()
          }
        )
      }
    ) {}
  }
}
