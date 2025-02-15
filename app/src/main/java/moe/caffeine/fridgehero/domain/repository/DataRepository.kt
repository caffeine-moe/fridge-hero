package moe.caffeine.fridgehero.domain.repository

import kotlinx.coroutines.flow.Flow
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.remote.openfoodfacts.OpenFoodFactsApi
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import org.mongodb.kbson.BsonObjectId

interface DataRepository {

  //used for interactions
  val realmProvider: RealmProvider
  val openFoodFactsApi: OpenFoodFactsApi

  //profile interactions
  fun getProfile(): Result<Profile>
  fun upsertProfile(profile: Profile)
  fun deleteProfile(profile: Profile)

  //food item interactions
  fun getAllFoodItemsAsFlow(): Flow<List<FoodItem>>
  fun getFoodItemById(objectId: BsonObjectId): Result<FoodItem>
  fun upsertFoodItem(foodItem: FoodItem): Result<FoodItem>
  fun deleteFoodItem(foodItem: FoodItem): Result<FoodItem>
  suspend fun fetchFoodItemFromApi(barcode: String): Result<FoodItem>
  suspend fun retrieveFoodItemCachedFirst(barcode: String): Result<FoodItem>

  //recipe interactions
  fun getAllRecipesAsFlow(): Flow<List<Recipe>>
  fun getRecipeById(objectId: BsonObjectId): Result<Recipe>
  fun upsertRecipe(recipe: Recipe)
  fun deleteRecipe(recipe: Recipe)
}
