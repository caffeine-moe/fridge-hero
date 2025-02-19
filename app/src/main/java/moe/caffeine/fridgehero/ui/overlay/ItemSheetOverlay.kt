package moe.caffeine.fridgehero.ui.overlay

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.component.item.ExpiryEditor
import moe.caffeine.fridgehero.ui.component.item.ItemEditor
import moe.caffeine.fridgehero.ui.component.item.ItemSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSheetOverlay(
  state: BottomSheetScaffoldState,
  prefill: FoodItem,
  onBarcodeFromScanner: suspend () -> Result<String>,
  onFoodItemFromBarcode: suspend (String) -> Result<FoodItem>,
  onExpiryDateRequest: suspend () -> Result<Long>,
  onComplete: (FoodItem) -> Unit,
  onDismiss: () -> Unit,
) {
  if (state.bottomSheetState.isVisible || state.bottomSheetState.targetValue != SheetValue.Hidden) {
    val scope = rememberCoroutineScope()
    var editableFoodItem by remember { mutableStateOf(prefill) }
    var expiryEditorIsSmall by remember { mutableStateOf(true) }
    ItemSheet(
      state = state,
      onComplete = {
        onComplete(editableFoodItem)
      },
      onDismiss = onDismiss,
      onResetRequest = {
        editableFoodItem = prefill
      }
    ) {
      Column {
        ItemEditor(
          editableFoodItem,
          onScannerRequest = { replaceAll ->
            scope.launch {
              val barcode =
                onBarcodeFromScanner().getOrNull() ?: return@launch
              if (!replaceAll) {
                editableFoodItem = editableFoodItem.copy(barcode = barcode)
                return@launch
              }
              onFoodItemFromBarcode(barcode).onSuccess { scannedItem ->
                editableFoodItem =
                  scannedItem.copy(
                    expiryDates =
                    scannedItem.expiryDates + editableFoodItem.expiryDates
                  )
              }
            }
          },
          onFieldChanged = { editedFoodItem ->
            editableFoodItem = editedFoodItem
          },
        )
        ExpiryEditor(
          editableFoodItem.expiryDates,
          onRequestExpiry = onExpiryDateRequest,
          small = expiryEditorIsSmall,
          onShowMore = {
            expiryEditorIsSmall = false
          },
          isolateState = true,
          onListChanged = { newDates ->
            editableFoodItem =
              editableFoodItem.copy(expiryDates = newDates)
          }
        )
        Spacer(Modifier.size(20.dp))
      }
    }
  }
}
