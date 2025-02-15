package moe.caffeine.fridgehero.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import moe.caffeine.fridgehero.data.mapper.toDomainModel
import moe.caffeine.fridgehero.data.model.RealmFoodItem
import moe.caffeine.fridgehero.data.model.RealmProfile
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.realm.deleteObject
import moe.caffeine.fridgehero.data.realm.fetchAllByType
import moe.caffeine.fridgehero.data.realm.fetchAllByTypeAsFlow
import moe.caffeine.fridgehero.data.realm.fetchObjectById
import moe.caffeine.fridgehero.data.realm.updateObject
import moe.caffeine.fridgehero.data.remote.openfoodfacts.OpenFoodFactsApi
import moe.caffeine.fridgehero.data.remote.openfoodfacts.OpenFoodFactsApi.fetchImageAsByteArrayFromURL
import moe.caffeine.fridgehero.data.remote.openfoodfacts.OpenFoodFactsApi.fetchProductByBarcode
import moe.caffeine.fridgehero.domain.mapper.toDomainModel
import moe.caffeine.fridgehero.domain.mapper.toRealmModel
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.repository.DataRepository
import org.mongodb.kbson.BsonObjectId

class DataRepositoryImpl(
  override val realmProvider: RealmProvider = RealmProvider,
  override val openFoodFactsApi: OpenFoodFactsApi = OpenFoodFactsApi
) : DataRepository {

  override fun getProfile(): Result<Profile> =
    realmProvider.realmInstance.fetchAllByType<RealmProfile>().firstOrNull()?.let {
      Result.success(it.toDomainModel())
    } ?: Result.failure(Throwable("No profiles found."))

  override fun upsertProfile(profile: Profile) {
    realmProvider.realmInstance.updateObject(
      profile.toRealmModel()
    ).fold(
      onSuccess = {
        Result.success(it.toDomainModel())
      },
      onFailure = {
        Result.failure(it)
      }
    )
  }

  override fun deleteProfile(profile: Profile) {
    realmProvider.realmInstance.deleteObject(
      profile.toRealmModel()
    )
  }

  override fun getAllFoodItemsAsFlow(): Flow<List<FoodItem>> =
    realmProvider.realmInstance.fetchAllByTypeAsFlow<RealmFoodItem>()
      .transform { list ->
        emit(list.map { foodItem -> foodItem.toDomainModel() })
      }

  override fun getFoodItemById(objectId: BsonObjectId): Result<FoodItem> =
    realmProvider.realmInstance.fetchObjectById<RealmFoodItem>(objectId).fold(
      onSuccess = {
        Result.success(it.toDomainModel())
      },
      onFailure = {
        Result.failure(it)
      }
    )

  override fun upsertFoodItem(foodItem: FoodItem): Result<FoodItem> =
    realmProvider.realmInstance.updateObject(
      foodItem.toRealmModel()
    ).fold(
      onSuccess = {
        Result.success(it.toDomainModel())
      },
      onFailure = {
        Result.failure(it)
      }
    )

  override fun deleteFoodItem(foodItem: FoodItem): Result<FoodItem> =
    realmProvider.realmInstance.deleteObject(
      foodItem.toRealmModel()
    ).fold(
      onSuccess = {
        Result.success(it.toDomainModel())
      },
      onFailure = {
        Result.failure(it)
      }
    )

  override suspend fun fetchFoodItemFromApi(barcode: String): Result<FoodItem> =
    fetchProductByBarcode(barcode).fold(
      onSuccess = {
        Result.success(
          it.toDomainModel(
            fetchImageAsByteArrayFromURL(it.imageThumbUrl).fold(
              onSuccess = { value -> value },
              onFailure = { byteArrayOf() }
            )
          )
        )
      },
      onFailure = {
        Result.failure(it)
      }
    )

  override suspend fun retrieveFoodItemCachedFirst(barcode: String): Result<FoodItem> =
    realmProvider.realmInstance.fetchAllByType<RealmFoodItem>()
      .firstOrNull { it.barcode == barcode }?.let {
        Result.success(it.toDomainModel())
      } ?: fetchFoodItemFromApi(
      barcode
    ).fold(
      onSuccess = { success ->
        Result.success(success)
      },
      onFailure = { failure ->
        Result.failure(failure)
      }
    )

  override fun getAllRecipesAsFlow(): Flow<List<Recipe>> {
    TODO("Not yet implemented")
  }

  override fun getRecipeById(objectId: BsonObjectId): Result<Recipe> {
    TODO("Not yet implemented")
  }

  override fun upsertRecipe(recipe: Recipe) {
    TODO("Not yet implemented")
  }

  override fun deleteRecipe(recipe: Recipe) {
    TODO("Not yet implemented")
  }
}
