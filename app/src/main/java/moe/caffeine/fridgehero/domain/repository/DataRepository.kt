package moe.caffeine.fridgehero.domain.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.domain.initialisation.InitialisationStage
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import org.mongodb.kbson.BsonObjectId

interface DataRepository {

  //used for interactions
  val realmProvider: RealmProvider
  val openFoodFactsApi: OpenFoodFactsApi
  val coroutineScope: CoroutineScope
  val initialisationStage: SharedFlow<InitialisationStage>

  //initialisation
  suspend fun initialise()

  //profile interactions
  fun getProfileAsFlow(): Flow<Result<Profile>?>
  suspend fun upsertProfile(profile: Profile)
  suspend fun deleteProfile(profile: Profile)

  //food item interactions
  fun getAllFoodItemsAsFlow(): Flow<List<FoodItem>>
  suspend fun getFoodItemById(objectId: BsonObjectId): Result<FoodItem>
  suspend fun upsertFoodItem(foodItem: FoodItem): Result<FoodItem>
  suspend fun deleteFoodItem(foodItem: FoodItem): Result<FoodItem>
  suspend fun fetchFoodItemFromApi(barcode: String): Result<FoodItem>
  suspend fun retrieveFoodItemCachedFirst(barcode: String): Result<FoodItem>

  //recipe interactions
  suspend fun getAllRecipesAsFlow(): Flow<List<Recipe>>
  suspend fun getRecipeById(objectId: BsonObjectId): Result<Recipe>
  suspend fun upsertRecipe(recipe: Recipe)
  suspend fun deleteRecipe(recipe: Recipe)
}
