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
import moe.caffeine.fridgehero.domain.initialisation.InitialisationStage
import moe.caffeine.fridgehero.domain.mapping.DomainModel
import moe.caffeine.fridgehero.domain.mapping.MappableModel
import moe.caffeine.fridgehero.domain.model.Profile
import moe.caffeine.fridgehero.domain.model.Recipe
import moe.caffeine.fridgehero.domain.model.fooditem.FoodItem
import moe.caffeine.fridgehero.domain.repository.DataRepository

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

  private suspend inline fun <D : DomainModel, reified R : RealmObject, M : MappableModel<D, R>> upsertDomainModelAndComplete(
    model: M,
    completable: CompletableDeferred<Result<D>>
  ) {
    completable.complete(
      repository.upsertDomainModel(model)
    )
  }

  private suspend inline fun <D : DomainModel, reified R : RealmObject, M : MappableModel<D, R>> deleteDomainModelAndComplete(
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

  val foodItems: StateFlow<List<FoodItem>> = repository.getAllFoodItemsAsFlow()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

  val recipes: StateFlow<List<Recipe>> = repository.getAllRecipesAsFlow()
    .stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
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
          event.result.complete(result)
        }

        //food item

        is Event.UpsertFoodItem ->
          upsertDomainModelAndComplete(event.foodItem, event.result)

        is Event.SoftRemoveFoodItem ->
          upsertDomainModelAndComplete(event.foodItem.copy(expiryDates = listOf()), event.result)

        is Event.DeleteFoodItem ->
          deleteDomainModelAndComplete(event.foodItem, event.result)

        //recipe

        is Event.UpsertRecipe ->
          upsertDomainModelAndComplete(event.recipe, event.result)

        else -> return@onEach
      }
    }.launchIn(viewModelScope)
  }
}
