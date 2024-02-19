package moe.caffeine.fridgehero.screen.myfridge

import moe.caffeine.fridgehero.openfoodfacts.OpenFoodFactsResponse

data class FoodItem(
    val barcode : String,
    val name : String,
    val brand : String,
    val image : String,
) {
    companion object Builder {
        fun fromOpenFoodFactsResponse(openFoodFactsResponse : OpenFoodFactsResponse) : FoodItem =
            FoodItem(
                openFoodFactsResponse.code,
                openFoodFactsResponse.product.product_name_en,
                openFoodFactsResponse.product.brands,
                openFoodFactsResponse.product.image_url
            )
    }
}