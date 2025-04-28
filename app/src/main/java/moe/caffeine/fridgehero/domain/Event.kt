package moe.caffeine.fridgehero.domain

import moe.caffeine.fridgehero.domain.model.NutrimentBreakdown
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem

// These are used for the asynchronous flow of data around the app
sealed class Event {
  // Launches a date picker from anywhere
  data class RequestDateFromPicker(
    val prefill: Long? = null,
    val onResult: Result<Long>.() -> Unit = {}
  ) : Event()

  // Launches the item editor sheet from anywhere, can be prefilled or not, can return the
  // new item.
  data class RequestItemSheet(
    val prefill: FoodItem = FoodItem(),
    val readOnly: Boolean = false,
    val expiryEditorExpanded: Boolean = false,
    val onResult: Result<FoodItem>.() -> Unit = {}
  ) : Event()

  data class RequestItemsFromSearch(
    val onResult: Result<List<FoodItem>>.() -> Unit = {}
  ) : Event()

  //launches the recipe editor
  data class RequestRecipeEditor(
    val prefill: Recipe = Recipe(),
    val onResult: Result<Recipe>.() -> Unit = {}
  ) : Event()

  //retireves a barcode from the barcode scanner
  data class RequestBarcodeFromScanner(
    val onResult: Result<String>.() -> Unit = {}
  ) : Event()

  data class RequestNutrimentBreakdown(
    val items: List<FoodItem>,
    val usingExpiryDates: Boolean,
    val onResult: Result<NutrimentBreakdown>.() -> Unit = {}
  ) : Event()

  //retrieves a food item domain object from openfoodfacts from a barcode
  data class RequestFoodItemFromBarcode(
    val barcode: String,
    val onResult: Result<FoodItem>.() -> Unit = {}
  ) : Event()

  // Opens a full screen view of an item
  data class RequestItemFullScreen(
    val foodItem: FoodItem,
  ) : Event()

  //upserts a food item into the database
  data class UpsertFoodItem(
    val foodItem: FoodItem,
    val onResult: Result<FoodItem>.() -> Unit = {}
  ) : Event()

  data class DeleteFoodItem(
    val foodItem: FoodItem,
    val onResult: Result<FoodItem>.() -> Unit = {}
  ) : Event()

  // Removes all expiry dates from the food item in the realm
  data class SoftRemoveFoodItem(
    val foodItem: FoodItem,
    val onResult: Result<FoodItem>.() -> Unit = {}
  ) : Event()

  //upserts a recipe into the database
  data class UpsertRecipe(
    val recipe: Recipe,
    val onResult: Result<Recipe>.() -> Unit = {}
  ) : Event()

  //creates a leftover from a recipe
  data class CreateLeftOver(
    val recipe: Recipe,
    val onResult: Result<FoodItem>.() -> Unit = {}
  ) : Event()

  // Tells the main composable to display a short toast with the message
  data class DisplayToast(
    val message: String,
  ) : Event()

  // Tells the main composable to ask the user for an image from their camera or gallery
  data class RequestExternalImage(
    val onResult: Result<ByteArray>.() -> Unit = {}
  ) : Event()

  data class FindPotentialMatches(
    val foodItem: FoodItem,
    val onResult: Result<List<FoodItem>>.() -> Unit = {}
  ) : Event()
}
