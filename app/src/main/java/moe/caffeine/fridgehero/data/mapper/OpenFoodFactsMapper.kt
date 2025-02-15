package moe.caffeine.fridgehero.data.mapper

import moe.caffeine.fridgehero.data.model.OpenFoodFactsProduct
import moe.caffeine.fridgehero.domain.model.FoodItem
import org.mongodb.kbson.BsonObjectId

fun OpenFoodFactsProduct.toDomainModel(
  thumbnail: ByteArray
): FoodItem =
  FoodItem(
    realmObjectId = BsonObjectId(),
    name = productName,
    barcode = code,
    brand = brandsTags.firstOrNull() ?: "N/A",
    imageByteArray = thumbnail
  )
