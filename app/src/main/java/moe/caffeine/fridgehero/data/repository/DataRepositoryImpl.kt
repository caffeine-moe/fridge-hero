package moe.caffeine.fridgehero.data.repository

import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.caffeine.fridgehero.data.mapper.toDomainModel
import moe.caffeine.fridgehero.data.mapper.toRealmModel
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
import moe.caffeine.fridgehero.domain.initialisation.InitialisationStage
import moe.caffeine.fridgehero.domain.mapper.toDomainModel
import moe.caffeine.fridgehero.domain.mapper.toRealmModel
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.repository.DataRepository
import org.mongodb.kbson.BsonObjectId

class DataRepositoryImpl(
  override val realmProvider: RealmProvider = RealmProvider,
  override val openFoodFactsApi: OpenFoodFactsApi = OpenFoodFactsApi,
  override val coroutineScope: CoroutineScope
) : DataRepository {

  private val realm = realmProvider.realmInstance

  private val _initialisationStageFlow: MutableSharedFlow<InitialisationStage> =
    MutableStateFlow(InitialisationStage.None)
  override val initialisationStage: SharedFlow<InitialisationStage> =
    _initialisationStageFlow.asSharedFlow()

  override suspend fun initialise() {
    withContext(Dispatchers.IO) {
      _initialisationStageFlow.emit(InitialisationStage.Started)
      when {
        realm.fetchAllByType<RealmFoodCategory>().isNotEmpty() -> {
          _initialisationStageFlow.emit(InitialisationStage.Finished)
        }

        else -> {
          val stage = InitialisationStage.TaxonomyInitialisation
          _initialisationStageFlow.emit(stage)
          initialiseTaxonomies(stage.progress).fold(
            onSuccess = {
              _initialisationStageFlow.emit(InitialisationStage.Finished)
            },
            onFailure = {
              _initialisationStageFlow.emit(InitialisationStage.Error)
              throw it //gently
            }
          )
        }
      }
    }
  }

  private suspend fun initialiseTaxonomies(progressFlow: MutableStateFlow<Float>): Result<Nothing?> {
    val taxonomyNodes =
      OpenFoodFactsTaxonomyParser.parse()
        .getOrElse { return Result.failure(it) }

    realm.write {
      //write all nodes
      taxonomyNodes.onEachIndexed { index, node ->
        coroutineScope.launch {
          progressFlow.emit(index / taxonomyNodes.keys.size.toFloat())
        }
        node.value.toRealmModel().also {
          it.children += node.value.children.map { child ->
            child.value.toRealmModel()
          }.toRealmList()
          copyToRealm(it, UpdatePolicy.ALL)
        }
      }
    }
    /*    realm.fetchAllByType<RealmFoodCategory>().takeLast(20).forEach {
          println(
            "${it.name} \n|| PARENTS: ${
              it.parentsMap.values.joinToString(", ") { it.name }
            } \n|| CHILDREN: ${
              it.childrenMap.values.joinToString(", ") { it.name }
            } \n|| LEVEL: ${it.findTrees().lastOrNull()?.values?.map { it.name }}"
          )
        }*/
    return Result.success(null)
  }

  override fun getProfileAsFlow(): Flow<Result<Profile>?> =
    realm.fetchAllByTypeAsFlow<RealmProfile>()
      .transform { profiles ->
        emit(
          profiles.map { profile -> Result.success(profile.toDomainModel()) }.firstOrNull()
            ?: Result.failure(Throwable("No profile found."))
        )
      }.flowOn(Dispatchers.IO)

  override suspend fun upsertProfile(profile: Profile) {
    withContext(Dispatchers.IO) {
      realm.updateObject(
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
      realm.deleteObjectById<RealmProfile>(
        profile.toRealmModel()._id
      )
    }
  }

  override fun getAllFoodItemsAsFlow(): Flow<List<FoodItem>> =
    realm.fetchAllByTypeAsFlow<RealmFoodItem>()
      .distinctUntilChanged()
      .transform { foodItems -> emit(foodItems.map { it.toDomainModel() }) }
      .flowOn(Dispatchers.IO)

  override suspend fun getFoodItemById(objectId: BsonObjectId): Result<FoodItem> =
    withContext(Dispatchers.IO) {
      realm.fetchObjectById<RealmFoodItem>(objectId).fold(
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
      realm.updateObject(
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
      realm.deleteObjectById<RealmFoodItem>(
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
        onSuccess = { product ->
          Result.success(
            product.toDomainModel(
              fetchImageAsByteArrayFromURL(product.imageThumbUrl).fold(
                onSuccess = { value -> value },
                onFailure = { byteArrayOf() }
              ),
              product.categoriesHierarchy
                .filter { it.startsWith(OpenFoodFactsTaxonomyParser.Constants.NODE_DEFINITION) }
                .map { category ->
                  category.removePrefix(OpenFoodFactsTaxonomyParser.Constants.NODE_DEFINITION)
                    .split("-")
                    .joinToString(" ").replaceFirstChar { char -> char.titlecase() }
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
      realm.fetchAllByType<RealmFoodItem>()
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
