package moe.caffeine.fridgehero.data.repository

import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.CompletableDeferred
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
import kotlinx.coroutines.withContext
import moe.caffeine.fridgehero.data.mapper.toDomainModel
import moe.caffeine.fridgehero.data.model.realm.RealmFoodItem
import moe.caffeine.fridgehero.data.model.realm.RealmProfile
import moe.caffeine.fridgehero.data.model.realm.RealmRecipe
import moe.caffeine.fridgehero.data.openfoodfacts.local.OpenFoodFactsTaxonomyParser
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi.fetchProductByBarcode
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.realm.deleteObjectById
import moe.caffeine.fridgehero.data.realm.fetchAllByType
import moe.caffeine.fridgehero.data.realm.fetchAllByTypeAsFlow
import moe.caffeine.fridgehero.data.realm.fetchObjectById
import moe.caffeine.fridgehero.data.realm.updateObject
import moe.caffeine.fridgehero.domain.initialisation.InitialisationStage
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.DomainModel
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
      /*      when {
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
            }*/
      _initialisationStageFlow.emit(InitialisationStage.Finished)
    }
  }

  /*  private suspend fun initialiseTaxonomies(progressFlow: MutableStateFlow<Float>): Result<Nothing?> {
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
      *//*    realm.fetchAllByType<RealmFoodCategory>().takeLast(20).forEach {
          println(
            "${it.name} \n|| PARENTS: ${
              it.parentsMap.values.joinToString(", ") { it.name }
            } \n|| CHILDREN: ${
              it.childrenMap.values.joinToString(", ") { it.name }
            } \n|| LEVEL: ${it.findTrees().lastOrNull()?.values?.map { it.name }}"
          )
        }*//*
    return Result.success(null)
  }*/

  override fun getProfileAsFlow(): Flow<Result<Profile>?> =
    realm.fetchAllByTypeAsFlow<RealmProfile>()
      .transform { profiles ->
        emit(
          profiles.map { profile -> Result.success(profile.toDomainModel()) }
            .firstOrNull()
            ?: Result.failure(Throwable("No profile found."))
        )
      }.flowOn(Dispatchers.IO)

  override fun getAllFoodItemsAsFlow(): Flow<List<FoodItem>> =
    realm.fetchAllByTypeAsFlow<RealmFoodItem>()
      .distinctUntilChanged()
      .transform { foodItems ->
        emit(
          foodItems
            .map { it.toDomainModel() }
            .sortedBy { it.name.lowercase() }
        )
      }
      .flowOn(Dispatchers.IO)

  override fun getAllFoodItemsAsList(): List<FoodItem> =
    realm.fetchAllByType<RealmFoodItem>().map { it.toDomainModel() }

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

  override suspend fun fetchFoodItemFromApi(barcode: String): Result<FoodItem> =
    withContext(Dispatchers.IO) {
      CompletableDeferred<ByteArray>()
      fetchProductByBarcode(barcode).fold(
        onSuccess = { product ->
          Result.success(
            product.toDomainModel(
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
          return@withContext Result.failure(it)
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
      )
    }

  override suspend fun retrieveItemImage(barcode: String): Result<ByteArray> =
    withContext(Dispatchers.IO) {
      realm.fetchAllByType<RealmFoodItem>()
        .firstOrNull { it.barcode == barcode }?.let {
          Result.success(it.toDomainModel().imageByteArray)
        } ?: openFoodFactsApi.fetchProductByBarcode(
        barcode
      ).getOrNull()?.let { openFoodFactsApi.fetchImageAsByteArrayFromURL(it.imageThumbUrl) }
      ?: Result.failure(
        Throwable("Failed to fetch image from api")
      )
    }

  override fun getAllRecipesAsFlow(): Flow<List<Recipe>> =
    realm.fetchAllByTypeAsFlow<RealmRecipe>()
      .distinctUntilChanged()
      .transform { recipes -> emit(recipes.map { it.toDomainModel() }) }
      .flowOn(Dispatchers.IO)

  override fun getLeftOverFromRecipe(recipe: Recipe): Result<FoodItem> =
    Result.success(
      recipe.toRealmModel().leftOver(realm).toDomainModel()
    )
}

suspend inline fun <D : DomainModel, reified R : RealmObject, M : MappableModel<D, R>> DataRepository.upsertDomainModel(
  model: M
): Result<D> =
  withContext(Dispatchers.IO) {
    this@upsertDomainModel.realmProvider.realmInstance.updateObject(
      model.toRealmModel()
    ).fold(
      onSuccess = {
        Result.success(model.toDomainModel())
      },
      onFailure = {
        Result.failure(it)
      },
    )
  }

suspend inline fun <D : DomainModel, reified R : RealmObject, M : MappableModel<D, R>> DataRepository.deleteDomainModel(
  model: M
): Result<D> =
  withContext(Dispatchers.IO) {
    this@deleteDomainModel.realmProvider.realmInstance.deleteObjectById<R>(
      model.realmObjectId
    ).fold(
      onSuccess = {
        Result.success(model.toDomainModel())
      },
      onFailure = {
        Result.failure(it)
      }
    )
  }
