package moe.caffeine.fridgehero.data.mapper

import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsNutriments
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsProduct
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsTaxonomyNode
import moe.caffeine.fridgehero.data.model.realm.RealmFoodCategory
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment

fun OpenFoodFactsNutriments.toDomainModel(): Map<Nutriment, String> =
  mapOf(
    Nutriment.FAT to "$fat $fatUnit",
    Nutriment.SALT to "$salt $saltUnit",
    Nutriment.FIBER to "$fiber $fiberUnit",
    Nutriment.ENERGY to "$energyKcal $energyKcalUnit",
    Nutriment.SODIUM to "$sodium $sodiumUnit",
    Nutriment.CARBOHYDRATES to "$carbohydrates $carbohydratesUnit",
    Nutriment.PROTEINS to "$proteins $proteinsUnit",
    Nutriment.SATURATED_FAT to "$saturatedFat $saturatedFatUnit",
    Nutriment.SUGARS to "$sugars $sugarsUnit"
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
    novaGroup = NovaGroup.enumByNumber(novaGroup),
    nutriScore = NutriScore.enumByLetter(nutriscoreGrade),
    nutriments = nutriments.toDomainModel()
  )

fun OpenFoodFactsTaxonomyNode.toRealmModel(): RealmFoodCategory =
  RealmFoodCategory().apply {
    _id = id
    name = this@toRealmModel.name
  }
