package moe.caffeine.fridgehero.ui.overlay

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.ui.component.item.ItemSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemSheetOverlay(
  state: SheetState,
  request: Event.RequestItemSheet?,
  onBarcodeFromScanner: suspend () -> Result<String>,
  onFoodItemFromBarcode: suspend (String) -> Result<FoodItem>,
  onExpiryDateRequest: suspend () -> Result<Long>,
  onComplete: suspend (FoodItem) -> Result<FoodItem>,
  onDismiss: suspend () -> Unit,
  onHardRemove: (FoodItem) -> Unit,
) {
  if (request == null) return
  ItemSheet(
    state = state,
    prefill = request.prefill,
    expiryEditorExpandedInitial = request.expiryEditorExpanded,
    onComplete = {
      val complete = if (it == request.prefill) Result.success(it) else onComplete(it)
      request.result.complete(complete)
      onDismiss()
    },
    onDismiss = {
      request.result.complete(Result.failure(Throwable("Dismissed")))
      onDismiss()
    },
    onBarcodeFromScanner = onBarcodeFromScanner,
    onFoodItemFromBarcode = onFoodItemFromBarcode,
    onExpiryDateRequest = onExpiryDateRequest,
    onHardRemove = onHardRemove
  )
}
