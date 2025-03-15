package moe.caffeine.fridgehero.data.model.openfoodfacts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
  @SerialName("no_nutrition_data")
  val noNutritionData: String = "",
  @SerialName("nutriscore_grade")
  val nutriscoreGrade: String = "",
  @SerialName("nutriscore_tags")
  val nutriscoreTags: List<String> = listOf(),
  @SerialName("nutriscore_version")
  val nutriscoreVersion: String = "",
  @SerialName("nova_group")
  val novaGroup: Int = 0,
  @SerialName("nutriments")
  val nutriments: OpenFoodFactsNutriments = OpenFoodFactsNutriments(),
  @SerialName("product_name")
  val productName: String = "",
  @SerialName("product_name_en")
  val productNameEnglish: String = "",
  val quantity: String = "", //weight
)
