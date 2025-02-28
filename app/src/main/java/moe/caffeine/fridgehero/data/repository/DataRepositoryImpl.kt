package moe.caffeine.fridgehero.data.repository

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import moe.caffeine.fridgehero.data.mapper.toDomainModel
import moe.caffeine.fridgehero.data.model.realm.RealmFoodCategory
import moe.caffeine.fridgehero.data.model.realm.RealmFoodItem
import moe.caffeine.fridgehero.data.model.realm.RealmProfile
import moe.caffeine.fridgehero.data.openfoodfacts.local.OpenFoodFactsTaxonomyParser
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi.fetchImageAsByteArrayFromURL
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi.fetchProductByBarcode
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.realm.deleteObjectById
import moe.caffeine.fridgehero.data.realm.fetchAllByType
import moe.caffeine.fridgehero.data.realm.fetchAllByTypeAsFlow
import moe.caffeine.fridgehero.data.realm.fetchObjectById
import moe.caffeine.fridgehero.data.realm.updateObject
import moe.caffeine.fridgehero.domain.mapper.toDomainModel
import moe.caffeine.fridgehero.domain.mapper.toRealmModel
import moe.caffeine.fridgehero.domain.model.FoodCategory
import moe.caffeine.fridgehero.domain.model.FoodItem
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.repository.DataRepository
import org.mongodb.kbson.BsonObjectId

class DataRepositoryImpl(
  override val realmProvider: RealmProvider = RealmProvider,
  override val openFoodFactsApi: OpenFoodFactsApi = OpenFoodFactsApi
) : DataRepository {
  override suspend fun ensureReady(): CompletableDeferred<Result<Boolean>> =
    CompletableDeferred<Result<Boolean>>().also { completable ->
      if (realmProvider.realmInstance.fetchAllByType<RealmFoodCategory>().isEmpty()) {
        withContext(Dispatchers.Default) {
          val taxonomyNodes = OpenFoodFactsTaxonomyParser.parse()
          val realmCategories = mutableMapOf<String, RealmFoodCategory>()
          realmProvider.realmInstance.writeBlocking {

            taxonomyNodes.values.forEach { node ->
              val realmCategory = RealmFoodCategory().apply {
                _id = node.id
                name = node.name
              }
              realmCategories[node.name] = realmCategory
              copyToRealm(realmCategory, UpdatePolicy.ALL)
            }

            realmCategories.values.forEach { category ->
              val taxnode = taxonomyNodes[category.name] ?: return@forEach
              taxnode.parents.keys.forEach {
                copyToRealm(
                  category.apply { parents.add(realmCategories[it]!!) }, UpdatePolicy.ALL
                )
              }
            }
          }
          completable.complete(Result.success(true))
          realmProvider.realmInstance.fetchAllByType<RealmFoodCategory>().forEach {
            println(
              "${it.name} \n|| PARENTS: ${
                it.parents.map { it.name }.joinToString(", ")
              } \n|| CHILDREN: ${
                it.children.map { it.name }.joinToString(
                  ", "
                )
              } "
            )
          }
        }
      } else {
        completable.complete(Result.success(true))
      }
    }

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
        foodItem.toRealmModel(realmProvider.realmInstance)
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
        foodItem.toRealmModel(realmProvider.realmInstance)._id
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
              ),
              it.categoriesHierarchy.map {
                realmProvider.realmInstance.query<RealmFoodCategory>("name == $0", it).first()
                  .find()?.toDomainModel() ?: FoodCategory(name = it)
              }
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
