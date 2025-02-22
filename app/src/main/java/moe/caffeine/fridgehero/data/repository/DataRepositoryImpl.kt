package moe.caffeine.fridgehero.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import moe.caffeine.fridgehero.data.mapper.toDomainModel
import moe.caffeine.fridgehero.data.model.RealmFoodItem
import moe.caffeine.fridgehero.data.model.RealmProfile
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.realm.deleteObjectById
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

  override fun getProfileAsFlow(): Flow<Result<Profile>?> =
    realmProvider.realmInstance.fetchAllByTypeAsFlow<RealmProfile>()
      .transform { profiles ->
        emit(
          profiles.map { profile -> Result.success(profile.toDomainModel()) }.firstOrNull()
            ?: Result.failure(Throwable("No profile found."))
        )
      }.flowOn(Dispatchers.IO)

  override suspend fun upsertProfile(profile: Profile) {
    withContext(Dispatchers.IO) {
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
  }

  override suspend fun deleteProfile(profile: Profile) {
    withContext(Dispatchers.IO) {
      realmProvider.realmInstance.deleteObjectById<RealmProfile>(
        profile.toRealmModel()._id
      )
    }
  }

  override fun getAllFoodItemsAsFlow(): Flow<List<FoodItem>> =
    realmProvider.realmInstance.fetchAllByTypeAsFlow<RealmFoodItem>()
      .transform { foodItems -> emit(foodItems.map { it.toDomainModel() }) }
      .flowOn(Dispatchers.IO)

  override suspend fun getFoodItemById(objectId: BsonObjectId): Result<FoodItem> =
    withContext(Dispatchers.IO) {
      realmProvider.realmInstance.fetchObjectById<RealmFoodItem>(objectId).fold(
        onSuccess = {
          Result.success(it.toDomainModel())
        },
        onFailure = {
          Result.failure(it)
        }
      )
    }

  override suspend fun upsertFoodItem(foodItem: FoodItem): Result<FoodItem> =
    withContext(Dispatchers.IO) {
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
    }

  override suspend fun deleteFoodItem(foodItem: FoodItem): Result<FoodItem> =
    withContext(Dispatchers.IO) {
      realmProvider.realmInstance.deleteObjectById<RealmFoodItem>(
        foodItem.toRealmModel()._id
      ).fold(
        onSuccess = {
          Result.success(foodItem)
        },
        onFailure = {
          Result.failure(it)
        }
      )
    }

  override suspend fun fetchFoodItemFromApi(barcode: String): Result<FoodItem> =
    withContext(Dispatchers.IO) {
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
    }

  override suspend fun retrieveFoodItemCachedFirst(barcode: String): Result<FoodItem> =
    withContext(Dispatchers.IO) {
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
    }

  override suspend fun getAllRecipesAsFlow(): Flow<List<Recipe>> {
    TODO("Not yet implemented")
  }

  override suspend fun getRecipeById(objectId: BsonObjectId): Result<Recipe> {
    TODO("Not yet implemented")
  }

  override suspend fun upsertRecipe(recipe: Recipe) {
    TODO("Not yet implemented")
  }

  override suspend fun deleteRecipe(recipe: Recipe) {
    TODO("Not yet implemented")
  }
}
