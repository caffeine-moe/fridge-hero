package moe.caffeine.fridgehero.data.model.openfoodfacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OpenFoodFactsNutriments(
  @SerialName("carbohydrates")
  val carbohydrates: Double = 0.0,
  @SerialName("carbohydrates_100g")
  val carbohydrates100g: Double = 0.0,
  @SerialName("carbohydrates_serving")
  val carbohydratesServing: Double = 0.0,
  @SerialName("carbohydrates_unit")
  val carbohydratesUnit: String = "",
  @SerialName("carbohydrates_value")
  val carbohydratesValue: Double = 0.0,
  @SerialName("carbon-footprint-from-known-ingredients_product")
  val carbonFootprintFromKnownIngredientsProduct: Double = 0.0,
  @SerialName("carbon-footprint-from-known-ingredients_serving")
  val carbonFootprintFromKnownIngredientsServing: Double = 0.0,
  @SerialName("energy")
  val energy: Double = 0.0,
  @SerialName("energy_100g")
  val energy100g: Double = 0.0,
  @SerialName("energy-kcal")
  val energyKcal: Double = 0.0,
  @SerialName("energy-kcal_100g")
  val energyKcal100g: Double = 0.0,
  @SerialName("energy-kcal_serving")
  val energyKcalServing: Double = 0.0,
  @SerialName("energy-kcal_unit")
  val energyKcalUnit: String = "",
  @SerialName("energy-kcal_value")
  val energyKcalValue: Double = 0.0,
  @SerialName("energy-kcal_value_computed")
  val energyKcalValueComputed: Double = 0.0,
  @SerialName("energy-kj")
  val energyKj: Double = 0.0,
  @SerialName("energy-kj_100g")
  val energyKj100g: Double = 0.0,
  @SerialName("energy-kj_serving")
  val energyKjServing: Double = 0.0,
  @SerialName("energy-kj_unit")
  val energyKjUnit: String = "",
  @SerialName("energy-kj_value")
  val energyKjValue: Double = 0.0,
  @SerialName("energy-kj_value_computed")
  val energyKjValueComputed: Double = 0.0,
  @SerialName("energy_serving")
  val energyServing: Double = 0.0,
  @SerialName("energy_unit")
  val energyUnit: String = "",
  @SerialName("energy_value")
  val energyValue: Double = 0.0,
  @SerialName("fat")
  val fat: Double = 0.0,
  @SerialName("fat_100g")
  val fat100g: Double = 0.0,
  @SerialName("fat_serving")
  val fatServing: Double = 0.0,
  @SerialName("fat_unit")
  val fatUnit: String = "",
  @SerialName("fat_value")
  val fatValue: Double = 0.0,
  @SerialName("fiber")
  val fiber: Double = 0.0,
  @SerialName("fiber_100g")
  val fiber100g: Double = 0.0,
  @SerialName("fiber_serving")
  val fiberServing: Double = 0.0,
  @SerialName("fiber_unit")
  val fiberUnit: String = "",
  @SerialName("fiber_value")
  val fiberValue: Double = 0.0,
  @SerialName("fruits-vegetables-legumes-estimate-from-ingredients_100g")
  val fruitsVegetablesLegumesEstimateFromIngredients100g: Double = 0.0,
  @SerialName("fruits-vegetables-legumes-estimate-from-ingredients_serving")
  val fruitsVegetablesLegumesEstimateFromIngredientsServing: Double = 0.0,
  @SerialName("fruits-vegetables-nuts-estimate-from-ingredients_100g")
  val fruitsVegetablesNutsEstimateFromIngredients100g: Double = 0.0,
  @SerialName("fruits-vegetables-nuts-estimate-from-ingredients_serving")
  val fruitsVegetablesNutsEstimateFromIngredientsServing: Double = 0.0,
  @SerialName("nova-group")
  val novaGroup: Double = 0.0,
  @SerialName("nova-group_100g")
  val novaGroup100g: Double = 0.0,
  @SerialName("nova-group_serving")
  val novaGroupServing: Double = 0.0,
  @SerialName("nutrition-score-fr")
  val nutritionScoreFr: Double = 0.0,
  @SerialName("nutrition-score-fr_100g")
  val nutritionScoreFr100g: Double = 0.0,
  @SerialName("proteins")
  val proteins: Double = 0.0,
  @SerialName("proteins_100g")
  val proteins100g: Double = 0.0,
  @SerialName("proteins_serving")
  val proteinsServing: Double = 0.0,
  @SerialName("proteins_unit")
  val proteinsUnit: String = "",
  @SerialName("proteins_value")
  val proteinsValue: Double = 0.0,
  @SerialName("salt")
  val salt: Double = 0.0,
  @SerialName("salt_100g")
  val salt100g: Double = 0.0,
  @SerialName("salt_serving")
  val saltServing: Double = 0.0,
  @SerialName("salt_unit")
  val saltUnit: String = "",
  @SerialName("salt_value")
  val saltValue: Double = 0.0,
  @SerialName("saturated-fat")
  val saturatedFat: Double = 0.0,
  @SerialName("saturated-fat_100g")
  val saturatedFat100g: Double = 0.0,
  @SerialName("saturated-fat_serving")
  val saturatedFatServing: Double = 0.0,
  @SerialName("saturated-fat_unit")
  val saturatedFatUnit: String = "",
  @SerialName("saturated-fat_value")
  val saturatedFatValue: Double = 0.0,
  @SerialName("sodium")
  val sodium: Double = 0.0,
  @SerialName("sodium_100g")
  val sodium100g: Double = 0.0,
  @SerialName("sodium_serving")
  val sodiumServing: Double = 0.0,
  @SerialName("sodium_unit")
  val sodiumUnit: String = "",
  @SerialName("sodium_value")
  val sodiumValue: Double = 0.0,
  @SerialName("sugars")
  val sugars: Double = 0.0,
  @SerialName("sugars_100g")
  val sugars100g: Double = 0.0,
  @SerialName("sugars_serving")
  val sugarsServing: Double = 0.0,
  @SerialName("sugars_unit")
  val sugarsUnit: String = "",
  @SerialName("sugars_value")
  val sugarsValue: Double = 0.0
)
