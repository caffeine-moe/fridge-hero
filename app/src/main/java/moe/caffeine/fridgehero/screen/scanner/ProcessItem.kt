package moe.caffeine.fridgehero.screen.scanner

import moe.caffeine.fridgehero.openfoodfacts.OpenFoodFactsClient
import moe.caffeine.fridgehero.screen.myfridge.FoodItem
import moe.caffeine.fridgehero.screen.myfridge.fridgeContents

suspend fun ProcessItem(barcode : String) : FoodItem? {
    val lookup = OpenFoodFactsClient.getInstance().lookupBarcode(barcode)
    if (!lookup.second) return null
    val foodItem = FoodItem.fromOpenFoodFactsResponse(lookup.first)
    fridgeContents += foodItem
    return foodItem
}