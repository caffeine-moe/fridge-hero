package moe.caffeine.fridgehero.ui.component.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.item.ItemSheet
import moe.caffeine.fridgehero.ui.item.components.ExpiryEditor
import moe.caffeine.fridgehero.ui.item.components.ItemEditor

@Composable
fun ItemSheetOverlay(
  itemSheetRequest: Event.RequestItemSheet?,
  onBarcodeFromScanner: suspend () -> Result<String>,
  onFoodItemFromBarcodeFromScanner: suspend () -> Result<FoodItem>,
  onExpiryDateRequest: suspend () -> Result<Long>,
  onComplete: (FoodItem) -> Unit,
  onDismiss: () -> Unit,
) {
  AnimatedVisibility(itemSheetRequest != null) {
    itemSheetRequest?.let {
      val scope = rememberCoroutineScope()
      var editableFoodItem by remember { mutableStateOf(itemSheetRequest.prefill) }
      ItemSheet(
        onDismiss = {
          itemSheetRequest.result.complete(
            Result.failure(Throwable("Dismissed"))
          )
          onDismiss()
        },
        onComplete = {
          onComplete(editableFoodItem)
        },
        onResetRequest = {
          editableFoodItem = itemSheetRequest.prefill
        }
      ) {
        Column {
          ItemEditor(
            editableFoodItem,
            onScannerRequest = { replaceAll ->
              scope.launch {
                if (!replaceAll) {
                  onBarcodeFromScanner().onSuccess {
                    editableFoodItem = editableFoodItem.copy(barcode = it)
                  }
                  return@launch
                }
                onFoodItemFromBarcodeFromScanner().onSuccess {
                  editableFoodItem = it
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
            onListChanged = { newDates ->
              editableFoodItem = editableFoodItem.copy(expiryDates = newDates)
            }
          )
        }
      }
    }
  }
}
