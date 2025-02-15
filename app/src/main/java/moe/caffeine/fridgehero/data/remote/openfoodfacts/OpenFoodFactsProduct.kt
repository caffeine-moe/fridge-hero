package moe.caffeine.fridgehero.data.remote.openfoodfacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import moe.caffeine.fridgehero.data.realm.FoodItem
import moe.caffeine.fridgehero.data.remote.openfoodfacts.OpenFoodFactsApi.fetchImageAsByteArrayFromURL

@Serializable
data class OpenFoodFactsProduct(
  val allergens: String = "",
  @SerialName("allergens_from_ingredients")
  val allergensFromIngredients: String = "",
  @SerialName("allergens_hierarchy")
  val allergensHierarchy: List<String> = listOf(),
  @SerialName("amino_acids_tags")
  val aminoAcidsTags: List<String> = listOf(),
  val brands: String = "",
  @SerialName("brands_tags")
  val brandsTags: List<String> = listOf(),
  val categories: String = "",
  @SerialName("categories_hierarchy")
  val categoriesHierarchy: List<String> = listOf(),
  @SerialName("categories_tags")
  val categoriesTags: List<String> = listOf(),
  val code: String = "",
  val complete: Int = 0,
  val completeness: Double = 0.0,
  @SerialName("food_groups_tags")
  val foodGroupsTags: List<String> = listOf(),
  @SerialName("generic_name")
  val genericName: String = "",
  @SerialName("generic_name_en")
  val genericNameEnglish: String = "",
  val id: String = "",
  @SerialName("image_front_small_url")
  val imageFrontSmallUrl: String = "",
  @SerialName("image_front_thumb_url")
  val imageFrontThumbUrl: String = "",
  @SerialName("image_front_url")
  val imageFrontUrl: String = "",
  @SerialName("image_small_url")
  val imageSmallUrl: String = "",
  @SerialName("image_thumb_url")
  val imageThumbUrl: String = "",
  @SerialName("image_url")
  val imageUrl: String = "",
  @SerialName("ingredients_from_palm_oil_tags")
  val ingredientsFromPalmOilTags: List<String> = listOf(),
  @SerialName("ingredients_text")
  val ingredientsText: String = "",
  @SerialName("ingredients_text_en")
  val ingredientsTextEnglish: String = "",
  @SerialName("ingredients_text_with_allergens")
  val ingredientsTextWithAllergens: String = "",
  @SerialName("ingredients_text_with_allergens_en")
  val ingredientsTextWithAllergensEnglish: String = "",
  @SerialName("ingredients_that_may_be_from_palm_oil_tags")
  val ingredientsThatMayBeFromPalmOilTags: List<String> = listOf(),
  val labels: String = "",
  @SerialName("labels_hierarchy")
  val labelsHierarchy: List<String> = listOf(),
  @SerialName("labels_tags")
  val labelsTags: List<String> = listOf(),
  val link: String = "",
  @SerialName("minerals_tags")
  val mineralsTags: List<String> = listOf(),
  @SerialName("no_nutrition_data")
  val noNutritionData: String = "",
  @SerialName("nutriscore_grade")
  val nutriscoreGrade: String = "",
  @SerialName("nutriscore_tags")
  val nutriscoreTags: List<String> = listOf(),
  @SerialName("nutriscore_version")
  val nutriscoreVersion: String = "",
  @SerialName("nutrition_data")
  val nutritionData: String = "",
  @SerialName("nutrition_data_per")
  val nutritionDataPer: String = "",
  @SerialName("nutrition_data_prepared")
  val nutritionDataPrepared: String = "",
  @SerialName("nutrition_data_prepared_per")
  val nutritionDataPreparedPer: String = "",
  @SerialName("nutrition_grade_fr")
  val nutritionGradeFr: String = "",
  @SerialName("nutrition_grades")
  val nutritionGrades: String = "",
  @SerialName("nutrition_grades_tags")
  val nutritionGradesTags: List<String> = listOf(),
  @SerialName("nutrition_score_beverage")
  val nutritionScoreBeverage: Int = 0,
  @SerialName("nutrition_score_debug")
  val nutritionScoreDebug: String = "",
  @SerialName("nutrition_score_warning_no_fiber")
  val nutritionScoreWarningNoFiber: Int = 0,
  @SerialName("nutrition_score_warning_no_fruits_vegetables_nuts")
  val nutritionScoreWarningNoFruitsVegetablesNuts: Int = 0,
  @SerialName("other_nutritional_substances_tags")
  val otherNutritionalSubstancesTags: List<String> = listOf(),
  val packaging: String = "",
  @SerialName("packaging_hierarchy")
  val packagingHierarchy: List<String> = listOf(),
  @SerialName("packaging_materials_tags")
  val packagingMaterialsTags: List<String> = listOf(),
  @SerialName("packaging_recycling_tags")
  val packagingRecyclingTags: List<String> = listOf(),
  @SerialName("packaging_shapes_tags")
  val packagingShapesTags: List<String> = listOf(),
  @SerialName("packaging_tags")
  val packagingTags: List<String> = listOf(),
  @SerialName("packaging_text")
  val packagingText: String = "",
  @SerialName("packaging_text_en")
  val packagingTextEn: String = "",
  @SerialName("product_name")
  val productName: String = "",
  @SerialName("product_name_en")
  val productNameEnglish: String = "",
  val quantity: String = "",
  @SerialName("vitamins_tags")
  val vitaminsTags: List<String> = listOf(),
) {
  suspend fun asFoodItem(): FoodItem {
    val imageFromUrl = fetchImageAsByteArrayFromURL(imageThumbUrl).await()
    return FoodItem().apply {
      name = productName
      barcode = code
      brand = brandsTags.firstOrNull() ?: "N/A"
      imageFromUrl.fold(
        onSuccess = { fetchedByteArray ->
          imageByteArray = fetchedByteArray
        },
        onFailure = {}
      )
    }
  }
}
