package moe.caffeine.fridgehero.data.mapper

import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsNutriments
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsProduct
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsTaxonomyNode
import moe.caffeine.fridgehero.data.model.realm.RealmFoodCategory
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import kotlin.math.round

fun OpenFoodFactsNutriments.toDomainModel(productQuantity: Double): Map<Nutriment, Double> =
  mapOf(
    Nutriment.FAT to round(fat100g * (productQuantity / 100)),
    Nutriment.SALT to round(salt100g * (productQuantity / 100)),
    Nutriment.FIBER to round(fiber100g * (productQuantity / 100)),
    Nutriment.ENERGY to round(energyKcal100g * (productQuantity / 100)),
    Nutriment.SODIUM to round(sodium100g * (productQuantity / 100)),
    Nutriment.CARBOHYDRATES to round(carbohydrates100g * (productQuantity / 100)),
    Nutriment.PROTEINS to round(proteins100g * (productQuantity / 100)),
    Nutriment.SATURATED_FAT to round(saturatedFat100g * (productQuantity / 100)),
    Nutriment.SUGARS to round(sugars100g * (productQuantity / 100))
  )

fun OpenFoodFactsProduct.toDomainModel(
  thumbnail: ByteArray,
  categoryNames: List<String>,
): FoodItem =
  FoodItem(
    realmId = "",
    name = productName,
    barcode = code,
    brand = brands.split(",").firstOrNull() ?: brands,
    imageByteArray = thumbnail,
    categories = categoryNames,
    novaGroup = NovaGroup.entries.getOrNull(novaGroup) ?: NovaGroup.UNKNOWN,
    nutriScore = NutriScore.enumByLetter(nutriscoreGrade),
    nutriments = nutriments.toDomainModel(productQuantity)
  )

fun OpenFoodFactsTaxonomyNode.toRealmModel(): RealmFoodCategory =
  RealmFoodCategory().apply {
    _id = id
    name = this@toRealmModel.name
  }
