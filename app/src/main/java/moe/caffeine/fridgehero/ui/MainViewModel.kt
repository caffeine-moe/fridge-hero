package moe.caffeine.fridgehero.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import moe.caffeine.fridgehero.data.openfoodfacts.remote.OpenFoodFactsApi
import moe.caffeine.fridgehero.data.realm.RealmProvider
import moe.caffeine.fridgehero.data.repository.DataRepositoryImpl
import moe.caffeine.fridgehero.data.repository.deleteDomainModel
import moe.caffeine.fridgehero.data.repository.upsertDomainModel
import moe.caffeine.fridgehero.domain.Event
import moe.caffeine.fridgehero.domain.helper.fuzzyMatch
import moe.caffeine.fridgehero.domain.initialisation.InitialisationStage
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.DomainModel
import moe.caffeine.fridgehero.domain.model.NutrimentBreakdown
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.model.fooditem.nutrition.Nutriment
import moe.caffeine.fridgehero.domain.repository.DataRepository
import kotlin.math.abs

class MainViewModel : ViewModel() {
  private val repository: DataRepository = DataRepositoryImpl(
    realmProvider = RealmProvider,
    openFoodFactsApi = OpenFoodFactsApi,
    viewModelScope
  )

  val profile: StateFlow<Result<Profile>?> = repository.getProfileAsFlow()
    .onStart { emit(null) }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = Result.failure(Throwable("No profile found."))
    )

  private suspend inline fun <D : DomainModel, reified R : RealmObject, M : MappableModel<D, R>>
          upsertDomainModelAndComplete(
    model: M,
    completable: CompletableDeferred<Result<D>>
  ) {
    completable.complete(
      repository.upsertDomainModel(model)
    )
  }

  private suspend inline fun <D : DomainModel, reified R : RealmObject, M : MappableModel<D, R>>
          deleteDomainModelAndComplete(
    model: M,
    completable: CompletableDeferred<Result<D>>
  ) {
    completable.complete(
      repository.deleteDomainModel(model)
    )
  }

  fun upsertProfile(profile: Profile) =
    viewModelScope.launch { repository.upsertDomainModel(profile) }

  private fun initialiseRepository() = viewModelScope.launch { repository.initialise() }

  val initialisationStage: StateFlow<InitialisationStage> = repository.initialisationStage
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      InitialisationStage.None
    )

  private fun sortByExpiry(foodItems: List<FoodItem>): List<FoodItem> {
    val expiredItems = foodItems.filter { it.isExpired }.sortedBy { it.name }
    val expiringSoonItems = foodItems.filter { it.expiresSoon }.sortedBy { it.name }
    val remainingItems =
      foodItems.filterNot { it.isExpired || it.expiresSoon }.sortedBy { it.name }

    return (expiredItems + expiringSoonItems + remainingItems).toSet().toMutableList()
  }

  val foodItems: StateFlow<List<FoodItem>> = repository.getAllFoodItemsAsFlow()
    .map { items -> sortByExpiry(items) }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      emptyList()
    )

  private fun recipeSort(recipes: List<Recipe>): List<Recipe> =
    recipes.sortedBy { recipe -> recipe.ingredients.count { it.expiresSoon && !it.isExpired } }
      .sortedBy { it.name.lowercase() }

  val recipes: StateFlow<List<Recipe>> = repository.getAllRecipesAsFlow()
    .map { recipeSort(it) }
    .stateIn(
      viewModelScope,
      SharingStarted.Eagerly,
      emptyList()
    )

  private val _eventFlow = MutableSharedFlow<Event>()
  val eventFlow: SharedFlow<Event> = _eventFlow

  fun emitEvent(event: Event) = viewModelScope.launch { _eventFlow.emit(event) }

  init {
    if (initialisationStage.value == InitialisationStage.None) {
      initialiseRepository()
    }
    eventFlow.onEach { event ->
      when (event) {
        is Event.RequestFoodItemFromBarcode -> {
          emitEvent(Event.DisplayToast("Processing barcode ${event.barcode}, please wait..."))
          val result = repository.retrieveFoodItemCachedFirst(event.barcode).fold(
            onSuccess = { foodItem ->
              emitEvent(Event.DisplayToast("Successfully retrieved ${foodItem.name}"))
              Result.success(foodItem)
            },
            onFailure = { failure ->
              emitEvent(Event.DisplayToast("ERROR: ${failure.message}"))
              Result.failure(failure)
            }
          )
          event.onResult(result)
        }

        //food item

        is Event.RequestNutrimentBreakdown ->
          if (event.items.isEmpty())
            event.onResult(
              Result.failure(Throwable("Need to request breakdown of at least one item."))
            )
          else
            event.onResult(Result.success(breakDownNutriments(event.items)))

        is Event.UpsertFoodItem ->
          repository.upsertDomainModel(event.foodItem).also { event.onResult(it) }

        is Event.SoftRemoveFoodItem ->
          repository.upsertDomainModel(event.foodItem.copy(expiryDates = listOf()))
            .also { event.onResult(it) }

        is Event.DeleteFoodItem ->
          repository.deleteDomainModel(event.foodItem).also { event.onResult(it) }

        //recipe

        is Event.UpsertRecipe ->
          repository.upsertDomainModel(event.recipe).also { event.onResult(it) }

        is Event.FindPotentialMatches ->
          event.onResult(Result.success(findPotentialMatches(event.foodItem)))

        else -> return@onEach
      }
    }.launchIn(viewModelScope)
  }

  private fun findPotentialMatches(item: FoodItem): List<FoodItem> =
    foodItems.value
      .filterNot { it.isRemoved || it.isExpired }
      .filter { avaliableItem ->
        avaliableItem.name
          .split(" ")
          .count { avaliableWord ->
            item.name
              .split(" ")
              .any { searchWord ->
                fuzzyMatch(avaliableWord, searchWord)
              }
          } >= (listOf(item.name, avaliableItem.name).min().split(" ").size * 0.5)
                || (avaliableItem.categories
          .count { item.categories.contains(it) } >=
                abs(item.categories.size) * 0.75 &&
                item.categories.isNotEmpty() &&
                avaliableItem.categories.isNotEmpty())
      }

  private fun breakDownNutriments(items: List<FoodItem>): NutrimentBreakdown {
    val totals: MutableMap<Nutriment, String> = mutableMapOf()
    val getNumber = { x: String -> x.split(" ").firstOrNull()?.toDouble() ?: 0.0 }
    val getUnit = { x: String -> x.split(" ").lastOrNull() ?: "g" }

    Nutriment.entries.forEach { nutriment ->
      var total = 0.0
      var unit = "g"
      items.forEach { item ->
        item.nutriments[nutriment]?.let { itemNutriment ->
          unit = getUnit(itemNutriment)
          total += getNumber(itemNutriment)
        }
      }
      totals[nutriment] =
        "${(total.toString().split(".").let { "${it.first()}.${it.last().take(1)}" })} $unit"
    }

    return NutrimentBreakdown(items, totals)
  }
}
