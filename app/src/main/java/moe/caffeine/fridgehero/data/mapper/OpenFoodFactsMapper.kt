package moe.caffeine.fridgehero.data.mapper

import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsNutriments
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsProduct
import moe.caffeine.fridgehero.data.model.openfoodfacts.OpenFoodFactsTaxonomyNode
import moe.caffeine.fridgehero.data.model.realm.RealmFoodCategory
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NovaGroup
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutriScore
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.NutrimentAvailability
import kotlin.math.round

fun OpenFoodFactsNutriments.toDomainModel(productQuantity: Double): Pair<Map<Nutriment, Double>, NutrimentAvailability> {
  val multiplier = if (productQuantity != 0.0) (productQuantity / 100) else 1.0
  var availability: NutrimentAvailability =
    if (productQuantity != 0.0) NutrimentAvailability.FULL else NutrimentAvailability.PER_100G
  var map = mapOf(
    Nutriment.FAT to round(fat100g * multiplier),
    Nutriment.SALT to round(salt100g * multiplier),
    Nutriment.FIBER to round(fiber100g * multiplier),
    Nutriment.ENERGY to round(energyKcal100g * multiplier),
    Nutriment.SODIUM to round(sodium100g * multiplier),
    Nutriment.CARBOHYDRATES to round(carbohydrates100g * multiplier),
    Nutriment.PROTEINS to round(proteins100g * multiplier),
    Nutriment.SATURATED_FAT to round(saturatedFat100g * multiplier),
    Nutriment.SUGARS to round(sugars100g * multiplier)
  )
  if (map.all { it.value == 0.0 }) {
    map = mapOf(
      Nutriment.FAT to round(fatServing),
      Nutriment.SALT to round(saltServing),
      Nutriment.FIBER to round(fiberServing),
      Nutriment.ENERGY to round(energyKcalServing),
      Nutriment.SODIUM to round(sodiumServing),
      Nutriment.CARBOHYDRATES to round(carbohydratesServing),
      Nutriment.PROTEINS to round(proteinsServing),
      Nutriment.SATURATED_FAT to round(saturatedFatServing),
      Nutriment.SUGARS to round(sugarsServing)
    )
    availability = NutrimentAvailability.PER_SERVING
  }
  if (map.all { it.value == 0.0 })
    availability = NutrimentAvailability.UNAVALIABLE

  return Pair(map, availability)
}

fun OpenFoodFactsProduct.toDomainModel(
  categoryNames: List<String>,
): FoodItem {
  val nutriments = nutriments.toDomainModel(productQuantity)
  return FoodItem(
    realmId = "",
    name = productName,
    barcode = code,
    brand = brands.split(",").firstOrNull() ?: brands,
    imageByteArray = byteArrayOf(),
    categories = categoryNames,
    novaGroup = NovaGroup.entries.getOrNull(novaGroup) ?: NovaGroup.UNKNOWN,
    nutriScore = NutriScore.enumByLetter(nutriscoreGrade),
    nutriments = nutriments.first,
    nutrimentAvailability = nutriments.second
  )
}

fun OpenFoodFactsTaxonomyNode.toRealmModel(): RealmFoodCategory =
  RealmFoodCategory().apply {
    _id = id
    name = this@toRealmModel.name
  }
