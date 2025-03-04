package moe.caffeine.fridgehero.domain

import kotlinx.coroutines.CompletableDeferred
import moe.caffeine.fridgehero.domain.model.FoodItem

// These are used for the asynchronous flow of data around the app
sealed class Event {

  // Launches a date picker from anywhere
  data class RequestDateFromPicker(
    val result: CompletableDeferred<Result<Long>> = CompletableDeferred()
  ) : Event()

  // Launches the item editor sheet from anywhere, can be prefilled or not, can return the
  // new item.
  data class RequestItemSheet(
    val prefill: FoodItem = FoodItem(),
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred(),
    val readOnly: Boolean = false,
    val expiryEditorExpanded: Boolean = false,
  ) : Event()

  data class RequestBarcodeFromScanner(
    val result: CompletableDeferred<Result<String>> = CompletableDeferred()
  ) : Event()

  data class RequestFoodItemFromBarcode(
    val barcode: String,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred()
  ) : Event()

  // Opens a full screen view of an item
  data class RequestItemFullScreen(
    val foodItem: FoodItem,
  ) : Event()

  // Updates an item, or inserts the item if it doesn't exist already
  data class UpsertFoodItem(
    val foodItem: FoodItem,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred()
  ) : Event()

  // Removes all expiry dates from the food item in the realm
  data class SoftRemoveFoodItem(
    val foodItem: FoodItem,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred()
  ) : Event()

  // Permanently deletes food item from realm
  data class HardRemoveFoodItem(
    val foodItem: FoodItem,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred()
  ) : Event()

  // Tells the main activity to display a short toast with the message
  data class DisplayToast(
    val message: String,
  ) : Event()
}
