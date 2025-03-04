package moe.caffeine.fridgehero.data.mapper

import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsProduct
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsTaxonomyNode
import moe.caffeine.fridgehero.data.model.realm.RealmFoodCategory
import moe.caffeine.fridgehero.domain.model.FoodItem

fun OpenFoodFactsProduct.toDomainModel(
  thumbnail: ByteArray,
  categoryNames: List<String>
): FoodItem =
  FoodItem(
    realmObjectId = "",
    name = productName,
    barcode = code,
    brand = brands,
    imageByteArray = thumbnail,
    categories = categoryNames
  )

fun OpenFoodFactsTaxonomyNode.toRealmModel(): RealmFoodCategory =
  RealmFoodCategory().apply {
    _id = id
    name = this@toRealmModel.name
  }
