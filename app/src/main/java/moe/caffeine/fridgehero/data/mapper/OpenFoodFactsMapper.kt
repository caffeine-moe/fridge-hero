package moe.caffeine.fridgehero.data.mapper

import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsProduct
import moe.caffeine.fridgehero.domain.model.FoodCategory
import moe.caffeine.fridgehero.domain.model.FoodItem

fun OpenFoodFactsProduct.toDomainModel(
  thumbnail: ByteArray,
  categories: List<FoodCategory>
): FoodItem =
  FoodItem(
    realmObjectId = "",
    name = productName,
    barcode = code,
    brand = brands,
    imageByteArray = thumbnail,
    categories = categories
  )
