package moe.caffeine.fridgehero.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem

@Composable
fun EventHandler(
  eventFlow: SharedFlow<Event>,
  onDisplayToast: (message: String) -> Unit,
  onBarcodeRequest: (request: Event.RequestBarcodeFromScanner) -> Unit,
  onDateRequest: (request: Event.RequestDateFromPicker) -> Unit,
  onItemSheetRequest: (request: Event.RequestItemSheet) -> Unit,
  onFullScreenRequest: (request: FoodItem) -> Unit,
  onRecipeEditorRequest: (request: Event.RequestRecipeEditor) -> Unit,
  onItemSearchRequest: (request: Event.RequestItemFromSearch) -> Unit
) {
  LaunchedEffect(Unit) {
    eventFlow.collectLatest { event ->
      when (event) {
        is Event.DisplayToast -> onDisplayToast(event.message)
        is Event.RequestBarcodeFromScanner -> onBarcodeRequest(event)
        is Event.RequestDateFromPicker -> onDateRequest(event)
        is Event.RequestItemSheet -> onItemSheetRequest(event)
        is Event.RequestItemFullScreen -> onFullScreenRequest(event.foodItem)
        is Event.RequestRecipeEditor -> onRecipeEditorRequest(event)
        is Event.RequestItemFromSearch -> onItemSearchRequest(event)
        else -> return@collectLatest
      }
    }
  }
}
