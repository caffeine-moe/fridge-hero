package moe.caffeine.fridgehero.domain

import kotlinx.coroutines.CompletableDeferred
import moe.caffeine.fridgehero.domain.model.FoodItem

// These are used for the asynchronous flow of data around the app
sealed class Event {

  // Launches a date picker from anywhere
  data class RequestDateFromPicker(
    val result: CompletableDeferred<Result<Long>>
  ) : Event()

  // Launches a bottom sheet from anywhere, can be prefilled or not, can return the
  // new item along with any edited expiry dates as well as upsert the new item automatically.
  data class RequestItemSheet(
    val prefill: FoodItem,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred(),
    val upsertResult: Boolean = false,
    val readOnly: Boolean = false
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

  // Permanently deletes food item from realm
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
