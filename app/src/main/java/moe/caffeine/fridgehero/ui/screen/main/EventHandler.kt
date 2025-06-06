package moe.caffeine.fridgehero.ui.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import moe.caffeine.fridgehero.domain.Event

@Composable
fun EventHandler(
  eventFlow: SharedFlow<Event>,
  onDisplayToast: (message: String) -> Unit,
  onBarcodeRequest: (request: Event.RequestBarcodeFromScanner) -> Unit,
  onDateRequest: (request: Event.RequestDateFromPicker) -> Unit,
  onItemSheetRequest: (request: Event.RequestItemSheet) -> Unit,
  onRecipeEditorRequest: (request: Event.RequestRecipeEditor) -> Unit,
  onItemSearchRequest: (request: Event.RequestItemsFromSearch) -> Unit,
) {
  LaunchedEffect(Unit) {
    eventFlow.collectLatest { event ->
      when (event) {
        is Event.DisplayToast -> onDisplayToast(event.message)
        is Event.RequestBarcodeFromScanner -> onBarcodeRequest(event)
        is Event.RequestDateFromPicker -> onDateRequest(event)
        is Event.RequestItemSheet -> onItemSheetRequest(event)
        is Event.RequestRecipeEditor -> onRecipeEditorRequest(event)
        is Event.RequestItemsFromSearch -> onItemSearchRequest(event)
        else -> return@collectLatest
      }
    }
  }
}
