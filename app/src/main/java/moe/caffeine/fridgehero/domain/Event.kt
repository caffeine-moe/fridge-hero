package moe.caffeine.fridgehero.domain

import kotlinx.coroutines.CompletableDeferred
import moe.caffeine.fridgehero.domain.model.NutrimentBreakdown
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem

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

  data class RequestItemFromSearch(
    val result: CompletableDeferred<Result<List<FoodItem>>> = CompletableDeferred()
  ) : Event()

  //launches the recipe editor
  data class RequestRecipeEditor(
    val prefill: Recipe = Recipe(),
    val result: CompletableDeferred<Result<Recipe>> = CompletableDeferred(),
  ) : Event()

  //retireves a barcode from the barcode scanner
  data class RequestBarcodeFromScanner(
    val result: CompletableDeferred<Result<String>> = CompletableDeferred()
  ) : Event()

  data class RequestNutrimentBreakdown(
    val items: List<FoodItem>,
    val result: CompletableDeferred<Result<NutrimentBreakdown>> = CompletableDeferred()
  ) : Event()

  //retrieves a food item domain object from openfoodfacts from a barcode
  data class RequestFoodItemFromBarcode(
    val barcode: String,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred()
  ) : Event()

  // Opens a full screen view of an item
  data class RequestItemFullScreen(
    val foodItem: FoodItem,
  ) : Event()

  //upserts a food item into the database
  data class UpsertFoodItem(
    val foodItem: FoodItem,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred()
  ) : Event()

  data class DeleteFoodItem(
    val foodItem: FoodItem,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred()
  ) : Event()

  // Removes all expiry dates from the food item in the realm
  data class SoftRemoveFoodItem(
    val foodItem: FoodItem,
    val result: CompletableDeferred<Result<FoodItem>> = CompletableDeferred()
  ) : Event()

  //upserts a recipe into the database
  data class UpsertRecipe(
    val recipe: Recipe,
    val result: CompletableDeferred<Result<Recipe>> = CompletableDeferred()
  ) : Event()

  // Tells the main composable to display a short toast with the message
  data class DisplayToast(
    val message: String,
  ) : Event()

  // Tells the main composable to ask the user for an image from their camera or gallery
  data class RequestExternalImage(
    val result: CompletableDeferred<Result<ByteArray>> = CompletableDeferred()
  ) : Event()

  data class FindPotentialMatches(
    val foodItem: FoodItem,
    val result: CompletableDeferred<Result<List<FoodItem>>> = CompletableDeferred()
  ) : Event()
}
